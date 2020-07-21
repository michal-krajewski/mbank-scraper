package pl.byteit.mbankscraper.operation.mbank.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Credentials {

	@JsonProperty("UserName")
	private final String username;

	@JsonProperty("Password")
	private final String password;

	public Credentials(String username, String password) {
		this.username = username;
		this.password = password;
	}

}
