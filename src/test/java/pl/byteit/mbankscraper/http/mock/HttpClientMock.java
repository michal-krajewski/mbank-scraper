package pl.byteit.mbankscraper.http.mock;

import pl.byteit.mbankscraper.http.HttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClientMock implements HttpClient {

	private final Map<String, List<RequestBuilderMock>> savedRequests;
	private final Map<String, String> mockedResponses;

	public HttpClientMock() {
		savedRequests = new HashMap<>();
		mockedResponses = new HashMap<>();
	}

	@Override
	public RequestBuilder post(String url) {
		RequestBuilderMock requestBuilder = new RequestBuilderMock(url, "POST", this);
		saveRequestBuilder(url, requestBuilder);
		return requestBuilder;
	}

	@Override
	public RequestBuilder get(String url) {
		RequestBuilderMock requestBuilder = new RequestBuilderMock(url, "GET", this);
		saveRequestBuilder(url, requestBuilder);
		return requestBuilder;
	}

	public void mockResponse(String url, String jsonValue) {
		mockedResponses.put(url, jsonValue);
	}

	public void reset() {
		savedRequests.clear();
		mockedResponses.clear();
	}

	String getMockedResponse(String url) {
		return mockedResponses.get(url);
	}

	private void saveRequestBuilder(String url, RequestBuilderMock requestBuilder) {
		if (savedRequests.containsKey(url)) {
			savedRequests.get(url).add(requestBuilder);
		} else {
			List<RequestBuilderMock> builders = new ArrayList<>();
			builders.add(requestBuilder);
			savedRequests.put(url, builders);
		}
	}

}
