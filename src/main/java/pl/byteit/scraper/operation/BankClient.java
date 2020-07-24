package pl.byteit.scraper.operation;

import java.util.List;

public interface BankClient {

	AuthenticationStatus login(String login, String password);

	void authenticateWithSecondFactor();

	List<Account> getAccounts();

}
