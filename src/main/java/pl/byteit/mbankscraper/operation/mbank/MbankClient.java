package pl.byteit.mbankscraper.operation.mbank;

import pl.byteit.mbankscraper.http.HttpClient;
import pl.byteit.mbankscraper.operation.BankClient;
import pl.byteit.mbankscraper.operation.Credentials;
import pl.byteit.mbankscraper.operation.InvalidCredentialsException;
import pl.byteit.mbankscraper.operation.mbank.account.SavingAccountInfo;
import pl.byteit.mbankscraper.operation.mbank.account.StandardAccountInfo;
import pl.byteit.mbankscraper.operation.mbank.authentication.LoginResponse;
import pl.byteit.mbankscraper.operation.mbank.authentication.SecondFactorAuthenticationManager;
import pl.byteit.mbankscraper.util.CommandLineInterface;

import java.util.List;

import static pl.byteit.mbankscraper.util.JsonParser.getFieldRawValueAsString;
import static pl.byteit.mbankscraper.util.TypeUtil.listTypeOf;

public class MbankClient implements BankClient {

	public static final String GET_STANDARD_ACCOUNTS_URL = "https://online.mbank.pl/pl/MyDesktop/Dashboard/GetProducts";
	public static final String LOGIN_URL = "https://online.mbank.pl/pl/LoginMain/Account/JsonLogin";
	public static final String GET_REQUEST_VERIFICATION_TOKEN_URL = "https://online.mbank.pl/pl/setup/data";
	public static final String GET_SAVING_ACCOUNTS_URL = "https://online.mbank.pl/pl/SavingGoals/Home/GetSavingProducts";

	private final HttpClient httpClient;
	private final CommandLineInterface cli;
	private final SecondFactorAuthenticationManager secondFactorAuthenticationManager;

	public MbankClient(
			HttpClient httpClient,
			CommandLineInterface cli,
			SecondFactorAuthenticationManager secondFactorAuthenticationManager
	) {
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
				.withResponsePreprocessor(MbankClient::extractListOfStandardAccounts)
				.perform(listTypeOf(StandardAccountInfo.class));
	}

	private List<SavingAccountInfo> getSavingAccounts(RequestVerificationToken requestVerificationToken) {
		return httpClient.get(GET_SAVING_ACCOUNTS_URL)
				.withHeader(requestVerificationToken.asHeader())
				.withResponsePreprocessor(MbankClient::extractListOfSavingAccounts)
				.perform(listTypeOf(SavingAccountInfo.class));
	}

	private static String extractListOfStandardAccounts(String response) {
		return getFieldRawValueAsString(response, "products");
	}

	private static String extractListOfSavingAccounts(String response) {
		return getFieldRawValueAsString(response, "goals");
	}

}
