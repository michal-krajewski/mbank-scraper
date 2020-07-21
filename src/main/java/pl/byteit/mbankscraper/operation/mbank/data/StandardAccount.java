package pl.byteit.mbankscraper.operation.mbank.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StandardAccount {

	public final String number;
	public final Balance balance;

	@ConstructorProperties({ "number", "balance" })
	public StandardAccount(String number, Balance balance) {
		this.number = number;
		this.balance = balance;
	}

	public static class Balance {

		public final BigDecimal value;
		public final String currency;

		@ConstructorProperties({ "value", "currency" })
		public Balance(BigDecimal value, String currency) {
			this.value = value;
			this.currency = currency;
		}

	}
}
