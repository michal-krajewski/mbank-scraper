package pl.byteit.mbankscraper.http;

import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.*;
import pl.byteit.mbankscraper.util.JsonParser;
import pl.byteit.mbankscraper.util.TypeReferences;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

import static pl.byteit.mbankscraper.util.JsonParser.asJson;

public class OkHttpClientWrapper implements HttpClient {
	private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json");

	private final OkHttpClient client;

	private OkHttpClientWrapper(OkHttpClient client) {
		this.client = client;
	}

	public static HttpClient httpClientWithCookieHandler() {
		CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.cookieJar(new JavaNetCookieJar(cookieManager))
				.build();

		return new OkHttpClientWrapper(okHttpClient);
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
		public RequestBuilder withHeader(Header header) {
			builder.header(header.getName(), header.getValue());
			return this;
		}

		@Override
		public void perform() {
			executeCall();
		}

		@Override
		public <T> T perform(Class<T> responseType) {
			return perform(TypeReferences.typeOf(responseType));
		}

		@Override
		public <T> T perform(TypeReference<T> responseType) {
			Response response = executeCall();
			try {
				return JsonParser.parse(response.body().string(), responseType);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
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

		private void ensureProperBodyIsDefined() {
			if (!containsCustomBody) {
				builder.method(method, defaultBody());
			}
		}

		private RequestBody defaultBody() {
			return method.equals(GET) ? null : RequestBody.create(JSON_MEDIA_TYPE, "{}");
		}

		private static void verifyStatusCodeIsOk(Response response) {
			if (!response.isSuccessful())
				throw new IllegalStateException("Non-200 response status code (code: " + response.code() + ")");
		}

	}

}
