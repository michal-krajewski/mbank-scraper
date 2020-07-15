package pl.byteit.mbankscraper.operation.mbank;

public interface Requests {

	String MBANK_BASE_URL = "https://online.mbank.pl";
	String GET_STANDARD_ACCOUNTS_URL = MBANK_BASE_URL + "/pl/MyDesktop/Dashboard/GetProducts";
	String LOGIN_URL = MBANK_BASE_URL + "/pl/LoginMain/Account/JsonLogin";
	String GET_REQUEST_VERIFICATION_TOKEN_URL = MBANK_BASE_URL + "/pl/setup/data";
	String GET_SAVING_ACCOUNTS_URL = MBANK_BASE_URL + "/pl/SavingGoals/Home/GetSavingProducts";
	String FETCH_AUTHENTICATION_ID_URL = MBANK_BASE_URL + "/pl/Sca/GetScaAuthorizationData";
	String START_SECOND_FACTOR_AUTHENTICATION_URL = MBANK_BASE_URL + "/api/auth/initprepare";
	String CHECK_AUTHENTICATION_STATUS_URL = MBANK_BASE_URL + "/api/auth/status";
	String EXECUTE_AUTHENTICATION_URL = MBANK_BASE_URL + "/api/auth/execute";
	String FINALIZE_AUTHENTICATION_URL = MBANK_BASE_URL + "/pl/Sca/FinalizeAuthorization";

}
