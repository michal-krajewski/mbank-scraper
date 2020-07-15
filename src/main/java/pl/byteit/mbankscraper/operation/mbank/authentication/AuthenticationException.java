package pl.byteit.mbankscraper.operation.mbank.authentication;

public class AuthenticationException extends RuntimeException {

	public AuthenticationException(SecondFactorAuthenticationStatus status) {
		super("Authentication failed with status: " + status.getStatus());
	}
}
