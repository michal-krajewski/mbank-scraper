package pl.byteit.mbankscraper.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class AwaitTest {

	@Mock
	Supplier<String> supplier;

	@Spy
	private Await await;

	@BeforeEach
	void beforeEachAwaitTest() {
		initMocks(this);
	}

	@Test
	void shouldAwaitForResult() {
		String expectedResult = "theResult";
		when(supplier.get()).thenReturn("1", "2", expectedResult);
		doNothing().when(await).forSeconds(1);

		String result = await.forResult(supplier, text -> text.equals(expectedResult), 10, 1);

		assertThat(result).isEqualTo(expectedResult);
		verify(await, times(3)).forSeconds(1);
	}

	@Test
	void shouldReturnLastResponseWhenTimedOut() {
		when(supplier.get()).thenReturn("1");
		doNothing().when(await).forSeconds(1);

		String result = await.forResult(supplier, text -> false, 10, 1);

		assertThat(result).isEqualTo("1");
		verify(await, times(10)).forSeconds(1);
	}
}
