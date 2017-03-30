import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class HorseRace implements Listener {

	private TelegramBot telegramBot;
	
	private static int numContestants = 10;
	private static boolean horseRaceActive = false;
	private static String[] horseNameChoices = {"Ali", "Yubi", "Ryleigh", "Habibi", "Thanatos", "Rob", "Leo", "Chris", "Kappa", "Beltre", "Eli", "Zac", "Autumn", "Rain", "Cross", "Dylan", "Stevey", "Isabelle", "Heidi", "Matt", "Moeses", "Nush", "Faroos", "Mccoy", "Viktoria", "Emily", "Zach", "Nini", "Hali", "Mila", "Priest", "Lygia", "Amy", "Hanan", "Blake", "Red", "Amz", "Jayessi", "Raif", "Pingu"};
	private static ArrayList<String> horses = new ArrayList<>();
	private static ArrayList<String> horseNames = new ArrayList<>();
	private static ArrayList<Integer> horseNumbers = new ArrayList<>();
	private static HashMap<Integer, String> contestants = new HashMap<>();
	private static HashMap<String, Integer> playersHorse = new HashMap<>();
	private static HashMap<String, BigInteger> playersBet = new HashMap<>();
	
	public HorseRace(TelegramBot telegramBot) {
		this.telegramBot = telegramBot;
	}

	@Override
	public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
		long id = event.getMessage().getSender().getId();
		ChatType chatType = event.getChat().getType();
		String receivedMessage = event.getContent().getContent();
		String username = event.getMessage().getSender().getUsername();
		String botUsername = telegramBot.getBotUsername();
		SendableTextMessage message;

		if (receivedMessage.equals("/horserace@" + botUsername) || receivedMessage.equals("/horserace") && chatType != ChatType.PRIVATE) {
			if (horseRaceActive) {
				message = SendableTextMessage.builder().message("A race is currently active\n" + listContestants() + "\nFormat for betting:\n/bet <contestant #> <amount>").build();
				telegramBot.sendMessage(event.getChat(), message);
			} else {
				horseRaceActive = true;
				addContestants();
				message = SendableTextMessage.builder().message("A new race has been started\n" + listContestants() + "\nFormat for betting:\n/bet <contestant #> <amount>\n\nThe race will end in one minute").build();
				telegramBot.sendMessage(event.getChat(), message);
				ExecutorService executor = Executors.newSingleThreadExecutor();
				executor.submit(new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(60000);
							SendableTextMessage m;
							m = SendableTextMessage.builder().message(listWinners()).build();
							telegramBot.sendMessage(event.getChat(), m);
							m = SendableTextMessage.builder().message(calculateWinnings()).build();
							telegramBot.sendMessage(event.getChat(), m);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
				executor.shutdown();
			}
		}

		if (receivedMessage.startsWith("/bet") && chatType != ChatType.PRIVATE) {
			if (playersHorse.containsKey(username)) {
				message = SendableTextMessage.builder().message("You cannot change your bet").replyTo(event.getMessage()).build();
				telegramBot.sendMessage(event.getChat(), message);
			} else {
				String[] arr = receivedMessage.split(" ");
				if (arr.length == 3) {
					if (!Cash.userCash.containsKey(username)) {
						Cash.userCash.put(username, Cash.startingCash);
					}
					if (arr[2].equalsIgnoreCase("max")) {
						try {
							int horse = Integer.parseInt(arr[1]);
							if (contestants.containsKey(horse)) {
								if (Cash.userCash.get(username).compareTo(BigInteger.ZERO) == 1) {
									playersHorse.put(username, horse);
									playersBet.put(username, Cash.userCash.get(username));
									Cash.userCash.put(username, BigInteger.ZERO);
									message = SendableTextMessage.builder().message(String.format("Your bet of $%,d has been placed", playersBet.get(username))).replyTo(event.getMessage()).build();
									telegramBot.sendMessage(event.getChat(), message);
								} else {
									message = SendableTextMessage.builder().message("Not enough money").replyTo(event.getMessage()).build();
									telegramBot.sendMessage(event.getChat(), message);
								}
							} else {
								message = SendableTextMessage.builder().message("Contestant " + horse + " is not in the race").replyTo(event.getMessage()).build();
								telegramBot.sendMessage(event.getChat(), message);
							}
						} catch (Exception e) {
							e.getMessage();
						}
					} else {
						try {
							int horse = Integer.parseInt(arr[1]);
							BigInteger bet = new BigInteger(arr[2].replaceAll("$", "").replaceAll(",", ""));
							if (contestants.containsKey(horse)) {
								if (Cash.userCash.get(username).compareTo(bet) >= 0) {
									if (bet.compareTo(BigInteger.ZERO) == 1) {
										playersHorse.put(username, horse);
										playersBet.put(username, bet);
										Cash.userCash.put(username, Cash.userCash.get(username).subtract(playersBet.get(username)));
										message = SendableTextMessage.builder().message(String.format("Your bet of $%,d has been placed", playersBet.get(username))).replyTo(event.getMessage()).build();
										telegramBot.sendMessage(event.getChat(), message);
									} else {
										message = SendableTextMessage.builder().message("Invalid amount").replyTo(event.getMessage()).build();
										telegramBot.sendMessage(event.getChat(), message);
									}
								} else {
									message = SendableTextMessage.builder().message("Not enough money").replyTo(event.getMessage()).build();
									telegramBot.sendMessage(event.getChat(), message);
								}
							} else {
								message = SendableTextMessage.builder().message("Contestant " + horse + " is not in the race").replyTo(event.getMessage()).build();
								telegramBot.sendMessage(event.getChat(), message);
							}
						} catch (Exception e) {
							e.getMessage();
						}
					}
				}
			}
		}

		if (receivedMessage.startsWith("/setcon") && (Main.admins.contains(username) || id == Main.id)) {
			String[] arr = receivedMessage.split(" ");
			try {
				if (!horseRaceActive) {
					numContestants = Integer.parseInt(arr[1]);
					message = SendableTextMessage.builder().message("Number of contestants set to " + numContestants).build();
					telegramBot.sendMessage(event.getChat(), message);
				}
			} catch (Exception e) {
				e.getMessage();
			}
		}
	}
	
	public String listContestants() {
		StringBuilder str = new StringBuilder("Contestants:\n");
		for (Integer num : contestants.keySet()) {
			str.append(num + ": " + contestants.get(num) + "\n");
		}
		return str.toString();
	}

	public void addContestants() {
		for (int i = 0; i < horseNameChoices.length; i++) {
			horses.add(horseNameChoices[i]);
		}
		Collections.shuffle(horses);
		for (int i = 0; i < numContestants; i++) {
			String horseName = horses.get(i);
			int horseNumber = new Random().nextInt(900) + 100;
			if (!contestants.containsKey(horseNumber)) {
				horseNumbers.add(horseNumber);
				horseNames.add(horseName);
				contestants.put(horseNumber, horseName);
			} else {
				--i;
			}
		}
	}

	public String listWinners() {
		Collections.shuffle(horseNumbers);
		StringBuilder str = new StringBuilder("The Race is over. Race outcome:\n");
		for (int i = 0; i < horseNumbers.size(); i++) {
			str.append((i + 1) + ". " + contestants.get(horseNumbers.get(i)) + " (" + horseNumbers.get(i) + ")\n");
		}
		return str.toString();
	}

	public String calculateWinnings() {
		StringBuilder result = new StringBuilder("Horse race winners:\n");
		for (String player : playersHorse.keySet()) {
			int horse = playersHorse.get(player);
			BigInteger bet = playersBet.get(player);
			if (horse == horseNumbers.get(0)) {
				Cash.userCash.put(player, Cash.userCash.get(player).add(bet.multiply(BigInteger.valueOf(5))));
				result.append(String.format("%s won $%,d%n", player, bet.multiply(BigInteger.valueOf(5))));
			} else if (horse == horseNumbers.get(1)) {
				Cash.userCash.put(player, Cash.userCash.get(player).add(bet.multiply(BigInteger.valueOf(3))));
				result.append(String.format("%s won $%,d%n", player, bet.multiply(BigInteger.valueOf(3))));
			} else if (horse == horseNumbers.get(2)) {
				Cash.userCash.put(player, Cash.userCash.get(player).add(bet.multiply(BigInteger.valueOf(2))));
				result.append(String.format("%s won $%,d%n", player, bet.multiply(BigInteger.valueOf(2))));
			} else {
				result.append(String.format("%s lost $%,d%n", player, bet));
			}
		}
		if (result.toString().equals("Horse race winners:\n")) {
			result.append("None");
		}
		horseRaceActive = false;
		horses.clear();
		horseNames.clear();
		horseNumbers.clear();
		contestants.clear();
		playersHorse.clear();
		playersBet.clear();
		return result.toString();
	}
}