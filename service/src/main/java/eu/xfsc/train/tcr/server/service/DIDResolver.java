package eu.xfsc.train.tcr.server.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.loader.DocumentLoader;
import com.apicatalog.jsonld.loader.DocumentLoaderOptions;
import com.danubetech.keyformats.crypto.PublicKeyVerifier;
import com.danubetech.keyformats.crypto.PublicKeyVerifierFactory;
import com.danubetech.keyformats.jose.JWK;
import com.danubetech.keyformats.jose.JWSAlgorithm;
import com.danubetech.keyformats.jose.KeyTypeName;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.github.benmanes.caffeine.cache.Cache;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

import eu.xfsc.train.tcr.server.exception.DidException;
import foundation.identity.did.DIDDocument;
import foundation.identity.did.Service;
import foundation.identity.did.VerificationMethod;
import foundation.identity.jsonld.JsonLDObject;
import foundation.identity.jsonld.JsonLDUtils;
import info.weboftrust.ldsignatures.LdProof;
import info.weboftrust.ldsignatures.verifier.LdVerifier;
import info.weboftrust.ldsignatures.verifier.LdVerifierRegistry;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import uniresolver.ResolutionException;
import uniresolver.UniResolver;
import uniresolver.result.ResolveRepresentationResult;

@Slf4j
@Component("did-resolver")
public class DIDResolver implements HealthIndicator {

	private static final Map<String, Object> RESOLVE_OPTIONS = Map.of("accept", "application/did+ld+json");
	private static final DocumentLoaderOptions DL_OPTIONS = new DocumentLoaderOptions();

	@Autowired
	private UniResolver resolver;
	@Autowired
	private DocumentLoader docLoader;
	@Autowired
	private Cache<String, DIDDocument> didDocumentCache;
	@Value("${tcr.did.base-uri}")
	private String resolverUri;

	@Override
	public Health health() {
		Health.Builder health;
		try {
			health = resolver.methods().contains("web") ? Health.up() : Health.outOfService();
		} catch (Exception ex) {
			health = Health.down(ex);
		}
		return health.withDetail("resolverUri", resolverUri).build();
	}

	private DIDDocument resolveDidDocument(String did) {
		log.debug("resolveDidDocument.enter; got did to resolve: {}", did);
		DIDDocument diDoc = didDocumentCache.getIfPresent(did);
		boolean cached = true;
		if (diDoc == null) {
			cached = false;
			ResolveRepresentationResult didResult;
			try {
				didResult = resolver.resolveRepresentation(did, RESOLVE_OPTIONS);
				log.trace("resolveDid; resolved to: {}", didResult.toJson());
			} catch (ResolutionException ex) {
				log.warn("resolveDidDocument.error;", ex);
				throw new DidException(ex);
			}
			if (didResult.isErrorResult()) {
				throw new DidException(didResult.getErrorMessage());
			}

			String docStream = didResult.getDidDocumentStreamAsString();
			log.trace("resolveDidDocument; doc stream is: {}", docStream);
			diDoc = DIDDocument.fromJson(docStream);
			didDocumentCache.put(did, diDoc);
		}
		log.debug("resolveDidDocument.exit; returning doc: {}, from cache: {}", diDoc, cached);
		return diDoc;
	}

	@SuppressWarnings("unchecked")
	private Stream<TypedEndpoint> resolveEndpoint(Object ep, String type) {
		if (ep instanceof String) {
			return Stream.of(new TypedEndpoint(ep.toString(), type));
		}
		if (ep instanceof List) {
			Stream<Stream<TypedEndpoint>> sss = ((List<?>) ep).stream().map(e -> resolveEndpoint(e, type));
			return sss.reduce(Stream.of(), (a, b) -> Stream.concat(a, b));
		}
		// else ep is a Map
		Map<String, Object> eps = (Map<String, Object>) ep;
		ep = eps.entrySet().iterator().next().getValue();
		return resolveEndpoint(ep, type);
	}

	public DIDResolveResult resolveDid(String did, List<String> types) {
		log.debug("resolveDid.enter; got did: {}, types; {}", did, types);
		Stream<Service> services;
		DIDDocument diDoc = resolveDidDocument(did);
		if (types == null || types.isEmpty()) {
			services = diDoc.getServices().stream();
		} else {
			services = diDoc.getServices().stream().filter(s -> {
				if (s.getTypes() != null && !s.getTypes().isEmpty()) {
					return types.stream().anyMatch(t -> s.getTypes().contains(t));
				}
				return types.contains(s.getType());
			});
		}
		List<TypedEndpoint> endpoints = services.flatMap(s -> resolveEndpoint(s.getServiceEndpoint(), s.getType()))
				.toList();
		String origin = resolveOrigin(diDoc.getId());
		if (origin == null) {
			origin = diDoc.getControllers().stream().map(uri -> resolveOrigin(uri)).findFirst().orElse(null);
		}
		if (origin == null && !endpoints.isEmpty()) {
			origin = endpoints.stream().map(epp -> resolveOrigin(fromString(epp.getUrl()))).findFirst().orElse(null);
		}
		log.debug("resolveDid.exit; returning endpoints: {}, origin: {}", endpoints, origin);
		return new DIDResolveResult(diDoc.getJsonObject(), endpoints, origin);
	}

