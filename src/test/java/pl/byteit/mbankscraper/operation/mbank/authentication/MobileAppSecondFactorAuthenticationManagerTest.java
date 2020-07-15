package pl.byteit.mbankscraper.operation.mbank.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.internal.verification.Times;
import pl.byteit.mbankscraper.http.mock.HttpClientMock;
import pl.byteit.mbankscraper.operation.mbank.RequestVerificationToken;
import pl.byteit.mbankscraper.util.Await;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static pl.byteit.mbankscraper.TestJsons.authenticationIdentifier;
import static pl.byteit.mbankscraper.TestJsons.startAuthentication;
import static pl.byteit.mbankscraper.operation.mbank.Requests.*;
import static pl.byteit.mbankscraper.util.JsonParser.asJson;

class MobileAppSecondFactorAuthenticationManagerTest {

	private static final RequestVerificationToken TOKEN = new RequestVerificationToken("token-value");
	private static final String AUTH_ID = "auth-id";
	private static final String TRAN_ID = "tran-id";

	@Mock
	private Await await;

	private HttpClientMock mockClient;
	private MobileAppSecondFactorAuthenticationManager secondFactorAuthenticationManager;

	@BeforeEach
	void beforeEachMobileAppSecondFactorAuthenticationManagerTest() {
		initMocks(this);
		mockClient = new HttpClientMock();
		secondFactorAuthenticationManager = new MobileAppSecondFactorAuthenticationManager(mockClient, await);
	}

	@Test
	void shouldProperlyAuthenticate() {
		mockClient.mockResponse(FETCH_AUTHENTICATION_ID_URL, authenticationIdentifier(AUTH_ID));
		mockClient.mockResponse(START_SECOND_FACTOR_AUTHENTICATION_URL, startAuthentication(TRAN_ID));
		mockClient.mockResponse(CHECK_AUTHENTICATION_STATUS_URL, asJson(new SecondFactorAuthenticationStatus("Authorized")));

		secondFactorAuthenticationManager.authenticate(TOKEN);

		mockClient.verify(FETCH_AUTHENTICATION_ID_URL)
				.hasPostMethod()
				.hasNoBodyDefined()
				.wasPerformed();
		mockClient.verify(START_SECOND_FACTOR_AUTHENTICATION_URL)
				.hasPostMethod()
				.hasHeaders(TOKEN.asHeader())
				.hasBodyDefined()
				.wasPerformed();
		mockClient.verify(CHECK_AUTHENTICATION_STATUS_URL)
				.hasPostMethod()
				.hasHeaders(TOKEN.asHeader())
				.hasBodyDefined()
				.wasPerformed();
		mockClient.verify(EXECUTE_AUTHENTICATION_URL)
				.hasPostMethod()
				.hasHeaders(TOKEN.asHeader())
				.hasNoBodyDefined()
				.wasPerformed();
		mockClient.verify(FINALIZE_AUTHENTICATION_URL)
				.hasPostMethod()
				.hasHeaders(TOKEN.asHeader())
				.hasBodyDefined()
				.wasPerformed();
		verify(await).forSeconds(2);
	}

	@Test
	void shouldThrowAuthenticationExceptionWhenAuthenticationFailed() {
		mockClient.mockResponse(FETCH_AUTHENTICATION_ID_URL, authenticationIdentifier("auth-id"));
		mockClient.mockResponse(START_SECOND_FACTOR_AUTHENTICATION_URL, startAuthentication("tran-id"));
		mockClient.mockResponse(CHECK_AUTHENTICATION_STATUS_URL, asJson(new SecondFactorAuthenticationStatus("Failed")));

		assertThrows(
				AuthenticationException.class,
				() -> secondFactorAuthenticationManager.authenticate(TOKEN)
		);

		mockClient.verify(FETCH_AUTHENTICATION_ID_URL);
		mockClient.verify(START_SECOND_FACTOR_AUTHENTICATION_URL);
		mockClient.verify(CHECK_AUTHENTICATION_STATUS_URL);
		verify(await).forSeconds(2);
	}

	@Test
	void shouldThrowAuthenticationExceptionWhenExceededCheckingStatusAttempts() {
		mockClient.mockResponse(FETCH_AUTHENTICATION_ID_URL, authenticationIdentifier("auth-id"));
		mockClient.mockResponse(START_SECOND_FACTOR_AUTHENTICATION_URL, startAuthentication("tran-id"));
		mockClient.mockResponse(CHECK_AUTHENTICATION_STATUS_URL, asJson(new SecondFactorAuthenticationStatus("Prepared")));

		assertThrows(
				AuthenticationException.class,
				() -> secondFactorAuthenticationManager.authenticate(TOKEN)
		);

		mockClient.verify(FETCH_AUTHENTICATION_ID_URL);
		mockClient.verify(START_SECOND_FACTOR_AUTHENTICATION_URL);
		mockClient.verify(15, CHECK_AUTHENTICATION_STATUS_URL);
		verify(await, new Times(15)).forSeconds(2);
	}

}
