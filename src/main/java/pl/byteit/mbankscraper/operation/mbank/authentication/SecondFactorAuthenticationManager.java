package pl.byteit.mbankscraper.operation.mbank.authentication;

import pl.byteit.mbankscraper.operation.AuthenticationResult;
import pl.byteit.mbankscraper.operation.mbank.RequestVerificationToken;

public interface SecondFactorAuthenticationManager {

	AuthenticationResult authenticate(RequestVerificationToken requestVerificationToken);

}
