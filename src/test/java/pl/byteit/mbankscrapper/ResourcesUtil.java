package pl.byteit.mbankscrapper;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ResourcesUtil {

	public static String loadFileFromResourcesAsString(String filename) {
		try {
			return IOUtils.toString(new FileInputStream(resourceFile(filename)), UTF_8);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static File resourceFile(String filename) {
		return new File("src/test/resources/" + filename);
	}

}
