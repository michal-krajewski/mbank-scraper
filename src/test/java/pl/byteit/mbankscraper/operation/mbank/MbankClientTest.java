package pl.byteit.mbankscraper.operation.mbank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import pl.byteit.mbankscraper.TestJsons.*;
import pl.byteit.mbankscraper.http.mock.HttpClientMock;
import pl.byteit.mbankscraper.operation.Account;
import pl.byteit.mbankscraper.operation.Account.AccountType;
import pl.byteit.mbankscraper.operation.AuthenticationStatus;
import pl.byteit.mbankscraper.operation.InvalidCredentialsException;
import pl.byteit.mbankscraper.operation.mbank.data.SecondFactorAuthenticationStatus;
import pl.byteit.mbankscraper.util.Await;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static pl.byteit.mbankscraper.TestJsons.*;
import static pl.byteit.mbankscraper.operation.Account.AccountType.SAVING;
import static pl.byteit.mbankscraper.operation.Account.AccountType.STANDARD;
import static pl.byteit.mbankscraper.operation.AuthenticationStatus.AUTHENTICATED;
import static pl.byteit.mbankscraper.operation.AuthenticationStatus.SECOND_FACTOR_AUTHENTICATION_REQUIRED;
import static pl.byteit.mbankscraper.operation.mbank.RequestUrls.*;

class MbankClientTest {

	private static final String SECOND_FACTOR_REQUIRED_REDIRECT_URL = "/authorization";
	private static final String SECOND_FACTOR_NOT_REQUIRED_REDIRECT_URL = "/dashboard";

	private static final String LOGIN = "login";
	private static final String PASSWORD = "passwd";
	private static final String TOKEN_VALUE = "token-value";
	private static final String AUTH_ID = "auth-id";
	private static final String TRAN_ID = "tran-id";

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
		mockClient.mockResponse(LOGIN_URL, loginResponse(true, SECOND_FACTOR_NOT_REQUIRED_REDIRECT_URL));

		AuthenticationStatus authenticationStatus = mbankClient.login(LOGIN, PASSWORD);

		assertThat(authenticationStatus).isEqualTo(AUTHENTICATED);
	}

	@Test
	void shouldReturnSecondFactorAuthenticationRequireStatusForValidCredentials() {
		mockClient.mockResponse(LOGIN_URL, loginResponse(true, SECOND_FACTOR_REQUIRED_REDIRECT_URL));

		AuthenticationStatus authenticationStatus = mbankClient.login(LOGIN, PASSWORD);

		assertThat(authenticationStatus).isEqualTo(SECOND_FACTOR_AUTHENTICATION_REQUIRED);
	}

	@Test
	void shouldThrowInvalidCredentialsExceptionWhenLoginFails() {
		mockClient.mockResponse(LOGIN_URL, loginResponse(false, SECOND_FACTOR_NOT_REQUIRED_REDIRECT_URL));

		assertThrows(
				InvalidCredentialsException.class,
				() -> mbankClient.login(LOGIN, PASSWORD)
		);
	}

	@Test
	void shouldFetchAccountData() {
		String standardAccountNumber = "11 2222 3333 4444 5555 6666 7777";
		String standardAccountBalance = "9280.55";
		String currency = "PLN";
		SavingAccountTestData savingAccount1 = new SavingAccountTestData(standardAccountNumber, "12333.66", "Saving1");
		SavingAccountTestData savingAccount2 = new SavingAccountTestData(standardAccountNumber, "2500.44", "Saving2");
		mockClient.mockResponse(GET_REQUEST_VERIFICATION_TOKEN_URL, antiForgeryToken(TOKEN_VALUE));
		mockClient.mockResponse(GET_STANDARD_ACCOUNTS_URL, standardAccounts(standardAccountNumber, standardAccountBalance, currency));
		mockClient.mockResponse(GET_SAVING_ACCOUNTS_URL, savingAccounts(savingAccount1, savingAccount2));

		List<Account> accounts = mbankClient.getAccounts();

		assertThat(accounts)
				.containsExactly(
						accountInfo(standardAccountNumber, standardAccountBalance, currency, STANDARD),
						accountInfo(savingAccount1.name, savingAccount1.currentAmount, currency, SAVING),
						accountInfo(savingAccount2.name, savingAccount2.currentAmount, currency, SAVING)
				);
	}

	@Nested
	class SecondFactorAuthenticationTest {

		@Test
		void shouldProperlyAuthenticateWithSecondFactor() {
			mockClient.mockResponse(GET_REQUEST_VERIFICATION_TOKEN_URL, antiForgeryToken(TOKEN_VALUE));
			mockClient.mockResponse(FETCH_AUTHENTICATION_ID_URL, authenticationIdentifier(AUTH_ID));
			mockClient.mockResponse(START_SECOND_FACTOR_AUTHENTICATION_URL, startAuthentication(TRAN_ID));
			when(awaitMock.forResult(Matchers.<Supplier<SecondFactorAuthenticationStatus>>any(), any(), eq(30), eq(2)))
					.thenReturn(new SecondFactorAuthenticationStatus("Authorized"));

			AuthenticationStatus authenticationStatus = mbankClient.authenticateWithSecondFactor();

			assertThat(authenticationStatus).isEqualTo(AUTHENTICATED);
		}

		@Test
		void shouldThrowAuthenticationExceptionWhenAuthenticationFailed() {
			mockClient.mockResponse(GET_REQUEST_VERIFICATION_TOKEN_URL, antiForgeryToken(TOKEN_VALUE));
			mockClient.mockResponse(FETCH_AUTHENTICATION_ID_URL, authenticationIdentifier(AUTH_ID));
			mockClient.mockResponse(START_SECOND_FACTOR_AUTHENTICATION_URL, startAuthentication(TRAN_ID));
			when(awaitMock.forResult(Matchers.<Supplier<SecondFactorAuthenticationStatus>>any(), any(), eq(30), eq(2)))
					.thenReturn(new SecondFactorAuthenticationStatus("Failed"));

			assertThrows(
					AuthenticationException.class,
					() -> mbankClient.authenticateWithSecondFactor()
			);

		}

		@Test
		void shouldThrowAuthenticationExceptionWhenExceededCheckingStatusAttempts() {
			mockClient.mockResponse(GET_REQUEST_VERIFICATION_TOKEN_URL, antiForgeryToken(TOKEN_VALUE));
			mockClient.mockResponse(FETCH_AUTHENTICATION_ID_URL, authenticationIdentifier(AUTH_ID));
			mockClient.mockResponse(START_SECOND_FACTOR_AUTHENTICATION_URL, startAuthentication(TRAN_ID));
			when(awaitMock.forResult(Matchers.<Supplier<SecondFactorAuthenticationStatus>>any(), any(), eq(30), eq(2)))
					.thenReturn(new SecondFactorAuthenticationStatus("Prepared"));

			assertThrows(
					AuthenticationException.class,
					() -> mbankClient.authenticateWithSecondFactor()
			);
		}

	}

	private static Account accountInfo(String name, String balance, String currency, AccountType type) {
		return new Account(name, new BigDecimal(balance), currency, type);
	}

}
