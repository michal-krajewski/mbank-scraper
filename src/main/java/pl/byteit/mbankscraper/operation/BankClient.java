package pl.byteit.mbankscraper.operation;

import java.util.List;

public interface BankClient {

	AuthenticationResult login(Credentials credentials);

	AuthenticationResult authenticateWithSecondFactor();

	List<AccountInfo> getAccounts();
}
