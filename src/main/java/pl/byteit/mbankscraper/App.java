package pl.byteit.mbankscraper;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import pl.byteit.mbankscraper.http.HttpClient;
import pl.byteit.mbankscraper.http.OkHttpClientWrapper;
import pl.byteit.mbankscraper.operation.BankClient;
import pl.byteit.mbankscraper.operation.mbank.MbankClient;
import pl.byteit.mbankscraper.operation.mbank.authentication.MobileAppSecondFactorAuthenticationManager;
import pl.byteit.mbankscraper.util.Await;
import pl.byteit.mbankscraper.util.UserInterface;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Scanner;

public class App {

	public static void main(String[] args) {
		HttpClient client = httpClientWithCookieHandler();
		UserInterface cli = commandLineInterface();
		BankClient bankClient = mBankClient(client);
		Scraper scraper = new Scraper(bankClient, cli);
		scraper.start();
	}

	private static MbankClient mBankClient(HttpClient client) {
		return new MbankClient(
				client,
				new MobileAppSecondFactorAuthenticationManager(client, new Await())
		);
	}

	private static UserInterface commandLineInterface() {
		Scanner scanner = new Scanner(System.in);
		return new UserInterface(scanner::nextLine, System.out::println);
	}

	public static HttpClient httpClientWithCookieHandler() {
		CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.cookieJar(new JavaNetCookieJar(cookieManager))
				.build();

		return new OkHttpClientWrapper(okHttpClient);
	}

}
