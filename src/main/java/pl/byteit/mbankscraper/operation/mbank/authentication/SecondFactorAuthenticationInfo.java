package pl.byteit.mbankscraper.operation.mbank.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SecondFactorAuthenticationInfo {

	private final TranId tranId;

	@JsonCreator
	public SecondFactorAuthenticationInfo(@JsonProperty("TranId") String tranId) {
		this.tranId = new TranId(tranId);
	}

	public TranId getTranId() {
		return tranId;
	}

	static class TranId {

		@JsonProperty("TranId")
		private final String tranId;

		private TranId(String tranId) {
			this.tranId = tranId;
		}

	}
}
