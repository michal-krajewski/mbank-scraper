package pl.byteit.scraper.mbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FinalizeAuthenticationRequest {

	@JsonProperty("scaAuthorizationId")
	private final String identifier;

	public FinalizeAuthenticationRequest(SecondFactorAuthenticationIdentifier identifier) {
		this.identifier = identifier.identifier;
	}

}
