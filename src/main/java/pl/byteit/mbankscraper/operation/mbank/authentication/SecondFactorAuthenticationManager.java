package pl.byteit.mbankscraper.operation.mbank.authentication;

import pl.byteit.mbankscraper.operation.mbank.RequestVerificationToken;

public interface SecondFactorAuthenticationManager {

	void authenticate(RequestVerificationToken requestVerificationToken);

}
