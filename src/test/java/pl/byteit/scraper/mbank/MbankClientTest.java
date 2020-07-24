package pl.byteit.scraper.mbank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import pl.byteit.scraper.TestResponses.*;
import pl.byteit.scraper.http.mock.HttpClientMock;
import pl.byteit.scraper.mbank.model.SecondFactorAuthenticationStatus;
import pl.byteit.scraper.operation.Account;
import pl.byteit.scraper.operation.Account.AccountType;
import pl.byteit.scraper.operation.AuthenticationStatus;
import pl.byteit.scraper.operation.exception.AuthenticationFailed;
import pl.byteit.scraper.operation.exception.InvalidCredentials;
import pl.byteit.scraper.util.Await;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static pl.byteit.scraper.TestResponses.*;
import static pl.byteit.scraper.mbank.RequestUrls.*;
import static pl.byteit.scraper.operation.Account.AccountType.SAVING;
import static pl.byteit.scraper.operation.Account.AccountType.STANDARD;
import static pl.byteit.scraper.operation.AuthenticationStatus.AUTHENTICATED;
import static pl.byteit.scraper.operation.AuthenticationStatus.SECOND_FACTOR_AUTHENTICATION_REQUIRED;

class MbankClientTest {

	@Mock
	private Await awaitMock;

	private HttpClientMock mockClient;
	private MbankClient mbankClient;

	@BeforeEach
	void beforeEachMbankOperationManagerTest() {
		initMocks(this);
		mockClient = new HttpClientMock();
		mbankClient = new MbankClient(mockClient, awaitMock);
	}

	@Test
	void shouldReturnSuccessfulStatusForValidCredentialsAndNoSecondFactorAuthenticationRequired() {
		mockClient.mockResponse(LOGIN_URL, successfulLoginWithNoSecondFactorRequired());

		AuthenticationStatus authenticationStatus = mbankClient.login("login", "passwd");

		assertThat(authenticationStatus).isEqualTo(AUTHENTICATED);
	}

	@Test
	void shouldReturnSecondFactorAuthenticationRequireStatusForValidCredentials() {
		mockClient.mockResponse(LOGIN_URL, successfulLoginWithSecondFactorRequired());

		AuthenticationStatus authenticationStatus = mbankClient.login("login", "passwd");

		assertThat(authenticationStatus).isEqualTo(SECOND_FACTOR_AUTHENTICATION_REQUIRED);
	}

	@Test
	void shouldThrowInvalidCredentialsExceptionWhenLoginFails() {
		mockClient.mockResponse(LOGIN_URL, loginFailedResponse());

		assertThrows(
				InvalidCredentials.class,
				() -> mbankClient.login("login", "passwd")
		);
	}

	@Test
	void shouldFetchAccountData() {
		String standardAccountNumber = "11 2222 3333 4444 5555 6666 7777";
		String standardAccountBalance = "9280.55";
		String currency = "PLN";
		SavingAccountTestData savingAccount1 = new SavingAccountTestData(standardAccountNumber, "12333.66", "Saving1");
		SavingAccountTestData savingAccount2 = new SavingAccountTestData(standardAccountNumber, "2500.44", "Saving2");
		mockClient.mockResponse(FETCH_REQUEST_VERIFICATION_TOKEN_URL, antiForgeryToken());
		mockClient.mockResponse(FETCH_STANDARD_ACCOUNTS_URL, standardAccounts(standardAccountNumber, standardAccountBalance, currency));
		mockClient.mockResponse(FETCH_SAVING_ACCOUNTS_URL, savingAccounts(savingAccount1, savingAccount2));

		List<Account> accounts = mbankClient.getAccounts();

		assertThat(accounts)
				.containsExactly(
						account(standardAccountNumber, standardAccountBalance, currency, STANDARD),
						account(savingAccount1.name, savingAccount1.currentAmount, currency, SAVING),
						account(savingAccount2.name, savingAccount2.currentAmount, currency, SAVING)
				);
	}

	@Nested
	class SecondFactorAuthenticationTest {

		@Test
		void shouldAuthenticateWithSecondFactor() {
			mockClient.mockResponse(FETCH_REQUEST_VERIFICATION_TOKEN_URL, antiForgeryToken());
			mockClient.mockResponse(FETCH_AUTHENTICATION_ID_URL, authenticationIdentifier());
			mockClient.mockResponse(START_SECOND_FACTOR_AUTHENTICATION_URL, startAuthentication());
			when(awaitMock.forResult(Matchers.<Supplier<SecondFactorAuthenticationStatus>>any(), any(), eq(30), eq(2)))
					.thenReturn(new SecondFactorAuthenticationStatus("Authorized"));

			mbankClient.authenticateWithSecondFactor();

			mockClient.verifyRequest(1, FINALIZE_AUTHENTICATION_URL, "POST");
		}

		@Test
		void shouldThrowAuthenticationExceptionWhenAuthenticationFailed() {
			mockClient.mockResponse(FETCH_REQUEST_VERIFICATION_TOKEN_URL, antiForgeryToken());
			mockClient.mockResponse(FETCH_AUTHENTICATION_ID_URL, authenticationIdentifier());
			mockClient.mockResponse(START_SECOND_FACTOR_AUTHENTICATION_URL, startAuthentication());
			when(awaitMock.forResult(Matchers.<Supplier<SecondFactorAuthenticationStatus>>any(), any(), eq(30), eq(2)))
					.thenReturn(new SecondFactorAuthenticationStatus("Failed"));

			assertThrows(
					AuthenticationFailed.class,
					() -> mbankClient.authenticateWithSecondFactor()
			);
		}

		@Test
		void shouldThrowAuthenticationExceptionWhenExceededCheckingStatusAttempts() {
			mockClient.mockResponse(FETCH_REQUEST_VERIFICATION_TOKEN_URL, antiForgeryToken());
			mockClient.mockResponse(FETCH_AUTHENTICATION_ID_URL, authenticationIdentifier());
			mockClient.mockResponse(START_SECOND_FACTOR_AUTHENTICATION_URL, startAuthentication());
			when(awaitMock.forResult(Matchers.<Supplier<SecondFactorAuthenticationStatus>>any(), any(), eq(30), eq(2)))
					.thenReturn(new SecondFactorAuthenticationStatus("Prepared"));

			assertThrows(
					AuthenticationFailed.class,
					() -> mbankClient.authenticateWithSecondFactor()
			);
		}

	}

	private static Account account(String name, String balance, String currency, AccountType type) {
		return new Account(name, new BigDecimal(balance), currency, type);
	}

}
