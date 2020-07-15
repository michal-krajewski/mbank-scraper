package pl.byteit.mbankscraper.http;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.byteit.mbankscraper.App;
import pl.byteit.mbankscraper.operation.Credentials;
import pl.byteit.mbankscraper.operation.mbank.account.StandardAccountInfo;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.lang.Integer.parseInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.byteit.mbankscraper.TestJsons.singleStandardAccount;
import static pl.byteit.mbankscraper.operation.mbank.account.StandardAccountInfoAssert.assertThatStandardAccountInfo;

class OkHttpClientWrapperTest {

	private static final String ACCOUNT_NUMBER = "11 2222 3333 4444 5555 6666 7777";
	private static final BigDecimal ACCOUNT_BALANCE = new BigDecimal("9280.55");
	private static final String HOST = "http://localhost";
	private static final String GET_PATH = "/get";
	private static final String POST_PATH = "/post";
	public static final String STANDARD_CURRENCY = "PLN";

	private static WireMockServer mockServer;
	private static HttpClient httpClient;
	private static int port;

	@BeforeAll
	static void beforeAllDefaultHttpClientTests() {
		port = parseInt(System.getProperty("MockServerPort", "8666"));
		System.out.println(port);
		mockServer = new WireMockServer(port);
		mockServer.start();
		httpClient = App.httpClientWithCookieHandler();
	}

	@AfterAll
	static void afterAllDefaultHttpClientTests() {
		mockServer.stop();
	}

	@BeforeEach
	void beforeEachDefaultHttpClientTest() {
		mockServer.resetAll();
	}

	@Test
	void shouldSendRequestWithNoResponseExpected() {
		mockServer.stubFor(get(GET_PATH));

		httpClient.get(urlFor(GET_PATH)).perform();

		mockServer.verify(1, getRequestedFor(urlEqualTo(GET_PATH)));
	}

	@Test
	void shouldSendRequestWithHttpHeader() {
		Header header = new Header("test", "yes");
		mockServer.stubFor(get(GET_PATH));

		httpClient.get(urlFor(GET_PATH))
				.withHeader(header)
				.perform();

		mockServer.verify(
				1,
				getRequestedFor(urlEqualTo(GET_PATH))
						.withHeader(header.getName(), equalTo(header.getValue()))
		);
	}

	@Test
	void shouldSendRequestAndParseResponseIntoObject() {
		mockServer.stubFor(
				get(GET_PATH)
						.willReturn(aResponse()
								.withBody(singleStandardAccount(ACCOUNT_NUMBER, ACCOUNT_BALANCE.toString(), STANDARD_CURRENCY))
						)
		);

		StandardAccountInfo accountInfo = httpClient.get(urlFor(GET_PATH)).perform(StandardAccountInfo.class);

		mockServer.verify(1, getRequestedFor(urlEqualTo(GET_PATH)));
		assertThatStandardAccountInfo(accountInfo)
				.hasNumber(ACCOUNT_NUMBER)
				.hasCurrency(STANDARD_CURRENCY)
				.hasBalance(ACCOUNT_BALANCE);
	}

	@Test
	void shouldSendRequestAndParseResponseIntoObjectWithResponsePreprocessing() {
		mockServer.stubFor(
				get(GET_PATH)
						.willReturn(aResponse()
								.withBody(singleStandardAccount(ACCOUNT_NUMBER, ACCOUNT_BALANCE.toString(), STANDARD_CURRENCY))
						)
		);

		StandardAccountInfo accountInfo = httpClient.get(urlFor(GET_PATH))
				.withResponsePreprocessor(response -> response.replaceAll("PLN", "EUR"))
				.perform(StandardAccountInfo.class);

		mockServer.verify(1, getRequestedFor(urlEqualTo(GET_PATH)));
		assertThatStandardAccountInfo(accountInfo)
				.hasNumber(ACCOUNT_NUMBER)
				.hasCurrency("EUR")
				.hasBalance(ACCOUNT_BALANCE);
	}

	@Test
	void shouldThrowIllegalStateExceptionWhenHttpResponseIsNot200OK() {
		mockServer.stubFor(
				get(GET_PATH)
						.willReturn(aResponse().withStatus(400))
		);

		IllegalStateException thrownException = assertThrows(
				IllegalStateException.class,
				() -> httpClient.get(urlFor(GET_PATH)).perform()
		);

		assertThat(thrownException).hasMessage("Non-200 response status code (code: 400)");
	}

	@Test
	void shouldSendPostWithEmptyJsonBody() {
		mockServer.stubFor(post(POST_PATH));

		httpClient.post(urlFor(POST_PATH)).perform();

		mockServer.verify(
				1,
				postRequestedFor(urlEqualTo(POST_PATH))
						.withRequestBody(equalTo("{}"))
		);
	}

	@Test
	void shouldSendPostWithJsonBody() {
		mockServer.stubFor(post(POST_PATH));

		httpClient.post(urlFor(POST_PATH))
				.withJsonBody(credentials("user", "passwd"))
				.perform();

		mockServer.verify(
				1,
				postRequestedFor(urlEqualTo(POST_PATH))
						.withRequestBody(equalTo("{\"UserName\":\"user\",\"Password\":\"passwd\"}"))
		);
	}

	private static Credentials credentials(String username, String password) {
		return new Credentials(username.toCharArray(), password.toCharArray());
	}

	private static String urlFor(String path) {
		return HOST + ":" + port + path;
	}

}
