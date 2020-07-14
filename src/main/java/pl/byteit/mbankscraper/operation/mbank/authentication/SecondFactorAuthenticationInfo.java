package pl.byteit.mbankscraper.operation.mbank.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SecondFactorAuthenticationInfo {

	private final String deviceName;
	private final TranId tranId;

	@JsonCreator
	public SecondFactorAuthenticationInfo(@JsonProperty("DeviceName") String deviceName, @JsonProperty("TranId") String tranId) {
		this.deviceName = deviceName;
		this.tranId = new TranId(tranId);
	}

	public String getDeviceName() {
		return deviceName;
	}

	public TranId getTranId() {
		return tranId;
	}

	static class TranId {

		private final String tranId;

		private TranId(String tranId) {
			this.tranId = tranId;
		}

		@JsonGetter("TranId")
		public String getTranId() {
			return tranId;
		}
	}
}
