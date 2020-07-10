package pl.byteit.mbankscrapper.operation.mbank.account;

import org.assertj.core.api.AbstractAssert;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class StandardAccountInfoAssert extends AbstractAssert<StandardAccountInfoAssert, StandardAccountInfo> {

	private StandardAccountInfoAssert(StandardAccountInfo standardAccountInfo) {
		super(standardAccountInfo, StandardAccountInfoAssert.class);
	}

	public static StandardAccountInfoAssert assertThatStandardAccountInfo(StandardAccountInfo standardAccountInfo) {
		return new StandardAccountInfoAssert(standardAccountInfo);
	}

	public StandardAccountInfoAssert hasNumber(String expectedNumber) {
		isNotNull();
		assertThat(actual.getNumber()).isEqualTo(expectedNumber);
		return this;
	}

	public StandardAccountInfoAssert hasCurrency(String expectedCurrency) {
		isNotNull();
		assertThat(actual.getCurrency()).isEqualTo(expectedCurrency);
		return this;
	}

	public StandardAccountInfoAssert hasBalance(BigDecimal expectedBalance) {
		isNotNull();
		assertThat(extractAmountFromPrintedInfo()).isEqualTo(expectedBalance.toString());
		return this;
	}

	private String extractAmountFromPrintedInfo() {
		return actual.print().substring(54).split(" ")[0];
	}

}
