package pl.byteit.mbankscraper.operation.mbank.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
class SecondFactorAuthenticationIdentifier {

	private final String identifier;

	@JsonCreator
	public SecondFactorAuthenticationIdentifier(@JsonProperty("ScaAuthorizationId") String identifier) {
		this.identifier = identifier;
	}

	@JsonGetter("ScaAuthorizationId")
	private String getIdentifier() {
		return identifier;
	}

	static class FinalizeAuthenticationRequest {

		private final String identifier;

		private FinalizeAuthenticationRequest(String identifier) {
			this.identifier = identifier;
		}

		static FinalizeAuthenticationRequest withIdentifier(SecondFactorAuthenticationIdentifier identifier) {
			return new FinalizeAuthenticationRequest(identifier.getIdentifier());
		}

		@JsonGetter("scaAuthorizationId")
		public String getIdentifier() {
			return identifier;
		}
	}
}
