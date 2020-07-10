package pl.byteit.mbankscrapper.operation.mbank.authentication;

public class AuthenticationException extends RuntimeException {

	public AuthenticationException(AuthenticationStatus status) {
		super("Authentication failed with status: " + status.getStatus());
	}
}
