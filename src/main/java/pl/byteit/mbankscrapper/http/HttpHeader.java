package pl.byteit.mbankscrapper.http;

import java.util.Objects;

public class HttpHeader {

	private final String name;
	private final String value;

	public HttpHeader(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof HttpHeader))
			return false;
		HttpHeader header = (HttpHeader) o;
		return name.equals(header.name) &&
				value.equals(header.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, value);
	}
}
