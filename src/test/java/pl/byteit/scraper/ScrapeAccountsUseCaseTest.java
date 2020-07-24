package pl.byteit.scraper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import pl.byteit.scraper.operation.Account;
import pl.byteit.scraper.operation.BankClient;
import pl.byteit.scraper.operation.ScrapeAccountsUseCase;
import pl.byteit.scraper.ui.UserInterface;

import java.math.BigDecimal;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static pl.byteit.scraper.operation.Account.AccountType.SAVING;
import static pl.byteit.scraper.operation.Account.AccountType.STANDARD;
import static pl.byteit.scraper.operation.AuthenticationStatus.AUTHENTICATED;
import static pl.byteit.scraper.operation.AuthenticationStatus.SECOND_FACTOR_AUTHENTICATION_REQUIRED;

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
	void shouldScrapeAccountsWithSecondFactorAuthenticationRequired() {
		when(userInterface.promptForInput(anyString())).thenReturn(LOGIN, PASSWORD);
		when(bankClient.login(LOGIN, PASSWORD)).thenReturn(SECOND_FACTOR_AUTHENTICATION_REQUIRED);
		when(bankClient.getAccounts()).thenReturn(asList(STANDARD_ACCOUNT, SAVING_ACCOUNT));

		scrapeAccountsUseCase.start();

		verify(userInterface).print("Waiting for 2FA. Check your device");
		verify(bankClient).authenticateWithSecondFactor();
		verify(userInterface).print(asList(STANDARD_ACCOUNT, SAVING_ACCOUNT));
	}

	@Test
	void shouldScrapeAccountsAfterSuccessfulLoginWithNoSecondFactorAuthentication() {
		when(userInterface.promptForInput(anyString())).thenReturn(LOGIN, PASSWORD);
		when(bankClient.login(LOGIN, PASSWORD)).thenReturn(AUTHENTICATED);
		when(bankClient.getAccounts()).thenReturn(asList(STANDARD_ACCOUNT, SAVING_ACCOUNT));

		scrapeAccountsUseCase.start();

		verify(userInterface).print(asList(STANDARD_ACCOUNT, SAVING_ACCOUNT));
	}

}
