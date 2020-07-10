package pl.byteit.mbankscrapper.http;

import com.github.tomakehurst.wiremock.WireMockServer;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.byteit.mbankscrapper.operation.Credentials;
import pl.byteit.mbankscrapper.operation.mbank.account.StandardAccountInfo;

import java.math.BigDecimal;
import java.net.CookieManager;
import java.net.CookiePolicy;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.byteit.mbankscrapper.ResourcesUtil.loadFileFromResourcesAsString;
import static pl.byteit.mbankscrapper.operation.mbank.account.StandardAccountInfoAssert.assertThatStandardAccountInfo;

class DefaultHttpClientTest {

	private static final String ACCOUNT_NUMBER = "11 2222 3333 4444 5555 6666 7777";
	private static final BigDecimal ACCOUNT_BALANCE = new BigDecimal("9280.55");
	private static final String HOST = "http://localhost:8080";
	private static final String GET_PATH = "/get";
	private static final String POST_PATH = "/post";
	private static final String GET_URL = HOST + GET_PATH;
	private static final String POST_URL = HOST + POST_PATH;

	private static WireMockServer wireMockServer = new WireMockServer(8080);

	private static HttpClient httpClient;

	@BeforeAll
	static void beforeAllDefaultHttpClientTests() {
		wireMockServer.start();
		httpClient = httpClient();
	}

	@AfterAll
	static void afterAllDefaultHttpClientTests() {
		wireMockServer.stop();
	}

	@BeforeEach
	void beforeEachDefaultHttpClientTest() {
		wireMockServer.resetAll();
	}

	@Test
	void shouldSendRequestWithNoResponseExpected() {
		wireMockServer.stubFor(get(urlEqualTo(GET_PATH)));

		httpClient.get(GET_URL)
				.perform();

		verify(1, getRequestedFor(urlEqualTo(GET_PATH)));
	}

	@Test
	void shouldSendRequestWithHttpHeader() {
		HttpHeader header = new HttpHeader("test", "yes");
		wireMockServer.stubFor(get(urlEqualTo(GET_PATH)));

		httpClient.get(GET_URL)
				.withHeader(header)
				.perform();

		verify(
				1,
				getRequestedFor(urlEqualTo(GET_PATH))
						.withHeader(header.getName(), equalTo(header.getValue()))
		);
	}

	@Test
	void shouldSendRequestAndParseResponseIntoObject() {
		wireMockServer.stubFor(
				get(urlEqualTo(GET_PATH))
						.willReturn(aResponse().withBody(standardAccountInfoJsonBody()))
		);

		StandardAccountInfo accountInfo = httpClient.get(GET_URL)
				.perform(StandardAccountInfo.class);

		verify(1, getRequestedFor(urlEqualTo(GET_PATH)));
		assertThatStandardAccountInfo(accountInfo)
				.hasNumber(ACCOUNT_NUMBER)
				.hasCurrency("PLN")
				.hasBalance(ACCOUNT_BALANCE);
	}

	@Test
	void shouldSendRequestAndParseResponseIntoObjectWithResponsePreprocessing() {
		wireMockServer.stubFor(
				get(urlEqualTo(GET_PATH))
						.willReturn(aResponse().withBody(standardAccountInfoJsonBody()))
		);

		StandardAccountInfo accountInfo = httpClient.get(GET_URL)
				.withResponsePreprocessor(response -> response.replaceAll("PLN", "EUR"))
				.perform(StandardAccountInfo.class);

		verify(1, getRequestedFor(urlEqualTo(GET_PATH)));
		assertThatStandardAccountInfo(accountInfo)
				.hasNumber(ACCOUNT_NUMBER)
				.hasCurrency("EUR")
				.hasBalance(ACCOUNT_BALANCE);
	}

	@Test
	void shouldThrowIllegalStateExceptionWhenHttpResponseIsNot200OK() {
		wireMockServer.stubFor(
				get(urlEqualTo(GET_PATH))
						.willReturn(aResponse().withStatus(400))
		);

		IllegalStateException thrownException = assertThrows(
				IllegalStateException.class,
				() -> httpClient.get(GET_URL).perform()
		);

		assertThat(thrownException).hasMessage("Non-200 response status code (code: 400)");
	}

	@Test
	void shouldSendPostWithEmptyJsonBody() {
		wireMockServer.stubFor(post(urlEqualTo(POST_PATH)));

		httpClient.post(POST_URL)
				.perform();

		verify(
				1,
				postRequestedFor(urlEqualTo(POST_PATH))
				.withRequestBody(equalTo("{}"))
		);
	}

	@Test
	void shouldSendPostWithJsonBody() {
		wireMockServer.stubFor(post(urlEqualTo(POST_PATH)));

		httpClient.post(POST_URL)
				.withJsonBody(getBody("user", "passwd"))
				.perform();

		verify(
				1,
				postRequestedFor(urlEqualTo(POST_PATH))
						.withRequestBody(equalTo("{\"UserName\":\"user\",\"Password\":\"passwd\"}"))
		);
	}

	private static HttpClient httpClient() {
		CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.cookieJar(new JavaNetCookieJar(cookieManager))
				.build();

		return new DefaultHttpClient(okHttpClient);
	}

	private String standardAccountInfoJsonBody() {
		return loadFileFromResourcesAsString("single-standard-account.json");
	}

	private Credentials getBody(String username, String password) {
		return new Credentials(username.toCharArray(), password.toCharArray());
	}

}