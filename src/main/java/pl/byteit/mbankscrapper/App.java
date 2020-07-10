package pl.byteit.mbankscrapper;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import pl.byteit.mbankscrapper.http.DefaultHttpClient;
import pl.byteit.mbankscrapper.http.HttpClient;
import pl.byteit.mbankscrapper.operation.BankOperationManager;
import pl.byteit.mbankscrapper.operation.Credentials;
import pl.byteit.mbankscrapper.operation.mbank.MbankOperationManager;
import pl.byteit.mbankscrapper.operation.mbank.authentication.MobileAppSecondFactorAuthenticationManager;
import pl.byteit.mbankscrapper.util.AwaitUtil;
import pl.byteit.mbankscrapper.util.CommandLineInterface;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Scanner;

public class App {

	public static void main(String[] args) {
		HttpClient client = httpClient();
		Scanner scanner = new Scanner(System.in);
		CommandLineInterface cli = new CommandLineInterface(scanner::nextLine, System.out::println);

		BankOperationManager operationManager = new MbankOperationManager(
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
