package pl.byteit.mbankscraper.operation.mbank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import pl.byteit.mbankscraper.http.mock.HttpClientMock;
import pl.byteit.mbankscraper.operation.AccountInfo;
import pl.byteit.mbankscraper.operation.AccountInfo.AccountType;
import pl.byteit.mbankscraper.operation.AuthenticationResult;
import pl.byteit.mbankscraper.operation.Credentials;
import pl.byteit.mbankscraper.operation.InvalidCredentialsException;
import pl.byteit.mbankscraper.operation.mbank.authentication.LoginResponse;
import pl.byteit.mbankscraper.operation.mbank.authentication.SecondFactorAuthenticationManager;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.MockitoAnnotations.initMocks;
import static pl.byteit.mbankscraper.ResourcesUtil.loadFileFromResourcesAsString;
import static pl.byteit.mbankscraper.operation.AccountInfo.AccountType.SAVING;
import static pl.byteit.mbankscraper.operation.AccountInfo.AccountType.STANDARD;
import static pl.byteit.mbankscraper.operation.AuthenticationResult.AuthenticationStatus.SECOND_FACTOR_AUTHENTICATION_REQUIRED;
import static pl.byteit.mbankscraper.operation.AuthenticationResult.AuthenticationStatus.SUCCESSFUL;
import static pl.byteit.mbankscraper.operation.AuthenticationResult.SecondFactorAuthenticationType.MOBILE_APP;
import static pl.byteit.mbankscraper.operation.mbank.Requests.*;
import static pl.byteit.mbankscraper.util.JsonParser.asJson;

class MbankClientTest {

	private static final String SECOND_FACTOR_REQUIRED_REDIRECT_URL = "/authorization";
	private static final String SECOND_FACTOR_NOT_REQUIRED_REDIRECT_URL = "/dashboard";

	private static final Credentials CREDENTIALS = new Credentials("test".toCharArray(), "passwd".toCharArray());
	private static final RequestVerificationToken TOKEN = new RequestVerificationToken("token-value");

	@Mock
	private SecondFactorAuthenticationManager secondFactorAuthenticationManager;

	private HttpClientMock mockClient;
	private MbankClient operationManager;

	@BeforeEach
	void beforeEachMbankOperationManagerTest() {
		initMocks(this);
		mockClient = new HttpClientMock();
		operationManager = new MbankClient(mockClient, secondFactorAuthenticationManager);
	}

	@Test
	void shouldReturnSuccessfulStatusForValidCredentialsAndNoSecondFactorAuthenticationRequired() {
		mockClient.mockResponse(LOGIN_URL, asJson(new LoginResponse(true, SECOND_FACTOR_NOT_REQUIRED_REDIRECT_URL)));

		AuthenticationResult result = operationManager.login(CREDENTIALS);

		mockClient.verify(LOGIN_URL)
				.hasPostMethod()
				.hasBodyDefined()
				.hasNoCustomPreprocessorDefined()
				.wasPerformed();
		assertThat(result.getAuthenticationStatus()).isEqualTo(SUCCESSFUL);
	}

	@Test
	void shouldReturnSecondFactorAuthenticationRequiredWithMobileAppResultForValidCredentials() {
		mockClient.mockResponse(LOGIN_URL, asJson(new LoginResponse(true, SECOND_FACTOR_REQUIRED_REDIRECT_URL)));

		AuthenticationResult result = operationManager.login(CREDENTIALS);

		mockClient.verify(LOGIN_URL)
				.hasPostMethod()
				.hasBodyDefined()
				.hasNoCustomPreprocessorDefined()
				.wasPerformed();
		assertThat(result.getAuthenticationStatus())
				.isEqualTo(SECOND_FACTOR_AUTHENTICATION_REQUIRED);
		assertThat(result.getSecondFactorAuthenticationType())
				.hasValueSatisfying(type -> assertThat(type).isEqualTo(MOBILE_APP));
	}

	@Test
	void shouldThrowInvalidCredentialsExceptionWhenLoginFails() {
		mockClient.mockResponse(LOGIN_URL, asJson(new LoginResponse(false, SECOND_FACTOR_NOT_REQUIRED_REDIRECT_URL)));

		assertThrows(
				InvalidCredentialsException.class,
				() -> operationManager.login(CREDENTIALS)
		);
	}

	@Test
	void shouldFetchAccountData() {
		mockClient.mockResponse(GET_REQUEST_VERIFICATION_TOKEN_URL, loadFileFromResourcesAsString("anti-forgery-token.json"));
		mockClient.mockResponse(GET_STANDARD_ACCOUNTS_URL, loadFileFromResourcesAsString("standard-accounts.json"));
		mockClient.mockResponse(GET_SAVING_ACCOUNTS_URL, loadFileFromResourcesAsString("saving-accounts.json"));

		List<AccountInfo> accounts = operationManager.getAccounts();

		mockClient.verify(GET_REQUEST_VERIFICATION_TOKEN_URL);
		mockClient.verify(GET_STANDARD_ACCOUNTS_URL)
				.hasPostMethod()
				.hasNoBodyDefined()
				.hasCustomPreprocessorDefined()
				.hasHeaders(TOKEN.asHeader())
				.wasPerformed();
		mockClient.verify(GET_SAVING_ACCOUNTS_URL)
				.hasGetMethod()
				.hasNoBodyDefined()
				.hasCustomPreprocessorDefined()
				.hasHeaders(TOKEN.asHeader())
				.wasPerformed();
		assertThat(accounts)
				.containsExactly(
						accountInfo("11 2222 3333 4444 5555 6666 7777", "9280.55", "PLN", STANDARD),
						accountInfo("Saving1", "12333.66", "PLN", SAVING),
						accountInfo("Saving2", "2500.00", "PLN", SAVING)
				);
	}

	private static AccountInfo accountInfo(String name, String balance, String currency, AccountType type) {
		return new AccountInfo(name, new BigDecimal(balance), currency, type);
	}

}
