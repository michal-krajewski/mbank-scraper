package pl.byteit.scraper.operation;

import pl.byteit.scraper.ui.Printable;

import java.math.BigDecimal;
import java.util.Objects;

public class Account implements Printable {

	private final String name;
	private final BigDecimal balance;
	private final String currency;
	private final AccountType accountType;

	public Account(String name, BigDecimal balance, String currency, AccountType accountType) {
		this.name = name;
		this.balance = balance;
		this.currency = currency;
		this.accountType = accountType;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public String getCurrency() {
		return currency;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	@Override
	public String print() {
		return String.format("Account: %-36s Type: %-9s Balance: %s %s", name, accountType.name(), balance.toString(), currency);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Account))
			return false;
		Account that = (Account) o;
		return name.equals(that.name) &&
				balance.equals(that.balance) &&
				currency.equals(that.currency) &&
				accountType == that.accountType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, balance, currency, accountType);
	}

	public enum AccountType {
		STANDARD,
		COMPANY,
		SAVING
	}

	@Override
	public String toString() {
		return "Account{" +
				"name='" + name + '\'' +
				", balance=" + balance +
				", currency='" + currency + '\'' +
				", accountType=" + accountType +
				'}';
	}
}
