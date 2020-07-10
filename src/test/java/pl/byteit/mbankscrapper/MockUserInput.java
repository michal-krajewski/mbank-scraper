package pl.byteit.mbankscrapper;

import java.util.List;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class MockUserInput {

	private int inputFetchingAttempts = 0;
	private List<String> mockedInput = emptyList();

	public static MockUserInput mockUserInput() {
		return new MockUserInput();
	}

	public void mockInput(String... input) {
		mockedInput = stream(input).collect(toList());
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
