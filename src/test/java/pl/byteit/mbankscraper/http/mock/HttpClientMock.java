package pl.byteit.mbankscraper.http.mock;

import pl.byteit.mbankscraper.http.HttpClient;
import pl.byteit.mbankscraper.http.mock.RequestBuilderMock.MockRequestBuilderAssert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public void reset() {
		savedRequests.clear();
		mockedResponses.clear();
	}

	public MockRequestBuilderAssert verify(String url) {
		return verify(1, url).get(0);
	}

	public List<MockRequestBuilderAssert> verify(int requestAmount, String url) {
		List<RequestBuilderMock> builders = savedRequests.get(url);
		if (builders.size() != requestAmount) {
			throw new RuntimeException("Expected " + requestAmount + " requests builder, but was " + builders.size());
		}
		return builders.stream()
				.map(MockRequestBuilderAssert::assertThatMockRequestBuilder)
				.collect(toList());
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
