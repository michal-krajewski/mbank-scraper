package pl.byteit.mbankscraper.operation.mbank;

import pl.byteit.mbankscraper.operation.mbank.data.SecondFactorAuthenticationStatus;

public class AuthenticationException extends RuntimeException {

	public AuthenticationException(SecondFactorAuthenticationStatus secondFactorAuthenticationStatus) {
		super("Authentication failed with status: " + secondFactorAuthenticationStatus.status);
	}
}
