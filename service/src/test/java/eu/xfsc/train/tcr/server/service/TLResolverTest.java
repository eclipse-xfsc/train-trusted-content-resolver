package eu.xfsc.train.tcr.server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URL;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import eu.xfsc.train.tspa.model.trustlist.TrustServiceStatusList;
import eu.xfsc.train.tspa.model.trustlist.tsp.TSPCustomType;


@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class TLResolverTest {
	
	private static final String fh_TrustList_Uri = "https://tspa.trust-scheme.de/tspa_train_domain/api/v1/scheme/federation1.test.train.trust-scheme.de";
	private static final String fh_TrustList_Hash = "QmcRE4LZ2vZxyP85AetrFV3gKxLeZ6hqAW655L8nypJFuP"; 

	@Autowired
	private TLResolver tlResolver;
	
	private void checkTrustList(TrustServiceStatusList trustList, String name, String issuer) {
		assertNotNull(trustList);
		assertNotNull(trustList.getFrameworkInformation());
		assertEquals("http://TRAIN/TrstSvc/TrustedList/TSLType/federation1-POC", trustList.getFrameworkInformation().getTSLType());
		assertEquals(1, trustList.getFrameworkInformation().getTSLVersionIdentifier());
		assertEquals(1, trustList.getFrameworkInformation().getTSLSequenceNumber());
		assertEquals("Federation 1", trustList.getFrameworkInformation().getFrameworkOperatorName().getName());
		assertEquals("https://TRAIN/interoperability/federation-Directory", trustList.getFrameworkInformation().getFrameworkInformationURI().getUri());
		assertNotNull(trustList.getTrustServiceProviderList());
		assertEquals(1, trustList.getTrustServiceProviderList().getTrustServiceProvider().size());
		TSPCustomType tspt = trustList.getTrustServiceProviderList().getTrustServiceProvider().get(0);
		assertNotNull(tspt.getTSPInformation());
		assertEquals(name, tspt.getTSPName());
		assertNotNull(tspt.getTSPServices());
		assertEquals(1, tspt.getTSPServices().getTspService().size());
		assertTrue(trustList.getTrustServiceProviderList().getTrustServiceProvider().stream()
			.filter(tsp -> tsp.getTSPServices().getTspService().stream().anyMatch(tsps -> tsps.getServiceTypeIdentifier().equals(issuer))).findFirst().isPresent());
	}

	@Test
	public void testTLResolveXMLFile() throws Exception {
		URL url = this.getClass().getResource("/AliceSampleTrustList.xml");
		File file = new File(url.getFile());
		assertTrue(file.exists());
		TrustServiceStatusList list = tlResolver.resolveTL(file.getCanonicalFile().toURI().toString());
		checkTrustList(list, "alice-company-ag", "did:web:companyA.de");

		list = tlResolver.resolveTL("https://tspa.train1.xfsc.dev/tspa-service/tspa/v1/alice.trust.train1.xfsc.dev/trust-list");
		//assertNotNull(list);
		//checkTrustList(list, "CompanyaA Gmbh", "did:web:companyA.de");
	}

	@Test
	public void testTLResolveJSONFile() throws Exception {
		URL url = this.getClass().getResource("/BobSampleTrustList.json");
		File file = new File(url.getFile());
		assertTrue(file.exists());
		TrustServiceStatusList list = tlResolver.resolveTL(file.getCanonicalFile().toURI().toString());
		checkTrustList(list, "bobs-burgers", "did:web:bobs-burgers.de");
		
		//list = tlResolver.resolveTL("https://tspa.train1.xfsc.dev/tspa-service/tspa/v1/bob.trust.train1.xfsc.dev/trust-list");
		//assertNotNull(list);
		//checkTrustList(list, "bobs-burgers", "did:web:bobs-burgers.de");
	}

	@Test
	public void testTLResolveXMLUri() throws Exception {
		TrustServiceStatusList list = tlResolver.resolveTL(fh_TrustList_Uri);
		assertNotNull(list);
		assertNotNull(list.getFrameworkInformation());
		assertEquals("Federation 1", list.getFrameworkInformation().getFrameworkOperatorName().getName());
		assertNotNull(list.getTrustServiceProviderList());
		assertEquals(1, list.getTrustServiceProviderList().getTrustServiceProvider().size());
	}

	@Test
	public void testTLResolveWithHash() throws Exception {
		TLResolver.TLResolveResult tlRes = tlResolver.resolveTLHash(fh_TrustList_Uri, fh_TrustList_Hash);
		assertNotNull(tlRes);
		assertTrue(tlRes.isHashVerified());
	}
	
}