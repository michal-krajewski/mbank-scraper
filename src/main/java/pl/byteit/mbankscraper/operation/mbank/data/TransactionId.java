package pl.byteit.mbankscraper.operation.mbank.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.beans.ConstructorProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionId {

	@JsonProperty("TranId")
	private final String tranId;

	@ConstructorProperties({ "TranId" })
	public TransactionId(String tranId) {
		this.tranId = tranId;
	}

}
