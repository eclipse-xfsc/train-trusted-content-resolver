package eu.xfsc.train.tcr.server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.io.StringReader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.github.benmanes.caffeine.cache.Cache;

import eu.xfsc.train.tcr.server.service.DIDResolver.VCResolveResult;
import uniresolver.UniResolver;
import uniresolver.result.ResolveRepresentationResult;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class VCResolveTest {

	@MockBean
	private UniResolver uniResolver;
	@Autowired
	private DIDResolver didResolver;
	//@Autowired
	//private Cache<String, DIDDocument> didDocumentCache;
	@Autowired
	private Cache<URI, Document> docLoaderCache;
	
	private static final String didWebEssif = "did:web:essif.iao.fraunhofer.de";
	
	private static final String didJwkRsa = "did:jwk:eyJlIjoiQVFBQiIsImtpZCI6InNpZ25SU0EyMDQ4Iiwia3R5IjoiUlNBIiwibiI6IjhDUl8ySUJuSGlDcjVIY0kxcWh1dXpiRmdMRjFlSVI3dGM1NTF2WlN2ek1rTEtIVEpxeHVZR1BlVXp2U29ndWctTDJRQ2tpVWdmSjFZUzRFUmhrRFM2cVBJbFdMM1BGbkc4VjZQVWxoODZjTjgxWkhZdkhoUFYwUDdKT25UeGVKcTV1a1dFT0ctMUxrTmk5eGVnelRoMEVwV0w4NU5aUmJOYWJ0R0VqTUhqY04yak9NUldSYkE4OVdENVhOTExwdWUyNVlxbHcxOGZsbXNNRmh5dXk2YlVmanR2aXJ5SThwb0RLOVpXcGtPUjN6ejlNN0hpQXVyZ2NLSks5ODlEX0UzZ3d3S1FhQ21CbEJ4X3hESlJoVFB1bnJZUFBySHlpMExmaG5CeWgzcEJRMWhyelFLblZyX18zYkVwcE5abmRFLVhlWTY0VzNNaGUwa1RrREp2VXJndyJ9";

	private static final String didSampleW3C = "https://example.com/issuer/123";
	
	private static final String docWeb2VM = """
			{"@context": ["https://www.w3.org/ns/did/v1", "https://w3id.org/security/suites/jws-2020/v1"], "id": "did:web:essif.iao.fraunhofer.de", "verificationMethod": [{"id": "did:web:essif.iao.fraunhofer.de#owner",	"type": "JsonWebKey2020",
			"controller": "did:web:essif.iao.fraunhofer.de", "publicKeyJwk": {"kty": "OKP",	"crv": "Ed25519", "x": "yaHbNw6nj4Pn3nGPHyyTqP-QHXYNJIpkA37PrIOND4c"}}, {"id": "did:web:essif.iao.fraunhofer.de#test", "type": "JsonWebKey2020",
			"controller": "did:web:essif.iao.fraunhofer.de", "publicKeyJwk": {"crv": "P-256", "kid": "test", "kty": "EC", "x": "IglrRKSINwyxro6sT4WKy-mowDW2io3b3jL9LML8a-A", "y": "IQ8l61-wV0mH4ND_O-hEcr-8SY1u8EivybLeMH3a_bM"}}],
	        "service": [{"id": "did:web:essif.iao.fraunhofer.de#gx-trust-list-issuer-public-xml", "type": "gx-trust-list-issuer-public-xml", "serviceEndpoint": "https://tspa.train1.xfsc.dev/tspa-service/tspa/v1/alice.trust.train1.xfsc.dev/vc/trust-list"},
		    {"id": "did:web:essif.iao.fraunhofer.de#gx-trust-list-issuer-public-json", "type": "gx-trust-list-issuer-public-json", "serviceEndpoint": "https://tspa.train1.xfsc.dev/tspa-service/tspa/v1/bob.trust.train1.xfsc.dev/vc/trust-list"}],
	        "authentication": ["did:web:essif.iao.fraunhofer.de#owner"], "assertionMethod": ["did:web:essif.iao.fraunhofer.de#owner"]}""";
	
	private static final String docJwkRsa = """
		{
		  "@context": ["https://www.w3.org/ns/did/v1", "https://w3id.org/security/suites/jws-2020/v1"],
		  "id": "did:jwk:eyJlIjoiQVFBQiIsImtpZCI6InNpZ25SU0EyMDQ4Iiwia3R5IjoiUlNBIiwibiI6IjhDUl8ySUJuSGlDcjVIY0kxcWh1dXpiRmdMRjFlSVI3dGM1NTF2WlN2ek1rTEtIVEpxeHVZR1BlVXp2U29ndWctTDJRQ2tpVWdmSjFZUzRFUmhrRFM2cVBJbFdMM1BGbkc4VjZQVWxoODZjTjgxWkhZdkhoUFYwUDdKT25UeGVKcTV1a1dFT0ctMUxrTmk5eGVnelRoMEVwV0w4NU5aUmJOYWJ0R0VqTUhqY04yak9NUldSYkE4OVdENVhOTExwdWUyNVlxbHcxOGZsbXNNRmh5dXk2YlVmanR2aXJ5SThwb0RLOVpXcGtPUjN6ejlNN0hpQXVyZ2NLSks5ODlEX0UzZ3d3S1FhQ21CbEJ4X3hESlJoVFB1bnJZUFBySHlpMExmaG5CeWgzcEJRMWhyelFLblZyX18zYkVwcE5abmRFLVhlWTY0VzNNaGUwa1RrREp2VXJndyJ9",
		  "verificationMethod": [
		    {
		      "id": "did:jwk:eyJlIjoiQVFBQiIsImtpZCI6InNpZ25SU0EyMDQ4Iiwia3R5IjoiUlNBIiwibiI6IjhDUl8ySUJuSGlDcjVIY0kxcWh1dXpiRmdMRjFlSVI3dGM1NTF2WlN2ek1rTEtIVEpxeHVZR1BlVXp2U29ndWctTDJRQ2tpVWdmSjFZUzRFUmhrRFM2cVBJbFdMM1BGbkc4VjZQVWxoODZjTjgxWkhZdkhoUFYwUDdKT25UeGVKcTV1a1dFT0ctMUxrTmk5eGVnelRoMEVwV0w4NU5aUmJOYWJ0R0VqTUhqY04yak9NUldSYkE4OVdENVhOTExwdWUyNVlxbHcxOGZsbXNNRmh5dXk2YlVmanR2aXJ5SThwb0RLOVpXcGtPUjN6ejlNN0hpQXVyZ2NLSks5ODlEX0UzZ3d3S1FhQ21CbEJ4X3hESlJoVFB1bnJZUFBySHlpMExmaG5CeWgzcEJRMWhyelFLblZyX18zYkVwcE5abmRFLVhlWTY0VzNNaGUwa1RrREp2VXJndyJ9#0",
		      "type": "JsonWebKey2020",
		      "controller": "did:jwk:eyJlIjoiQVFBQiIsImtpZCI6InNpZ25SU0EyMDQ4Iiwia3R5IjoiUlNBIiwibiI6IjhDUl8ySUJuSGlDcjVIY0kxcWh1dXpiRmdMRjFlSVI3dGM1NTF2WlN2ek1rTEtIVEpxeHVZR1BlVXp2U29ndWctTDJRQ2tpVWdmSjFZUzRFUmhrRFM2cVBJbFdMM1BGbkc4VjZQVWxoODZjTjgxWkhZdkhoUFYwUDdKT25UeGVKcTV1a1dFT0ctMUxrTmk5eGVnelRoMEVwV0w4NU5aUmJOYWJ0R0VqTUhqY04yak9NUldSYkE4OVdENVhOTExwdWUyNVlxbHcxOGZsbXNNRmh5dXk2YlVmanR2aXJ5SThwb0RLOVpXcGtPUjN6ejlNN0hpQXVyZ2NLSks5ODlEX0UzZ3d3S1FhQ21CbEJ4X3hESlJoVFB1bnJZUFBySHlpMExmaG5CeWgzcEJRMWhyelFLblZyX18zYkVwcE5abmRFLVhlWTY0VzNNaGUwa1RrREp2VXJndyJ9",
		      "publicKeyJwk": {
		        "e": "AQAB",
		        "kid": "signRSA2048",
		        "kty": "RSA",
		        "n": "8CR_2IBnHiCr5HcI1qhuuzbFgLF1eIR7tc551vZSvzMkLKHTJqxuYGPeUzvSogug-L2QCkiUgfJ1YS4ERhkDS6qPIlWL3PFnG8V6PUlh86cN81ZHYvHhPV0P7JOnTxeJq5ukWEOG-1LkNi9xegzTh0EpWL85NZRbNabtGEjMHjcN2jOMRWRbA89WD5XNLLpue25Yqlw18flmsMFhyuy6bUfjtviryI8poDK9ZWpkOR3zz9M7HiAurgcKJK989D_E3gwwKQaCmBlBx_xDJRhTPunrYPPrHyi0LfhnByh3pBQ1hrzQKnVr__3bEppNZndE-XeY64W3Mhe0kTkDJvUrgw"
		      }
		    }
		  ]
		}""";

	private static final String docJwkRsaVC = """
{"@context":["https://www.w3.org/2018/credentials/v1","https://w3id.org/security/suites/jws-2020/v1","https://schema.org"],"credentialSubject":{"hash":"QmbXgQJ67fawbWTHNQWjxT3KriaTopVXXc1fu9KTJkWPpS","id":"uuid:2632367287r82729","trustlistURI":"https://tspa.train1.xfsc.dev/tspa-service/tspa/v1/bob.trust.train1.xfsc.dev/trust-list","trustlisttype":"XML based Trust-lists"},"issuanceDate":"2024-02-28T08:12:31.974892617Z","issuer":"did:jwk:eyJlIjoiQVFBQiIsImtpZCI6InNpZ25SU0EyMDQ4Iiwia3R5IjoiUlNBIiwibiI6IjhDUl8ySUJuSGlDcjVIY0kxcWh1dXpiRmdMRjFlSVI3dGM1NTF2WlN2ek1rTEtIVEpxeHVZR1BlVXp2U29ndWctTDJRQ2tpVWdmSjFZUzRFUmhrRFM2cVBJbFdMM1BGbkc4VjZQVWxoODZjTjgxWkhZdkhoUFYwUDdKT25UeGVKcTV1a1dFT0ctMUxrTmk5eGVnelRoMEVwV0w4NU5aUmJOYWJ0R0VqTUhqY04yak9NUldSYkE4OVdENVhOTExwdWUyNVlxbHcxOGZsbXNNRmh5dXk2YlVmanR2aXJ5SThwb0RLOVpXcGtPUjN6ejlNN0hpQXVyZ2NLSks5ODlEX0UzZ3d3S1FhQ21CbEJ4X3hESlJoVFB1bnJZUFBySHlpMExmaG5CeWgzcEJRMWhyelFLblZyX18zYkVwcE5abmRFLVhlWTY0VzNNaGUwa1RrREp2VXJndyJ9","proof":{"created":"2024-02-28T08:12:31.994144281Z","jws":"eyJhbGciOiJQUzI1NiIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..pnSTLU5xozK-d2TU4XXt7ZcPLkUeNPazMCUcUjjQa2zP7W92FXqjKTdBNfdQoFqGMjpfMNLM-y0icNb7cBUuZWDsDK8mftG8kU6dMz56fNGFmgxBZemSBpVuzY6lGK7_eTTlnWRFA-cUd-D0yTCQctKDheZ6MLmq2gkZMOCshEFXscEjohlrWR2QaMb9asDrx22wXleQdrx_8fISGyYPXHDsXhqzXEvTRqx4LLLcfd4nYsqlXbUsfZhN3dAhmy-QiNsq_74NH_F_81XsfWtbM_Zdth5J2fTUVNL_UxPxv2ZDDks0rFENmqIOjOuppWHJI8XkeOA3bqexmsv60NgqPw","proofPurpose":"assertionMethod","type":"JsonWebSignature2020","verificationMethod":"did:jwk:eyJlIjoiQVFBQiIsImtpZCI6InNpZ25SU0EyMDQ4Iiwia3R5IjoiUlNBIiwibiI6IjhDUl8ySUJuSGlDcjVIY0kxcWh1dXpiRmdMRjFlSVI3dGM1NTF2WlN2ek1rTEtIVEpxeHVZR1BlVXp2U29ndWctTDJRQ2tpVWdmSjFZUzRFUmhrRFM2cVBJbFdMM1BGbkc4VjZQVWxoODZjTjgxWkhZdkhoUFYwUDdKT25UeGVKcTV1a1dFT0ctMUxrTmk5eGVnelRoMEVwV0w4NU5aUmJOYWJ0R0VqTUhqY04yak9NUldSYkE4OVdENVhOTExwdWUyNVlxbHcxOGZsbXNNRmh5dXk2YlVmanR2aXJ5SThwb0RLOVpXcGtPUjN6ejlNN0hpQXVyZ2NLSks5ODlEX0UzZ3d3S1FhQ21CbEJ4X3hESlJoVFB1bnJZUFBySHlpMExmaG5CeWgzcEJRMWhyelFLblZyX18zYkVwcE5abmRFLVhlWTY0VzNNaGUwa1RrREp2VXJndyJ9#0"},"type":"VerifiableCredential"}""";	
	
	private static final String docWebSignerVC = """
			{"@context":["https://www.w3.org/2018/credentials/v1","https://w3id.org/security/suites/jws-2020/v1","https://schema.org"],"credentialSubject":{"hash":"QmbXgQJ67fawbWTHNQWjxT3KriaTopVXXc1fu9KTJkWPpS","id":"uuid:2632367287r82729",
			"trustlistURI":"https://tspa.train1.xfsc.dev/tspa-service/tspa/v1/bob.trust.train1.xfsc.dev/trust-list","trustlisttype":"XML based Trust-lists"},"issuanceDate":"2024-02-27T12:52:33.168245633Z","issuer":"did:web:essif.iao.fraunhofer.de",
			"proof":{"created":"2024-02-27T12:52:33.174685601Z","jws":"eyJhbGciOiJFUzI1NiIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..MEUCIQC4GWafqAHqksRAeLumXFqEUUJfQgVhaT_eTfDNC-9CXAIgTFu4g_JP1XIOHirhe1fkCvaThCa6rlk00phqwDAJLQc","proofPurpose":"assertionMethod",
			"type":"JsonWebSignature2020","verificationMethod":"did:web:essif.iao.fraunhofer.de#test"},"type":"VerifiableCredential"}""";

	private static final String docJwkRsaVCFC3Schemas = """
		{
		  "@context" : [ "https://www.w3.org/2018/credentials/v1", "https://w3id.org/security/suites/jws-2020/v1", "https://schema.org" ],
		  "credentialSubject" : {
		    "hash" : "QmbXgQJ67fawbWTHNQWjxT3KriaTopVXXc1fu9KTJkWPpS",
		    "id" : "uuid:2632367287r82729",
		    "trustlistURI" : "https://tspa.train1.xfsc.dev/tspa-service/tspa/v1/bob.trust.train1.xfsc.dev/trust-list",
		    "trustlisttype" : "XML based Trust-lists"
		  },
		  "issuanceDate" : "2024-02-19T14:33:03.644663187Z",
		  "issuer" : "did:jwk:eyJlIjoiQVFBQiIsImtpZCI6InNpZ25SU0EyMDQ4Iiwia3R5IjoiUlNBIiwibiI6IjhDUl8ySUJuSGlDcjVIY0kxcWh1dXpiRmdMRjFlSVI3dGM1NTF2WlN2ek1rTEtIVEpxeHVZR1BlVXp2U29ndWctTDJRQ2tpVWdmSjFZUzRFUmhrRFM2cVBJbFdMM1BGbkc4VjZQVWxoODZjTjgxWkhZdkhoUFYwUDdKT25UeGVKcTV1a1dFT0ctMUxrTmk5eGVnelRoMEVwV0w4NU5aUmJOYWJ0R0VqTUhqY04yak9NUldSYkE4OVdENVhOTExwdWUyNVlxbHcxOGZsbXNNRmh5dXk2YlVmanR2aXJ5SThwb0RLOVpXcGtPUjN6ejlNN0hpQXVyZ2NLSks5ODlEX0UzZ3d3S1FhQ21CbEJ4X3hESlJoVFB1bnJZUFBySHlpMExmaG5CeWgzcEJRMWhyelFLblZyX18zYkVwcE5abmRFLVhlWTY0VzNNaGUwa1RrREp2VXJndyJ9",
		  "type" : "VerifiableCredential",
		  "proof" : {
		    "type" : "JsonWebSignature2020",
		    "created" : "2024-02-28T06:36:17Z",
		    "proofPurpose" : "assertionMethod",
		    "verificationMethod" : "did:jwk:eyJlIjoiQVFBQiIsImtpZCI6InNpZ25SU0EyMDQ4Iiwia3R5IjoiUlNBIiwibiI6IjhDUl8ySUJuSGlDcjVIY0kxcWh1dXpiRmdMRjFlSVI3dGM1NTF2WlN2ek1rTEtIVEpxeHVZR1BlVXp2U29ndWctTDJRQ2tpVWdmSjFZUzRFUmhrRFM2cVBJbFdMM1BGbkc4VjZQVWxoODZjTjgxWkhZdkhoUFYwUDdKT25UeGVKcTV1a1dFT0ctMUxrTmk5eGVnelRoMEVwV0w4NU5aUmJOYWJ0R0VqTUhqY04yak9NUldSYkE4OVdENVhOTExwdWUyNVlxbHcxOGZsbXNNRmh5dXk2YlVmanR2aXJ5SThwb0RLOVpXcGtPUjN6ejlNN0hpQXVyZ2NLSks5ODlEX0UzZ3d3S1FhQ21CbEJ4X3hESlJoVFB1bnJZUFBySHlpMExmaG5CeWgzcEJRMWhyelFLblZyX18zYkVwcE5abmRFLVhlWTY0VzNNaGUwa1RrREp2VXJndyJ9#0",
		    "jws" : "eyJiNjQiOmZhbHNlLCJjcml0IjpbImI2NCJdLCJhbGciOiJQUzI1NiJ9..gYDTkfaXwGYVX5PZwutQ3ODPJr8TBACDVcavMJ9fBw5leCeJ-AHp-4oVE9D_4M9HQAKqdUD29kqBVOLe0E9Fxu9NmYMz82e_u0Dg6btCWk0SIkqtglCz_GmOnJCT3XL9easQzunxiLrR4DADjuZ6Inlt86srYHZ8L4S-T1duve24MxDiYR9cZbDPTgxh9OksYPX1G8rw6wNupjLdVP7CTvNtgIviJGp_VXe0XlBOCgLTNToMObOQ3VjgNuYW9CQUj2ToNdyV4aTvWyNelf3klf3PRaPThPgktB236U-qlbH_di42yMyLj4gS-pn4ZeG_G5TWsismRBEME0A-N7-pSA"
		  }
		}""";
	
	private static final String docW3CSampleDid = """
		{
		    "@context": [
			  "https://www.w3.org/ns/did/v1",
			  "https://w3id.org/security/suites/jws-2020/v1",
			  {
			    "@base": "https://example.com/issuer/123"
			  }
			],
			"id": "https://example.com/issuer/123",
			"verificationMethod": [
			  {
			    "id": "#ovsDKYBjFemIy8DVhc-w2LSi8CvXMw2AYDzHj04yxkc",
			    "type": "JsonWebKey2020",
			    "controller": "https://example.com/issuer/123",
			    "publicKeyJwk": {
			      "kty": "OKP",
			      "crv": "Ed25519",
			      "x": "CV-aGlld3nVdgnhoZK0D36Wk-9aIMlZjZOK2XhPMnkQ"
			    }
			  }
			],
			"assertionMethod": ["#ovsDKYBjFemIy8DVhc-w2LSi8CvXMw2AYDzHj04yxkc"]
		}""";
	
	private static final String docW3CSampleVC = """
		{
	    "@context": [
	      "https://www.w3.org/2018/credentials/v1",
	      "https://www.w3.org/2018/credentials/examples/v1",
	      "https://w3id.org/security/suites/jws-2020/v1"
	    ],
	    "id": "http://example.gov/credentials/3732",
	    "type": ["VerifiableCredential", "UniversityDegreeCredential"],
	    "issuer": {
	      "id": "https://example.com/issuer/123"
	    },
	    "issuanceDate": "2020-03-10T04:24:12.164Z",
	    "credentialSubject": {
	      "id": "did:example:456",
	      "degree": {
	        "type": "BachelorDegree",
	        "name": "Bachelor of Science and Arts"
	      }
	    },
	    "proof": {
	      "type": "JsonWebSignature2020",
	      "created": "2019-12-11T03:50:55Z",
	      "jws": "eyJhbGciOiJFZERTQSIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..MJ5GwWRMsadCyLNXU_flgJtsS32584MydBxBuygps_cM0sbU3abTEOMyUvmLNcKOwOBE1MfDoB1_YY425W3sAg",
	      "proofPurpose": "assertionMethod",
	      "verificationMethod": "https://example.com/issuer/123#ovsDKYBjFemIy8DVhc-w2LSi8CvXMw2AYDzHj04yxkc"
	    }
	  }"""; 
	
		// Tests disabled as services that are used within those tests are not available anymore.
/*	@Test
    public void testVCResolutionSigner() throws Exception {
    	ResolveRepresentationResult rrr = ResolveRepresentationResult.build(null, docJwkRsa.getBytes(), null); 
        when(uniResolver.resolveRepresentation(eq(didJwkRsa), any())).thenReturn(rrr);
        URI uri = new URI("https://tspa.train1.xfsc.dev/tspa-service/tspa/v1/bob.trust.train1.xfsc.dev/vc");
        Document doc = JsonDocument.of(new StringReader(docJwkRsaVC));
        docLoaderCache.put(uri, doc);
        assertNotNull(docLoaderCache.asMap().get(uri));

    	VCResolveResult vcRes = didResolver.resolveVC(uri.toString(), didJwkRsa);
        assertEquals("https://tspa.train1.xfsc.dev/tspa-service/tspa/v1/bob.trust.train1.xfsc.dev/trust-list", vcRes.getTrustListUri());
        assertFalse(vcRes.isVerified()); // because of the wrong signature from Signer
    }
	
	
	@Test
    public void testVCResolutionFCRsa() throws Exception {
    	ResolveRepresentationResult rrr = ResolveRepresentationResult.build(null, docJwkRsa.getBytes(), null); 
        when(uniResolver.resolveRepresentation(eq(didJwkRsa), any())).thenReturn(rrr);
        URI uri = new URI("https://tspa.train1.xfsc.dev/tspa-service/tspa/v1/alice.trust.train1.xfsc.dev/vc");
        Document doc = JsonDocument.of(new StringReader(docJwkRsaVCFC3Schemas));
        docLoaderCache.put(uri, doc);
        assertNotNull(docLoaderCache.asMap().get(uri));

    	VCResolveResult vcRes = didResolver.resolveVC(uri.toString(), didJwkRsa);
        assertEquals("https://tspa.train1.xfsc.dev/tspa-service/tspa/v1/bob.trust.train1.xfsc.dev/trust-list", vcRes.getTrustListUri());
        assertTrue(vcRes.isVerified());
    }
	
	@Test
    public void testVCResolutionFHWeb() throws Exception {
    	ResolveRepresentationResult rrr = ResolveRepresentationResult.build(null, docWeb2VM.getBytes(), null); 
        when(uniResolver.resolveRepresentation(eq(didWebEssif), any())).thenReturn(rrr);
        URI uri = new URI("https://tspa.train1.xfsc.dev/tspa-service/tspa/v1/bob.trust.train1.xfsc.dev/vc");
        Document doc = JsonDocument.of(new StringReader(docWebSignerVC));
        docLoaderCache.put(uri, doc);
        assertNotNull(docLoaderCache.asMap().get(uri));

    	VCResolveResult vcRes = didResolver.resolveVC(uri.toString(), didWebEssif);
        assertEquals("https://tspa.train1.xfsc.dev/tspa-service/tspa/v1/bob.trust.train1.xfsc.dev/trust-list", vcRes.getTrustListUri());
        assertFalse(vcRes.isVerified()); // because of the wrong signature from Signer
    }

	@Test
    public void testVCResolutionW3C() throws Exception {
    	ResolveRepresentationResult rrr = ResolveRepresentationResult.build(null, docW3CSampleDid.getBytes(), null); 
        when(uniResolver.resolveRepresentation(eq(didSampleW3C), any())).thenReturn(rrr);
        URI uri = new URI(didSampleW3C + "/vc");
        Document doc = JsonDocument.of(new StringReader(docW3CSampleVC));
        docLoaderCache.put(uri, doc);
        assertNotNull(docLoaderCache.asMap().get(uri));

    	VCResolveResult vcRes = didResolver.resolveVC(uri.toString(), didSampleW3C); 
        assertNull(vcRes.getTrustListUri());
        assertTrue(vcRes.isVerified());
    }
	*/
}
