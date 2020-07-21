package pl.byteit.mbankscraper.operation.mbank.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import pl.byteit.mbankscraper.http.Header;

import java.beans.ConstructorProperties;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestVerificationToken {

	private final String requestVerificationToken;

	@ConstructorProperties({ "antiForgeryToken" })
	public RequestVerificationToken(String requestVerificationToken) {
		this.requestVerificationToken = requestVerificationToken;
	}

	public Header asHeader() {
		return new Header("x-request-verification-token", requestVerificationToken);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof RequestVerificationToken))
			return false;
		RequestVerificationToken that = (RequestVerificationToken) o;
		return requestVerificationToken.equals(that.requestVerificationToken);
	}

	@Override
	public int hashCode() {
		return Objects.hash(requestVerificationToken);
	}
}
