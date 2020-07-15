package pl.byteit.mbankscraper.operation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Credentials {

	@JsonProperty("UserName")
	private final char[] username;

	@JsonProperty("Password")
	private final char[] password;

	public Credentials(char[] username, char[] password) {
		this.username = username;
		this.password = password;
	}

}
