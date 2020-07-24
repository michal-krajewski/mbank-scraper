package pl.byteit.scraper.mbank.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.beans.ConstructorProperties;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = ANY)
public class LoginResponse {

	public final boolean successful;
	public final String redirectUrl;

	@ConstructorProperties({ "successful", "redirectUrl" })
	public LoginResponse(boolean successful, String redirectUrl) {
		this.successful = successful;
		this.redirectUrl = redirectUrl;
	}

}
