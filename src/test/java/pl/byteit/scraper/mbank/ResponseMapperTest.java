package pl.byteit.scraper.mbank;

import org.junit.jupiter.api.Test;
import pl.byteit.scraper.mbank.model.LoginResponse;
import pl.byteit.scraper.mbank.model.SavingAccount;
import pl.byteit.scraper.mbank.model.StandardAccount;
import pl.byteit.scraper.mbank.model.StandardAccount.Balance;
import pl.byteit.scraper.operation.Account;
import pl.byteit.scraper.operation.AuthenticationStatus;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.byteit.scraper.operation.Account.AccountType.SAVING;
import static pl.byteit.scraper.operation.Account.AccountType.STANDARD;

class ResponseMapperTest {

	@Test
	void shouldMapLoginResponseToAuthenticatedStatus() {
		String authenticatedRedirectUrl = "/dashboard";
		LoginResponse loginResponse = new LoginResponse(true, authenticatedRedirectUrl);

		AuthenticationStatus authenticationStatus = ResponseMapper.asAuthenticationStatus(loginResponse);

		assertThat(authenticationStatus).isEqualTo(AuthenticationStatus.AUTHENTICATED);
	}

	@Test
	void shouldMapLoginResponseToSecondFactorAuthenticationRequired() {
		String secondFactorAuthRequiredRedirectUrl = "/authorization";
		LoginResponse loginResponse = new LoginResponse(true, secondFactorAuthRequiredRedirectUrl);

		AuthenticationStatus authenticationStatus = ResponseMapper.asAuthenticationStatus(loginResponse);

		assertThat(authenticationStatus).isEqualTo(AuthenticationStatus.SECOND_FACTOR_AUTHENTICATION_REQUIRED);
	}

	@Test
	void shouldMapStandardAccountToAccount() {
		String number = "12345";
		BigDecimal value = new BigDecimal("1333.66");
		String currency = "PLN";
		StandardAccount standardAccount = new StandardAccount(number, new Balance(value, currency));

		Account mappedAccount = ResponseMapper.asAccount(standardAccount);

		assertThat(mappedAccount).isEqualTo(new Account(number, value, currency, STANDARD));
	}

	@Test
	void shouldMapSavingAccountToAccount() {
		String name = "Saving";
		BigDecimal value = new BigDecimal("6663.33");
		String currency = "EUR";
		String linkedAccountNumber = "1234";
		SavingAccount savingAccount = new SavingAccount(name, linkedAccountNumber, value);
		List<StandardAccount> availableAccounts = asList(
				new StandardAccount("987654321", new Balance(new BigDecimal("100.00"), "USD")),
				new StandardAccount(linkedAccountNumber, new Balance(new BigDecimal("123.45"), currency))
		);

		Account mappedAccount = ResponseMapper.asAccount(savingAccount, availableAccounts);

		assertThat(mappedAccount).isEqualTo(new Account(name, value, currency, SAVING));
	}

}
