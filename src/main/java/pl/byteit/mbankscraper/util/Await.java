package pl.byteit.mbankscraper.util;

public class Await {

	public void forSeconds(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
