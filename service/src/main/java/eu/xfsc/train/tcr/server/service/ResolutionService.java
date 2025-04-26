package eu.xfsc.train.tcr.server.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.xfsc.train.tcr.api.generated.model.Error;
import eu.xfsc.train.tcr.api.generated.model.ResolvedDid;
import eu.xfsc.train.tcr.api.generated.model.ResolvedDoc;
import eu.xfsc.train.tcr.api.generated.model.ResolvedPointer;
import eu.xfsc.train.tcr.api.generated.model.ResolveRequest;
import eu.xfsc.train.tcr.api.generated.model.ResolveResponse;
import eu.xfsc.train.tcr.api.generated.model.ResolvedTrustList;
import eu.xfsc.train.tcr.api.generated.model.ValidateRequest;
import eu.xfsc.train.tcr.api.generated.model.ValidateResponse;
import eu.xfsc.train.tcr.server.generated.controller.TrustedContentResolverApiDelegate;
import eu.xfsc.train.tcr.server.service.DIDResolver.DCResolveResult;
import eu.xfsc.train.tcr.server.service.DIDResolver.DIDResolveResult;
import eu.xfsc.train.tcr.server.service.DIDResolver.VCResolveResult;
import eu.xfsc.train.tcr.server.service.TLResolver.TLResolveResult;
import eu.xfsc.train.tspa.model.trustlist.TrustServiceStatusList;
import eu.xfsc.train.tspa.model.trustlist.tsp.TSPCustomType;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the
 * {@link eu.xfsc.train.tcr.server.generated.controller.TrustedContentResolverApiDelegate}
 * interface.
 */
@Slf4j
@Service
public class ResolutionService implements TrustedContentResolverApiDelegate {

	private static final TypeReference<Map<String, Object>> MAP_TYPE_REF = new TypeReference<Map<String, Object>>() {
	};

	@Autowired
	private DNSResolver reDNS;
	@Autowired
	private DIDResolver reDID;
	@Autowired
	private TLResolver reTL;
	@Autowired
	private ObjectMapper jsonMapper;

	@Override
	public ResponseEntity<ResolveResponse> resolveTrustList(ResolveRequest resolveRequest) {
		log.debug("resolveTrustList.enter; got request: {}", resolveRequest);
		ResolveResponse result = new ResolveResponse();
		Set<String> resolved = new HashSet<>();
		resolveRequest.getTrustSchemePointers().forEach(ptr -> {
			ResolvedPointer rePtr = new ResolvedPointer().pointer(ptr);
			try {
				Collection<String> dids = reDNS.resolveDomain(ptr);
				rePtr.setDids(new ArrayList<>(dids));
				resolved.addAll(dids);
			} catch (Throwable ex) {
				log.warn("resolveTrustList; error processing PTR {}", ptr, ex);
				rePtr.setError(new Error("dns_error", ex.getMessage()));
			}
			result.addTrustSchemePointersItem(rePtr);
		});
		resolved.forEach(did -> {
			ResolvedDid reDid = new ResolvedDid().did(did);
			try {
				DIDResolveResult didRes = reDID.resolveDid(did, resolveRequest.getEndpointTypes());
				boolean didVerified = false;
				if (didRes.getOrigin() != null) {
					// here we perform well-known did-configuration check..
					DCResolveResult dcRes = reDID.resolveDidConfig(didRes.getOrigin());
					didVerified = dcRes.isVerified();
				}
				ResolvedDoc reDoc = new ResolvedDoc().document(didRes.getDocument()).didVerified(didVerified);
				didRes.getEndpoints().forEach(endpoint -> {
					try {
						VCResolveResult vcRes = reDID.resolveVC(endpoint.getUrl(), did);
						ResolvedTrustList rtl = new ResolvedTrustList(endpoint.getUrl(), vcRes.getTrustListUri(), null,
								false);
						TLResolveResult tlRes = reTL.resolveTLHash(vcRes.getTrustListUri(), vcRes.getHash());
						// TL verified by hash
						if (tlRes != null) {
							rtl.setVcVerified(vcRes.isVerified() && tlRes.isHashVerified());
							Optional<TSPCustomType> tsp = findIssuerProvider(tlRes.getTrustList(),
									resolveRequest.getIssuer());
							if (tsp.isPresent()) {
								rtl.setTrustList(convertToMap(tsp.get()));
							}
						}
						reDoc.addEndpointsItem(rtl);
					} catch (Throwable ex) {
						log.warn("resolveTrustList; error processing endpoint {}", endpoint, ex);
						reDid.setError(new Error("did_error", ex.getMessage()));
					}
				});
				reDid.setResolvedDoc(reDoc);
				if (reDoc.getEndpoints() != null && reDoc.getEndpoints().size() > 0) {
					reDid.setError(null);
				}
			} catch (Throwable ex) {
				log.warn("resolveTrustList; error processing DID {}", did, ex);
				reDid.setError(new Error("did_error", ex.getMessage()));
			}
			result.addResolvedResultsItem(reDid);
		});
		log.debug("resolveTrustList.exit; returning: {}", result);
		return ResponseEntity.ok(result);
	}

