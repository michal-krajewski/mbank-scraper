package pl.byteit.mbankscrapper.operation.mbank.authentication;

import pl.byteit.mbankscrapper.http.HttpClient;
import pl.byteit.mbankscrapper.operation.mbank.RequestVerificationToken;
import pl.byteit.mbankscrapper.operation.mbank.authentication.SecondFactorAuthenticationIdentifier.FinalizeAuthenticationRequest;
import pl.byteit.mbankscrapper.operation.mbank.authentication.SecondFactorAuthenticationInfo.TranId;
import pl.byteit.mbankscrapper.util.AwaitUtil;
import pl.byteit.mbankscrapper.util.CommandLineInterface;

public class MobileAppSecondFactorAuthenticationManager implements SecondFactorAuthenticationManager {

	private static final String FETCH_AUTHENTICATION_ID_URL = "https://online.mbank.pl/pl/Sca/GetScaAuthorizationData";
	private static final String START_SECOND_FACTOR_AUTHENTICATION_URL = "https://online.mbank.pl/api/auth/initprepare";
	private static final String CHECK_AUTHENTICATION_STATUS_URL = "https://online.mbank.pl/api/auth/status";
	private static final String EXECUTE_AUTHENTICATION_URL = "https://online.mbank.pl/api/auth/execute";
	private static final String FINALIZE_AUTHENTICATION_URL = "https://online.mbank.pl/pl/Sca/FinalizeAuthorization";
	private static final int MAX_CHECKING_STATUS_ATTEMPTS = 15;

	private final HttpClient httpClient;
	private final CommandLineInterface cli;
	private final AwaitUtil await;

	public MobileAppSecondFactorAuthenticationManager(HttpClient httpClient, CommandLineInterface cli, AwaitUtil await) {
		this.httpClient = httpClient;
		this.cli = cli;
		this.await = await;
	}

	@Override
	public void authenticate(RequestVerificationToken requestVerificationToken) {
		SecondFactorAuthenticationIdentifier authenticationId = fetchAuthenticationId();
		SecondFactorAuthenticationInfo authenticationInfo = startSecondFactorAuthentication(authenticationId, requestVerificationToken);

		cli.print("Waiting for 2FA. Check your " + authenticationInfo.getDeviceName() + " device.");

		waitForAuthentication(requestVerificationToken, authenticationInfo);
		finalizeAuthentication(requestVerificationToken, authenticationId);
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
		AuthenticationStatus authenticationStatus;
		int attempt = 0;
		do {
			authenticationStatus = checkStatus(authenticationInfo.getTranId(), verificationToken);
			attempt++;
			await.forSeconds(2);
		}
		while (authenticationStatus.isInProgress() && attempt < MAX_CHECKING_STATUS_ATTEMPTS);

		verifyAuthenticationSucceed(authenticationStatus);
	}

	private void verifyAuthenticationSucceed(AuthenticationStatus authenticationStatus) {
		if (!authenticationStatus.isSuccessful()) {
			throw new AuthenticationException(authenticationStatus);
		}
	}

	private AuthenticationStatus checkStatus(TranId tranId, RequestVerificationToken requestVerificationToken) {
		return httpClient.post(CHECK_AUTHENTICATION_STATUS_URL)
				.withHeader(requestVerificationToken.asHeader())
				.withJsonBody(tranId)
				.perform(AuthenticationStatus.class);
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

}
