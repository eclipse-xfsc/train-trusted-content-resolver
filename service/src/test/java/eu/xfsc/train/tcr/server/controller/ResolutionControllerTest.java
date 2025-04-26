package eu.xfsc.train.tcr.server.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.xfsc.train.tcr.api.generated.model.ResolveRequest;
import eu.xfsc.train.tcr.api.generated.model.ResolveResponse;
import eu.xfsc.train.tcr.api.generated.model.ResolvedDoc;
import eu.xfsc.train.tcr.api.generated.model.ResolvedTrustList;
import eu.xfsc.train.tcr.api.generated.model.ValidateRequest;
import eu.xfsc.train.tcr.api.generated.model.ValidateResponse;
import eu.xfsc.train.tspa.model.trustlist.tsp.TSPCustomType;
import uniresolver.UniResolver;
import uniresolver.result.ResolveRepresentationResult;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class ResolutionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper jsonMapper;
	@MockBean
	private UniResolver uniResolver;
	
	private static final String testDoc = """
			{"@context":["https://www.w3.org/ns/did/v1","https://w3id.org/security/suites/jws-2020/v1"],"id":"did:web:essif.trust-scheme.de","verificationMethod":[{"id":"did:web:essif.trust-scheme.de#owner","type":"JsonWebKey2020",
			"controller":"did:web:essif.trust-scheme.de","publicKeyJwk":{"kty":"OKP","crv":"Ed25519","x":"U8vPLaL0zjnRSjVwaYQNfPVW5k9j_XMF6dTxTOiAojs"}}],"service":[{"id":"did:web:essif.trust-scheme.de#issuer-list",
			"type":"issuer-list","serviceEndpoint":"https://essif.iao.fraunhofer.de/files/policy/schuelerausweis.xml"}],"authentication":["did:web:essif.trust-scheme.de#owner"],
			"assertionMethod":["did:web:essif.trust-scheme.de#owner"]}""";
	
	private static final String essifDoc = """
			{"@context":["https://www.w3.org/ns/did/v1","https://w3id.org/security/suites/jws-2020/v1"],"id":"did:web:essif.iao.fraunhofer.de","verificationMethod":[{"id":"did:web:essif.iao.fraunhofer.de#owner","type":"JsonWebKey2020",
			"controller":"did:web:essif.iao.fraunhofer.de","publicKeyJwk":{"kty":"OKP","crv":"Ed25519","x":"yaHbNw6nj4Pn3nGPHyyTqP-QHXYNJIpkA37PrIOND4c"}}],"service":[{"id":"did:web:essif.iao.fraunhofer.de#gx-trust-list-issuer",
			"type":"gx-trust-list-issuer","serviceEndpoint":"https://essif.iao.fraunhofer.de/files/trustlist/federation1.test.train.trust-scheme.de.json"},{"id":"did:web:essif.iao.fraunhofer.de#eidas-trust-list","type":"eidas-trust-list",
			"serviceEndpoint":"https://ec.europa.eu/tools/lotl/eu-lotl.xml"}],"authentication":["did:web:essif.iao.fraunhofer.de#owner"],"assertionMethod":["did:web:essif.iao.fraunhofer.de#owner"]}""";
	
    @Test
    public void postResolveRequestShouldReturnErrorVCResponse() throws Exception {

    	String did1 = "did:web:essif.trust-scheme.de";
    	ResolveRepresentationResult rrr = ResolveRepresentationResult.build(null, testDoc.getBytes(), null);
        when(uniResolver.resolveRepresentation(eq(did1), any())).thenReturn(rrr);
    	String did2 = "did:web:essif.iao.fraunhofer.de";
    	rrr = ResolveRepresentationResult.build(null, essifDoc.getBytes(), null);
        when(uniResolver.resolveRepresentation(eq(did2), any())).thenReturn(rrr);
    	
        String ptr = "did-web.test.train.trust-scheme.de";
        ResolveRequest request = new ResolveRequest();
        request.setIssuer("https://test-issuer.sample.org");
        //request.addEndpointTypesItem("gx-trust-list-issuer");
        request.addTrustSchemePointersItem(ptr); 
        
        String response = mockMvc
            .perform(MockMvcRequestBuilders.post("/tcr/v1/resolve")
            .content(jsonMapper.writeValueAsString(request))		
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()) 
            .andReturn()
            .getResponse()
            .getContentAsString();
        ResolveResponse result = jsonMapper.readValue(response, ResolveResponse.class);
        assertNotNull(result);
        assertNotNull(result.getTrustSchemePointers());
        assertEquals(1, result.getTrustSchemePointers().size());
        assertEquals(ptr, result.getTrustSchemePointers().get(0).getPointer());
        assertNotNull(result.getResolvedResults());
        assertEquals(1, result.getResolvedResults().size());
        assertEquals(did1, result.getResolvedResults().get(0).getDid());
        assertNotNull(result.getResolvedResults().get(0).getResolvedDoc());
        assertNotNull(result.getResolvedResults().get(0).getResolvedDoc().getDocument());
        assertNull(result.getResolvedResults().get(0).getResolvedDoc().getEndpoints());
        assertNotNull(result.getResolvedResults().get(0).getError());
        assertEquals("did_error", result.getResolvedResults().get(0).getError().getCode());
    }

    @Test
    public void postResolveRequestShouldReturnNullDidResponse() throws Exception {
      
        String ptr = "did-web.test.train.trust-scheme.bad";
        ResolveRequest request = new ResolveRequest();
        request.setIssuer("https://test-issuer.sample.org");
        request.addTrustSchemePointersItem(ptr);
        
        String response = mockMvc
            .perform(MockMvcRequestBuilders.post("/tcr/v1/resolve")
            .content(jsonMapper.writeValueAsString(request))		
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        ResolveResponse result = jsonMapper.readValue(response, ResolveResponse.class);
        assertNotNull(result);
        assertNotNull(result.getTrustSchemePointers());
        assertEquals(1, result.getTrustSchemePointers().size());
        assertEquals(ptr, result.getTrustSchemePointers().get(0).getPointer());
        assertNull(result.getResolvedResults());
    }
    
    @Test
    public void postResolveRequestShouldReturnEmptyTrustListResponse() throws Exception {
      
    	String did = "did:web:essif.trust-scheme.de";
    	ResolveRepresentationResult rrr = ResolveRepresentationResult.build(null, testDoc.getBytes(), null);
        when(uniResolver.resolveRepresentation(eq(did), any())).thenReturn(rrr);

        String ptr = "did-web.test.train.trust-scheme.de";
        ResolveRequest request = new ResolveRequest();
        request.setIssuer("https://test-issuer.sample.org");
        request.addEndpointTypesItem("wrong-type"); 
        request.addTrustSchemePointersItem(ptr);
        
        String response = mockMvc
            .perform(MockMvcRequestBuilders.post("/tcr/v1/resolve")
            .content(jsonMapper.writeValueAsString(request))		
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        ResolveResponse result = jsonMapper.readValue(response, ResolveResponse.class);
        assertNotNull(result);
        assertNotNull(result.getTrustSchemePointers());
        assertEquals(1, result.getTrustSchemePointers().size());
        assertEquals(ptr, result.getTrustSchemePointers().get(0).getPointer());
        assertNotNull(result.getResolvedResults());
        assertEquals(1, result.getResolvedResults().size());
        assertEquals(did, result.getResolvedResults().get(0).getDid());
        assertNotNull(result.getResolvedResults().get(0).getResolvedDoc());
        assertNotNull(result.getResolvedResults().get(0).getResolvedDoc().getDocument());
        assertNull(result.getResolvedResults().get(0).getResolvedDoc().getEndpoints());
        assertNull(result.getResolvedResults().get(0).getError());
    }

    @Test
    public void postResolveRequestShouldReturnTrustListResponse() throws Exception {
      
    	String did = "did:web:essif.iao.fraunhofer.de";
    	ResolveRepresentationResult rrr = ResolveRepresentationResult.build(null, essifDoc.getBytes(), null); 
        when(uniResolver.resolveRepresentation(eq(did), any())).thenReturn(rrr);
   	
        String ptr = "gxfs.test.train.trust-scheme.de";
        String issuer = "did:web:notary.company1.com"; 
        ResolveRequest request = new ResolveRequest(issuer, List.of(ptr), null);
        
        String response = mockMvc
            .perform(MockMvcRequestBuilders.post("/tcr/v1/resolve")
            .content(jsonMapper.writeValueAsString(request))		
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        ResolveResponse result = jsonMapper.readValue(response, ResolveResponse.class);
        assertNotNull(result);
        assertEquals(1, result.getTrustSchemePointers().size());
        assertEquals(ptr, result.getTrustSchemePointers().get(0).getPointer());
        assertNotNull(result.getResolvedResults());
        assertEquals(1, result.getResolvedResults().size());
        assertEquals(did, result.getResolvedResults().get(0).getDid());
        ResolvedDoc reDoc = result.getResolvedResults().get(0).getResolvedDoc();
        assertNotNull(reDoc);
        assertNotNull(reDoc.getDocument());
        assertFalse(reDoc.getEndpoints().isEmpty());
        ResolvedTrustList reTl = reDoc.getEndpoints().get(0);
        assertNotNull(reTl);
        //assertTrue(reTl.getVcVerified());
        assertEquals("https://essif.iao.fraunhofer.de/files/trustlist/federation1.test.train.trust-scheme.de.json", reTl.getVcUri());
        assertEquals("https://tspa.trust-scheme.de/tspa_train_domain/api/v1/scheme/federation1.test.train.trust-scheme.de", reTl.getTlUri());
        assertNotNull(reTl.getTrustList());
        String map = jsonMapper.writeValueAsString(reTl.getTrustList());
        TSPCustomType tsp = jsonMapper.readValue(map, TSPCustomType.class);
        assertTrue(tsp.getTSPServices().getTspService().stream().anyMatch(tsps -> tsps.getServiceTypeIdentifier().equals(issuer)));
    }

    
    @Test
    public void postValidateRequestShouldReturnValidateResponse() throws Exception {
      
    	String did = "did:web:essif.iao.fraunhofer.de";
    	ResolveRepresentationResult rrr = ResolveRepresentationResult.build(null, essifDoc.getBytes(), null); 
        when(uniResolver.resolveRepresentation(eq(did), any())).thenReturn(rrr);
   	
        String issuer = "did:web:notary.company1.com"; 
        String uri = "https://essif.iao.fraunhofer.de/files/trustlist/federation1.test.train.trust-scheme.de.json";
        ValidateRequest request = new ValidateRequest(issuer, did, List.of(uri));
        
        String response = mockMvc
            .perform(MockMvcRequestBuilders.post("/tcr/v1/validate")
            .content(jsonMapper.writeValueAsString(request))		
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        ValidateResponse result = jsonMapper.readValue(response, ValidateResponse.class);
        assertNotNull(result);
        assertTrue(result.getDidVerified());
        ResolvedTrustList reTl = result.getEndpoints().get(0);
        assertNotNull(reTl);
        //assertTrue(reTl.getVcVerified());
        assertEquals(uri, reTl.getVcUri());
        assertEquals("https://tspa.trust-scheme.de/tspa_train_domain/api/v1/scheme/federation1.test.train.trust-scheme.de", reTl.getTlUri());
        assertNotNull(reTl.getTrustList());
        String map = jsonMapper.writeValueAsString(reTl.getTrustList());
        TSPCustomType tsp = jsonMapper.readValue(map, TSPCustomType.class);
        assertTrue(tsp.getTSPServices().getTspService().stream().anyMatch(tsps -> tsps.getServiceTypeIdentifier().equals(issuer)));
    }
    
}


