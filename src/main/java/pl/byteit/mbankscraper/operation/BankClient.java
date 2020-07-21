package pl.byteit.mbankscraper.operation;

import java.util.List;

public interface BankClient {

	AuthenticationStatus login(String login, String password);

	AuthenticationStatus authenticateWithSecondFactor();

	List<Account> getAccounts();
}
