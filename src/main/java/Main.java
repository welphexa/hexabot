import static spark.Spark.get;
import static spark.SparkBase.port;
import static spark.SparkBase.staticFileLocation;

import java.util.ArrayList;

import pro.zackpollard.telegrambot.api.TelegramBot;

public class Main {

	private static String API_KEY = "308864:AAEUZrVlUubl8e0kIjGPitx-R-hHy212e5s";
	public static final int id = 202146102;
	public static ArrayList<String> admins = new ArrayList<String>();

	private TelegramBot telegramBot;

	public static void main(String[] args) {

		port(Integer.valueOf(System.getenv("PORT")));
		staticFileLocation("/public");
		
		get("/", (request, response) -> new Main(API_KEY));
	}

	public Main(String key) {
		telegramBot = TelegramBot.login(API_KEY);
		telegramBot.getEventsManager().register(new Data(telegramBot));
		telegramBot.getEventsManager().register(new Cash(telegramBot));
		telegramBot.getEventsManager().register(new Triggers(telegramBot));
		telegramBot.getEventsManager().register(new Lottery(telegramBot));
		telegramBot.getEventsManager().register(new HorseRace(telegramBot));
		telegramBot.getEventsManager().register(new Numbers(telegramBot));
		telegramBot.getEventsManager().register(new BankRob(telegramBot));
		telegramBot.getEventsManager().register(new Stocks(telegramBot));
		telegramBot.getEventsManager().register(new Properties(telegramBot));
		telegramBot.getEventsManager().register(new Monopoly(telegramBot));
		telegramBot.getEventsManager().register(new Pokemon(telegramBot));
		telegramBot.getEventsManager().register(new BetGame(telegramBot));
		telegramBot.startUpdates(false);
	}
}
