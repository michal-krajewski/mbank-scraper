package pl.byteit.mbankscraper.operation.mbank.authentication;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import pl.byteit.mbankscraper.operation.AuthenticationResult;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static pl.byteit.mbankscraper.operation.AuthenticationResult.mobileSecondFactorAuthenticationRequired;
import static pl.byteit.mbankscraper.operation.AuthenticationResult.successful;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE)
public class LoginResponse {

	private static final String SECOND_FACTOR_REQUIRED_REDIRECT_URL = "/authorization";

	private final boolean successful;
	private final String redirectUrl;

	@JsonCreator
	public LoginResponse(@JsonProperty("successful") boolean successful, @JsonProperty("redirectUrl") String redirectUrl) {
		this.successful = successful;
		this.redirectUrl = redirectUrl;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public AuthenticationResult getStatus() {
		if (successful) {
			return SECOND_FACTOR_REQUIRED_REDIRECT_URL.equals(redirectUrl) ? mobileSecondFactorAuthenticationRequired() : successful();
		} else {
			return null;
		}
	}
}
