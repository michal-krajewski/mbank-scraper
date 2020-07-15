package pl.byteit.mbankscraper.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import pl.byteit.mbankscraper.operation.AccountInfo;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static pl.byteit.mbankscraper.operation.AccountInfo.AccountType.SAVING;
import static pl.byteit.mbankscraper.operation.AccountInfo.AccountType.STANDARD;

class UserInterfaceTest {

	private static final String MOCKED_INPUT = "mocked-input";

	@Mock
	private static List<String> mockIO;

	private UserInterface cli;

	@BeforeEach
	void beforeEachCommandLineInterfaceTest() {
		initMocks(this);
		mockIO.clear();
		cli = new UserInterface(() -> mockIO.get(0), msg -> mockIO.add(msg));
	}

	@Test
	void shouldPrintMessage() {
		String printedMessage = "the message";

		cli.print(printedMessage);

		verify(mockIO).add(printedMessage);
	}

	@Test
	void shouldPrintPrintableObject() {
		AccountInfo account = new AccountInfo("1234", new BigDecimal("1234.30"), "PLN", STANDARD);

		cli.print(account);

		verify(mockIO).add(account.print());
	}

	@Test
	void shouldPrintPrintableCollection() {
		AccountInfo account = new AccountInfo("1234", new BigDecimal("1234.30"), "PLN", STANDARD);
		AccountInfo account2 = new AccountInfo("5678", new BigDecimal("5678.30"), "EUR", SAVING);

		cli.print(asList(account, account2));

		verify(mockIO).add(account.print());
		verify(mockIO).add(account2.print());
	}

	@Test
	void shouldPromptForInput() {
		when(mockIO.get(0)).thenReturn(MOCKED_INPUT);
		String printedMessage = "Provide input";

		char[] input = cli.promptForInput(printedMessage);

		verify(mockIO).add(printedMessage);
		verify(mockIO).get(0);
		assertThat(input)
				.containsExactly(MOCKED_INPUT.toCharArray());
	}

}
