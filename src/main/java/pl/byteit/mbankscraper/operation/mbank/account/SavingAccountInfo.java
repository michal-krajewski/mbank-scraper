package pl.byteit.mbankscraper.operation.mbank.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import pl.byteit.mbankscraper.operation.AccountInfo;

import java.math.BigDecimal;
import java.util.List;

import static pl.byteit.mbankscraper.operation.AccountInfo.AccountType.SAVING;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SavingAccountInfo {

	private final String name;
	private final String linkedAccount;
	private final BigDecimal amount;
	private String currency;

	@JsonCreator
	public SavingAccountInfo(
			@JsonProperty("fullName") String name,
			@JsonProperty("accountNumber") String linkedAccount,
			@JsonProperty("currentGoalAmount") BigDecimal amount) {
		this.name = name;
		this.amount = amount;
		this.linkedAccount = linkedAccount;
	}

	public void setCurrencyBasedOnLinkedAccount(List<StandardAccountInfo> availableAccounts) {
		availableAccounts.stream()
				.filter(account -> account.getNumber().equals(linkedAccount))
				.findFirst()
				.ifPresentOrElse(
						account -> this.currency = account.getCurrency(),
						() -> {
							throw new IllegalStateException("Linked account not found for " + name + " Saving account");
						}
				);
	}

	public AccountInfo toAccountInfo() {
		return new AccountInfo(name, amount, currency, SAVING);
	}

}
