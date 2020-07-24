package pl.byteit.scraper.mbank.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SavingAccount {

	public final String name;
	public final String linkedAccountNumber;
	public final BigDecimal amount;
	public String currency;

	@ConstructorProperties({ "fullName", "accountNumber", "currentGoalAmount" })
	public SavingAccount(String name, String linkedAccountNumber, BigDecimal amount) {
		this.name = name;
		this.amount = amount;
		this.linkedAccountNumber = linkedAccountNumber;
	}

}
