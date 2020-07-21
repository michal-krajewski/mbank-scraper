package pl.byteit.mbankscraper.operation.mbank.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SecondFactorAuthenticationIdentifier {

	@JsonProperty("ScaAuthorizationId")
	private final String identifier;

	@JsonCreator
	public SecondFactorAuthenticationIdentifier(@JsonProperty("ScaAuthorizationId") String identifier) {
		this.identifier = identifier;
	}

	public static class FinalizeAuthenticationRequest {

		@JsonProperty("scaAuthorizationId")
		private final String identifier;

		private FinalizeAuthenticationRequest(String identifier) {
			this.identifier = identifier;
		}

		public static FinalizeAuthenticationRequest withIdentifier(SecondFactorAuthenticationIdentifier identifier) {
			return new FinalizeAuthenticationRequest(identifier.identifier);
		}

	}
}
