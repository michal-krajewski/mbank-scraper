package pl.byteit.mbankscraper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import pl.byteit.mbankscraper.operation.Account;
import pl.byteit.mbankscraper.operation.BankClient;
import pl.byteit.mbankscraper.ui.UserInterface;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static pl.byteit.mbankscraper.operation.Account.AccountType.SAVING;
import static pl.byteit.mbankscraper.operation.Account.AccountType.STANDARD;
import static pl.byteit.mbankscraper.operation.AuthenticationStatus.AUTHENTICATED;
import static pl.byteit.mbankscraper.operation.AuthenticationStatus.SECOND_FACTOR_AUTHENTICATION_REQUIRED;

class ScrapeAccountsUseCaseTest {

	private static final Account STANDARD_ACCOUNT = new Account("123456", new BigDecimal("1245.45"), "PLN", STANDARD);
	private static final Account SAVING_ACCOUNT = new Account("Savings", new BigDecimal("1666.00"), "EUR", SAVING);
	private static final String LOGIN = "login";
	private static final String PASSWORD = "password";

	@Mock
	private BankClient bankClient;

	@Mock
	private UserInterface userInterface;

	private ScrapeAccountsUseCase scrapeAccountsUseCase;

	@BeforeEach
	void beforeEachScraperTest() {
		initMocks(this);
		scrapeAccountsUseCase = new ScrapeAccountsUseCase(bankClient, userInterface);
	}

	@Test
	void shouldScrapeBankDataWithSecondFactorAuthenticationRequired() {
		when(userInterface.promptForInput(anyString())).thenReturn(LOGIN, PASSWORD);
		when(bankClient.login(LOGIN, PASSWORD)).thenReturn(SECOND_FACTOR_AUTHENTICATION_REQUIRED);
		when(bankClient.authenticateWithSecondFactor()).thenReturn(AUTHENTICATED);
		when(bankClient.getAccounts()).thenReturn(asList(STANDARD_ACCOUNT, SAVING_ACCOUNT));

		scrapeAccountsUseCase.start();

		verify(userInterface).print("Waiting for 2FA. Check your device");
		ArgumentCaptor<List<Account>> scrappedAccounts = listArgumentCaptor();
		verify(userInterface).print(scrappedAccounts.capture());
		assertThat(scrappedAccounts.getValue())
				.containsExactly(STANDARD_ACCOUNT, SAVING_ACCOUNT);
	}

	@Test
	void shouldScrapeBankDataAfterSuccessfulLoginWithNoSecondFactorAuthentication() {
		when(userInterface.promptForInput(anyString())).thenReturn(LOGIN, PASSWORD);
		when(bankClient.login(LOGIN, PASSWORD)).thenReturn(AUTHENTICATED);
		when(bankClient.getAccounts()).thenReturn(asList(STANDARD_ACCOUNT, SAVING_ACCOUNT));

		scrapeAccountsUseCase.start();

		ArgumentCaptor<List<Account>> scrappedAccounts = listArgumentCaptor();
		verify(userInterface).print(scrappedAccounts.capture());
		assertThat(scrappedAccounts.getValue())
				.containsExactly(STANDARD_ACCOUNT, SAVING_ACCOUNT);
	}

	@Test
	void shouldThrowIllegalStateExceptionWhenSecondFactorAuthenticationResultIsNotSuccessful() {
		when(userInterface.promptForInput(anyString())).thenReturn(LOGIN, PASSWORD);
		when(bankClient.login(LOGIN, PASSWORD)).thenReturn(SECOND_FACTOR_AUTHENTICATION_REQUIRED);
		when(bankClient.authenticateWithSecondFactor()).thenReturn(SECOND_FACTOR_AUTHENTICATION_REQUIRED);

		assertThrows(
				IllegalStateException.class,
				() -> scrapeAccountsUseCase.start()
		);
		verify(userInterface).print("Waiting for 2FA. Check your device");
	}

	@SuppressWarnings("all")
	private static ArgumentCaptor listArgumentCaptor() {
		return ArgumentCaptor.forClass((Class) List.class);
	}

}
