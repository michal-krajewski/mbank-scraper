package pl.byteit.mbankscraper;

import pl.byteit.mbankscraper.operation.AuthenticationResult;
import pl.byteit.mbankscraper.operation.BankClient;
import pl.byteit.mbankscraper.operation.Credentials;
import pl.byteit.mbankscraper.util.UserInterface;

import static pl.byteit.mbankscraper.operation.AuthenticationResult.AuthenticationStatus.SECOND_FACTOR_AUTHENTICATION_REQUIRED;

public class Scraper {

	private final BankClient bankClient;
	private final UserInterface ui;

	public Scraper(BankClient bankClient, UserInterface userInterface) {
		this.bankClient = bankClient;
		this.ui = userInterface;
	}

	public void start() {
		Credentials credentials = new Credentials(ui.promptForInput("Username: "), ui.promptForInput("Password: "));
		AuthenticationResult authenticationResult = bankClient.login(credentials);
		if (requiresSecondFactorAuthentication(authenticationResult)) {
			ui.print("Waiting for 2FA. Check your device");
			AuthenticationResult secondFactorAuthenticationResult = bankClient.authenticateWithSecondFactor();
			verifyAuthenticationSuccedded(secondFactorAuthenticationResult);
		}
		ui.print(bankClient.getAccounts());
	}

	private static boolean requiresSecondFactorAuthentication(AuthenticationResult authenticationResult) {
		return authenticationResult.getAuthenticationStatus().equals(SECOND_FACTOR_AUTHENTICATION_REQUIRED);
	}

	private static void verifyAuthenticationSuccedded(AuthenticationResult secondFactorAuthenticationResult) {
		if (!secondFactorAuthenticationResult.isSuccessful()) {
			throw new IllegalStateException("Failed to authenticate with second factor");
		}
	}

}
