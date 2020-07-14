package pl.byteit.mbankscraper.operation.mbank.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import pl.byteit.mbankscraper.util.Printable;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StandardAccountInfo implements Printable {

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

	@Override
	public String print() {
		return String.format("Number: %-36s Balance: %s", number, balance.print());
	}

	static class Balance implements Printable {

		private final BigDecimal value;
		private final String currency;

		@JsonCreator
		public Balance(@JsonProperty("value") BigDecimal value, @JsonProperty("currency") String currency) {
			this.value = value;
			this.currency = currency;
		}

		@Override
		public String print() {
			return value.toString() + " " + currency;
		}
	}
}
