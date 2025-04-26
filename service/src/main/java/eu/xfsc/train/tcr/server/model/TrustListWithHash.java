package eu.xfsc.train.tcr.server.model;

import eu.xfsc.train.tspa.model.trustlist.TrustServiceStatusList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class TrustListWithHash {
	
	private String content;
	private TrustServiceStatusList trustList;
	private String hash;
	
}
