package pl.byteit.scraper.mbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.beans.ConstructorProperties;

public class TransactionId {

	@JsonProperty("TranId")
	private final String tranId;

	@ConstructorProperties({ "TranId" })
	public TransactionId(String tranId) {
		this.tranId = tranId;
	}

}