	@Override
	public ResponseEntity<ValidateResponse> validateTrustList(ValidateRequest validateRequest) {
		log.debug("validateTrustList.enter; got request: {}", validateRequest);
		ValidateResponse result = new ValidateResponse();
		DIDResolveResult didRes = reDID.resolveDid(validateRequest.getDid(), null);
		if (didRes.getOrigin() == null) {
			result.setDidVerified(false);
			log.info("validateTrustList; origin not resolved");
		} else {
			// well-known did-configuration check..
			DCResolveResult dcRes = reDID.resolveDidConfig(didRes.getOrigin());
			result.setDidVerified(dcRes.isVerified());
			if (!dcRes.isVerified()) {
				log.info("validateTrustList; resolved origin not verified: {}", didRes.getOrigin());
			}
		}
		validateRequest.getEndpoints().forEach(endpoint -> {
			try {
				VCResolveResult vcRes = reDID.resolveVC(endpoint, validateRequest.getDid());
				ResolvedTrustList rtl = new ResolvedTrustList(endpoint, vcRes.getTrustListUri(), null, false);
				TLResolveResult tlRes = reTL.resolveTLHash(vcRes.getTrustListUri(), vcRes.getHash());
				if (rtl != null) {
					rtl.setVcVerified(vcRes.isVerified() && tlRes.isHashVerified());
					Optional<TSPCustomType> tsp = findIssuerProvider(tlRes.getTrustList(), validateRequest.getIssuer());
					if (tsp.isPresent()) {
						rtl.setTrustList(convertToMap(tsp.get()));
					}
				}
				result.addEndpointsItem(rtl);
			} catch (Throwable ex) {
				log.warn("validateTrustList; error processing endpoint {}: {}", endpoint, ex.getMessage());
			}
		});
		log.debug("validateTrustList.exit; returning result: {}", result);
		return ResponseEntity.ok(result);
	}

	private Optional<TSPCustomType> findIssuerProvider(TrustServiceStatusList trustList, String issuer) {
		return trustList
				.getTrustServiceProviderList().getTrustServiceProvider().stream().filter(tsp -> tsp.getTSPServices()
						.getTspService().stream().anyMatch(tsps -> tsps.getServiceTypeIdentifier().equals(issuer)))
				.findFirst();
	}

	private Map<String, Object> convertToMap(Object value) {
		try {
			byte[] json = jsonMapper.writeValueAsBytes(value);
			return jsonMapper.readValue(json, MAP_TYPE_REF);
		} catch (IOException ex) {
			log.warn("convertToMap.error; ", ex);
			return null;
		}
	}

}
