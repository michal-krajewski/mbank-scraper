package pl.byteit.scraper.operation.exception;

public class InvalidCredentials extends RuntimeException {

	public InvalidCredentials() {
		super("Credentials are not correct");
	}
}
