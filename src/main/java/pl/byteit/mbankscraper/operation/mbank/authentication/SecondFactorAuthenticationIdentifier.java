package pl.byteit.mbankscraper.operation.mbank.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
class SecondFactorAuthenticationIdentifier {

	@JsonProperty("ScaAuthorizationId")
	private final String identifier;

	@JsonCreator
	public SecondFactorAuthenticationIdentifier(@JsonProperty("ScaAuthorizationId") String identifier) {
		this.identifier = identifier;
	}

	static class FinalizeAuthenticationRequest {

		@JsonProperty("scaAuthorizationId")
		private final String identifier;

		private FinalizeAuthenticationRequest(String identifier) {
			this.identifier = identifier;
		}

		static FinalizeAuthenticationRequest withIdentifier(SecondFactorAuthenticationIdentifier identifier) {
			return new FinalizeAuthenticationRequest(identifier.identifier);
		}

	}
}
