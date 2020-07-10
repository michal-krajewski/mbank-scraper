package pl.byteit.mbankscrapper.operation.mbank.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
class AuthenticationStatus {

	private static final List<String> IN_PROGRESS_STATUSES = Arrays.asList("Prepared", "PreAuthorized");
	private static final String SUCCESSFUL_STATUS = "Authorized";

	private final String status;

	@JsonCreator
	public AuthenticationStatus(@JsonProperty("Status") String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public boolean isInProgress() {
		return IN_PROGRESS_STATUSES.contains(status);
	}

	public boolean isSuccessful() {
		return SUCCESSFUL_STATUS.equals(status);
	}
}
