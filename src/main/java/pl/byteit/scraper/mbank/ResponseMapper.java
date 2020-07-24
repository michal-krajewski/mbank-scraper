package pl.byteit.scraper.mbank;

import pl.byteit.scraper.mbank.model.LoginResponse;
import pl.byteit.scraper.mbank.model.SavingAccount;
import pl.byteit.scraper.mbank.model.StandardAccount;
import pl.byteit.scraper.operation.Account;
import pl.byteit.scraper.operation.AuthenticationStatus;
import pl.byteit.scraper.operation.exception.AuthenticationFailed;

import java.util.List;

import static pl.byteit.scraper.operation.Account.AccountType.SAVING;
import static pl.byteit.scraper.operation.Account.AccountType.STANDARD;

public class ResponseMapper {

	private static final String SECOND_FACTOR_REQUIRED_REDIRECT_URL = "/authorization";

	public static AuthenticationStatus asAuthenticationStatus(LoginResponse loginResponse) {
		if (!loginResponse.successful) {
			throw new AuthenticationFailed("Login response status must be successful");
		}
		return SECOND_FACTOR_REQUIRED_REDIRECT_URL.equals(loginResponse.redirectUrl) ?
				AuthenticationStatus.SECOND_FACTOR_AUTHENTICATION_REQUIRED :
				AuthenticationStatus.AUTHENTICATED;
	}

	public static Account asAccount(SavingAccount account, List<StandardAccount> availableAccounts) {
		return new Account(account.name, account.amount, currencyBasedOnLinkedAccount(account, availableAccounts), SAVING);
	}

	public static Account asAccount(StandardAccount standardAccount) {
		return new Account(standardAccount.number, standardAccount.balance.value, standardAccount.balance.currency, STANDARD);
	}

	private static String currencyBasedOnLinkedAccount(SavingAccount savingAccount, List<StandardAccount> availableLinkedAccounts) {
		return availableLinkedAccounts.stream()
				.filter(account -> account.number.equals(savingAccount.linkedAccountNumber))
				.findFirst()
				.map(account -> account.balance.currency)
				.orElseThrow(() -> new IllegalStateException("Linked account not found for " + savingAccount.name + " saving account"));
	}

}
