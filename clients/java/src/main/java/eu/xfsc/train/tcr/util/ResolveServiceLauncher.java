package eu.xfsc.train.tcr.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.xfsc.train.tcr.api.generated.model.ResolveRequest;
import eu.xfsc.train.tcr.api.generated.model.ValidateRequest;
import eu.xfsc.train.tcr.client.ResolveServiceClient;

public class ResolveServiceLauncher {
	
	static final String DEF_URI = "http://localhost:8087/tcr/v1";
	static ObjectMapper mapper = new ObjectMapper();

	/* Command Line entry point */
	public static void main(String[] args) throws Exception {
		int errorCode = cmd(args);
		if (errorCode != 0) {
			System.exit(errorCode);
		}
	}

	/*
	* Command Line entry point Helper
	* To avoid complex mocking this method will just return error code instead of System Exist,
	* to be used when testing Halt-on-error use cases
	*/
	public static int cmd(String[] args) throws Exception {

		String baseUri = null;
    	String endpoint = null;
    	String data = null;
    	ResolveServiceParams params = null;
    	//System.out.println("args: " + Arrays.toString(args));
    	for (String arg: args) {
    		String[] parts = arg.split("=");
    		if ("u".equals(parts[0]) || "uri".equals(parts[0])) {
    			baseUri = parts[1];
    			continue;
    		}
    		if ("e".equals(parts[0]) || "endpoint".equals(parts[0])) {
    			endpoint = parts[1];
    			continue;
    		}
    		if ("d".equals(parts[0]) || "data".equals(parts[0])) {
    			data = parts[1];
    			continue;
    		}
    		if ("f".equals(parts[0]) || "file".equals(parts[0])) {
    			params = mapper.readValue(new File(parts[1]), ResolveServiceParams.class);
    			continue;
    		}

			if (arg.startsWith("-D")) {
				// ignore java -D arguments
				continue;
			}
			System.err.println("unknown parameter: " + arg);
			return 1;
    	}
    	
    	if (params != null) {
    		if (baseUri == null) {
    			baseUri = params.getUri();
    		}
    		if (endpoint == null) {
    			endpoint = params.getEndpoint();
    		}
    		if (data == null) {
    			data = params.getData();
    		}
    	}
    	if (baseUri == null) {
    		baseUri = DEF_URI;
    	}
    	
    	if (endpoint == null) {
			System.err.println("error: no TCR endpoint specified");
			return 2;
    	}

		List<String> endpoints = Arrays.asList("resolve", "validate");
		if (! endpoints.contains(endpoint) ) {
			System.err.println("error: unknown TCR endpoint: " + endpoint);
			return 3;
		}

    	if (data == null) {
			String method = ("resolve".equals(endpoint) ? "resolution" : "validation");
			System.err.println("error: no " + method + " data provided");
			return 4;
    	}

		ResolveServiceClient client = new ResolveServiceClient(baseUri);

		Object result = switch (endpoint) {
            case "resolve" -> {
                ResolveRequest rrq = mapper.readValue(data, ResolveRequest.class);
                yield client.resolveTrustList(rrq);
            }
            case "validate" -> {
                ValidateRequest vrq = mapper.readValue(data, ValidateRequest.class);
                yield client.validateTrustList(vrq);
            }
            default -> null;
        };

		System.out.println(mapper.writeValueAsString(result));
        return 0;
    }
    
}
