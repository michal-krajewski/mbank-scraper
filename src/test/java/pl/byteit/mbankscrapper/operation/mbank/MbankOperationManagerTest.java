package pl.byteit.mbankscrapper.operation.mbank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import pl.byteit.mbankscrapper.http.mock.HttpClientMock;
import pl.byteit.mbankscrapper.operation.Credentials;
import pl.byteit.mbankscrapper.operation.InvalidCredentialsException;
import pl.byteit.mbankscrapper.operation.mbank.account.SavingAccountInfo;
import pl.byteit.mbankscrapper.operation.mbank.account.StandardAccountInfo;
import pl.byteit.mbankscrapper.operation.mbank.authentication.LoginResponse;
import pl.byteit.mbankscrapper.operation.mbank.authentication.SecondFactorAuthenticationManager;
import pl.byteit.mbankscrapper.util.CommandLineInterface;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static pl.byteit.mbankscrapper.ResourcesUtil.loadFileFromResourcesAsString;
import static pl.byteit.mbankscrapper.operation.mbank.MbankOperationManager.*;
import static pl.byteit.mbankscrapper.operation.mbank.account.AccountInfoTestFactory.savingAccountInfo;
import static pl.byteit.mbankscrapper.operation.mbank.account.AccountInfoTestFactory.standardAccountInfo;
import static pl.byteit.mbankscrapper.util.JsonParser.asJson;

class MbankOperationManagerTest {

	private static final Credentials CREDENTIALS = new Credentials("test".toCharArray(), "passwd".toCharArray());
	private static final RequestVerificationToken TOKEN = new RequestVerificationToken("token-value");

	@Mock
	private SecondFactorAuthenticationManager secondFactorAuthenticationManager;

	private HttpClientMock mockClient;
	private MbankOperationManager operationManager;
	private List<String> mockPrinter;

	@BeforeEach
	void beforeEachMbankOperationManagerTest() {
		initMocks(this);
		mockPrinter = new ArrayList<>();
		mockClient = new HttpClientMock();
		CommandLineInterface cli = new CommandLineInterface(() -> "", msg -> mockPrinter.add(msg));
		operationManager = new MbankOperationManager(mockClient, cli, secondFactorAuthenticationManager);
	}

	@Test
	void shouldLoginWithCorrectCredentials() {
		mockClient.mockResponse(LOGIN_URL, asJson(new LoginResponse(true)));
		mockClient.mockResponse(GET_REQUEST_VERIFICATION_TOKEN_URL, loadFileFromResourcesAsString("anti-forgery-token.json"));

		operationManager.login(CREDENTIALS);

		mockClient.verify(LOGIN_URL)
				.hasPostMethod()
				.hasBodyDefined()
				.hasNoCustomPreprocessorDefined()
				.wasPerformed();
		mockClient.verify(GET_REQUEST_VERIFICATION_TOKEN_URL)
				.hasGetMethod()
				.hasNoBodyDefined()
				.wasPerformed();
		ArgumentCaptor<RequestVerificationToken> argument = ArgumentCaptor.forClass(RequestVerificationToken.class);
		verify(secondFactorAuthenticationManager).authenticate(argument.capture());
		assertThat(argument.getValue()).isEqualTo(TOKEN);
	}

	@Test
	void shouldThrowInvalidCredentialsExceptionWhenLoginFails() {
		mockClient.mockResponse(LOGIN_URL, asJson(new LoginResponse(false)));

		assertThrows(
				InvalidCredentialsException.class,
				() -> operationManager.login(CREDENTIALS)
		);
		verify(secondFactorAuthenticationManager, never()).authenticate(any());
	}

	@Test
	void shouldFetchAccountData() {
		mockClient.mockResponse(GET_REQUEST_VERIFICATION_TOKEN_URL, loadFileFromResourcesAsString("anti-forgery-token.json"));
		mockClient.mockResponse(GET_STANDARD_ACCOUNTS_URL, loadFileFromResourcesAsString("standard-accounts.json"));
		mockClient.mockResponse(GET_SAVING_ACCOUNTS_URL, loadFileFromResourcesAsString("saving-accounts.json"));

		operationManager.getAccounts();

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
		assertThat(mockPrinter)
				.containsExactly(
						"Standard accounts:",
						standardAccount().print(),
						"Saving accounts:",
						linkedSavingAccounts().get(0).print(),
						linkedSavingAccounts().get(1).print()
				);
	}

	private static StandardAccountInfo standardAccount() {
		return standardAccountInfo("11 2222 3333 4444 5555 6666 7777", "9280.55", "PLN");
	}

	private static List<SavingAccountInfo> linkedSavingAccounts() {
		return asList(
				savingAccountInfo("Saving1", "11 2222 3333 4444 5555 6666 7777", "12333.66", "PLN"),
				savingAccountInfo("Saving2", "11 2222 3333 4444 5555 6666 7777", "2500.00", "PLN")
		);
	}

}
