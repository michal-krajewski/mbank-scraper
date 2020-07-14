package pl.byteit.mbankscraper.operation.mbank.account;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class AccountInfoTestFactory {

	public static SavingAccountInfo savingAccountInfo(String name, String linkedAccountNumber, String money, String currency) {
		try {
			SavingAccountInfo savingAccountInfo = new SavingAccountInfo(name, linkedAccountNumber, new BigDecimal(money));

			Field currencyField = SavingAccountInfo.class.getDeclaredField("currency");
			currencyField.setAccessible(true);
			currencyField.set(savingAccountInfo, currency);

			return savingAccountInfo;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static StandardAccountInfo standardAccountInfo(String number, String money, String currency) {
		return new StandardAccountInfo(number, new StandardAccountInfo.Balance(new BigDecimal(money), currency));
	}
}
