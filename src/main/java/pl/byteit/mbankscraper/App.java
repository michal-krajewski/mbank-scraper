package pl.byteit.mbankscraper;

import pl.byteit.mbankscraper.http.HttpClient;
import pl.byteit.mbankscraper.operation.BankClient;
import pl.byteit.mbankscraper.operation.mbank.MbankClient;
import pl.byteit.mbankscraper.ui.UserInterface;
import pl.byteit.mbankscraper.util.Await;

import java.util.Scanner;

import static pl.byteit.mbankscraper.http.OkHttpClientWrapper.httpClientWithCookieHandler;

public class App {

	public static void main(String[] args) {
		HttpClient client = httpClientWithCookieHandler();
		UserInterface cli = commandLineInterface();
		BankClient bankClient = mBankClient(client);
		ScrapeAccountsUseCase scrapeAccountsUseCase = new ScrapeAccountsUseCase(bankClient, cli);
		scrapeAccountsUseCase.start();
	}

	private static MbankClient mBankClient(HttpClient client) {
		return new MbankClient(
				client,
				new Await()
		);
	}

	private static UserInterface commandLineInterface() {
		Scanner scanner = new Scanner(System.in);
		return new UserInterface(scanner::nextLine, System.out::println);
	}

}
