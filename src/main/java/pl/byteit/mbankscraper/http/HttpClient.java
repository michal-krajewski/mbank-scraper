package pl.byteit.mbankscraper.http;

import com.fasterxml.jackson.core.type.TypeReference;

public interface HttpClient {

	RequestBuilder post(String url);

	RequestBuilder get(String url);

	interface RequestBuilder {
		RequestBuilder withJsonBody(Object body);

		RequestBuilder withHeader(Header header);

		void perform();

		<T> T perform(Class<T> responseType);

		<T> T perform(TypeReference<T> responseType);
	}
}
