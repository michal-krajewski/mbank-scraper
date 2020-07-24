package pl.byteit.scraper.operation.exception;

public class SecondFactorAuthenticationFailed extends RuntimeException {

	public SecondFactorAuthenticationFailed(String msg) {
		super(msg);
	}

}
