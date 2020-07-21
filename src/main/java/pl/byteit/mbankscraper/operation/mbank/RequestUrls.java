package pl.byteit.mbankscraper.operation.mbank;

public class RequestUrls {

	private static final String MBANK_BASE_URL = "https://online.mbank.pl";
	public static final String GET_STANDARD_ACCOUNTS_URL = MBANK_BASE_URL + "/pl/MyDesktop/Dashboard/GetProducts";
	public static final String LOGIN_URL = MBANK_BASE_URL + "/pl/LoginMain/Account/JsonLogin";
	public static final String GET_REQUEST_VERIFICATION_TOKEN_URL = MBANK_BASE_URL + "/pl/setup/data";
	public static final String GET_SAVING_ACCOUNTS_URL = MBANK_BASE_URL + "/pl/SavingGoals/Home/GetSavingProducts";
	public static final String FETCH_AUTHENTICATION_ID_URL = MBANK_BASE_URL + "/pl/Sca/GetScaAuthorizationData";
	public static final String START_SECOND_FACTOR_AUTHENTICATION_URL = MBANK_BASE_URL + "/api/auth/initprepare";
	public static final String CHECK_AUTHENTICATION_STATUS_URL = MBANK_BASE_URL + "/api/auth/status";
	public static final String EXECUTE_AUTHENTICATION_URL = MBANK_BASE_URL + "/api/auth/execute";
	public static final String FINALIZE_AUTHENTICATION_URL = MBANK_BASE_URL + "/pl/Sca/FinalizeAuthorization";

}
