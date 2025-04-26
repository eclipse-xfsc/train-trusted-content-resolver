package eu.xfsc.train.tcr.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.xfsc.train.tcr.api.generated.model.ResolvedDid;
import eu.xfsc.train.tcr.api.generated.model.ResolvedDoc;
import eu.xfsc.train.tcr.api.generated.model.ResolvedPointer;
import eu.xfsc.train.tcr.api.generated.model.ResolveResponse;
import eu.xfsc.train.tcr.api.generated.model.ResolvedTrustList;
import eu.xfsc.train.tcr.api.generated.model.ValidateResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ResolveServiceLauncherTest {
	
    private static MockWebServer mockBackEnd;
    private static final int TCR_PORT = 8087;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.noClientAuth();
        mockBackEnd.start(TCR_PORT);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }
    
	@Test
	public void testLauncherFailEmptyRequest() throws Exception {
		String[] arguments = new String[] {};

	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    PrintStream out = new PrintStream(byteArrayOutputStream);
	    System.setErr(out);
		
    	ResolveServiceLauncher.cmd(arguments);

	    String output = byteArrayOutputStream.toString(Charset.defaultCharset());
	    assertEquals("error: no TCR endpoint specified\n", output);
	    out.close();
	}

	@Test
	public void testLauncherFailOUnknownParameter() throws Exception {
		String[] arguments = new String[] {"-Dtrain.opt=value", "--flag-on", "-d", "endpoint=validate"};

	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    PrintStream out = new PrintStream(byteArrayOutputStream);
	    System.setErr(out);

    	int returnCode = ResolveServiceLauncher.cmd(arguments);

		assertEquals(returnCode, 1);
	    String output = byteArrayOutputStream.toString(Charset.defaultCharset());
	    assertEquals(output, "unknown parameter: --flag-on\n");
	    out.close();

	}

	@Test
	public void testLauncherFailOptionsRequest() throws Exception {
		String[] arguments = new String[] {"-Dtrain.opt=value", "endpoint=validate"};

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(byteArrayOutputStream);
		System.setErr(out);

		int returnCode = ResolveServiceLauncher.cmd(arguments);

		assertEquals(returnCode, 4);
		String output = byteArrayOutputStream.toString(Charset.defaultCharset());
		assertTrue(output.endsWith("error: no validation data provided\n"));
		out.close();

	}
	
	@Test
	public void testLauncherFailNoEndpointRequest() throws Exception {
		String[] arguments = new String[] {"d={\"issuer\": \"issuer1\", \"trustSchemePointers\": [\"ptr1\", \"ptr2\"]}"};

	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    PrintStream out = new PrintStream(byteArrayOutputStream);
	    System.setErr(out);
		
    	ResolveServiceLauncher.cmd(arguments);

	    String output = byteArrayOutputStream.toString(Charset.defaultCharset());
	    assertEquals("error: no TCR endpoint specified\n", output);
	    out.close();
	}

	@Test
	public void testLauncherFailUnknownEndpointRequest() throws Exception {
		String[] arguments = new String[] {"e=verify"};

	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    PrintStream out = new PrintStream(byteArrayOutputStream);
	    System.setErr(out);

	    ResolveServiceLauncher.cmd(arguments);

	    String output = byteArrayOutputStream.toString(Charset.defaultCharset());
	    assertEquals("error: unknown TCR endpoint: verify\n", output);
	    out.close();
	}

	@Test
	public void testLauncherFailNoDataRequest() throws Exception {
		String[] arguments = new String[] {"e=validate"};

	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    PrintStream out = new PrintStream(byteArrayOutputStream);
	    System.setErr(out);

	    ResolveServiceLauncher.cmd(arguments);

	    String output = byteArrayOutputStream.toString(Charset.defaultCharset());
	    assertEquals("error: no validation data provided\n", output);
	    out.close();
	}

	@Test
	public void testLauncherResolveResponse() throws Exception {
		// GIVEN
		String[] arguments = new String[] {"e=resolve", "d={\"issuer\": \"issuer1\", \"trustSchemePointers\": [\"ptr1\", \"ptr2\"]}"};

	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    PrintStream out = new PrintStream(byteArrayOutputStream);
	    System.setOut(out);
		
		ResolvedPointer rePtr1 = new ResolvedPointer("ptr1", List.of(), null);
		ResolvedPointer rePtr2 = new ResolvedPointer("ptr2", List.of("did:mock:sample-issuer1"), null);  
	    ResolvedDoc reDoc = new ResolvedDoc(Map.of("content", "value"), List.of(new ResolvedTrustList("https://issuer1.test/verifiable-credentials", "https://issuer1.test/trust-list", Map.of(), true)), true);
		ResolvedDid reDid = new ResolvedDid("did:mock:sample-issuer1", reDoc, null);
		ResolveResponse mock = new ResolveResponse(List.of(rePtr1, rePtr2), List.of(reDid));
		mockBackEnd.enqueue(new MockResponse().setBody(mapper.writeValueAsString(mock)).addHeader("Content-Type", "application/json"));

		// WHEN
    	ResolveServiceLauncher.main(arguments);

		// THEN
	    String output = byteArrayOutputStream.toString(Charset.defaultCharset());
	    ResolveResponse resp = mapper.readValue(output, ResolveResponse.class);
	    assertNotNull(resp.getTrustSchemePointers());
	    assertEquals(2, resp.getTrustSchemePointers().size());
	    assertEquals(mock.getTrustSchemePointers().get(0).getPointer(), resp.getTrustSchemePointers().get(0).getPointer());
	    assertEquals(0, resp.getTrustSchemePointers().get(0).getDids().size());
	    assertEquals(mock.getTrustSchemePointers().get(1).getPointer(), resp.getTrustSchemePointers().get(1).getPointer());
	    assertEquals(1, resp.getTrustSchemePointers().get(1).getDids().size());
	    assertEquals(rePtr2.getDids().get(0), resp.getTrustSchemePointers().get(1).getDids().get(0));

	    assertNotNull(resp.getResolvedResults());
	    assertEquals(1, resp.getResolvedResults().size());
	    assertEquals(mock.getResolvedResults().get(0).getDid(), resp.getResolvedResults().get(0).getDid());

	    out.close();
	}

	@Test
	public void testLauncherValidateResponse() throws Exception {
		String[] arguments = new String[] {"e=validate", "d={\"issuer\": \"issuer1\", \"did\": \"did:mock:sample-issuer1\", \"endpoints\": [\"https://issuer1.test/trust-list\"]}"};

	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    PrintStream out = new PrintStream(byteArrayOutputStream);
	    System.setOut(out);
		
		ValidateResponse vrs = new ValidateResponse(true, List.of(new ResolvedTrustList("https://issuer1.test/verifiable-credentials", "https://issuer1.test/trust-list", Map.of(), true)));
		mockBackEnd.enqueue(new MockResponse().setBody(mapper.writeValueAsString(vrs)).addHeader("Content-Type", "application/json"));

    	ResolveServiceLauncher.main(arguments);

	    String output = byteArrayOutputStream.toString(Charset.defaultCharset());
	    ValidateResponse resp = mapper.readValue(output, ValidateResponse.class);
	    assertEquals(vrs, resp);

	    out.close();
	}

	@Test
	public void testLauncherResolveFileResponse() throws Exception {
		String[] arguments = new String[] {"uri=http://localhost:8087", "f=./src/test/resources/resolve-input.json"};

	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    PrintStream out = new PrintStream(byteArrayOutputStream);
	    System.setOut(out);
		
		ResolvedPointer rePtr1 = new ResolvedPointer("ptr1", List.of(), null);
		ResolvedPointer rePtr2 = new ResolvedPointer("ptr2", List.of("did:mock:sample-issuer1"), null);  
	    ResolvedDoc reDoc = new ResolvedDoc(Map.of("content", "value"), List.of(new ResolvedTrustList("https://issuer1.test/verifiable-credentials", "https://issuer1.test/trust-list", Map.of(), true)), true);
		ResolvedDid reDid = new ResolvedDid("did:mock:sample-issuer1", reDoc, null);
		ResolveResponse mock = new ResolveResponse(List.of(rePtr1, rePtr2), List.of(reDid));
		mockBackEnd.enqueue(new MockResponse().setBody(mapper.writeValueAsString(mock)).addHeader("Content-Type", "application/json"));

    	ResolveServiceLauncher.main(arguments);

	    String output = byteArrayOutputStream.toString(Charset.defaultCharset());
	    ResolveResponse resp = mapper.readValue(output, ResolveResponse.class);
	    assertNotNull(resp.getTrustSchemePointers());
	    assertEquals(2, resp.getTrustSchemePointers().size());
	    assertEquals(mock.getTrustSchemePointers().get(0).getPointer(), resp.getTrustSchemePointers().get(0).getPointer());
	    assertEquals(0, resp.getTrustSchemePointers().get(0).getDids().size());
	    assertEquals(mock.getTrustSchemePointers().get(1).getPointer(), resp.getTrustSchemePointers().get(1).getPointer());
	    assertEquals(1, resp.getTrustSchemePointers().get(1).getDids().size());
	    assertEquals(rePtr2.getDids().get(0), resp.getTrustSchemePointers().get(1).getDids().get(0));

	    out.close();
	}

	@Test
	public void testLauncherValidateFileResponse() throws Exception {
		String[] arguments = new String[] {"f=src/test/resources/validate-input.json"};
		
        mockBackEnd.shutdown();
        mockBackEnd = new MockWebServer();
        mockBackEnd.noClientAuth();
        mockBackEnd.start(8080); 

	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    PrintStream out = new PrintStream(byteArrayOutputStream);
	    System.setOut(out);
		
		ValidateResponse vrs = new ValidateResponse(true, List.of(new ResolvedTrustList("https://issuer1.test/verifiable-credentials", "https://issuer1.test/trust-list", Map.of(), true)));
		mockBackEnd.enqueue(new MockResponse().setBody(mapper.writeValueAsString(vrs)).addHeader("Content-Type", "application/json"));

    	ResolveServiceLauncher.main(arguments);

	    String output = byteArrayOutputStream.toString(Charset.defaultCharset());
	    ValidateResponse resp = mapper.readValue(output, ValidateResponse.class);
	    assertEquals(vrs, resp);

	    out.close();
	    
	    tearDown();
	    setUp();
	}
	
}
