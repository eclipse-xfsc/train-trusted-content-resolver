package eu.xfsc.train.tcr.server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.Type;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class DNSResolverTest {
	
	private static final String gxfs_ptr = "gxfs.test.train.trust-scheme.de";
	private static final String test_ptr = "did-web.test.train.trust-scheme.de";

	//@MockBean
	@Autowired
	private Resolver resolver;
	
	@Autowired
	private DNSResolver dnsResolver;

    @Test
    public void testDNSResolutionGXFS() throws Exception {
        Collection<String> dids = dnsResolver.resolveDomain(gxfs_ptr); 
        assertNotNull(dids);
        assertEquals(Set.of("did:web:essif.iao.fraunhofer.de"), dids);
    }
	
    @Test
    public void testDNSResolutionTEST() throws Exception {
        Collection<String> dids = dnsResolver.resolveDomain(test_ptr); 
        assertNotNull(dids);
        assertEquals(Set.of("did:web:essif.trust-scheme.de"), dids);
    }
    
    private Message buildQuery(String uri, int type) {
    	Record query = Record.newRecord(Name.fromConstantString(uri), type, DClass.IN);
    	return Message.newQuery(query);
    }

    //@Test
    // TODO: implement recursion test.. 
    // figure out how to setup proper DNS response 
    public void testDNSResolutionRecursive() throws Exception {
    	Message req = buildQuery("_scheme._trust.gxfs.test.train.trust-scheme.de.", Type.PTR);
        Message resp = new Message();
    	when(resolver.send(req)).thenReturn(resp);
    	
        Collection<String> dids = dnsResolver.resolveDomain(test_ptr); 
        assertNotNull(dids);
        assertEquals(Set.of("did:web:essif.trust-scheme.de"), dids);
    }
    
}
