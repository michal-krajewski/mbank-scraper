package pl.byteit.mbankscrapper.util;

import org.junit.jupiter.api.Test;
import pl.byteit.mbankscrapper.operation.Credentials;
import pl.byteit.mbankscrapper.operation.mbank.account.StandardAccountInfo;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.byteit.mbankscrapper.ResourcesUtil.loadFileFromResourcesAsString;
import static pl.byteit.mbankscrapper.operation.mbank.account.StandardAccountInfoAssert.assertThatStandardAccountInfo;
import static pl.byteit.mbankscrapper.util.TypeUtil.asTypeReference;

class JsonParserTest {

	private static final String CREDENTIALS_JSON = "{\"UserName\":\"user\",\"Password\":\"passwd\"}";

	@Test
	void shouldParseObjectIntoJson() {
		String json = JsonParser.asJson(new Credentials("user".toCharArray(), "passwd".toCharArray()));

		assertThat(json).isEqualTo(CREDENTIALS_JSON);
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
		StandardAccountInfo account = JsonParser.parse(
				loadFileFromResourcesAsString("single-standard-account.json"),
				asTypeReference(StandardAccountInfo.class)
		);

		assertThatStandardAccountInfo(account)
				.hasNumber("11 2222 3333 4444 5555 6666 7777")
				.hasCurrency("PLN")
				.hasBalance(new BigDecimal("9280.55"));
	}

	@Test
	void shouldThrowIllegalStateExceptionWhenJsonCannotBeDeserializedIntoObject() {
		assertThrows(
				IllegalStateException.class,
				() -> JsonParser.parse("{\"field\":\"value\"}", asTypeReference(Credentials.class))
		);
	}

	@Test
	void shouldExtractJsonNodeValueFromSelectedField() {
		String balanceFieldValue = JsonParser.getFieldRawValueAsString(
				loadFileFromResourcesAsString("single-standard-account.json"),
				"balance"
		);

		assertThat(balanceFieldValue).isEqualTo("{\"value\":9280.55,\"currency\":\"PLN\"}");
	}

	@Test
	void shouldThrowIllegalStateExceptionWhenTryingToExtractJsonNodeValueFromCorruptedJson() {
		assertThrows(
				IllegalStateException.class,
				() -> JsonParser.getFieldRawValueAsString("{\"field\"}", "field")
		);
	}

}