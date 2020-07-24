package pl.byteit.scraper.operation;

import pl.byteit.scraper.ui.UserInterface;

import static pl.byteit.scraper.operation.AuthenticationStatus.SECOND_FACTOR_AUTHENTICATION_REQUIRED;

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
			bankClient.authenticateWithSecondFactor();
		}
		ui.print(bankClient.getAccounts());
	}

}
