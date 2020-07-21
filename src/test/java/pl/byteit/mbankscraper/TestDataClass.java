package pl.byteit.mbankscraper;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.beans.ConstructorProperties;
import java.util.Objects;

public class TestDataClass {

	@JsonProperty("primaryValue")
	private final String primaryValue;

	@JsonProperty("secondaryValue")
	private final String secondaryValue;

	@ConstructorProperties({ "primaryValue", "secondaryValue" })
	public TestDataClass(String primaryValue, String secondaryValue) {
		this.primaryValue = primaryValue;
		this.secondaryValue = secondaryValue;
	}

	public static TestDataClass testObject() {
		return new TestDataClass("value", "yet-another-value");
	}

	public static String testJson() {
		return "{\"primaryValue\":\"value\",\"secondaryValue\":\"yet-another-value\"}";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof TestDataClass))
			return false;
		TestDataClass that = (TestDataClass) o;
		return primaryValue.equals(that.primaryValue) &&
				secondaryValue.equals(that.secondaryValue);
	}

	@Override
	public int hashCode() {
		return Objects.hash(primaryValue, secondaryValue);
	}
}
