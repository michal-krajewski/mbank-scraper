package pl.byteit.mbankscraper.operation.mbank.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.internal.verification.Times;
import pl.byteit.mbankscraper.http.mock.HttpClientMock;
import pl.byteit.mbankscraper.operation.mbank.RequestVerificationToken;
import pl.byteit.mbankscraper.util.AwaitUtil;
import pl.byteit.mbankscraper.util.CommandLineInterface;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static pl.byteit.mbankscraper.ResourcesUtil.loadFileFromResourcesAsString;
import static pl.byteit.mbankscraper.operation.mbank.authentication.MobileAppSecondFactorAuthenticationManager.*;
import static pl.byteit.mbankscraper.util.JsonParser.asJson;

class MobileAppSecondFactorAuthenticationManagerTest {

	private static final RequestVerificationToken TOKEN = new RequestVerificationToken("token-value");

	@Mock
	private AwaitUtil awaitUtil;

	private HttpClientMock mockClient;
	private List<String> mockPrint;
	private MobileAppSecondFactorAuthenticationManager secondFactorAuthenticationManager;

	@BeforeEach
	void beforeEachMobileAppSecondFactorAuthenticationManagerTest() {
		initMocks(this);
		mockClient = new HttpClientMock();
		mockPrint = new ArrayList<>();
		CommandLineInterface cli = new CommandLineInterface(() -> "", msg -> mockPrint.add(msg));
		secondFactorAuthenticationManager = new MobileAppSecondFactorAuthenticationManager(mockClient, cli, awaitUtil);
	}

	@Test
	void shouldProperlyAuthenticate() {
		mockClient.mockResponse(FETCH_AUTHENTICATION_ID_URL, loadFileFromResourcesAsString("authentication-identifier.json"));
		mockClient.mockResponse(START_SECOND_FACTOR_AUTHENTICATION_URL, loadFileFromResourcesAsString("start-authentication.json"));
		mockClient.mockResponse(CHECK_AUTHENTICATION_STATUS_URL, asJson(new AuthenticationStatus("Authorized")));

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
		assertThat(mockPrint)
				.containsExactly("Waiting for 2FA. Check your Pixel device.");
		verify(awaitUtil).forSeconds(2);
	}

	@Test
	void shouldThrowAuthenticationExceptionWhenAuthenticationFailed() {
		mockClient.mockResponse(FETCH_AUTHENTICATION_ID_URL, loadFileFromResourcesAsString("authentication-identifier.json"));
		mockClient.mockResponse(START_SECOND_FACTOR_AUTHENTICATION_URL, loadFileFromResourcesAsString("start-authentication.json"));
		mockClient.mockResponse(CHECK_AUTHENTICATION_STATUS_URL, asJson(new AuthenticationStatus("Failed")));

		assertThrows(
				AuthenticationException.class,
				() -> secondFactorAuthenticationManager.authenticate(TOKEN)
		);

		mockClient.verify(FETCH_AUTHENTICATION_ID_URL);
		mockClient.verify(START_SECOND_FACTOR_AUTHENTICATION_URL);
		mockClient.verify(CHECK_AUTHENTICATION_STATUS_URL);
		verify(awaitUtil).forSeconds(2);
	}

	@Test
	void shouldThrowAuthenticationExceptionWhenExceededCheckingStatusAttempts() {
		mockClient.mockResponse(FETCH_AUTHENTICATION_ID_URL, loadFileFromResourcesAsString("authentication-identifier.json"));
		mockClient.mockResponse(START_SECOND_FACTOR_AUTHENTICATION_URL, loadFileFromResourcesAsString("start-authentication.json"));
		mockClient.mockResponse(CHECK_AUTHENTICATION_STATUS_URL, asJson(new AuthenticationStatus("Prepared")));

		assertThrows(
				AuthenticationException.class,
				() -> secondFactorAuthenticationManager.authenticate(TOKEN)
		);

		mockClient.verify(FETCH_AUTHENTICATION_ID_URL);
		mockClient.verify(START_SECOND_FACTOR_AUTHENTICATION_URL);
		mockClient.verify(15, CHECK_AUTHENTICATION_STATUS_URL);
		verify(awaitUtil, new Times(15)).forSeconds(2);
	}

}
