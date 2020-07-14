package pl.byteit.mbankscraper.operation;

public interface BankClient {

	void login(Credentials credentials);

	void getAccounts();
}
