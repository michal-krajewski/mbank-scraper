package pl.byteit.mbankscrapper.operation.mbank.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse {

	private final boolean successful;

	@JsonCreator
	public LoginResponse(@JsonProperty("successful") boolean successful) {
		this.successful = successful;
	}

	public boolean isSuccessful() {
		return successful;
	}
}
