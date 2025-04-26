package eu.xfsc.train.tcr.client;

import java.util.List;
import java.util.Map;

import org.springframework.web.reactive.function.client.WebClient;

import eu.xfsc.train.tcr.api.generated.model.ResolveRequest;
import eu.xfsc.train.tcr.api.generated.model.ResolveResponse;
import eu.xfsc.train.tcr.api.generated.model.ValidateRequest;
import eu.xfsc.train.tcr.api.generated.model.ValidateResponse;

/**
 * Client wrapper hiding communication via REST with TCR server
 */
public class ResolveServiceClient extends ServiceClient {
	
	/**
	 * The Client constructor
	 * 
	 * @param baseUrl: TCR base url
	 */
    public ResolveServiceClient(String baseUrl) {
        super(baseUrl, (String) null);
    }

    /**
     * The Client constructor
     * 
     * @param baseUrl: TCR base url
     * @param client: pre-configured WebClient instance
     */
    public ResolveServiceClient(String baseUrl, WebClient client) {
        super(baseUrl, client);
    }
    
    /**
     * Resolves TrustLists for specified issuer and pointer 
     * 
     * @param issuer: TrustList issuer DID/URI
     * @param pointer: Trust Framework Pointer
     * @param serviceTypes: service types to confider. null allows any service type
     * @return list of ResolveResut structures
     */
    public ResolveResponse resolveTrustList(String issuer, List<String> pointers, List<String> serviceEndpointTypes) { 
    	ResolveRequest rrq = new ResolveRequest(issuer, pointers, serviceEndpointTypes);
        return resolveTrustList(rrq); 
    }

    /**
     * Resolves TrustList for specified /resolve params
     * 
     * @param rrq: ResolveRequest structure containing a bunch of /resolve parameters
     * @return list of ResolveResult structures
     */
    public ResolveResponse resolveTrustList(ResolveRequest rrq) { 
        return doPost(baseUrl + "/resolve", rrq, Map.of(), ResolveResponse.class); 
    }
    
    /**
     * Validates resolution for specified issuer and DID
     * 
     * @param issuer: TrustList issuer DID/URI
     * @param did: DID resolved for provided Pointer
     * @param tlEndpoints: resolved TrustList endpoints
     * @return issuer/DID/VC verification status
     */
    public ValidateResponse validateTrustList(String issuer, String did, List<String> tlEndpoints) { 
    	ValidateRequest vrq = new ValidateRequest(issuer, did, tlEndpoints);
        return validateTrustList(vrq); 
    }
    
    /**
     * Validates resolution for specified issuer and DID
     * 
     * @param vrq: ValidateRequest structure containing a bunch of /validate parameters
     * @return issuer/DID/VC verification status
     */
    public ValidateResponse validateTrustList(ValidateRequest vrq) { 
        return doPost(baseUrl + "/validate", vrq, Map.of(), ValidateResponse.class); 
    }
    
}
