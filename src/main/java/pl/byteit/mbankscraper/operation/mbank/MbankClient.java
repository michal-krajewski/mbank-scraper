package pl.byteit.mbankscraper.operation.mbank;

import com.fasterxml.jackson.databind.JsonNode;
import pl.byteit.mbankscraper.http.HttpClient;
import pl.byteit.mbankscraper.operation.Account;
import pl.byteit.mbankscraper.operation.AuthenticationStatus;
import pl.byteit.mbankscraper.operation.BankClient;
import pl.byteit.mbankscraper.operation.InvalidCredentialsException;
import pl.byteit.mbankscraper.operation.mbank.data.*;
import pl.byteit.mbankscraper.util.Await;
import pl.byteit.mbankscraper.util.JsonParser;
import pl.byteit.mbankscraper.util.TypeReferences;

import java.util.ArrayList;
import java.util.List;

import static pl.byteit.mbankscraper.operation.mbank.AccountMerger.setCurrencyBasedOnLinkedAccount;
import static pl.byteit.mbankscraper.operation.mbank.RequestUrls.*;
import static pl.byteit.mbankscraper.util.JsonParser.getFieldRawValueAsString;

public class MbankClient implements BankClient {
	private final HttpClient httpClient;

	private final Await await;

	public MbankClient(
			HttpClient httpClient,
			Await await
	) {
		this.httpClient = httpClient;
		this.await = await;

	}

	@Override
	public AuthenticationStatus login(String login, String password) {
		LoginResponse loginResponse = submitCredentials(new Credentials(login, password));
		if (!loginResponse.successful) {
			throw new InvalidCredentialsException();
		}
		return ResponseMapper.asAuthenticationStatus(loginResponse);
	}

	private LoginResponse submitCredentials(Credentials credentials) {
		return httpClient
				.post(LOGIN_URL)
				.withJsonBody(credentials)
				.perform(LoginResponse.class);
	}

	@Override
	public AuthenticationStatus authenticateWithSecondFactor() {
		RequestVerificationToken requestVerificationToken = getRequestVerificationToken();
		SecondFactorAuthenticationIdentifier authenticationId = fetchAuthenticationId();
		TransactionId transactionId = startSecondFactorAuthentication(authenticationId, requestVerificationToken);
		waitForAuthentication(requestVerificationToken, transactionId);
		finalizeAuthentication(requestVerificationToken, authenticationId);
		return AuthenticationStatus.AUTHENTICATED;
	}

	private RequestVerificationToken getRequestVerificationToken() {
		return httpClient.get(GET_REQUEST_VERIFICATION_TOKEN_URL)
				.perform(RequestVerificationToken.class);
	}

	private SecondFactorAuthenticationIdentifier fetchAuthenticationId() {
		return httpClient.post(FETCH_AUTHENTICATION_ID_URL)
				.perform(SecondFactorAuthenticationIdentifier.class);
	}

	private TransactionId startSecondFactorAuthentication(
			SecondFactorAuthenticationIdentifier identifier,
			RequestVerificationToken requestVerificationToken
	) {
		return httpClient.post(START_SECOND_FACTOR_AUTHENTICATION_URL)
				.withHeader(requestVerificationToken.asHeader())
				.withJsonBody(StartSecondFactorAuthenticationRequest.withId(identifier))
				.perform(TransactionId.class);
	}

	private void waitForAuthentication(RequestVerificationToken verificationToken, TransactionId transactionId) {
		SecondFactorAuthenticationStatus status = await.forResult(
				() -> checkStatus(transactionId, verificationToken),
				SecondFactorAuthenticationStatus::isFinished,
				30,
				2
		);
		verifyAuthenticationSucceed(status);
	}

	private SecondFactorAuthenticationStatus checkStatus(TransactionId transactionId, RequestVerificationToken requestVerificationToken) {
		return httpClient.post(CHECK_AUTHENTICATION_STATUS_URL)
				.withHeader(requestVerificationToken.asHeader())
				.withJsonBody(transactionId)
				.perform(SecondFactorAuthenticationStatus.class);
	}

	private void finalizeAuthentication(
			RequestVerificationToken requestVerificationToken,
			SecondFactorAuthenticationIdentifier identifier
	) {
		httpClient.post(EXECUTE_AUTHENTICATION_URL)
				.withHeader(requestVerificationToken.asHeader())
				.perform();

		httpClient.post(FINALIZE_AUTHENTICATION_URL)
				.withHeader(requestVerificationToken.asHeader())
				.withJsonBody(SecondFactorAuthenticationIdentifier.FinalizeAuthenticationRequest.withIdentifier(identifier))
				.perform();
	}

	private static void verifyAuthenticationSucceed(SecondFactorAuthenticationStatus secondFactorAuthenticationStatus) {
		if (!secondFactorAuthenticationStatus.isSuccessful()) {
			throw new AuthenticationException(secondFactorAuthenticationStatus);
		}
	}

	@Override
	public List<Account> getAccounts() {
		RequestVerificationToken verificationToken = getRequestVerificationToken();
		List<StandardAccount> standardAccounts = getStandardAccounts(verificationToken);
		List<SavingAccount> savingAccounts = getSavingAccounts(verificationToken);
		savingAccounts.forEach(savingAccount -> setCurrencyBasedOnLinkedAccount(savingAccount, standardAccounts));
		return mapToAccountInfoList(standardAccounts, savingAccounts);
	}

	private List<StandardAccount> getStandardAccounts(RequestVerificationToken requestVerificationToken) {
		JsonNode rawResponse = httpClient.post(GET_STANDARD_ACCOUNTS_URL)
				.withHeader(requestVerificationToken.asHeader())
				.perform(JsonNode.class);
		return extractListOfStandardAccounts(rawResponse);
	}

	private List<SavingAccount> getSavingAccounts(RequestVerificationToken requestVerificationToken) {
		JsonNode rawResponse = httpClient.get(GET_SAVING_ACCOUNTS_URL)
				.withHeader(requestVerificationToken.asHeader())
				.perform(JsonNode.class);
		return extractListOfSavingAccounts(rawResponse);
	}

	private static List<Account> mapToAccountInfoList(
			List<StandardAccount> standardAccounts,
			List<SavingAccount> savingAccounts
	) {
		List<Account> accounts = new ArrayList<>();
		standardAccounts.stream()
				.map(ResponseMapper::asAccount)
				.forEach(accounts::add);
		savingAccounts.stream()
				.map(ResponseMapper::asAccount)
				.forEach(accounts::add);
		return accounts;
	}

	private static List<StandardAccount> extractListOfStandardAccounts(JsonNode response) {
		return JsonParser.parse(
				getFieldRawValueAsString(response, "products"),
				TypeReferences.listTypeOf(StandardAccount.class)
		);
	}

	private static List<SavingAccount> extractListOfSavingAccounts(JsonNode response) {
		return JsonParser.parse(
				getFieldRawValueAsString(response, "goals"),
				TypeReferences.listTypeOf(SavingAccount.class)
		);
	}

}
