package pl.byteit.mbankscrapper.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonParser {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static String getFieldRawValueAsString(String json, String fieldName) {
		try {
			return OBJECT_MAPPER.readTree(json).path(fieldName).toString();
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Could not parse field " + fieldName, e);
		}
	}

	public static String asJson(Object object) {
		try {
			return OBJECT_MAPPER.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}

	public static <T> T parse(String json, TypeReference<T> responseType) {
		try {
			return OBJECT_MAPPER.readValue(json, responseType);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}

}