	private URI fromString(String url) {
		try {
			return new URI(url);
		} catch (URISyntaxException e) {
			log.info("fromString.error: {}", e.getMessage());
			return null;
		}
	}

	private String resolveOrigin(URI uri) {
		if (uri == null) {
			return null;
		}
		if ("https".equals(uri.getScheme()) || "http".equals(uri.getScheme())) {
			return uri.getScheme() + "://" + uri.getHost();
		}
		if ("did".equals(uri.getScheme())) {
			String part = uri.getSchemeSpecificPart();
			if (part.startsWith("web:")) {
				uri = fromString("https://" + part.substring(4));
				return resolveOrigin(uri);
			}
		}
		return null;
	}

	public DCResolveResult resolveDidConfig(String origin) {
		log.debug("resolveDidConfig.enter; got origin: {}", origin);
		String configUri = origin + "/.well-known/did-configuration";
		String configUrl = configUri + ".json";
		JsonDocument doc;
		try {
			doc = (JsonDocument) docLoader.loadDocument(JsonLDUtils.stringToUri(configUrl), DL_OPTIONS);
			log.debug("resolveDidConfig; got document: {}", doc);
		} catch (JsonLdError ex) {
			log.warn("resolveDidConfig.error at {}", configUrl, ex);
			return new DCResolveResult(ex.getMessage());
		}
		if (doc == null) {
			return new DCResolveResult("cannot load document from " + configUrl);
		}
		if (doc.getJsonContent().isEmpty()) {
			return new DCResolveResult("empty did-configuration at " + configUrl);
		}
		JsonObject json = doc.getJsonContent().get().asJsonObject();
		String context = json.getString("@context");
		if (context == null) {
			return new DCResolveResult("did-configuration missing @context");
		}
		// if (!context.startsWith(configUri)) {
		// throw new DidException("wrong @context: " + context);
		// }
		JsonArray dids = json.getJsonArray("linked_dids");
		if (dids == null) {
			return new DCResolveResult("did-configuration missing linked-dids");
		}
		if (json.keySet().size() > 2) {
			return new DCResolveResult("did-configuration contains unexpected members: " + json.keySet());
		}

		int passed = 0;
		String did = null;
		String alg = null;
		JsonObject dataProof = null;
		for (JsonValue linkedDid : dids) {
			if (linkedDid.getValueType() == ValueType.OBJECT) {
				String local = resolveLinkedData(linkedDid.asJsonObject(), origin);
				if (local != null) {
					if (did == null) {
						did = local;
						dataProof = linkedDid.asJsonObject();
					} else {
						log.debug("resolveDidConfig; additional did resolved: {}; initial: {}", local, did);
					}
					passed++;
				}
			} else if (linkedDid.getValueType() == ValueType.STRING) {
				String local = resolveLinkedJWT(linkedDid.toString(), origin);
				if (local != null) {
					if (alg == null) {
						alg = local;
					} else {
						log.debug("resolveDidConfig; additional alg resolved: {}; initial: {}", local, alg);
					}
					passed++;
				}
			} else {
				log.debug("resolveDidConfig; unexpected linked-did type: {}; {}", linkedDid.getValueType(), linkedDid);
			}
		}
		log.debug("resolveDidConfig; resolved: {} DIDs, out of: {}", passed, dids.size());

		boolean resolved = false;
		if (did != null) {
			if (alg == null) {
				alg = getAlgFromProof(dataProof);
			}
			JsonLDObject payload = JsonLDObject.fromJson(dataProof.toString());
			resolved = verifyVCSignature(payload, did, alg);
		}
		log.debug("resolveDidConfig.exit; returning: {}; alg: {}, did: {}", resolved, alg, did);
		return new DCResolveResult(resolved);
	}

