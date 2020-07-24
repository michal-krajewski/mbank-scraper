package pl.byteit.scraper.ui;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class UserInterface {

	private final Supplier<String> inputSupplier;
	private final Consumer<String> printer;

	public UserInterface(Supplier<String> inputSupplier, Consumer<String> printer) {
		this.inputSupplier = inputSupplier;
		this.printer = printer;
	}

	public void print(String message) {
		printer.accept(message);
	}

	public void print(Printable printable) {
		print(printable.print());
	}

	public void print(List<? extends Printable> collection) {
		collection.forEach(this::print);
	}

	public String promptForInput(String message) {
		print(message);
		return inputSupplier.get();
	}

}
