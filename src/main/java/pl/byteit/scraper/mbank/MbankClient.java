package pl.byteit.scraper.mbank;

import com.fasterxml.jackson.databind.JsonNode;
import pl.byteit.scraper.http.Header;
import pl.byteit.scraper.http.HttpClient;
import pl.byteit.scraper.mbank.model.*;
import pl.byteit.scraper.operation.Account;
import pl.byteit.scraper.operation.AuthenticationStatus;
import pl.byteit.scraper.operation.BankClient;
import pl.byteit.scraper.operation.exception.InvalidCredentials;
import pl.byteit.scraper.operation.exception.SecondFactorAuthenticationFailed;
import pl.byteit.scraper.util.Await;
import pl.byteit.scraper.util.JsonParser;
import pl.byteit.scraper.util.TypeReferences;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static pl.byteit.scraper.mbank.RequestUrls.*;
import static pl.byteit.scraper.util.JsonParser.getFieldRawValueAsString;

public class MbankClient implements BankClient {

	private static final String SCA_AUTHORIZATION_DISPOSABLE_URL = "sca/authorization/disposable";

	private final HttpClient httpClient;
	private final Await await;

	public MbankClient(HttpClient httpClient, Await await) {
		this.httpClient = httpClient;
		this.await = await;
	}

	@Override
	public AuthenticationStatus login(String login, String password) {
		LoginResponse loginResponse = submitCredentials(new Credentials(login, password));
		if (!loginResponse.successful) {
			throw new InvalidCredentials();
		}
		return ResponseMapper.asAuthenticationStatus(loginResponse);
	}

	private LoginResponse submitCredentials(Credentials credentials) {
		return httpClient
				.post(LOGIN_URL)
				.withJsonBody(credentials)
				.fetch(LoginResponse.class);
	}

	@Override
	public void authenticateWithSecondFactor() {
		Header requestVerificationHeader = fetchRequestVerificationToken().asHeader();
		SecondFactorAuthenticationIdentifier authenticationId = fetchAuthenticationId();
		TransactionId transactionId = startSecondFactorAuthentication(authenticationId, requestVerificationHeader);
		waitForAuthentication(requestVerificationHeader, transactionId);
		finalizeAuthentication(requestVerificationHeader, authenticationId);
	}

	private RequestVerificationToken fetchRequestVerificationToken() {
		return httpClient.get(FETCH_REQUEST_VERIFICATION_TOKEN_URL)
				.fetch(RequestVerificationToken.class);
	}

	private SecondFactorAuthenticationIdentifier fetchAuthenticationId() {
		return httpClient.post(FETCH_AUTHENTICATION_ID_URL)
				.fetch(SecondFactorAuthenticationIdentifier.class);
	}

	private TransactionId startSecondFactorAuthentication(
			SecondFactorAuthenticationIdentifier identifier,
			Header requestVerificationHeader
	) {
		return httpClient.post(START_SECOND_FACTOR_AUTHENTICATION_URL)
				.withHeader(requestVerificationHeader)
				.withJsonBody(startSecondFactorAuthenticationRequest(identifier))
				.fetch(TransactionId.class);
	}

	private void waitForAuthentication(Header requestVerificationHeader, TransactionId transactionId) {
		SecondFactorAuthenticationStatus status = await.forResult(
				() -> checkStatus(transactionId, requestVerificationHeader),
				SecondFactorAuthenticationStatus::isFinished,
				30,
				2
		);
		assertAuthenticated(status);
	}

	private SecondFactorAuthenticationStatus checkStatus(TransactionId transactionId, Header requestVerificationHeader) {
		return httpClient.post(CHECK_AUTHENTICATION_STATUS_URL)
				.withHeader(requestVerificationHeader)
				.withJsonBody(transactionId)
				.fetch(SecondFactorAuthenticationStatus.class);
	}

	/**
	 * Throws illegal state exception in case of failed second factor authentication attempt
	 */
	private void finalizeAuthentication(Header requestVerificationHeader, SecondFactorAuthenticationIdentifier identifier) {
		httpClient.post(EXECUTE_AUTHENTICATION_URL)
				.withHeader(requestVerificationHeader)
				.fetch();
		httpClient.post(FINALIZE_AUTHENTICATION_URL)
				.withHeader(requestVerificationHeader)
				.withJsonBody(new FinalizeAuthenticationRequest(identifier))
				.fetch();
	}

	private static void assertAuthenticated(SecondFactorAuthenticationStatus secondFactorAuthenticationStatus) {
		if (!secondFactorAuthenticationStatus.isSuccessful())
			throw new SecondFactorAuthenticationFailed("Authentication failed with status: " + secondFactorAuthenticationStatus.status);
	}

	@Override
	public List<Account> getAccounts() {
		Header requestVerificationHeader = fetchRequestVerificationToken().asHeader();
		List<StandardAccount> standardAccounts = fetchStandardAccounts(requestVerificationHeader);
		List<SavingAccount> savingAccounts = fetchSavingAccounts(requestVerificationHeader);
		return mapToAccounts(standardAccounts, savingAccounts);
	}

	private List<StandardAccount> fetchStandardAccounts(Header requestVerificationHeader) {
		JsonNode rawResponse = httpClient.post(FETCH_STANDARD_ACCOUNTS_URL)
				.withHeader(requestVerificationHeader)
				.fetch(JsonNode.class);
		return extractListOfStandardAccounts(rawResponse);
	}

	private List<SavingAccount> fetchSavingAccounts(Header requestVerificationHeader) {
		JsonNode rawResponse = httpClient.get(FETCH_SAVING_ACCOUNTS_URL)
				.withHeader(requestVerificationHeader)
				.fetch(JsonNode.class);
		return extractListOfSavingAccounts(rawResponse);
	}

	private static List<Account> mapToAccounts(List<StandardAccount> standardAccounts, List<SavingAccount> savingAccounts) {
		return Stream.concat(
				standardAccounts.stream().map(ResponseMapper::asAccount),
				savingAccounts.stream().map(account -> ResponseMapper.asAccount(account, standardAccounts))
		)
				.collect(toList());
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

	private static StartSecondFactorAuthenticationRequest startSecondFactorAuthenticationRequest(
			SecondFactorAuthenticationIdentifier identifier
	) {
		return new StartSecondFactorAuthenticationRequest(identifier, "POST", SCA_AUTHORIZATION_DISPOSABLE_URL);
	}

}
