package pl.byteit.scraper.http;

import java.util.Objects;

public class Header {

	final String name;
	final String value;

	public Header(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Header))
			return false;
		Header header = (Header) o;
		return name.equals(header.name) &&
				value.equals(header.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, value);
	}
}
