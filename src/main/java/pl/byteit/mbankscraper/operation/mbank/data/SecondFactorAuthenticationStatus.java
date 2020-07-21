package pl.byteit.mbankscraper.operation.mbank.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SecondFactorAuthenticationStatus {

	private static final List<String> IN_PROGRESS_STATUSES = Arrays.asList("Prepared", "PreAuthorized");
	private static final String SUCCESSFUL_STATUS = "Authorized";

	public final String status;

	@ConstructorProperties({ "Status" })
	public SecondFactorAuthenticationStatus(String status) {
		this.status = status;
	}

	public boolean isFinished() {
		return !IN_PROGRESS_STATUSES.contains(status);
	}

	public boolean isSuccessful() {
		return SUCCESSFUL_STATUS.equals(status);
	}
}
