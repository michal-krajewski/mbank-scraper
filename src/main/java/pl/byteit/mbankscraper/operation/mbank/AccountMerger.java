package pl.byteit.mbankscraper.operation.mbank;

import pl.byteit.mbankscraper.operation.mbank.data.SavingAccount;
import pl.byteit.mbankscraper.operation.mbank.data.StandardAccount;

import java.util.List;

public class AccountMerger {

	public static void setCurrencyBasedOnLinkedAccount(SavingAccount accountToUpdate, List<StandardAccount> availableLinkedAccounts) {
		availableLinkedAccounts.stream()
				.filter(account -> account.number.equals(accountToUpdate.linkedAccount))
				.findFirst()
				.ifPresentOrElse(
						account -> accountToUpdate.currency = account.balance.currency,
						() -> {
							throw new IllegalStateException("Linked account not found for " + accountToUpdate.name + " saving account");
						}
				);
	}

}
