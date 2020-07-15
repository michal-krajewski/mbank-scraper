package pl.byteit.mbankscraper.operation.mbank;

import pl.byteit.mbankscraper.http.HttpClient;
import pl.byteit.mbankscraper.operation.*;
import pl.byteit.mbankscraper.operation.mbank.account.SavingAccountInfo;
import pl.byteit.mbankscraper.operation.mbank.account.StandardAccountInfo;
import pl.byteit.mbankscraper.operation.mbank.authentication.LoginResponse;
import pl.byteit.mbankscraper.operation.mbank.authentication.SecondFactorAuthenticationManager;
import pl.byteit.mbankscraper.util.TypeReferences;

import java.util.ArrayList;
import java.util.List;

import static pl.byteit.mbankscraper.operation.mbank.Requests.*;
import static pl.byteit.mbankscraper.util.JsonParser.getFieldRawValueAsString;

public class MbankClient implements BankClient {



	private final HttpClient httpClient;
	private final SecondFactorAuthenticationManager secondFactorAuthenticationManager;

	public MbankClient(
			HttpClient httpClient,
			SecondFactorAuthenticationManager secondFactorAuthenticationManager
	) {
		this.httpClient = httpClient;
		this.secondFactorAuthenticationManager = secondFactorAuthenticationManager;
	}

	@Override
	public AuthenticationResult login(Credentials credentials) {
		LoginResponse loginResponse = submitCredentials(credentials);
		if (!loginResponse.isSuccessful()) {
			throw new InvalidCredentialsException();
		}
		return loginResponse.getStatus();
	}

	@Override
	public AuthenticationResult authenticateWithSecondFactor() {
		RequestVerificationToken requestVerificationToken = getRequestVerificationToken();
		return secondFactorAuthenticationManager.authenticate(requestVerificationToken);
	}

	@Override
	public List<AccountInfo> getAccounts() {
		RequestVerificationToken verificationToken = getRequestVerificationToken();
		List<StandardAccountInfo> standardAccounts = getStandardAccounts(verificationToken);
		List<SavingAccountInfo> savingAccounts = getSavingAccounts(verificationToken);
		savingAccounts.forEach(savingAccount -> savingAccount.setCurrencyBasedOnLinkedAccount(standardAccounts));
		return mapToAccountInfoList(standardAccounts, savingAccounts);
	}

	private LoginResponse submitCredentials(Credentials credentials) {
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
				.perform(TypeReferences.listTypeOf(StandardAccountInfo.class));
	}

	private List<SavingAccountInfo> getSavingAccounts(RequestVerificationToken requestVerificationToken) {
		return httpClient.get(GET_SAVING_ACCOUNTS_URL)
				.withHeader(requestVerificationToken.asHeader())
				.withResponsePreprocessor(MbankClient::extractListOfSavingAccounts)
				.perform(TypeReferences.listTypeOf(SavingAccountInfo.class));
	}

	private static List<AccountInfo> mapToAccountInfoList(
			List<StandardAccountInfo> standardAccounts,
			List<SavingAccountInfo> savingAccounts
	) {
		List<AccountInfo> accounts = new ArrayList<>();
		standardAccounts.stream()
				.map(StandardAccountInfo::toAccountInfo)
				.forEach(accounts::add);
		savingAccounts.stream()
				.map(SavingAccountInfo::toAccountInfo)
				.forEach(accounts::add);
		return accounts;
	}

	private static String extractListOfStandardAccounts(String response) {
		return getFieldRawValueAsString(response, "products");
	}

	private static String extractListOfSavingAccounts(String response) {
		return getFieldRawValueAsString(response, "goals");
	}

}
