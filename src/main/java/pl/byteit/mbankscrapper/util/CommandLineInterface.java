package pl.byteit.mbankscrapper.util;

import java.util.Collection;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CommandLineInterface {

	private Scanner scanner;
	private Supplier<String> inputSupplier;
	private final Consumer<String> printer;

	public CommandLineInterface(Supplier<String> inputSupplier, Consumer<String> printer) {
		this.inputSupplier = inputSupplier;
		this.printer = printer;
	}

	public void print(String message) {
		printer.accept(message);
	}

	public void print(Printable printable) {
		print(printable.print());
	}

	public void print(Collection<? extends Printable> collection) {
		collection.forEach(this::print);
	}

	public char[] promptForInput(String message) {
		print(message);

		return inputSupplier.get().toCharArray();
	}

}
