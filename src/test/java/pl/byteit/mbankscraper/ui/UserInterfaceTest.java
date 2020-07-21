package pl.byteit.mbankscraper.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class UserInterfaceTest {

	@Mock
	private Supplier<String> mockInput;

	@Mock
	private Consumer<String> mockPrint;

	private UserInterface cli;

	@BeforeEach
	void beforeEachCommandLineInterfaceTest() {
		initMocks(this);
		cli = new UserInterface(() -> mockInput.get(), msg -> mockPrint.accept(msg));
	}

	@Test
	void shouldPrintMessage() {
		String printedMessage = "the message";

		cli.print(printedMessage);

		verify(mockPrint).accept(printedMessage);
	}

	@Test
	void shouldPrintPrintableObject() {
		PrintableTestClass printable = new PrintableTestClass("test-value");

		cli.print(printable);

		verify(mockPrint).accept(printable.print());
	}

	@Test
	void shouldPrintPrintableCollection() {
		PrintableTestClass printable = new PrintableTestClass("test-value");
		PrintableTestClass printable2 = new PrintableTestClass("yet-another-test-value");

		cli.print(asList(printable, printable2));

		verify(mockPrint).accept(printable.print());
		verify(mockPrint).accept(printable2.print());
	}

	@Test
	void shouldPromptForInput() {
		String mockedInput = "input";
		String printedMessage = "Provide input";
		when(mockInput.get()).thenReturn(mockedInput);

		String input = cli.promptForInput(printedMessage);

		verify(mockPrint).accept(printedMessage);
		assertThat(input).isEqualTo(mockedInput);
	}

	static class PrintableTestClass implements Printable {

		private final String value;

		public PrintableTestClass(String value) {
			this.value = value;
		}

		@Override
		public String print() {
			return "Test value: " + value;
		}
	}
}
