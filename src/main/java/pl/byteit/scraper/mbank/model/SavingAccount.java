package pl.byteit.scraper.mbank.model;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;

public class SavingAccount {

	public final String name;
	public final String linkedAccountNumber;
	public final BigDecimal amount;

	@ConstructorProperties({ "fullName", "accountNumber", "currentGoalAmount" })
	public SavingAccount(String name, String linkedAccountNumber, BigDecimal amount) {
		this.name = name;
		this.amount = amount;
		this.linkedAccountNumber = linkedAccountNumber;
	}

}
