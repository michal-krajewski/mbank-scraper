package pl.byteit.mbankscrapper.util;

public class AwaitUtil {

	public void forSeconds(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
