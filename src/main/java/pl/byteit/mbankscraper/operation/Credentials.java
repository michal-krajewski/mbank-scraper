package pl.byteit.mbankscraper.operation;

import com.fasterxml.jackson.annotation.JsonGetter;

public class Credentials {

	private final char[] username;
	private final char[] password;

	public Credentials(char[] username, char[] password) {
		this.username = username;
		this.password = password;
	}

	@JsonGetter("UserName")
	private char[] getUsername() {
		return username;
	}

	@JsonGetter("Password")
	private char[] getPassword() {
		return password;
	}
}
