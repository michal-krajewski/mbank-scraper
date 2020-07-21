package pl.byteit.mbankscraper.operation.mbank;

import org.junit.jupiter.api.Test;
import pl.byteit.mbankscraper.operation.Account;
import pl.byteit.mbankscraper.operation.AuthenticationStatus;
import pl.byteit.mbankscraper.operation.mbank.data.LoginResponse;
import pl.byteit.mbankscraper.operation.mbank.data.SavingAccount;
import pl.byteit.mbankscraper.operation.mbank.data.StandardAccount;
import pl.byteit.mbankscraper.operation.mbank.data.StandardAccount.Balance;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.byteit.mbankscraper.operation.Account.AccountType.SAVING;
import static pl.byteit.mbankscraper.operation.Account.AccountType.STANDARD;
import static pl.byteit.mbankscraper.operation.AccountAssert.assertThatAccount;

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
	void shouldThrowIllegalStateExceptionWhenTryingToMapNotSuccessfulLoginResponse() {
		LoginResponse loginResponse = new LoginResponse(false, "/any");

		assertThrows(
				IllegalStateException.class,
				() -> ResponseMapper.asAuthenticationStatus(loginResponse)
		);
	}

	@Test
	void shouldMapStandardAccountToAccount() {
		String number = "12345";
		BigDecimal value = new BigDecimal("1333.66");
		String currency = "PLN";
		StandardAccount standardAccount = new StandardAccount(number, new Balance(value, currency));

		Account account = ResponseMapper.asAccount(standardAccount);

		assertThatAccount(account)
				.hasName(number)
				.hasBalance(value)
				.hasCurrency(currency)
				.hasAccountType(STANDARD);
	}

	@Test
	void shouldMapSavingAccountToAccount() {
		String name = "Saving";
		BigDecimal value = new BigDecimal("6663.33");
		String currency = "EUR";
		SavingAccount savingAccount = new SavingAccount(name, "1234", value);
		savingAccount.currency = currency;

		Account account = ResponseMapper.asAccount(savingAccount);

		assertThatAccount(account)
				.hasName(name)
				.hasBalance(value)
				.hasCurrency(currency)
				.hasAccountType(SAVING);
	}

}
