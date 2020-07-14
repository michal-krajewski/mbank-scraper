package pl.byteit.mbankscraper.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.byteit.mbankscraper.MockUserInput;
import pl.byteit.mbankscraper.operation.mbank.account.SavingAccountInfo;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Index.atIndex;
import static pl.byteit.mbankscraper.operation.mbank.account.AccountInfoTestFactory.savingAccountInfo;

class CommandLineInterfaceTest {

	private static final String MOCKED_INPUT = "mocked-input";

	private static List<String> mockPrinter;
	private final MockUserInput mockUserInput = new MockUserInput();
	private CommandLineInterface cli;

	@BeforeEach
	void beforeEachCommandLineInterfaceTest() {
		mockPrinter = new ArrayList<>();
		mockUserInput.reset();
		cli = new CommandLineInterface(() -> mockUserInput.getInput(), msg -> mockPrinter.add(msg));
	}

	@Test
	void shouldPrintMessage() {
		String printedMessage = "the message";

		cli.print(printedMessage);

		assertThat(mockPrinter)
				.hasSize(1)
				.contains(printedMessage, atIndex(0));
	}

	@Test
	void shouldPrintPrintableObject() {
		SavingAccountInfo savingAccountInfo = savingAccountInfo("S1", "1234", "10.50", "PLN");

		cli.print(savingAccountInfo);

		assertThat(mockPrinter)
				.hasSize(1)
				.contains(savingAccountInfo.print(), atIndex(0));
	}

	@Test
	void shouldPrintPrintableCollection() {
		SavingAccountInfo savingAccountInfo = savingAccountInfo("S1", "1234", "10.50", "PLN");
		SavingAccountInfo savingAccountInfo2 = savingAccountInfo("S2", "3456", "12313.50", "EUR");

		cli.print(asList(savingAccountInfo, savingAccountInfo2));

		assertThat(mockPrinter)
				.hasSize(2)
				.contains(savingAccountInfo.print(), atIndex(0))
				.contains(savingAccountInfo2.print(), atIndex(1));
	}

	@Test
	void shouldPromptForInput() {
		mockUserInput.mockInput(MOCKED_INPUT);
		String printedMessage = "Provide input";

		char[] input = cli.promptForInput(printedMessage);

		assertThat(mockPrinter)
				.hasSize(1)
				.contains(printedMessage, atIndex(0));
		assertThat(input)
				.containsExactly(MOCKED_INPUT.toCharArray());
		assertThat(mockUserInput.getInvocationAmount())
				.isEqualTo(1);
	}

}