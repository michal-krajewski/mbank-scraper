package pl.byteit.mbankscraper.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.byteit.mbankscraper.MockUserInput;
import pl.byteit.mbankscraper.operation.AccountInfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Index.atIndex;
import static pl.byteit.mbankscraper.operation.AccountInfo.AccountType.SAVING;
import static pl.byteit.mbankscraper.operation.AccountInfo.AccountType.STANDARD;

class UserInterfaceTest {

	private static final String MOCKED_INPUT = "mocked-input";

	private static List<String> mockPrinter;
	private final MockUserInput mockUserInput = new MockUserInput();
	private UserInterface cli;

	@BeforeEach
	void beforeEachCommandLineInterfaceTest() {
		mockPrinter = new ArrayList<>();
		mockUserInput.reset();
		cli = new UserInterface(mockUserInput::getInput, msg -> mockPrinter.add(msg));
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
		AccountInfo account = new AccountInfo("1234", new BigDecimal("1234.30"), "PLN", STANDARD);

		cli.print(account);

		assertThat(mockPrinter)
				.hasSize(1)
				.contains(account.print(), atIndex(0));
	}

	@Test
	void shouldPrintPrintableCollection() {
		AccountInfo account = new AccountInfo("1234", new BigDecimal("1234.30"), "PLN", STANDARD);
		AccountInfo account2 = new AccountInfo("5678", new BigDecimal("5678.30"), "EUR", SAVING);

		cli.print(asList(account, account2));

		assertThat(mockPrinter)
				.hasSize(2)
				.contains(account.print(), atIndex(0))
				.contains(account2.print(), atIndex(1));
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
