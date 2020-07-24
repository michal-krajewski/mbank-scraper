package pl.byteit.scraper.util;

import org.junit.jupiter.api.Test;
import pl.byteit.scraper.TestDataClass;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonParserTest {

	@Test
	void shouldParseObjectIntoJson() {
		String json = JsonParser.asJson(TestDataClass.testObject());

		assertThat(json).isEqualTo(TestDataClass.testJson());
	}

	@Test
	void shouldThrowIllegalStateExceptionWhenObjectCannotBeParsedIntoJson() {
		assertThrows(
				IllegalStateException.class,
				() -> JsonParser.asJson(new Object())
		);
	}

	@Test
	void shouldDeserializeCorrectJsonToObject() {
		TestDataClass parsedObject = JsonParser.parse(
				TestDataClass.testJson(),
				TypeReferences.typeOf(TestDataClass.class)
		);

		assertThat(parsedObject).isEqualTo(TestDataClass.testObject());
	}

	@Test
	void shouldThrowIllegalStateExceptionWhenJsonCannotBeDeserializedIntoObject() {
		assertThrows(
				IllegalStateException.class,
				() -> JsonParser.parse("{\"field\":\"value\"}", TypeReferences.typeOf(TestDataClass.class))
		);
	}

	@Test
	void shouldExtractJsonNodeValueFromSelectedField() {
		String balanceFieldValue = JsonParser.getFieldRawValueAsString(
				TestDataClass.testJson(),
				"secondaryValue"
		);

		assertThat(balanceFieldValue).isEqualTo("\"yet-another-value\"");
	}

	@Test
	void shouldThrowIllegalStateExceptionWhenTryingToExtractJsonNodeValueFromCorruptedJson() {
		assertThrows(
				IllegalStateException.class,
				() -> JsonParser.getFieldRawValueAsString("{\"field\"}", "field")
		);
	}

}
