package pl.byteit.mbankscraper;

import pl.byteit.mbankscraper.operation.AuthenticationStatus;
import pl.byteit.mbankscraper.operation.BankClient;
import pl.byteit.mbankscraper.ui.UserInterface;

import static pl.byteit.mbankscraper.operation.AuthenticationStatus.AUTHENTICATED;
import static pl.byteit.mbankscraper.operation.AuthenticationStatus.SECOND_FACTOR_AUTHENTICATION_REQUIRED;

public class ScrapeAccountsUseCase {

	private final BankClient bankClient;
	private final UserInterface ui;

	public ScrapeAccountsUseCase(BankClient bankClient, UserInterface userInterface) {
		this.bankClient = bankClient;
		this.ui = userInterface;
	}

	public void start() {
		String login = ui.promptForInput("Username: ");
		String password = ui.promptForInput("Password: ");
		AuthenticationStatus authenticationStatus = bankClient.login(login, password);
		if (authenticationStatus.equals(SECOND_FACTOR_AUTHENTICATION_REQUIRED)) {
			ui.print("Waiting for 2FA. Check your device");
			AuthenticationStatus secondFactorAuthenticationStatus = bankClient.authenticateWithSecondFactor();
			verifyAuthenticationSuccedded(secondFactorAuthenticationStatus);
		}
		ui.print(bankClient.getAccounts());
	}

	private static void verifyAuthenticationSuccedded(AuthenticationStatus authenticationStatus) {
		if (!authenticationStatus.equals(AUTHENTICATED)) {
			throw new IllegalStateException("Failed to authenticate with second factor");
		}
	}

}
