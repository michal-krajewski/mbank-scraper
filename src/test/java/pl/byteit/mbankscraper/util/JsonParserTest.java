package pl.byteit.mbankscraper.util;

import org.junit.jupiter.api.Test;
import pl.byteit.mbankscraper.operation.Credentials;
import pl.byteit.mbankscraper.operation.mbank.account.StandardAccountInfo;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.byteit.mbankscraper.TestJsons.singleStandardAccount;
import static pl.byteit.mbankscraper.operation.mbank.account.StandardAccountInfoAssert.assertThatStandardAccountInfo;

class JsonParserTest {

	private static final String CREDENTIALS_JSON = "{\"UserName\":\"user\",\"Password\":\"passwd\"}";
	public static final String ACCOUNT_NUMBER = "11 2222 3333 4444 5555 6666 7777";
	public static final String ACCOUNT_CURRENCY = "PLN";
	public static final String ACCOUNT_BALANCE = "9280.55";

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
				singleStandardAccount(ACCOUNT_NUMBER, ACCOUNT_BALANCE, ACCOUNT_CURRENCY),
				TypeReferences.typeOf(StandardAccountInfo.class)
		);

		assertThatStandardAccountInfo(account)
				.hasNumber(ACCOUNT_NUMBER)
				.hasCurrency(ACCOUNT_CURRENCY)
				.hasBalance(new BigDecimal(ACCOUNT_BALANCE));
	}

	@Test
	void shouldThrowIllegalStateExceptionWhenJsonCannotBeDeserializedIntoObject() {
		assertThrows(
				IllegalStateException.class,
				() -> JsonParser.parse("{\"field\":\"value\"}", TypeReferences.typeOf(Credentials.class))
		);
	}

	@Test
	void shouldExtractJsonNodeValueFromSelectedField() {
		String balanceFieldValue = JsonParser.getFieldRawValueAsString(
				singleStandardAccount(ACCOUNT_NUMBER, ACCOUNT_BALANCE, ACCOUNT_CURRENCY),
				"balance"
		);

		assertThat(balanceFieldValue).isEqualTo("{\"value\":" + ACCOUNT_BALANCE + ",\"currency\":\"" + ACCOUNT_CURRENCY + "\"}");
	}

	@Test
	void shouldThrowIllegalStateExceptionWhenTryingToExtractJsonNodeValueFromCorruptedJson() {
		assertThrows(
				IllegalStateException.class,
				() -> JsonParser.getFieldRawValueAsString("{\"field\"}", "field")
		);
	}

}
