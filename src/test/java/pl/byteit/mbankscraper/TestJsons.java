package pl.byteit.mbankscraper;

public class TestJsons {

	public static String antiForgeryToken(String tokenValue) {
		return "{\n"
				+ "  \"entity\": \"1234\",\n"
				+ "  \"antiForgeryToken\": \"" + tokenValue + "\"\n"
				+ "}";
	}

	public static String authenticationIdentifier(String scaAuthorizationId) {
		return "{\n"
				+ "  \"ScaAuthorizationId\": \"" + scaAuthorizationId + "\"\n"
				+ "}";
	}

	public static String savingAccounts(SavingAccountTestData savingAccount1, SavingAccountTestData savingAccount2) {
		return "{"
				+ "  \"goals\": [\n"
				+ "    {\n"
				+ "      \"accountNumber\": \"" + savingAccount1.linkedAccountNumber + "\",\n"
				+ "      \"currentGoalAmount\": " + savingAccount1.currentAmount + ",\n"
				+ "      \"fullName\": \"" + savingAccount1.name + "\"\n"
				+ "    },\n"
				+ "    {\n"
				+ "      \"accountNumber\": \"" + savingAccount2.linkedAccountNumber + "\",\n"
				+ "      \"currentGoalAmount\": " + savingAccount2.currentAmount + ",\n"
				+ "      \"fullName\": \"" + savingAccount2.name + "\"\n"
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

	public static String startAuthentication(String tranId) {
		return "{\n"
				+ "  \"TranId\": \"" + tranId + "\",\n"
				+ "  \"DeviceName\": \"Pixel\"\n"
				+ "}";
	}

	public static String loginResponse(boolean successful, String redirectUrl) {
		return "{\n"
				+ "  \"successful\": " + successful + ",\n"
				+ "  \"redirectUrl\": \"" + redirectUrl + "\"\n"
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
