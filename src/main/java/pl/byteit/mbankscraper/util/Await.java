package pl.byteit.mbankscraper.util;

import java.util.function.Function;
import java.util.function.Supplier;

public class Await {

	public <T> T forResult(
			Supplier<T> resultProvider,
			Function<T, Boolean> isDoneCondition,
			int timeoutInSeconds,
			int secondsBetweenExecutions
	) {
		T result;
		int attempt = 0;
		do {
			result = resultProvider.get();
			attempt++;
			forSeconds(secondsBetweenExecutions);
		} while (!isDoneCondition.apply(result) && attempt * secondsBetweenExecutions < timeoutInSeconds);
		return result;
	}

	public void forSeconds(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
