package pl.byteit.mbankscraper.operation.mbank;

import pl.byteit.mbankscraper.operation.Account;
import pl.byteit.mbankscraper.operation.AuthenticationStatus;
import pl.byteit.mbankscraper.operation.mbank.data.LoginResponse;
import pl.byteit.mbankscraper.operation.mbank.data.SavingAccount;
import pl.byteit.mbankscraper.operation.mbank.data.StandardAccount;

import static pl.byteit.mbankscraper.operation.Account.AccountType.SAVING;
import static pl.byteit.mbankscraper.operation.Account.AccountType.STANDARD;

public class ResponseMapper {

	private static final String SECOND_FACTOR_REQUIRED_REDIRECT_URL = "/authorization";

	public static AuthenticationStatus asAuthenticationStatus(LoginResponse loginResponse) {
		if (loginResponse.successful) {
			return SECOND_FACTOR_REQUIRED_REDIRECT_URL.equals(loginResponse.redirectUrl) ?
					AuthenticationStatus.SECOND_FACTOR_AUTHENTICATION_REQUIRED :
					AuthenticationStatus.AUTHENTICATED;
		} else {
			throw new IllegalStateException("Login response status must be successful");
		}
	}

	public static Account asAccount(SavingAccount savingAccount) {
		return new Account(savingAccount.name, savingAccount.amount, savingAccount.currency, SAVING);
	}

	public static Account asAccount(StandardAccount standardAccount) {
		return new Account(standardAccount.number, standardAccount.balance.value, standardAccount.balance.currency, STANDARD);
	}

}
