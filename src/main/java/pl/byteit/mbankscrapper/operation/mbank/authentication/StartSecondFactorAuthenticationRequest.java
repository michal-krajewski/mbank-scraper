package pl.byteit.mbankscrapper.operation.mbank.authentication;

import com.fasterxml.jackson.annotation.JsonGetter;

class StartSecondFactorAuthenticationRequest {

	private static final String SCA_AUTHORIZATION_DISPOSABLE_URL = "sca/authorization/disposable";

	private final SecondFactorAuthenticationIdentifier secondFactorAuthenticationIdentifier;
	private final String method;
	private final String url;

	private StartSecondFactorAuthenticationRequest(SecondFactorAuthenticationIdentifier identifier, String method, String url) {
		this.secondFactorAuthenticationIdentifier = identifier;
		this.method = method;
		this.url = url;
	}

	static StartSecondFactorAuthenticationRequest withId(SecondFactorAuthenticationIdentifier identifier) {
		return new StartSecondFactorAuthenticationRequest(identifier, "POST", SCA_AUTHORIZATION_DISPOSABLE_URL);
	}

	@JsonGetter("Data")
	private SecondFactorAuthenticationIdentifier getAuthorizationIdentifier() {
		return secondFactorAuthenticationIdentifier;
	}

	@JsonGetter("Method")
	private String getMethod() {
		return method;
	}

	@JsonGetter("Url")
	private String getUrl() {
		return url;
	}
}
