package pl.byteit.mbankscrapper.operation;

public interface BankOperationManager {

	void login(Credentials credentials);

	void getAccounts();
}
