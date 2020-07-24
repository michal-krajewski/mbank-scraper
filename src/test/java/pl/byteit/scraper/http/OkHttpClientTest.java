package pl.byteit.scraper.http;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.byteit.scraper.TestDataClass;
import pl.byteit.scraper.mbank.model.Credentials;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.lang.Integer.parseInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OkHttpClientTest {

	private static final String HOST = "http://localhost";
	private static final String GET_PATH = "/get";
	private static final String POST_PATH = "/post";

	private static WireMockServer mockServer;
	private static HttpClient httpClient;
	private static int port;

	@BeforeAll
	static void beforeAllDefaultHttpClientTests() {
		port = parseInt(System.getProperty("MockServerPort", "8666"));
		System.out.println(port);
		mockServer = new WireMockServer(port);
		mockServer.start();
		httpClient = OkHttpClient.withCookieHandler();
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

		httpClient.get(urlFor(GET_PATH)).fetch();

		mockServer.verify(1, getRequestedFor(urlEqualTo(GET_PATH)));
	}

	@Test
	void shouldSendRequestWithHttpHeader() {
		mockServer.stubFor(get(GET_PATH));

		httpClient.get(urlFor(GET_PATH))
				.withHeader(new Header("test", "yes"))
				.fetch();

		mockServer.verify(
				1,
				getRequestedFor(urlEqualTo(GET_PATH))
						.withHeader("test", equalTo("yes"))
		);
	}

	@Test
	void shouldSendRequestAndParseResponseIntoObject() {
		mockServer.stubFor(
				get(GET_PATH)
						.willReturn(aResponse()
								.withBody(TestDataClass.testJson())
						)
		);

		TestDataClass response = httpClient.get(urlFor(GET_PATH)).fetch(TestDataClass.class);

		mockServer.verify(1, getRequestedFor(urlEqualTo(GET_PATH)));
		assertThat(response).isEqualTo(TestDataClass.testObject());
	}

	@Test
	void shouldThrowIllegalStateExceptionWhenHttpResponseIsNot200OK() {
		mockServer.stubFor(
				get(GET_PATH)
						.willReturn(aResponse().withStatus(400))
		);

		IllegalStateException thrownException = assertThrows(
				IllegalStateException.class,
				() -> httpClient.get(urlFor(GET_PATH)).fetch()
		);

		assertThat(thrownException).hasMessage("Non-200 response status code (code: 400)");
	}

	@Test
	void shouldSendPostWithEmptyJsonBody() {
		mockServer.stubFor(post(POST_PATH));

		httpClient.post(urlFor(POST_PATH)).fetch();

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
				.withJsonBody(new Credentials("user", "passwd"))
				.fetch();

		mockServer.verify(
				1,
				postRequestedFor(urlEqualTo(POST_PATH))
						.withRequestBody(equalTo("{\"UserName\":\"user\",\"Password\":\"passwd\"}"))
		);
	}

	private static String urlFor(String path) {
		return HOST + ":" + port + path;
	}

}
