package pl.byteit.mbankscraper;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import pl.byteit.mbankscraper.http.DefaultHttpClient;
import pl.byteit.mbankscraper.http.HttpClient;
import pl.byteit.mbankscraper.operation.BankClient;
import pl.byteit.mbankscraper.operation.Credentials;
import pl.byteit.mbankscraper.operation.mbank.MbankClient;
import pl.byteit.mbankscraper.operation.mbank.authentication.MobileAppSecondFactorAuthenticationManager;
import pl.byteit.mbankscraper.util.AwaitUtil;
import pl.byteit.mbankscraper.util.CommandLineInterface;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Scanner;

public class App {

	public static void main(String[] args) {
		HttpClient client = httpClient();
		Scanner scanner = new Scanner(System.in);
		CommandLineInterface cli = new CommandLineInterface(scanner::nextLine, System.out::println);

		BankClient operationManager = new MbankClient(
				client,
				cli,
				new MobileAppSecondFactorAuthenticationManager(client, cli, new AwaitUtil())
		);

		operationManager.login(new Credentials(cli.promptForInput("Username: "), cli.promptForInput("Password: ")));
		operationManager.getAccounts();
	}

	private static HttpClient httpClient() {
		CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.cookieJar(new JavaNetCookieJar(cookieManager))
				.build();

		return new DefaultHttpClient(okHttpClient);
	}

}
