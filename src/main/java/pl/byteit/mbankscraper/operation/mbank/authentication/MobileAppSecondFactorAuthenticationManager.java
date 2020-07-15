package pl.byteit.mbankscraper.operation.mbank.authentication;

import pl.byteit.mbankscraper.http.HttpClient;
import pl.byteit.mbankscraper.operation.AuthenticationResult;
import pl.byteit.mbankscraper.operation.mbank.RequestVerificationToken;
import pl.byteit.mbankscraper.operation.mbank.authentication.SecondFactorAuthenticationIdentifier.FinalizeAuthenticationRequest;
import pl.byteit.mbankscraper.operation.mbank.authentication.SecondFactorAuthenticationInfo.TranId;
import pl.byteit.mbankscraper.util.Await;

public class MobileAppSecondFactorAuthenticationManager implements SecondFactorAuthenticationManager {

	public static final String FETCH_AUTHENTICATION_ID_URL = "https://online.mbank.pl/pl/Sca/GetScaAuthorizationData";
	public static final String START_SECOND_FACTOR_AUTHENTICATION_URL = "https://online.mbank.pl/api/auth/initprepare";
	public static final String CHECK_AUTHENTICATION_STATUS_URL = "https://online.mbank.pl/api/auth/status";
	public static final String EXECUTE_AUTHENTICATION_URL = "https://online.mbank.pl/api/auth/execute";
	public static final String FINALIZE_AUTHENTICATION_URL = "https://online.mbank.pl/pl/Sca/FinalizeAuthorization";
	private static final int MAX_CHECKING_STATUS_ATTEMPTS = 15;

	private final HttpClient httpClient;
	private final Await await;

	public MobileAppSecondFactorAuthenticationManager(HttpClient httpClient, Await await) {
		this.httpClient = httpClient;
		this.await = await;
	}

	@Override
	public AuthenticationResult authenticate(RequestVerificationToken requestVerificationToken) {
		SecondFactorAuthenticationIdentifier authenticationId = fetchAuthenticationId();
		SecondFactorAuthenticationInfo authenticationInfo = startSecondFactorAuthentication(authenticationId, requestVerificationToken);
		waitForAuthentication(requestVerificationToken, authenticationInfo);
		finalizeAuthentication(requestVerificationToken, authenticationId);
		return AuthenticationResult.successful();
	}

	private SecondFactorAuthenticationIdentifier fetchAuthenticationId() {
		return httpClient.post(FETCH_AUTHENTICATION_ID_URL)
				.perform(SecondFactorAuthenticationIdentifier.class);
	}

	private SecondFactorAuthenticationInfo startSecondFactorAuthentication(SecondFactorAuthenticationIdentifier identifier,
			RequestVerificationToken requestVerificationToken) {
		return httpClient.post(START_SECOND_FACTOR_AUTHENTICATION_URL)
				.withHeader(requestVerificationToken.asHeader())
				.withJsonBody(StartSecondFactorAuthenticationRequest.withId(identifier))
				.perform(SecondFactorAuthenticationInfo.class);
	}

	private void waitForAuthentication(RequestVerificationToken verificationToken, SecondFactorAuthenticationInfo authenticationInfo) {
		SecondFactorAuthenticationStatus authenticationStatus;
		int attempt = 0;
		do {
			authenticationStatus = checkStatus(authenticationInfo.getTranId(), verificationToken);
			attempt++;
			await.forSeconds(2);
		}
		while (authenticationStatus.isInProgress() && attempt < MAX_CHECKING_STATUS_ATTEMPTS);
		verifyAuthenticationSucceed(authenticationStatus);
	}

	private SecondFactorAuthenticationStatus checkStatus(TranId tranId, RequestVerificationToken requestVerificationToken) {
		return httpClient.post(CHECK_AUTHENTICATION_STATUS_URL)
				.withHeader(requestVerificationToken.asHeader())
				.withJsonBody(tranId)
				.perform(SecondFactorAuthenticationStatus.class);
	}

	private void finalizeAuthentication(RequestVerificationToken requestVerificationToken,
			SecondFactorAuthenticationIdentifier identifier) {
		httpClient.post(EXECUTE_AUTHENTICATION_URL)
				.withHeader(requestVerificationToken.asHeader())
				.perform();

		httpClient.post(FINALIZE_AUTHENTICATION_URL)
				.withHeader(requestVerificationToken.asHeader())
				.withJsonBody(FinalizeAuthenticationRequest.withIdentifier(identifier))
				.perform();
	}

	private static void verifyAuthenticationSucceed(SecondFactorAuthenticationStatus secondFactorAuthenticationStatus) {
		if (!secondFactorAuthenticationStatus.isSuccessful()) {
			throw new AuthenticationException(secondFactorAuthenticationStatus);
		}
	}

}
