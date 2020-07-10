package pl.byteit.mbankscrapper.operation;

public class InvalidCredentialsException extends RuntimeException {

	public InvalidCredentialsException() {
		super("Credentials are not correct");
	}
}
