package pl.byteit.mbankscraper.operation;

import pl.byteit.mbankscraper.util.Printable;

import java.math.BigDecimal;
import java.util.Objects;

public class AccountInfo implements Printable {

	private final String name;
	private final BigDecimal balance;
	private final String currency;
	private final AccountType accountType;

	public AccountInfo(String name, BigDecimal balance, String currency, AccountType accountType) {
		this.name = name;
		this.balance = balance;
		this.currency = currency;
		this.accountType = accountType;
	}

	@Override
	public String print() {
		return String.format("Account: %-36s Type: %-9s Balance: %s %s", name, accountType.name(), balance.toString(), currency);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof AccountInfo))
			return false;
		AccountInfo that = (AccountInfo) o;
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
}
