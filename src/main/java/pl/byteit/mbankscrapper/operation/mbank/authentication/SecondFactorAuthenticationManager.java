package pl.byteit.mbankscrapper.operation.mbank.authentication;

import pl.byteit.mbankscrapper.operation.mbank.RequestVerificationToken;

public interface SecondFactorAuthenticationManager {

	void authenticate(RequestVerificationToken requestVerificationToken);

}
