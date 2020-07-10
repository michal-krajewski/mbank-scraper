package pl.byteit.mbankscrapper.operation.mbank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import pl.byteit.mbankscrapper.http.HttpHeader;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestVerificationToken {

	private final String requestVerificationToken;

	@JsonCreator
	public RequestVerificationToken(@JsonProperty("antiForgeryToken") String requestVerificationToken) {
		this.requestVerificationToken = requestVerificationToken;
	}

	public HttpHeader asHeader() {
		return new HttpHeader("x-request-verification-token", requestVerificationToken);
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof RequestVerificationToken))
			return false;
		RequestVerificationToken that = (RequestVerificationToken) o;
		return requestVerificationToken.equals(that.requestVerificationToken);
	}

	@Override public int hashCode() {
		return Objects.hash(requestVerificationToken);
	}
}
