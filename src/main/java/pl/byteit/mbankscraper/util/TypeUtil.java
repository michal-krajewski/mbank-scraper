package pl.byteit.mbankscraper.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Type;
import java.util.List;

public class TypeUtil {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static <T> TypeReference<List<T>> listTypeOf(Class<T> aClass) {
		return new TypeReference<List<T>>() {
			@Override
			public Type getType() {
				return OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, aClass);
			}
		};
	}

	public static <T> TypeReference<T> asTypeReference(Class<T> aClass) {
		return new TypeReference<T>() {
			@Override
			public Type getType() {
				return aClass;
			}
		};
	}

}
