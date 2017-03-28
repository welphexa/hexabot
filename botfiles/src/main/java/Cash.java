import java.math.BigInteger;
import java.util.HashMap;

import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class Cash implements Listener {
	
	private TelegramBot telegramBot;
	
	public static HashMap<String, BigInteger> userCash = new HashMap<>();
	public static final BigInteger startingCash = BigInteger.valueOf(1000);
	
	public Cash(TelegramBot telegramBot) {
		this.telegramBot = telegramBot;
	}

	@Override
	public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
		long id = event.getMessage().getSender().getId();
		String receivedMessage = event.getContent().getContent();
		String username = event.getMessage().getSender().getUsername();
		String botUsername = telegramBot.getBotUsername();
		SendableTextMessage message;

		if (receivedMessage.equals("/mycash@" + botUsername) || receivedMessage.equals("/mycash")) {
			if (!userCash.containsKey(username)) {
				userCash.put(username, startingCash);
			}
			String cash = String.format("%,d", userCash.get(username));
			String[] arr = cash.split(",");
			if (arr.length == 1) {
				message = SendableTextMessage.builder().message(String.format("You have $%s", cash)).replyTo(event.getMessage()).build();
				telegramBot.sendMessage(event.getChat(), message);
			} else {
				String suffix = getSuffix(arr.length);
				double prefix = Double.parseDouble(arr[0]) + (Integer.parseInt(arr[1]) / 100) / 10.0;
				message = SendableTextMessage.builder().message(String.format("You have $%s" + (arr.length > 2 ? "\n\n$" + prefix + " " + suffix : ""), cash)).replyTo(event.getMessage()).build();
				telegramBot.sendMessage(event.getChat(), message);
			}
		}

		if (receivedMessage.startsWith("/give")) {
			String arr[] = receivedMessage.split(" ");
			if (!userCash.containsKey(username)) {
				userCash.put(username, startingCash);
			}
			if (arr.length == 3) {
				try {
					String user = arr[1];
					String parsed = arr[2].replaceAll("$", "").replaceAll(",", "");
					BigInteger amount = new BigInteger(parsed);
					if (!userCash.containsKey(user)) {
						userCash.put(user, startingCash);
					}
					if (!user.equals(username)) {
						if (amount.compareTo(BigInteger.ONE) == -1) {
							message = SendableTextMessage.builder().message("Invalid amount").build();
							telegramBot.sendMessage(event.getChat(), message);
						} else if (userCash.get(username).compareTo(amount) >= 0) {
							userCash.put(username, userCash.get(username).subtract(amount));
							userCash.put(user, userCash.get(user).add(amount));
							message = SendableTextMessage.builder().message(String.format("Sent $%,d to %s", amount, user)).build();
							telegramBot.sendMessage(event.getChat(), message);
						} else {
							message = SendableTextMessage.builder().message("Not enough money").build();
							telegramBot.sendMessage(event.getChat(), message);
						}
					}
				} catch (Exception e) {
					e.getMessage();
				}
			} else if (arr.length == 2) {
				try {
					String user = event.getMessage().getRepliedTo().getSender().getUsername();
					String parsed = arr[1].replaceAll("$", "").replaceAll(",", "");
					BigInteger amount = new BigInteger(parsed);
					if (!userCash.containsKey(user)) {
						userCash.put(user, startingCash);
					}
					if (!user.equals(username)) {
						if (amount.compareTo(BigInteger.ONE) == -1) {
							message = SendableTextMessage.builder().message("Invalid amount").build();
							telegramBot.sendMessage(event.getChat(), message);
						} else if (userCash.get(username).compareTo(amount) >= 0) {
							userCash.put(username, userCash.get(username).subtract(amount));
							userCash.put(user, userCash.get(user).add(amount));
							message = SendableTextMessage.builder().message(String.format("Sent $%,d to %s", amount, user)).build();
							telegramBot.sendMessage(event.getChat(), message);
						} else {
							message = SendableTextMessage.builder().message("Not enough money").build();
							telegramBot.sendMessage(event.getChat(), message);
						}
					}
				} catch (Exception e) {
					e.getMessage();
				}
			}
		}

		if (receivedMessage.startsWith("/get") && (Main.admins.contains(username) || id == Main.id)) {
			String arr[] = receivedMessage.split(" ");
			try {
				BigInteger amount = new BigInteger(arr[1]);
				if (amount.compareTo(BigInteger.ZERO) == 1) {
					if (!userCash.containsKey(username)) {
						userCash.put(username, startingCash);
					}
					userCash.put(username, userCash.get(username).add(amount));
				}
			} catch (Exception e) {
				e.getMessage();
			}
		}

		if (receivedMessage.equals("/check") && (Main.admins.contains(username) || id == Main.id)) {
			String user = event.getMessage().getRepliedTo().getSender().getUsername();
			if (!userCash.containsKey(user)) {
				userCash.put(user, startingCash);
			}
			if (!Stocks.shares.containsKey(user)) {
				Stocks.shares.put(user,BigInteger.ZERO);
			}
			message = SendableTextMessage.builder().message(String.format("This user has $%,d%nand %,d " + (Stocks.shares.get(user).compareTo(BigInteger.ONE) == 0 ? "share" : "shares"), userCash.get(user), Stocks.shares.get(user))).build();
			telegramBot.sendMessage(event.getChat(), message);
		}

		if (receivedMessage.startsWith("/take") && (Main.admins.contains(username) || id == Main.id)) {
			String arr[] = receivedMessage.split(" ");
			if (arr.length == 3) {
				String user = arr[1];
				if (arr[2].equalsIgnoreCase("all")) {
					if (userCash.containsKey(user)) {
						userCash.put(user, BigInteger.ZERO);
					}
					if (Stocks.shares.containsKey(user)) {
						Stocks.shares.put(user, BigInteger.ZERO);
					}
					if (Properties.cars.containsKey(user)) {
						Properties.cars.remove(user);
					}
					if (Properties.apartments.containsKey(user)) {
						Properties.apartments.remove(user);
					}
					if (Properties.stores.containsKey(user)) {
						Properties.stores.remove(user);
					}
					if (Properties.mansions.containsKey(user)) {
						Properties.mansions.remove(user);
					}
					if (Properties.jets.containsKey(user)) {
						Properties.jets.remove(user);
					}
					if (Properties.islands.containsKey(user)) {
						Properties.islands.remove(user);
					}
					if (Properties.spaceships.containsKey(user)) {
						Properties.spaceships.remove(user);
					}
					if (event.getChat().getType() != ChatType.PRIVATE) {
						message = SendableTextMessage.builder().message("Rekt").build();
						telegramBot.sendMessage(event.getChat(), message);
					}
				} else {
					try {
						BigInteger amount = new BigInteger(arr[2]);
						if (!user.equals(username)) {
							if (!userCash.containsKey(user)) {
								userCash.put(user, startingCash);
							}
							if (userCash.get(user).compareTo(amount) >= 0 && amount.compareTo(BigInteger.ZERO) >= 0) {
								userCash.put(user, userCash.get(user).subtract(amount));
							}
						}
					} catch (Exception e) {
						e.getMessage();
					}
				}
			} else if (arr.length == 2) {
				String user = event.getMessage().getRepliedTo().getSender().getUsername();
				if (arr[1].equalsIgnoreCase("all")) {
					if (userCash.containsKey(user)) {
						userCash.put(user, BigInteger.ZERO);
					}
					if (Stocks.shares.containsKey(user)) {
						Stocks.shares.put(user, BigInteger.ZERO);
					}
					if (Properties.cars.containsKey(user)) {
						Properties.cars.remove(user);
					}
					if (Properties.apartments.containsKey(user)) {
						Properties.apartments.remove(user);
					}
					if (Properties.stores.containsKey(user)) {
						Properties.stores.remove(user);
					}
					if (Properties.mansions.containsKey(user)) {
						Properties.mansions.remove(user);
					}
					if (Properties.jets.containsKey(user)) {
						Properties.jets.remove(user);
					}
					if (Properties.islands.containsKey(user)) {
						Properties.islands.remove(user);
					}
					if (Properties.spaceships.containsKey(user)) {
						Properties.spaceships.remove(user);
					}
					if (event.getChat().getType() != ChatType.PRIVATE) {
						message = SendableTextMessage.builder().message("Rekt").build();
						telegramBot.sendMessage(event.getChat(), message);
					}
				} else {
					try {
						BigInteger amount = new BigInteger(arr[2]);
						if (!user.equals(username)) {
							if (!userCash.containsKey(user)) {
								userCash.put(user, startingCash);
							}
							if (userCash.get(user).compareTo(amount) >= 0 && amount.compareTo(BigInteger.ZERO) >= 0) {
								userCash.put(user, userCash.get(user).subtract(amount));
							}
						}
					} catch (Exception e) {
						e.getMessage();
					}
				}
			}
		}
	}
	
	private String getSuffix(int places) {
		switch (places) {
		case 3: return "million";
		case 4: return "billion";
		case 5: return "trillion";
		case 6: return "quadrillion";
		case 7: return "quintillion";
		case 8: return "sextillion";
		case 9: return "septillion";
		case 10: return "octillion";
		case 11: return "nonillion";
		case 12: return "decillion";
		case 13: return "undecillion";
		case 14: return "duodecillion";
		case 15: return "tredecillion";
		case 16: return "quattuordecillion";
		case 17: return "quinquadecillion";
		case 18: return "sedecillion";
		case 19: return "septendecillion";
		case 20: return "octodecillion";
		case 21: return "novendecillion";
		case 22: return "vigintillion";
		case 23: return "unvigintillion";
		case 24: return "duovigintillion";
		case 25: return "tresvigintillion";
		case 26: return "quattuorvigintillion";
		case 27: return "quinquavigintillion";
		case 28: return "sesvigintillion";
		case 29: return "septemvigintillion";
		case 30: return "octovigintillion";
		case 31: return "novemvigintillion";
		case 32: return "trigintillion";
		case 33: return "untrigintillion";
		case 34: return "duotrigintillion";
		case 35: return "trestrigintillion";
		case 36: return "quattuortrigintillioin";
		case 37: return "quinquatrigintillion";
		case 38: return "sestrigintillion";
		case 39: return "septentrigintillion";
		case 40: return "octotrigintillion";
		case 41: return "noventrigintillion";
		case 42: return "quadragintillion";
		default: return "";
		}
	}
}