package pl.byteit.mbankscrapper.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.byteit.mbankscrapper.MockUserInput;
import pl.byteit.mbankscrapper.operation.mbank.account.SavingAccountInfo;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Index.atIndex;
import static pl.byteit.mbankscrapper.MockUserInput.mockUserInput;
import static pl.byteit.mbankscrapper.operation.mbank.account.AccountTestUtil.savingAccountInfo;

class CommandLineInterfaceTest {

	private static final String MOCKED_INPUT = "mocked-input";

	private MockUserInput mockUserInput = mockUserInput();
	private CommandLineInterface cli;
	private static List<String> mockPrinter;

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