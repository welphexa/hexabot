import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class Stocks implements Listener {

	private TelegramBot telegramBot;
	
	public static int stockPrice = 50;
	public static HashMap<String, BigInteger> shares = new HashMap<>();
	
	public Stocks(TelegramBot telegramBot) {
		this.telegramBot = telegramBot;
		changeStockPrice();
	}

	@Override
	public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
		String receivedMessage = event.getContent().getContent();
		String username = event.getMessage().getSender().getUsername();
		String botUsername = telegramBot.getBotUsername();
		SendableTextMessage message;
		
		if (receivedMessage.equals("/stockprice@" + botUsername) || receivedMessage.equals("/stockprice")) {
			message = SendableTextMessage.builder().message("The current stock price for @" + botUsername + " is " + String.format("$%,d", stockPrice) + "\n\n/help to learn how to buy/sell").build();
			telegramBot.sendMessage(event.getChat(), message);
		}
		
		if (receivedMessage.equals("/help@" + botUsername) || receivedMessage.equals("/help")) {
			message = SendableTextMessage.builder().message("Buy low, sell high\n\nTo buy:\n/buy <number of shares>\nor /buy max\nor /buy <percent of total money> e.g. /buy 25%\n\nTo sell:\n/sell <number of shares>\nor /sell all").build();
			telegramBot.sendMessage(event.getChat(), message);
		}
		
		if (receivedMessage.startsWith("/buy")) {
			String[] arr = receivedMessage.split(" ");
			if (arr.length == 2) {
				if (!Cash.userCash.containsKey(username)) {
					Cash.userCash.put(username, Cash.startingCash);
				}
				if (!shares.containsKey(username)) {
					shares.put(username, BigInteger.ZERO);
				}
				if (arr[1].equalsIgnoreCase("max")) {
					try {
						BigInteger max = Cash.userCash.get(username).divide(BigInteger.valueOf(stockPrice));
						if (max.compareTo(BigInteger.ONE) == -1) {
							message = SendableTextMessage.builder().message("Not enough money").replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
						} else {
							shares.put(username, shares.get(username).add(max));
							Cash.userCash.put(username, Cash.userCash.get(username).subtract(max.multiply(BigInteger.valueOf(stockPrice))));
							message = SendableTextMessage.builder().message("You bought " + String.format("%,d", max) + (max.compareTo(BigInteger.ONE) == 0 ? " share " : " shares ") + "of @" + botUsername + " stock at " + String.format("$%,d", stockPrice) + " per share\n\nTotal purchased: " + String.format("$%,d", max.multiply(BigInteger.valueOf(stockPrice)))).replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
						}
					} catch (Exception e) {
						e.getMessage();
					}
				} else if (arr[1].endsWith("%")) {
					try {
						double percent = Double.parseDouble(arr[1].substring(0, arr[1].length() - 1).trim());
						if (percent > 0 && percent <= 100) {
							BigDecimal money = new BigDecimal(Cash.userCash.get(username)).multiply(BigDecimal.valueOf(percent / 100));
							BigInteger amount = money.toBigInteger().divide(BigInteger.valueOf(stockPrice));
							if (amount.compareTo(BigInteger.ZERO) == 0) {
								message = SendableTextMessage.builder().message("Not enough money").replyTo(event.getMessage()).build();
								telegramBot.sendMessage(event.getChat(), message);
							} else {
								shares.put(username, shares.get(username).add(amount));
								Cash.userCash.put(username, Cash.userCash.get(username).subtract(amount.multiply(BigInteger.valueOf(stockPrice))));
								message = SendableTextMessage.builder().message("You invested " + percent + "% of your money and bought " + String.format("%,d", amount) + (amount.compareTo(BigInteger.ONE) == 0 ? " share " : " shares ") + "of @" + botUsername + " stock at " + String.format("$%,d", stockPrice) + " per share\n\nTotal purchased: " + String.format("$%,d", amount.multiply(BigInteger.valueOf(stockPrice)))).replyTo(event.getMessage()).build();
								telegramBot.sendMessage(event.getChat(), message);
							}
						} else {
							message = SendableTextMessage.builder().message("Invalid percentage").replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
						}
					} catch (Exception e) {
						e.getMessage();
					}
				} else {
					try {
						BigInteger amount = new BigInteger(arr[1].replaceAll(",", ""));
						if (amount.compareTo(BigInteger.ONE) == -1 || (amount.multiply(BigInteger.valueOf(stockPrice)).compareTo(BigInteger.ONE) == -1)) {
							message = SendableTextMessage.builder().message("Invalid amount").replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
						} else if ((amount.multiply(BigInteger.valueOf(stockPrice)).compareTo(Cash.userCash.get(username)) <= 0)) {
							shares.put(username, shares.get(username).add(amount));
							Cash.userCash.put(username, Cash.userCash.get(username).subtract(amount.multiply(BigInteger.valueOf(stockPrice))));
							message = SendableTextMessage.builder().message("You bought " + String.format("%,d", amount) + (amount.compareTo(BigInteger.ONE) == 0 ? " share " : " shares ") + "of @" + botUsername + " stock at " + String.format("$%,d", stockPrice) + " per share\n\nTotal purchased: " + String.format("$%,d", amount.multiply(BigInteger.valueOf(stockPrice)))).replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
						} else {
							message = SendableTextMessage.builder().message("Not enough money").replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
						}
					} catch (Exception e) {
						e.getMessage();
					}
				}
			}
		}
		
		if (receivedMessage.startsWith("/sell")) {
			String[] arr = receivedMessage.split(" ");
			if (arr.length == 2) {
				if (!shares.containsKey(username)) {
					shares.put(username, BigInteger.ZERO);
				}
				if (shares.get(username).compareTo(BigInteger.ZERO) == 0) {
					message = SendableTextMessage.builder().message("You do not own any shares").replyTo(event.getMessage()).build();
					telegramBot.sendMessage(event.getChat(), message);
				} else {
					if (arr[1].equalsIgnoreCase("all")) {
						try {
							BigInteger all = shares.get(username);
							shares.put(username, BigInteger.ZERO);
							Cash.userCash.put(username, Cash.userCash.get(username).add(all.multiply(BigInteger.valueOf(stockPrice))));
							message = SendableTextMessage.builder().message("You sold " + String.format("%,d", all) + (all.compareTo(BigInteger.ONE) == 0 ? " share " : " shares ") + "of @" + botUsername + " stock at " + String.format("$%,d", stockPrice) + " per share\n\nTotal sold: " + String.format("$%,d", all.multiply(BigInteger.valueOf(stockPrice)))).replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
						} catch (Exception e) {
							e.getMessage();
						}
					} else {
						try {
							BigInteger amount = new BigInteger(arr[1].replaceAll(",", ""));
							if (amount.compareTo(BigInteger.ONE) == -1) {
								message = SendableTextMessage.builder().message("Invalid amount").replyTo(event.getMessage()).build();
								telegramBot.sendMessage(event.getChat(), message);
							} else if (amount.compareTo(shares.get(username)) == 1) {
								message = SendableTextMessage.builder().message("Not enough shares owned").replyTo(event.getMessage()).build();
								telegramBot.sendMessage(event.getChat(), message);
							} else {
								shares.put(username, shares.get(username).subtract(amount));
								Cash.userCash.put(username, Cash.userCash.get(username).add(amount.multiply(BigInteger.valueOf(stockPrice))));
								message = SendableTextMessage.builder().message("You sold " + String.format("%,d", amount) + (amount.compareTo(BigInteger.ONE) == 0 ? " share " : " shares ") + "of @" + botUsername + " stock at " + String.format("$%,d", stockPrice) + " per share\n\nTotal sold: " + String.format("$%,d", amount.multiply(BigInteger.valueOf(stockPrice)))).replyTo(event.getMessage()).build();
								telegramBot.sendMessage(event.getChat(), message);
							}
						} catch (Exception e) {
							e.getMessage();
						}
					}
				}
			}
		}
		
		if (receivedMessage.equals("/mystocks@" + botUsername) || receivedMessage.equals("/mystocks")) {
			if (!shares.containsKey(username)) {
				shares.put(username, BigInteger.ZERO);
			}
			if (shares.get(username).compareTo(BigInteger.ZERO) == 0) {
				message = SendableTextMessage.builder().message("You do not own any shares").replyTo(event.getMessage()).build();
				telegramBot.sendMessage(event.getChat(), message);
			} else {
				message = SendableTextMessage.builder().message("You own " + String.format("%,d", shares.get(username)) + " shares of @" + botUsername + " stock\n" + String.format("Current worth: $%,d", shares.get(username).multiply(BigInteger.valueOf(stockPrice)))).replyTo(event.getMessage()).build();
				telegramBot.sendMessage(event.getChat(), message);
			}
		}
	}
	
	public void changeStockPrice() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					int plusMinus = new Random().nextInt(2);
					int change = new Random().nextInt(5) + 1;
					try {
						Thread.sleep(5000);
						if (plusMinus == 0) {
							stockPrice += change;
						} else {
							if (stockPrice - change < 1) {
								stockPrice += change;
							} else {
								stockPrice -= change;
							}
						}
					} catch (Exception e) {
						e.getMessage();
					}
				}
			}
		});
	}
}