package pl.byteit.mbankscraper;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class MockUserInput {

	private int inputFetchingAttempts = 0;
	private List<String> mockedInput = emptyList();

	public void mockInput(String... input) {
		mockedInput = asList(input);
	}

	public String getInput() {
		return mockedInput.get(inputFetchingAttempts++);
	}

	public int getInvocationAmount() {
		return inputFetchingAttempts;
	}

	public void reset() {
		inputFetchingAttempts = 0;
		mockedInput = emptyList();
	}

}