	private String resolveLinkedData(JsonObject linkedData, String origin) {
		if (linkedData.containsKey("id")) {
			log.debug("resolveLinkedData; unexpected id member");
			return null;
		}
		if (!linkedData.containsKey("issuanceDate")) {
			log.debug("resolveLinkedData; absent issuanceDate member");
			return null;
		}
		if (!linkedData.containsKey("expirationDate")) {
			log.debug("resolveLinkedData; absent expirationDate member");
			return null;
		}
		JsonObject credSubj = linkedData.getJsonObject("credentialSubject");
		if (credSubj == null) {
			log.debug("resolveLinkedData; absent credentialSubject member");
			return null;
		}
		String did = credSubj.getString("id");
		if (did == null) {
			log.debug("resolveLinkedData; absent credentialSubject.id member");
			return null;
		}
		if (!did.startsWith("did:")) {
			log.debug("resolveLinkedData; unexpected credentialSubject.id value: {}", did);
			return null;
		}
		String subOrigin = credSubj.getString("origin");
		if (subOrigin == null) {
			log.debug("resolveLinkedData; absent credentialSubject.origin member");
			return null;
		}
		// if (!subOrigin.equals(origin)) {
		// log.debug("resolveLinkedData; unexpected credentialSubject.origin value: {}",
		// subOrigin);
		// return null;
		// }
		return did;
	}

	@SuppressWarnings("unchecked")
	private String resolveLinkedJWT(String linkedJWT, String origin) {
		JWT jwt;
		try {
			jwt = JWTParser.parse(linkedJWT);
		} catch (ParseException ex) {
			log.debug("resolveLinkedJWT; error parsing token: {}", ex.getMessage());
			return null;
		}

		if (!jwt.getHeader().getIncludedParams().contains("alg")) {
			log.debug("resolveLinkedJWT; absent 'alg' member in header");
			return null;
		}
		if (!jwt.getHeader().getIncludedParams().contains("kid")) {
			log.debug("resolveLinkedJWT; absent 'kid' member in header");
			return null;
		}
		if (jwt.getHeader().getIncludedParams().size() > 2) {
			log.debug("resolveLinkedJWT; unexpected header members: {}", jwt.getHeader().getIncludedParams());
			return null;
		}

		try {
			Map<String, Object> vc = jwt.getJWTClaimsSet().getJSONObjectClaim("vc");
			Map<String, Object> credSubj = (Map<String, Object>) vc.get("credentialSubject");
			if (credSubj == null) {
				log.debug("resolveLinkedJWT; absent credentialSubject member");
				return null;
			}
			String did = (String) credSubj.get("id");
			if (did == null) {
				log.debug("resolveLinkedJWT; absent credentialSubject.id member");
				return null;
			}
			if (!did.equals(jwt.getJWTClaimsSet().getIssuer())) {
				log.debug("resolveLinkedJWT; unexpected iss/did values: {}/{}", jwt.getJWTClaimsSet().getIssuer(), did);
				return null;
			}
			if (!did.equals(jwt.getJWTClaimsSet().getSubject())) {
				log.debug("resolveLinkedJWT; unexpected sub/did values: {}/{}", jwt.getJWTClaimsSet().getSubject(),
						did);
				return null;
			}
			String subOrigin = (String) credSubj.get("origin");
			if (subOrigin == null) {
				log.debug("resolveLinkedJWT; absent credentialSubject.origin member");
				return null;
			}
			if (!origin.endsWith(subOrigin)) {
				log.debug("resolveLinkedData; unexpected credentialSubject.origin value: {}", subOrigin);
				return null;
			}
		} catch (ParseException ex) {
			log.debug("resolveLinkedJWT; error parsing VC: {}", ex.getMessage());
			return null;
		}
		return jwt.getHeader().getAlgorithm().getName();
	}

	@SuppressWarnings("unchecked")
	private boolean verifyVCSignature(JsonLDObject payload, String did, String alg) {
		log.debug("verifyVCSignature.enter; did: {}, alg: {}, payload: {}", did, alg, payload);
		DIDDocument diDoc = resolveDidDocument(did);
		List<VerificationMethod> vrMethods = diDoc.getVerificationMethods();
		payload.setDocumentLoader(docLoader);
		LdProof proof = LdProof.fromJsonObject((Map<String, Object>) payload.getJsonObject().get("proof"));
		log.debug("verifyVCSignature; methods: {}; resolved proof: {}", vrMethods, proof);
		boolean verified = vrMethods.stream().anyMatch(vm -> {
			log.debug("verifyVCSignature; veryfying with: {}", vm);
			try {
				JWK jwkPublic = JWK.fromMap(vm.getPublicKeyJwk());
				LdVerifier<?> verifier = LdVerifierRegistry.getLdVerifierBySignatureSuiteTerm(proof.getType());
				PublicKeyVerifier<?> pkVerifier = PublicKeyVerifierFactory.publicKeyVerifierForJWK(jwkPublic, alg);
				verifier.setVerifier(pkVerifier);
				if (verifier.verify(payload, proof)) {
					return true;
				}
				log.debug("verifyVCSignature; payload not verified; suite: {}", proof.getType());
			} catch (Throwable ex) {
				log.warn("verifyVCSignature; error verifying signature", ex);
			}
			return false;
		});
		log.debug("verifyVCSignature.exit; verified: {}", verified);
		return verified;
	}

