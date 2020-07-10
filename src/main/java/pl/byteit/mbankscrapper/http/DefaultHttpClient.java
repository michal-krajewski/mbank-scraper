package pl.byteit.mbankscrapper.http;

import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.*;
import pl.byteit.mbankscrapper.util.JsonParser;

import java.io.IOException;
import java.util.function.Function;

import static pl.byteit.mbankscrapper.util.JsonParser.asJson;
import static pl.byteit.mbankscrapper.util.TypeUtil.asTypeReference;

public class DefaultHttpClient implements HttpClient {
	private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json");

	private final OkHttpClient client;

	public DefaultHttpClient(OkHttpClient client) {
		this.client = client;
	}

	@Override
	public RequestBuilder post(String url) {
		return HttpRequestBuilder.post(url, client);
	}

	@Override
	public RequestBuilder get(String url) {
		return HttpRequestBuilder.get(url, client);
	}

	public static class HttpRequestBuilder implements RequestBuilder {

		private static final String GET = "GET";
		private static final String POST = "POST";

		private final OkHttpClient client;
		private final String method;
		private final Request.Builder builder;
		private boolean containsCustomBody = false;
		private Function<String, String> responsePreprocessor = Function.identity();

		private HttpRequestBuilder(OkHttpClient client, String method, String requestUrl) {
			this.client = client;
			this.method = method;
			this.builder = new Request.Builder();
			builder.url(requestUrl);
		}

		static RequestBuilder post(String requestUrl, OkHttpClient okHttpClient) {
			return new HttpRequestBuilder(okHttpClient, POST, requestUrl);
		}

		static RequestBuilder get(String requestUrl, OkHttpClient okHttpClient) {
			return new HttpRequestBuilder(okHttpClient, GET, requestUrl);
		}

		@Override
		public RequestBuilder withJsonBody(Object body) {
			builder.method(method, RequestBody.create(JSON_MEDIA_TYPE, asJson(body)));
			containsCustomBody = true;
			return this;
		}

		@Override
		public RequestBuilder withHeader(HttpHeader header) {
			builder.header(header.getName(), header.getValue());
			return this;
		}

		@Override
		public RequestBuilder withResponsePreprocessor(Function<String, String> responsePreprocessor) {
			this.responsePreprocessor = responsePreprocessor;
			return this;
		}

		@Override
		public void perform() {
			executeCall();
		}

		@Override
		public <T> T perform(Class<T> responseType) {
			return perform(asTypeReference(responseType));
		}

		@Override
		public <T> T perform(TypeReference<T> responseType) {
			Response response = executeCall();

			String processedResponse = processResponse(response);

			return JsonParser.parse(processedResponse, responseType);
		}

		private Response executeCall() {
			ensureProperBodyIsDefined();
			try {
				Response response = client.newCall(builder.build()).execute();
				verifyStatusCodeIsOk(response);
				return response;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private void verifyStatusCodeIsOk(Response response) {
			if (!response.isSuccessful())
				throw new IllegalStateException("Non-200 response status code (code: " + response.code() + ")");
		}

		private String processResponse(Response response) {
			try {
				return responsePreprocessor.apply(response.body().string());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private void ensureProperBodyIsDefined() {
			if (!containsCustomBody) {
				builder.method(method, defaultBody());
			}
		}

		private RequestBody defaultBody() {
			return method.equals(GET) ? null : RequestBody.create(JSON_MEDIA_TYPE, "{}");
		}

	}

}
