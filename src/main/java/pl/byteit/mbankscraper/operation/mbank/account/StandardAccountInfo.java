package pl.byteit.mbankscraper.operation.mbank.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import pl.byteit.mbankscraper.operation.AccountInfo;

import java.math.BigDecimal;

import static pl.byteit.mbankscraper.operation.AccountInfo.AccountType.STANDARD;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StandardAccountInfo {

	private final String number;
	private final Balance balance;

	@JsonCreator
	public StandardAccountInfo(@JsonProperty("number") String number, @JsonProperty("balance") Balance balance) {
		this.number = number;
		this.balance = balance;
	}

	public String getNumber() {
		return number;
	}

	String getCurrency() {
		return balance.currency;
	}

	BigDecimal getBalance() {
		return balance.value;
	}

	public AccountInfo toAccountInfo() {
		return new AccountInfo(number, balance.value, balance.currency, STANDARD);
	}

	static class Balance {

		private final BigDecimal value;
		private final String currency;

		@JsonCreator
		public Balance(@JsonProperty("value") BigDecimal value, @JsonProperty("currency") String currency) {
			this.value = value;
			this.currency = currency;
		}

	}
}
