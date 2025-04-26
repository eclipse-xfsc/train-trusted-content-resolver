package eu.xfsc.train.tcr.server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.benmanes.caffeine.cache.Cache;

import eu.xfsc.train.tcr.server.exception.DidException;
import eu.xfsc.train.tcr.server.service.DIDResolver.DCResolveResult;
import eu.xfsc.train.tcr.server.service.DIDResolver.DIDResolveResult;
import eu.xfsc.train.tcr.server.service.DIDResolver.TypedEndpoint;
import eu.xfsc.train.tcr.server.service.DIDResolver.VCResolveResult;
import foundation.identity.did.DIDDocument;
import uniresolver.UniResolver;
import uniresolver.result.ResolveRepresentationResult;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class DIDResolverTest {

	@MockBean
	private UniResolver uniResolver;
	@Autowired
	private DIDResolver didResolver;
	@Autowired
	private Cache<String, DIDDocument> didDocumentCache;
	
	private static final String didWebEssif = "did:web:essif.iao.fraunhofer.de";

	private static final String didWebTrust = "did:web:essif.trust-scheme.de";
	
	private static final String docWebSengleEP = """
			{"@context":["https://www.w3.org/ns/did/v1","https://w3id.org/security/suites/jws-2020/v1"],"id":"did:web:essif.trust-scheme.de","verificationMethod":[{"id":"did:web:essif.trust-scheme.de#owner","type":"JsonWebKey2020",
			"controller":"did:web:essif.trust-scheme.de","publicKeyJwk":{"kty":"OKP","crv":"Ed25519","x":"U8vPLaL0zjnRSjVwaYQNfPVW5k9j_XMF6dTxTOiAojs"}}],"service":[{"id":"did:web:essif.trust-scheme.de#issuer-list",
			"type":"issuer-list","serviceEndpoint":"https://essif.iao.fraunhofer.de/files/policy/schuelerausweis.xml"}],"authentication":["did:web:essif.trust-scheme.de#owner"],
			"assertionMethod":["did:web:essif.trust-scheme.de#owner"]}""";
	
	private static final String docWebManyEP = """
			{"@context":["https://www.w3.org/ns/did/v1","https://w3id.org/security/suites/jws-2020/v1"],"id":"did:web:essif.trust-scheme.de","verificationMethod":[{"id":"did:web:essif.trust-scheme.de#owner","type":"JsonWebKey2020",
			"controller":"did:web:essif.trust-scheme.de","publicKeyJwk":{"kty":"OKP","crv":"Ed25519","x":"U8vPLaL0zjnRSjVwaYQNfPVW5k9j_XMF6dTxTOiAojs"}}],"service":[{"id":"did:web:essif.trust-scheme.de#issuer-list",
			"type":"issuer-list","serviceEndpoint":["https://essif.iao.fraunhofer.de/firstEP", "https://essif.iao.fraunhofer.de/secondEP"]}],"authentication":["did:web:essif.trust-scheme.de#owner"],
			"assertionMethod":["did:web:essif.trust-scheme.de#owner"]}""";
	
	private static final String docWebObjectEP = """
			{"@context":["https://www.w3.org/ns/did/v1","https://w3id.org/security/suites/jws-2020/v1"],"id":"did:web:essif.trust-scheme.de","verificationMethod":[{"id":"did:web:essif.trust-scheme.de#owner","type":"JsonWebKey2020",
			"controller":"did:web:essif.trust-scheme.de","publicKeyJwk":{"kty":"OKP","crv":"Ed25519","x":"U8vPLaL0zjnRSjVwaYQNfPVW5k9j_XMF6dTxTOiAojs"}}],"service":[{"id":"did:web:essif.trust-scheme.de#issuer-list",
			"type":"issuer-list","serviceEndpoint":{"services":["https://essif.iao.fraunhofer.de/firstEP", "https://essif.iao.fraunhofer.de/secondEP"]}}],"authentication":["did:web:essif.trust-scheme.de#owner"],
			"assertionMethod":["did:web:essif.trust-scheme.de#owner"]}""";
	
	private static final String docWebKeyVC = """
			{"@context": ["https://www.w3.org/ns/did/v1", {"Ed25519VerificationKey2018": "https://w3id.org/security#Ed25519VerificationKey2018", "publicKeyJwk": {"@id": "https://w3id.org/security#publicKeyJwk", "@type": "@json"}}],
			 "id": "did:key:z6MkoTHsgNNrby8JzCNQ1iRLyW5QQ6R8Xuu6AA8igGrMVPUM", "verificationMethod": [{"id": "did:key:z6MkoTHsgNNrby8JzCNQ1iRLyW5QQ6R8Xuu6AA8igGrMVPUM#z6MkoTHsgNNrby8JzCNQ1iRLyW5QQ6R8Xuu6AA8igGrMVPUM", "type": "Ed25519VerificationKey2018",
		     "controller": "did:key:z6MkoTHsgNNrby8JzCNQ1iRLyW5QQ6R8Xuu6AA8igGrMVPUM", "publicKeyJwk": {"kty": "OKP", "crv": "Ed25519", "x": "hbtAIehGcx_wXTFzIYJzrHOwl8IGV8EzRgx__FUEnso"}}], "authentication": [
		     "did:key:z6MkoTHsgNNrby8JzCNQ1iRLyW5QQ6R8Xuu6AA8igGrMVPUM#z6MkoTHsgNNrby8JzCNQ1iRLyW5QQ6R8Xuu6AA8igGrMVPUM"], "assertionMethod": ["did:key:z6MkoTHsgNNrby8JzCNQ1iRLyW5QQ6R8Xuu6AA8igGrMVPUM#z6MkoTHsgNNrby8JzCNQ1iRLyW5QQ6R8Xuu6AA8igGrMVPUM"]}""";
	
	private static final String docWebEssif = """
			{"@context":["https://www.w3.org/ns/did/v1","https://w3id.org/security/suites/jws-2020/v1"],"id":"did:web:essif.iao.fraunhofer.de","verificationMethod":[{"id":"did:web:essif.iao.fraunhofer.de#owner","type":"JsonWebKey2020",
			"controller":"did:web:essif.iao.fraunhofer.de","publicKeyJwk":{"kty":"OKP","crv":"Ed25519","x":"yaHbNw6nj4Pn3nGPHyyTqP-QHXYNJIpkA37PrIOND4c"}}],"service":[{"id":"did:web:essif.iao.fraunhofer.de#gx-trust-list-issuer",
			"type":"gx-trust-list-issuer","serviceEndpoint":"https://essif.iao.fraunhofer.de/files/trustlist/federation1.test.train.trust-scheme.de.json"},{"id":"did:web:essif.iao.fraunhofer.de#eidas-trust-list","type":"eidas-trust-list",
			"serviceEndpoint":"https://ec.europa.eu/tools/lotl/eu-lotl.xml"}],"authentication":["did:web:essif.iao.fraunhofer.de#owner"],"assertionMethod":["did:web:essif.iao.fraunhofer.de#owner"]}""";

	private static final String docWeb3Svc = """
			{"@context":["https: //www.w3.org/ns/did/v1","https://w3id.org/security/suites/jws-2020/v1"],"id":"did:web:essif.iao.fraunhofer.de","verificationMethod":[{"id":"did:web:essif.iao.fraunhofer.de#owner","type":"JsonWebKey2020",
			"controller":"did:web:essif.iao.fraunhofer.de","publicKeyJwk":{"kty":"OKP","crv":"Ed25519","x":"yaHbNw6nj4Pn3nGPHyyTqP-QHXYNJIpkA37PrIOND4c"}}],"service":[{"id":"did:web:essif.iao.fraunhofer.de#gx-trust-list-issuer","type":"gx-trust-list-issuer",
			"serviceEndpoint":"http://fed1-tfm:16003/tspa-service/tspa/v1/workshop-test.federation1.train/vc/trust-list"},{"id":"did:web:essif.iao.fraunhofer.de#gx-trust-list-issuer-public","type":"gx-trust-list-issuer-public",
			"serviceEndpoint":"http://tspa.train1.xfsc.dev/tspa-service/tspa/v1/alice.trust.train1.xfsc.dev/vc/trust-list"},{"id":"did:web:essif.iao.fraunhofer.de#eidas-trust-list","type":"eidas-trust-list",
			"serviceEndpoint":"https://ec.europa.eu/tools/lotl/eu-lotl.xml"}],"authentication":["did:web:essif.iao.fraunhofer.de#owner"],"assertionMethod":["did:web:essif.iao.fraunhofer.de#owner"]}""";
	
	private static final String docWeb2VM = """
			{"@context": ["https://www.w3.org/ns/did/v1", "https://w3id.org/security/suites/jws-2020/v1"], "id": "did:web:essif.iao.fraunhofer.de", "verificationMethod": [{"id": "did:web:essif.iao.fraunhofer.de#owner",	"type": "JsonWebKey2020",
			"controller": "did:web:essif.iao.fraunhofer.de", "publicKeyJwk": {"kty": "OKP",	"crv": "Ed25519", "x": "yaHbNw6nj4Pn3nGPHyyTqP-QHXYNJIpkA37PrIOND4c"}}, {"id": "did:web:essif.iao.fraunhofer.de#test", "type": "JsonWebKey2020",
			"controller": "did:web:essif.iao.fraunhofer.de", "publicKeyJwk": {"crv": "P-256", "kid": "test", "kty": "EC", "x": "IglrRKSINwyxro6sT4WKy-mowDW2io3b3jL9LML8a-A", "y": "IQ8l61-wV0mH4ND_O-hEcr-8SY1u8EivybLeMH3a_bM"}}],
	        "service": [{"id": "did:web:essif.iao.fraunhofer.de#gx-trust-list-issuer-public-xml", "type": "gx-trust-list-issuer-public-xml", "serviceEndpoint": "https://tspa.train1.xfsc.dev/tspa-service/tspa/v1/alice.trust.train1.xfsc.dev/vc/trust-list"},
		    {"id": "did:web:essif.iao.fraunhofer.de#gx-trust-list-issuer-public-json", "type": "gx-trust-list-issuer-public-json", "serviceEndpoint": "https://tspa.train1.xfsc.dev/tspa-service/tspa/v1/bob.trust.train1.xfsc.dev/vc/trust-list"}],
	        "authentication": ["did:web:essif.iao.fraunhofer.de#owner"], "assertionMethod": ["did:web:essif.iao.fraunhofer.de#owner"]}""";
	
    @AfterEach
    public void teardown() {
        didDocumentCache.invalidateAll();
    }
    
    private List<String> toEndpoints(List<TypedEndpoint> points) {
    	return points.stream().map(p -> p.getUrl()).toList();
    }
	
    @Test
    public void testDIDResolutionSingleEP() throws Exception {
    	ResolveRepresentationResult rrr = ResolveRepresentationResult.build(null, docWebSengleEP.getBytes(), null); 
        when(uniResolver.resolveRepresentation(eq(didWebTrust), any())).thenReturn(rrr);
    	
        DIDResolveResult didRes = didResolver.resolveDid(didWebTrust, null);
        assertNotNull(didRes);
        assertNotNull(didRes.getDocument());
        assertTrue(toEndpoints(didRes.getEndpoints()).contains("https://essif.iao.fraunhofer.de/files/policy/schuelerausweis.xml"));
        assertEquals("https://essif.trust-scheme.de", didRes.getOrigin());
    }

    @Test
    public void testDIDResolutionManyEP() throws Exception {
    	ResolveRepresentationResult rrr = ResolveRepresentationResult.build(null, docWebManyEP.getBytes(), null); 
        when(uniResolver.resolveRepresentation(eq(didWebTrust), any())).thenReturn(rrr);
    	
        DIDResolveResult didRes = didResolver.resolveDid(didWebTrust, null);
        assertNotNull(didRes);
        assertNotNull(didRes.getDocument());
        assertEquals(2, didRes.getEndpoints().size());
        List<String> endpoints = toEndpoints(didRes.getEndpoints());
        assertTrue(endpoints.contains("https://essif.iao.fraunhofer.de/firstEP"));
        assertTrue(endpoints.contains("https://essif.iao.fraunhofer.de/secondEP"));
        assertEquals("https://essif.trust-scheme.de", didRes.getOrigin());
    }
    
    @Test
    public void testDIDResolutionObjectEP() throws Exception {
    	ResolveRepresentationResult rrr = ResolveRepresentationResult.build(null, docWebObjectEP.getBytes(), null); 
        when(uniResolver.resolveRepresentation(eq(didWebTrust), any())).thenReturn(rrr);
    	
        DIDResolveResult didRes = didResolver.resolveDid(didWebTrust, null);
        assertNotNull(didRes);
        assertNotNull(didRes.getDocument());
        assertEquals(2, didRes.getEndpoints().size());
        List<String> endpoints = toEndpoints(didRes.getEndpoints());
        assertTrue(endpoints.contains("https://essif.iao.fraunhofer.de/firstEP"));
        assertTrue(endpoints.contains("https://essif.iao.fraunhofer.de/secondEP"));
        assertEquals("https://essif.trust-scheme.de", didRes.getOrigin());
    }
    
    @Test
    public void testDIDResolution3Svc() throws Exception {
    	ResolveRepresentationResult rrr = ResolveRepresentationResult.build(null, docWeb3Svc.getBytes(), null); 
        when(uniResolver.resolveRepresentation(eq(didWebEssif), any())).thenReturn(rrr);
    	
        DIDResolveResult didRes = didResolver.resolveDid(didWebEssif, null);
        assertNotNull(didRes);
        assertNotNull(didRes.getDocument());
        assertEquals(3, didRes.getEndpoints().size());
        List<String> endpoints = toEndpoints(didRes.getEndpoints());
        assertTrue(endpoints.contains("http://fed1-tfm:16003/tspa-service/tspa/v1/workshop-test.federation1.train/vc/trust-list"));
        assertTrue(endpoints.contains("http://tspa.train1.xfsc.dev/tspa-service/tspa/v1/alice.trust.train1.xfsc.dev/vc/trust-list"));
        assertTrue(endpoints.contains("https://ec.europa.eu/tools/lotl/eu-lotl.xml"));
        assertEquals("https://essif.iao.fraunhofer.de", didRes.getOrigin());
    }

    @Test
    public void testDIDConfigResolution() throws Exception {
    	String did = "did:key:z6MkoTHsgNNrby8JzCNQ1iRLyW5QQ6R8Xuu6AA8igGrMVPUM";
    	ResolveRepresentationResult rrr = ResolveRepresentationResult.build(null, docWebKeyVC.getBytes(), null); 
        when(uniResolver.resolveRepresentation(eq(did), any())).thenReturn(rrr);
    	
    	String origin = "https://identity.foundation";
    	DCResolveResult dcRes = didResolver.resolveDidConfig(origin);
    	assertTrue(dcRes.isVerified());
    	assertNull(dcRes.getError());
    }
    
    @Test
    public void testDIDAndConfigResolution() throws Exception {
    	ResolveRepresentationResult rrr = ResolveRepresentationResult.build(null, docWebEssif.getBytes(), null); 
        when(uniResolver.resolveRepresentation(eq(didWebEssif), any())).thenReturn(rrr);
    	
        DIDResolveResult didRes = didResolver.resolveDid(didWebEssif, null);
        assertNotNull(didRes);
        assertNotNull(didRes.getDocument());
        assertEquals("https://essif.iao.fraunhofer.de", didRes.getOrigin());
        List<String> endpoints = toEndpoints(didRes.getEndpoints());
        assertEquals(List.of("https://essif.iao.fraunhofer.de/files/trustlist/federation1.test.train.trust-scheme.de.json", "https://ec.europa.eu/tools/lotl/eu-lotl.xml"), endpoints);
    	
        // now test config..
        DCResolveResult dcRes = didResolver.resolveDidConfig("https://essif.iao.fraunhofer.de");
    	assertTrue(dcRes.isVerified()); 
    	assertNull(dcRes.getError());
    }
    
    @Test
    public void testVCResolutionSuccess() throws Exception {
    	ResolveRepresentationResult rrr = ResolveRepresentationResult.build(null, docWebEssif.getBytes(), null); 
        when(uniResolver.resolveRepresentation(eq(didWebEssif), any())).thenReturn(rrr);

    	VCResolveResult vcRes = didResolver.resolveVC("https://essif.iao.fraunhofer.de/files/trustlist/federation1.test.train.trust-scheme.de.json", didWebEssif);
        assertEquals("https://tspa.trust-scheme.de/tspa_train_domain/api/v1/scheme/federation1.test.train.trust-scheme.de", vcRes.getTrustListUri());
        assertTrue(vcRes.isVerified());
    }
    
    @Test
    public void testVCResolutionFailure() throws Exception {
    	ResolveRepresentationResult rrr = ResolveRepresentationResult.build(null, docWebEssif.getBytes(), null); 
        when(uniResolver.resolveRepresentation(eq(didWebEssif), any())).thenReturn(rrr);

        Exception ex = assertThrowsExactly(DidException.class, () -> didResolver.resolveVC("https://ec.europa.eu/tools/lotl/eu-lotl.xml", didWebEssif));
        assertTrue(ex.getMessage().contains("LOADING_DOCUMENT_FAILED"));
    }
    
    @Test
    public void testVCResolutionTwoMethods() throws Exception {
    	ResolveRepresentationResult rrr = ResolveRepresentationResult.build(null, docWeb2VM.getBytes(), null); 
        when(uniResolver.resolveRepresentation(eq(didWebEssif), any())).thenReturn(rrr);

    	VCResolveResult vcRes = didResolver.resolveVC("https://tspa.train1.xfsc.dev/tspa-service/tspa/v1/alice.trust.train1.xfsc.dev/vc/trust-list", didWebEssif);
        assertEquals("https://tspa.train1.xfsc.dev/tspa-service/tspa/v1/alice.trust.train1.xfsc.dev/trust-list", vcRes.getTrustListUri());
        assertTrue(vcRes.isVerified());
    }
    
}
