package eu.xfsc.train.tcr.util;

import com.fasterxml.jackson.annotation.JsonProperty;

@lombok.Getter
@lombok.Setter
public class ResolveServiceParams {
	
    @JsonProperty("uri")
	private String uri;
    @JsonProperty("endpoint")
	private String endpoint;
    @JsonProperty("data")
	private String data;

}
