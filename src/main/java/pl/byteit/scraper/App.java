package pl.byteit.scraper;

import pl.byteit.scraper.http.HttpClient;
import pl.byteit.scraper.http.OkHttpClient;
import pl.byteit.scraper.mbank.MbankClient;
import pl.byteit.scraper.operation.BankClient;
import pl.byteit.scraper.operation.ScrapeAccountsUseCase;
import pl.byteit.scraper.ui.UserInterface;
import pl.byteit.scraper.util.Await;

import java.util.Scanner;

public class App {

	public static void main(String[] args) {
		HttpClient client = OkHttpClient.withCookieHandler();
		UserInterface cli = commandLineInterface();
		BankClient bankClient = mBankClient(client);
		ScrapeAccountsUseCase scrapeAccountsUseCase = new ScrapeAccountsUseCase(bankClient, cli);
		scrapeAccountsUseCase.start();
	}

	private static MbankClient mBankClient(HttpClient client) {
		return new MbankClient(client, new Await());
	}

	private static UserInterface commandLineInterface() {
		Scanner scanner = new Scanner(System.in);
		return new UserInterface(scanner::nextLine, System.out::println);
	}

}
