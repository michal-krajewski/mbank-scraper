package pl.byteit.mbankscraper.operation;

import org.assertj.core.api.AbstractAssert;
import pl.byteit.mbankscraper.operation.Account.AccountType;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountAssert extends AbstractAssert<AccountAssert, Account> {

	private AccountAssert(Account account) {
		super(account, AccountAssert.class);
	}

	public static AccountAssert assertThatAccount(Account account) {
		return new AccountAssert(account);
	}

	public AccountAssert hasName(String expectedName) {
		isNotNull();
		assertThat(actual.getName()).isEqualTo(expectedName);
		return this;
	}

	public AccountAssert hasBalance(BigDecimal expectedBalance) {
		isNotNull();
		assertThat(actual.getBalance()).isEqualTo(expectedBalance);
		return this;
	}

	public AccountAssert hasCurrency(String expectedCurrency) {
		isNotNull();
		assertThat(actual.getCurrency()).isEqualTo(expectedCurrency);
		return this;
	}

	public AccountAssert hasAccountType(AccountType expectedType) {
		isNotNull();
		assertThat(actual.getAccountType()).isEqualTo(expectedType);
		return this;
	}
}