	private String getAlgFromProof(JsonObject vc) {
		LdProof proof = LdProof.fromJson(vc.getJsonObject("proof").toString());
		if (proof.getType().contains(KeyTypeName.Ed25519.getValue())) {
			return JWSAlgorithm.EdDSA;
		}
		if (proof.getType().startsWith("BbsBls")) {
			return JWSAlgorithm.BBSPlus;
		}
		if (proof.getType().startsWith("RSA")) {
			return JWSAlgorithm.RS256;
		}
		if (proof.getType().startsWith("EcdsaSecp256k")) {
			return JWSAlgorithm.ES256K;
		}
		if (proof.getType().startsWith("EcdsaKoblitz")) {
			return JWSAlgorithm.ES256K;
		}
		if (proof.getType().startsWith("JcsEcdsaSecp256k")) {
			return JWSAlgorithm.ES256K;
		}
		// else we got JsonWebSignature2020 which maps to:
		// Map.of(KeyTypeName.RSA, List.of(JWSAlgorithm.PS256, JWSAlgorithm.RS256),
		// KeyTypeName.Ed25519, List.of(JWSAlgorithm.EdDSA),
		// KeyTypeName.secp256k1, List.of(JWSAlgorithm.ES256K),
		// KeyTypeName.P_256, List.of(JWSAlgorithm.ES256),
		// KeyTypeName.P_384, List.of(JWSAlgorithm.ES384)),
		// so, will need more info on how to choose proper algo..
		if (proof.getJws() != null) {
			JWT jwt;
			try {
				jwt = JWTParser.parse(proof.getJws());
				return jwt.getHeader().getAlgorithm().getName();
			} catch (ParseException ex) {
				log.debug("getAlgFromProof; error parsing JWS: {}", ex.getMessage());
			}
		}
		return JWSAlgorithm.ES256K;
	}

	public VCResolveResult resolveVC(String uri, String did) {
		log.debug("resolveVC.enter; got uri: {}", uri);
		JsonObject jsonVC = loadJsonDocument(uri);
		VCResolveResult result = new VCResolveResult(false, null, null);
		if (jsonVC != null) {
			String json = jsonVC.toString();
			log.trace("resolveVC; got JSON:: {}", json);
			VerifiableCredential vc = VerifiableCredential.fromJson(json);
			Map<String, Object> claims = vc.getCredentialSubject().getClaims();
			result.setTrustListUri((String) claims.get("trustlistURI"));
			result.setHash((String) claims.get("hash"));
			JsonObject vcJson = vc.toJsonObject();
			String alg = getAlgFromProof(vcJson);
			result.setVerified(verifyVCSignature(JsonLDObject.fromJson(json), did, alg));
		}
		log.debug("resolveVC.exit; returning: {}", result);
		return result;
	}

	private JsonObject loadJsonDocument(String uri) {
		log.debug("loadJsonDocument.enter; got uri: {}", uri);
		JsonDocument doc;
		try {
			// DL_OPTIONS.
			doc = (JsonDocument) docLoader.loadDocument(JsonLDUtils.stringToUri(uri), DL_OPTIONS);
		} catch (JsonLdError ex) {
			throw new DidException(ex);
		}
		if (doc == null) {
			throw new DidException("cannot load document from " + uri);
		}
		return doc.getJsonContent().get().asJsonObject();
	}

	@Getter
	@AllArgsConstructor
	@ToString
	static class TypedEndpoint {

		private String url;
		private String type;
	}

	@Getter
	@AllArgsConstructor
	@ToString
	static class DIDResolveResult {

		private Map<String, Object> document;
		private List<TypedEndpoint> endpoints;
		private String origin;

	}

	@Getter
	@ToString
	static class DCResolveResult {

		private boolean verified;
		private String error;

		DCResolveResult(boolean verified) {
			this.verified = verified;
		}

		DCResolveResult(String error) {
			this.error = error;
			this.verified = false;
		}

	}

	@Getter
	@Setter
	@AllArgsConstructor
	@ToString
	static class VCResolveResult {

		private boolean verified;
		private String trustListUri;
		private String hash;

	}

}
