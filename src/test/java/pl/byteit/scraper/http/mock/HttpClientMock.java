package pl.byteit.scraper.http.mock;

import org.junit.jupiter.api.Assertions;
import pl.byteit.scraper.http.HttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

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

	public void verifyRequest(int times, String url, String method) {
		List<RequestBuilderMock> requests = savedRequests.getOrDefault(url, emptyList()).stream()
				.filter(request -> request.method.equals(method))
				.collect(toList());
		if (requests.size() != times) {
			Assertions.fail("Request " + method + " " + url + " was expected to be performed " + times + " but was: " + requests.size());
		}
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
