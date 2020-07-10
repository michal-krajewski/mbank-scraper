package pl.byteit.mbankscrapper.http.mock;

import com.fasterxml.jackson.core.type.TypeReference;
import org.assertj.core.api.AbstractAssert;
import pl.byteit.mbankscrapper.http.HttpClient;
import pl.byteit.mbankscrapper.http.HttpHeader;
import pl.byteit.mbankscrapper.util.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.byteit.mbankscrapper.util.TypeUtil.asTypeReference;

public class RequestBuilderMock implements HttpClient.RequestBuilder {

	private HttpClientMock httpClient;
	private String url;
	private String method;
	private Object body = null;
	private List<HttpHeader> headers = new ArrayList<>();
	private boolean customPreprocessor = false;
	private boolean performed = false;

	public RequestBuilderMock(String url, String method, HttpClientMock httpClient) {
		this.url = url;
		this.method = method;
		this.httpClient = httpClient;
	}

	@Override
	public HttpClient.RequestBuilder withJsonBody(Object body) {
		this.body = body;
		return this;
	}

	@Override
	public HttpClient.RequestBuilder withHeader(HttpHeader header) {
		this.headers.add(header);
		return this;
	}

	@Override
	public HttpClient.RequestBuilder withResponsePreprocessor(Function<String, String> responsePreprocessor) {
		customPreprocessor = true;
		return this;
	}

	@Override
	public void perform() {
		performed = true;
	}

	@Override
	public <T> T perform(Class<T> responseType) {
		return perform(asTypeReference(responseType));
	}

	@Override
	public <T> T perform(TypeReference<T> responseType) {
		perform();
		return JsonParser.parse(httpClient.getMockedResponse(url), responseType);
	}

	public static class MockRequestBuilderAssert extends AbstractAssert<MockRequestBuilderAssert, RequestBuilderMock> {

		private MockRequestBuilderAssert(RequestBuilderMock requestBuilderMock) {
			super(requestBuilderMock, MockRequestBuilderAssert.class);
		}

		static MockRequestBuilderAssert assertThatMockRequestBuilder(RequestBuilderMock requestBuilderMock) {
			return new MockRequestBuilderAssert(requestBuilderMock);
		}

		public MockRequestBuilderAssert hasPostMethod() {
			isNotNull();
			assertThat(actual.method).isEqualTo("POST");
			return this;
		}

		public MockRequestBuilderAssert hasGetMethod() {
			isNotNull();
			assertThat(actual.method).isEqualTo("GET");
			return this;
		}

		public MockRequestBuilderAssert hasBodyDefined() {
			isNotNull();
			assertThat(actual.body).isNotNull();
			return this;
		}

		public MockRequestBuilderAssert hasNoBodyDefined() {
			isNotNull();
			assertThat(actual.body).isNull();
			return this;
		}

		public MockRequestBuilderAssert hasHeaders(HttpHeader... headers) {
			isNotNull();
			assertThat(actual.headers).containsExactlyInAnyOrder(headers);
			return this;
		}

		public MockRequestBuilderAssert hasCustomPreprocessorDefined() {
			isNotNull();
			assertThat(actual.customPreprocessor).isTrue();
			return this;
		}

		public MockRequestBuilderAssert hasNoCustomPreprocessorDefined() {
			isNotNull();
			assertThat(actual.customPreprocessor).isFalse();
			return this;
		}

		public MockRequestBuilderAssert wasPerformed() {
			isNotNull();
			assertThat(actual.performed).isTrue();
			return this;
		}
	}
}