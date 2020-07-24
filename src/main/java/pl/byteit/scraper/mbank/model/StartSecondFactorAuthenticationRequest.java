package pl.byteit.scraper.mbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StartSecondFactorAuthenticationRequest {

	@JsonProperty("Data")
	private final SecondFactorAuthenticationIdentifier secondFactorAuthenticationIdentifier;

	@JsonProperty("Method")
	private final String method;

	@JsonProperty("Url")
	private final String url;

	public StartSecondFactorAuthenticationRequest(SecondFactorAuthenticationIdentifier identifier, String method, String url) {
		this.secondFactorAuthenticationIdentifier = identifier;
		this.method = method;
		this.url = url;
	}

}
