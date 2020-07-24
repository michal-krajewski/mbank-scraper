package pl.byteit.scraper.mbank.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SecondFactorAuthenticationIdentifier {

	@JsonProperty("ScaAuthorizationId") final String identifier;

	@JsonCreator
	public SecondFactorAuthenticationIdentifier(@JsonProperty("ScaAuthorizationId") String identifier) {
		this.identifier = identifier;
	}

}
