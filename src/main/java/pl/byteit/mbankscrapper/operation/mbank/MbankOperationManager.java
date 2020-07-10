package pl.byteit.mbankscrapper.operation.mbank;

import pl.byteit.mbankscrapper.http.HttpClient;
import pl.byteit.mbankscrapper.operation.BankOperationManager;
import pl.byteit.mbankscrapper.operation.Credentials;
import pl.byteit.mbankscrapper.operation.InvalidCredentialsException;
import pl.byteit.mbankscrapper.operation.mbank.account.SavingAccountInfo;
import pl.byteit.mbankscrapper.operation.mbank.account.StandardAccountInfo;
import pl.byteit.mbankscrapper.operation.mbank.authentication.LoginResponse;
import pl.byteit.mbankscrapper.operation.mbank.authentication.SecondFactorAuthenticationManager;
import pl.byteit.mbankscrapper.util.CommandLineInterface;

import java.util.List;

import static pl.byteit.mbankscrapper.util.JsonParser.getFieldRawValueAsString;
import static pl.byteit.mbankscrapper.util.TypeUtil.listTypeOf;

public class MbankOperationManager implements BankOperationManager {

	private static final String GET_STANDARD_ACCOUNTS_URL = "https://online.mbank.pl/pl/MyDesktop/Dashboard/GetProducts";
	private static final String LOGIN_URL = "https://online.mbank.pl/pl/LoginMain/Account/JsonLogin";
	private static final String GET_REQUEST_VERIFICATION_TOKEN_URL = "https://online.mbank.pl/pl/setup/data";
	private static final String GET_SAVING_ACCOUNTS_URL = "https://online.mbank.pl/pl/SavingGoals/Home/GetSavingProducts";

	private final HttpClient httpClient;
	private final CommandLineInterface cli;
	private final SecondFactorAuthenticationManager secondFactorAuthenticationManager;

	public MbankOperationManager(
			HttpClient httpClient,
			CommandLineInterface cli,
			SecondFactorAuthenticationManager secondFactorAuthenticationManager) {

		this.httpClient = httpClient;
		this.cli = cli;
		this.secondFactorAuthenticationManager = secondFactorAuthenticationManager;
	}

	@Override
	public void login(Credentials credentials) {
		LoginResponse loginResponse = performLogin(credentials);

		if (!loginResponse.isSuccessful()) {
			throw new InvalidCredentialsException();
		}

		RequestVerificationToken verificationToken = getRequestVerificationToken();

		secondFactorAuthenticationManager.authenticate(verificationToken);
	}

	@Override
	public void getAccounts() {
		RequestVerificationToken verificationToken = getRequestVerificationToken();
		List<StandardAccountInfo> standardAccounts = getStandardAccounts(verificationToken);
		List<SavingAccountInfo> savingAccounts = getSavingAccounts(verificationToken);
		savingAccounts.forEach(savingAccount -> savingAccount.setCurrencyBasedOnLinkedAccount(standardAccounts));

		cli.print("Standard accounts:");
		cli.print(standardAccounts);

		cli.print("Saving accounts:");
		cli.print(savingAccounts);
	}

	private LoginResponse performLogin(Credentials credentials) {
		return httpClient
				.post(LOGIN_URL)
				.withJsonBody(credentials)
				.perform(LoginResponse.class);
	}

	private RequestVerificationToken getRequestVerificationToken() {
		return httpClient.get(GET_REQUEST_VERIFICATION_TOKEN_URL)
				.perform(RequestVerificationToken.class);
	}

	private List<StandardAccountInfo> getStandardAccounts(RequestVerificationToken requestVerificationToken) {
		return httpClient.post(GET_STANDARD_ACCOUNTS_URL)
				.withHeader(requestVerificationToken.asHeader())
				.withResponsePreprocessor(this::extractListOfStandardAccounts)
				.perform(listTypeOf(StandardAccountInfo.class));
	}

	private String extractListOfStandardAccounts(String response) {
		return getFieldRawValueAsString(response, "products");
	}

	private List<SavingAccountInfo> getSavingAccounts(RequestVerificationToken requestVerificationToken) {
		return httpClient.get(GET_SAVING_ACCOUNTS_URL)
				.withHeader(requestVerificationToken.asHeader())
				.withResponsePreprocessor(this::extractListOfSavingAccounts)
				.perform(listTypeOf(SavingAccountInfo.class));
	}

	private String extractListOfSavingAccounts(String response) {
		return getFieldRawValueAsString(response, "goals");
	}

}
