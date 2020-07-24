package pl.byteit.scraper.http.mock;

import com.fasterxml.jackson.core.type.TypeReference;
import pl.byteit.scraper.http.Header;
import pl.byteit.scraper.http.HttpClient;
import pl.byteit.scraper.util.JsonParser;
import pl.byteit.scraper.util.TypeReferences;

import java.util.ArrayList;
import java.util.List;

public class RequestBuilderMock implements HttpClient.RequestBuilder {

	public final HttpClientMock httpClient;
	public final String url;
	public final String method;
	public final List<Header> headers = new ArrayList<>();
	public Object body = null;
	public boolean performed = false;

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
	public HttpClient.RequestBuilder withHeader(Header header) {
		this.headers.add(header);
		return this;
	}

	@Override
	public void fetch() {
		performed = true;
	}

	@Override
	public <T> T fetch(Class<T> responseType) {
		return fetch(TypeReferences.typeOf(responseType));
	}

	@Override
	public <T> T fetch(TypeReference<T> responseType) {
		fetch();
		return JsonParser.parse(httpClient.getMockedResponse(url), responseType);
	}

}
