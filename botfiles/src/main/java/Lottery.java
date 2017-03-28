import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class Lottery implements Listener {

	private TelegramBot telegramBot;

	private static int minimum = 3;
	private static int lotteryPrize = 5000;
	private static HashMap<String, Boolean> lotteryActive = new HashMap<>();
	private static HashMap<String, ArrayList<String>> lotteryPlayers = new HashMap<>();
	
	public Lottery(TelegramBot telegramBot) {
		this.telegramBot = telegramBot;
	}

	@Override
	public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
		long id = event.getMessage().getSender().getId();
		ChatType chatType = event.getChat().getType();
		String chat = event.getChat().getId();
		String receivedMessage = event.getContent().getContent();
		String username = event.getMessage().getSender().getUsername();
		String botUsername = telegramBot.getBotUsername();
		SendableTextMessage message;

		if (receivedMessage.equals("/lottery@" + botUsername) || receivedMessage.equals("/lottery") && chatType != ChatType.PRIVATE) {
			if (!lotteryActive.containsKey(chat)) {
				lotteryActive.put(chat, false);
			}
			if (!lotteryPlayers.containsKey(chat)) {
				lotteryPlayers.put(chat, new ArrayList<String>());
			}
			if (lotteryActive.get(chat)) {
				message = SendableTextMessage.builder().message("A lottery is currently active\nUse /join to join the lottery.").build();
				telegramBot.sendMessage(event.getChat(), message);
			} else {
				lotteryActive.put(chat, true);
				message = SendableTextMessage.builder().message("New lottery started\nUse /join to join the lottery\nUse /end to end the lottery").build();
				telegramBot.sendMessage(event.getChat(), message);
			}
		}

		if (receivedMessage.equals("/join@" + botUsername) || receivedMessage.equals("/join") && chatType != ChatType.PRIVATE) {
			if (!lotteryActive.containsKey(chat)) {
				lotteryActive.put(chat, false);
			}
			if (lotteryActive.get(chat)) {
				if (lotteryPlayers.get(chat).contains(username)) {
					message = SendableTextMessage.builder().message("You are already in the lottery").replyTo(event.getMessage()).build();
					telegramBot.sendMessage(event.getChat(), message);
				} else {
					lotteryPlayers.get(chat).add(username);
					message = SendableTextMessage.builder().message(username + " joined the lottery\n" + lotteryStats(lotteryPlayers.get(chat).size())).build();
					telegramBot.sendMessage(event.getChat(), message);		
				}
			} else {
				message = SendableTextMessage.builder().message("No lottery active\nUse /lottery to start one").build();
				telegramBot.sendMessage(event.getChat(), message);
			}
		}

		if (receivedMessage.equals("/members@" + botUsername) || receivedMessage.equals("/members") && chatType != ChatType.PRIVATE) {
			if (!lotteryActive.containsKey(chat)) {
				lotteryActive.put(chat, false);
			}
			if (lotteryActive.get(chat)) {
				StringBuilder str = new StringBuilder("Lottery members:\n");
				for (String member : lotteryPlayers.get(chat)) {
					str.append(member + "\n");
				}
				message = SendableTextMessage.builder().message(str.toString()).build();
				telegramBot.sendMessage(event.getChat(), message);
			} else {
				message = SendableTextMessage.builder().message("No lottery active\nUse /lottery to start one").build();
				telegramBot.sendMessage(event.getChat(), message);
			}
		}

		if (receivedMessage.equals("/end@" + botUsername) || receivedMessage.equals("/end") && chatType != ChatType.PRIVATE) {
			if (!lotteryActive.containsKey(chat)) {
				lotteryActive.put(chat, false);
			}
			if (lotteryActive.get(chat)) {
				if (lotteryPlayers.get(chat).size() < minimum) {
					message = SendableTextMessage.builder().message("Not enough people have joined\nUse /join to join the lottery").build();
					telegramBot.sendMessage(event.getChat(), message);
				} else {
					String winner = lotteryPlayers.get(chat).get(new Random().nextInt(lotteryPlayers.get(chat).size()));
					if (!Cash.userCash.containsKey(winner)) {
						Cash.userCash.put(winner, Cash.startingCash);
					}
					Cash.userCash.put(winner, Cash.userCash.get(winner).add(BigInteger.valueOf(lotteryPlayers.get(chat).size() * lotteryPrize)));
					message = SendableTextMessage.builder().message("Winner is: " + winner + "\nYou have won the prize of " + String.format("$%,d", lotteryPlayers.get(chat).size() * lotteryPrize)).build();
					telegramBot.sendMessage(event.getChat(), message);
					lotteryPlayers.get(chat).clear();
					lotteryActive.put(chat, false);
				}
			} else {
				message = SendableTextMessage.builder().message("No lottery active\nUse /lottery to start one").build();
				telegramBot.sendMessage(event.getChat(), message);
			}
		}
		
		if (receivedMessage.startsWith("/setmin") && (Main.admins.contains(username) || id == Main.id)) {
			String[] arr = receivedMessage.split(" ");
			try {
				minimum = Integer.parseInt(arr[1]);
				message = SendableTextMessage.builder().message("Minimum required for lottery set to " + minimum).build();
				telegramBot.sendMessage(event.getChat(), message);
			} catch (Exception e) {
				e.getMessage();
			}
		}
		
		if (receivedMessage.equals("/terminate") && (Main.admins.contains(username) || id == Main.id)) {
			lotteryPlayers.get(chat).clear();
			lotteryActive.put(chat, false);
		}
	}
	
	public String lotteryStats(int members) {
		String message;
		if (members < minimum) {
			message = "Joined: " + members + "\nMinimum needed: " + minimum;
		} else {
			message = String.format("Joined: %d%nPrize: $%,d%n%nUse /end when you are ready to end the lottery", members, members * lotteryPrize);
		}
		return message;
	}
}