package pl.byteit.scraper;

public class TestResponses {

	public static String antiForgeryToken() {
		return "{\n"
				+ "  \"entity\": \"1234\",\n"
				+ "  \"antiForgeryToken\": \"token-value\"\n"
				+ "}";
	}

	public static String authenticationIdentifier() {
		return "{\n"
				+ "  \"ScaAuthorizationId\": \"auth-id\"\n"
				+ "}";
	}

	public static String savingAccounts(SavingAccountTestData first, SavingAccountTestData second) {
		return "{"
				+ "  \"goals\": [\n"
				+ "    {\n"
				+ "      \"accountNumber\": \"" + first.linkedAccountNumber + "\",\n"
				+ "      \"currentGoalAmount\": " + first.currentAmount + ",\n"
				+ "      \"fullName\": \"" + first.name + "\"\n"
				+ "    },\n"
				+ "    {\n"
				+ "      \"accountNumber\": \"" + second.linkedAccountNumber + "\",\n"
				+ "      \"currentGoalAmount\": " + second.currentAmount + ",\n"
				+ "      \"fullName\": \"" + second.name + "\"\n"
				+ "    }\n"
				+ "  ]"
				+ "}";
	}

	public static String standardAccounts(String number, String balance, String currency) {
		return "{"
				+ "  \"products\": [\n"
				+ "    {\n"
				+ "      \"number\": \"" + number + "\",\n"
				+ "      \"balance\": {\n"
				+ "        \"value\": " + balance + ",\n"
				+ "        \"currency\": \"" + currency + "\"\n"
				+ "      }\n"
				+ "    }\n"
				+ "  ]\n"
				+ "}";
	}

	public static String startAuthentication() {
		return "{\n"
				+ "  \"TranId\": \"tran-id\",\n"
				+ "  \"DeviceName\": \"Pixel\"\n"
				+ "}";
	}

	public static String loginFailedResponse() {
		return "{\n"
				+ "  \"successful\": false,\n"
				+ "  \"redirectUrl\": \"/error\"\n"
				+ "}";
	}

	public static String successfulLoginWithSecondFactorRequired() {
		return "{\n"
				+ "  \"successful\": true,\n"
				+ "  \"redirectUrl\": \"/authorization\"\n"
				+ "}";
	}

	public static String successfulLoginWithNoSecondFactorRequired() {
		return "{\n"
				+ "  \"successful\": true,\n"
				+ "  \"redirectUrl\": \"/dashboard\"\n"
				+ "}";
	}

	public static class SavingAccountTestData {
		public final String linkedAccountNumber;
		public final String currentAmount;
		public final String name;

		public SavingAccountTestData(String linkedAccountNumber, String currentAmount, String name) {
			this.linkedAccountNumber = linkedAccountNumber;
			this.currentAmount = currentAmount;
			this.name = name;
		}
	}
}
