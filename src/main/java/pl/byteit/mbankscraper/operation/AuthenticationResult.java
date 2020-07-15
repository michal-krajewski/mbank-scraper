package pl.byteit.mbankscraper.operation;

import java.util.Optional;

import static pl.byteit.mbankscraper.operation.AuthenticationResult.AuthenticationStatus.SECOND_FACTOR_AUTHENTICATION_REQUIRED;
import static pl.byteit.mbankscraper.operation.AuthenticationResult.AuthenticationStatus.SUCCESSFUL;
import static pl.byteit.mbankscraper.operation.AuthenticationResult.SecondFactorAuthenticationType.MOBILE_APP;

public class AuthenticationResult {

	private final AuthenticationStatus authenticationStatus;
	private final SecondFactorAuthenticationType secondFactorAuthenticationType;

	private AuthenticationResult(
			AuthenticationStatus authenticationStatus,
			SecondFactorAuthenticationType secondFactorAuthenticationType
	) {
		this.authenticationStatus = authenticationStatus;
		this.secondFactorAuthenticationType = secondFactorAuthenticationType;
	}

	public static AuthenticationResult successful() {
		return new AuthenticationResult(SUCCESSFUL, null);
	}

	public static AuthenticationResult mobileSecondFactorAuthenticationRequired() {
		return new AuthenticationResult(SECOND_FACTOR_AUTHENTICATION_REQUIRED, MOBILE_APP);
	}

	public AuthenticationStatus getAuthenticationStatus() {
		return authenticationStatus;
	}

	public boolean isSuccessful() {
		return SUCCESSFUL.equals(authenticationStatus);
	}

	public Optional<SecondFactorAuthenticationType> getSecondFactorAuthenticationType() {
		return Optional.ofNullable(secondFactorAuthenticationType);
	}

	public enum AuthenticationStatus {
		SUCCESSFUL,
		SECOND_FACTOR_AUTHENTICATION_REQUIRED
	}

	public enum SecondFactorAuthenticationType {
		MOBILE_APP
	}

}
