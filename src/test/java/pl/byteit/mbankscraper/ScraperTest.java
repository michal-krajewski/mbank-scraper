package pl.byteit.mbankscraper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import pl.byteit.mbankscraper.operation.AccountInfo;
import pl.byteit.mbankscraper.operation.BankClient;
import pl.byteit.mbankscraper.operation.Credentials;
import pl.byteit.mbankscraper.util.UserInterface;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static pl.byteit.mbankscraper.operation.AccountInfo.AccountType.SAVING;
import static pl.byteit.mbankscraper.operation.AccountInfo.AccountType.STANDARD;
import static pl.byteit.mbankscraper.operation.AuthenticationResult.mobileSecondFactorAuthenticationRequired;
import static pl.byteit.mbankscraper.operation.AuthenticationResult.successful;

class ScraperTest {

	private static final AccountInfo STANDARD_ACCOUNT = new AccountInfo("123456", new BigDecimal("1245.45"), "PLN", STANDARD);
	private static final AccountInfo SAVING_ACCOUNT = new AccountInfo("Savings", new BigDecimal("1666.00"), "EUR", SAVING);
	private static final char[] LOGIN = "login".toCharArray();
	private static final char[] PASSWORD = "password".toCharArray();

	@Captor
	ArgumentCaptor<List<AccountInfo>> scrappedAccounts;

	@Mock
	private BankClient bankClient;

	@Mock
	private UserInterface userInterface;

	private Scraper scraper;

	@BeforeEach
	void beforeEachScraperTest() {
		initMocks(this);
		scraper = new Scraper(bankClient, userInterface);
	}

	@Test
	void shouldScrapeBankDataWithSecondFactorAuthenticationRequired() {
		when(bankClient.login(any(Credentials.class))).thenReturn(mobileSecondFactorAuthenticationRequired());
		when(bankClient.authenticateWithSecondFactor()).thenReturn(successful());
		when(bankClient.getAccounts()).thenReturn(asList(STANDARD_ACCOUNT, SAVING_ACCOUNT));
		when(userInterface.promptForInput(anyString())).thenReturn(LOGIN, PASSWORD);

		scraper.start();

		verify(bankClient).login(any(Credentials.class));
		verify(bankClient).authenticateWithSecondFactor();
		verify(bankClient).getAccounts();
		verify(userInterface, times(2)).promptForInput(anyString());
		verify(userInterface).print(eq("Waiting for 2FA. Check your device"));
		verify(userInterface).print(scrappedAccounts.capture());
		assertThat(scrappedAccounts.getValue())
				.containsExactly(STANDARD_ACCOUNT, SAVING_ACCOUNT);
	}

	@Test
	void shouldScrapeBankDataAfterSuccessfulLoginWithNoSecondFactorAuthentication() {
		when(bankClient.login(any(Credentials.class))).thenReturn(successful());
		when(bankClient.getAccounts()).thenReturn(asList(STANDARD_ACCOUNT, SAVING_ACCOUNT));
		when(userInterface.promptForInput(anyString())).thenReturn(LOGIN, PASSWORD);

		scraper.start();

		verify(bankClient).login(any(Credentials.class));
		verify(bankClient, never()).authenticateWithSecondFactor();
		verify(bankClient).getAccounts();
		verify(userInterface, times(2)).promptForInput(anyString());
		verify(userInterface, never()).print(eq("Waiting for 2FA. Check your device"));
		verify(userInterface).print(scrappedAccounts.capture());
		assertThat(scrappedAccounts.getValue())
				.containsExactly(STANDARD_ACCOUNT, SAVING_ACCOUNT);
	}

	@Test
	void shouldThrowIllegalStateExceptionWhenSecondFactorAuthenticationResultIsNotSuccessful() {
		when(bankClient.login(any(Credentials.class))).thenReturn(mobileSecondFactorAuthenticationRequired());
		when(userInterface.promptForInput(anyString())).thenReturn(LOGIN, PASSWORD);
		when(bankClient.authenticateWithSecondFactor()).thenReturn(mobileSecondFactorAuthenticationRequired());

		assertThrows(
				IllegalStateException.class,
				() -> scraper.start()
		);

		verify(bankClient).login(any(Credentials.class));
		verify(bankClient).authenticateWithSecondFactor();
		verify(bankClient, never()).getAccounts();
		verify(userInterface, times(2)).promptForInput(anyString());
		verify(userInterface).print(eq("Waiting for 2FA. Check your device"));
	}

}
