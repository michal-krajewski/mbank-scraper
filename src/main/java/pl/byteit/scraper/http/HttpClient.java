package pl.byteit.scraper.http;

import com.fasterxml.jackson.core.type.TypeReference;

public interface HttpClient {

	RequestBuilder post(String url);

	RequestBuilder get(String url);

	interface RequestBuilder {
		RequestBuilder withJsonBody(Object body);

		RequestBuilder withHeader(Header header);

		void fetch();

		<T> T fetch(Class<T> responseType);

		<T> T fetch(TypeReference<T> responseType);
	}
}
