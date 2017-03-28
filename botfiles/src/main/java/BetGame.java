import java.math.BigInteger;
import java.util.Random;

import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class BetGame implements Listener {

	private TelegramBot telegramBot;
	
	public BetGame(TelegramBot telegramBot) {
		this.telegramBot = telegramBot;
	}

	@Override
	public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
		String receivedMessage = event.getContent().getContent();
		String username = event.getMessage().getSender().getUsername();
		SendableTextMessage message;
		
		if (receivedMessage.startsWith("/straight")) {
			String[] arr = receivedMessage.split(" ");
			if (arr.length == 1) {
				message = SendableTextMessage.builder().message("Straight bet (3 digit number)\n\nHow to win\nmatch your number with the winning number (exact match order)\n\nExample -\nYour number:\n123\nWinning numbers:\n123\n\nPayout:\n$50,000,000 to $1\n\nOdds: 1 in 1000\n\nUsage:\n/straight <your number> <bet amount>\n\nFor other bet type see /box").build();
				telegramBot.sendMessage(event.getChat(), message);
			} else if (arr.length == 3) {
				BigInteger betAmount = new BigInteger(arr[2].replaceAll(",", ""));
				if (betAmount.compareTo(BigInteger.ZERO) == 1 && betAmount.compareTo(Cash.userCash.get(username)) <= 0) {
					Cash.userCash.put(username, Cash.userCash.get(username).subtract(betAmount));
					int number = Integer.parseInt(arr[1]);
					if (number >= 100 && number <= 999) {
						int winningNumber = new Random().nextInt(900) + 100;
						Bet bet = new Bet("1", betAmount, number, winningNumber);
						BigInteger amountWon = bet.getAmountWon();
						Cash.userCash.put(username, Cash.userCash.get(username).add(amountWon));
						message = SendableTextMessage.builder().message("Bet type: Straight bet\nBet amount: " + String.format("$%,d%n", betAmount) + "Your number: " + number + "\nWinning number: " + winningNumber + "\n\nPayout: " + String.format("$%,d", amountWon)).build();
						telegramBot.sendMessage(event.getChat(), message);
					} else {
						message = SendableTextMessage.builder().message("Your number must be a 3 digit number").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					}
				}
			}
		}
		
		if (receivedMessage.startsWith("/box")) {
			String[] arr = receivedMessage.split(" ");
			if (arr.length == 1) {
				message = SendableTextMessage.builder().message("Box bet (3 digit number)\n\nHow to win:\nmatch your number with the winning number in any order\n\nExample (3 different numbers) -\nYour number:\n123\nWinning numbers:\n123\n132\n213\n231\n312\n321\n\nPayout:\n$5,000,000 to $1\n\nOdds: 1 in 167\n\nExample (1 duplicate number) -\nYour number:\n121\nWinning numbers:\n121\n211\n112\n\nPayout:\n$15,000,000 to $1\n\nOdds: 1 in 333\n\n\nUsage:\n/box <your number> <bet amount>\n\nFor other bet type see /straight").build();
				telegramBot.sendMessage(event.getChat(), message);
			} else if (arr.length == 3) {
				BigInteger betAmount = new BigInteger(arr[2].replaceAll(",", ""));
				if (betAmount.compareTo(BigInteger.ZERO) == 1 && betAmount.compareTo(Cash.userCash.get(username)) <= 0) {
					Cash.userCash.put(username, Cash.userCash.get(username).subtract(betAmount));
					int number = Integer.parseInt(arr[1]);
					if (number >= 100 && number <= 999) {
						int winningNumber = new Random().nextInt(900) + 100;
						Bet bet = new Bet("2", betAmount, number, winningNumber);
						BigInteger amountWon = bet.getAmountWon();
						Cash.userCash.put(username, Cash.userCash.get(username).add(amountWon));
						message = SendableTextMessage.builder().message("Bet type: Box bet\nBet amount: " + String.format("$%,d%n", betAmount) + "Your number: " + number + "\nWinning number: " + winningNumber + "\n\nPayout: " + String.format("$%,d", amountWon)).build();
						telegramBot.sendMessage(event.getChat(), message);
					} else {
						message = SendableTextMessage.builder().message("Your number must be a 3 digit number").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					}
				}
			}
		}
	}
}
