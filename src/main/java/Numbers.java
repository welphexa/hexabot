import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;

public class Numbers implements Listener {

	private TelegramBot telegramBot;
	
	private static HashMap<String, Boolean> numbersActive = new HashMap<>();
	private static HashMap<String, Long> messageId = new HashMap<>();
	private static HashMap<String, HashMap<String, Integer>> numbers = new HashMap<>();
	private static HashMap<String, HashMap<Integer, Integer>> numbersPrizes = new HashMap<>();
	
	public Numbers(TelegramBot telegramBot) {
		this.telegramBot = telegramBot;
	}

	@Override
	public void onTextMessageReceived(TextMessageReceivedEvent event) {
		String chat = event.getChat().getId();
		String receivedMessage = event.getContent().getContent();
		String username = event.getMessage().getSender().getUsername();
		SendableTextMessage message;
		
		if (event.getMessage().getRepliedTo().getSender().getUsername() != null && event.getMessage().getRepliedTo().getMessageId() == messageId.get(chat) && numbersActive.get(chat)) {
			int number = Integer.parseInt(receivedMessage);
			if (number >= 1 && number <= 10 && numbersActive.get(chat)) {
				if (numbers.get(chat).containsKey(username)) {
					message = SendableTextMessage.builder().message("You already chose a number").replyTo(event.getMessage()).build();
					telegramBot.sendMessage(event.getChat(), message);
				} else if (!numbersPrizes.get(chat).containsKey(number)){
					message = SendableTextMessage.builder().message("This number has already been chosen").replyTo(event.getMessage()).build();
					telegramBot.sendMessage(event.getChat(), message);
				} else {
					numbers.get(chat).put(username, numbersPrizes.get(chat).get(number));
					numbersPrizes.get(chat).remove(number);
					message = SendableTextMessage.builder().message(username + " chose number " + number).build();
					telegramBot.sendMessage(event.getChat(), message);
				}	
			}
		}	
	}

	@Override
	public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
		ChatType chatType = event.getChat().getType();
		String chat = event.getChat().getId();
		String receivedMessage = event.getContent().getContent();
		String botUsername = telegramBot.getBotUsername();
		SendableTextMessage message;
		
		if (receivedMessage.equals("/numbers@" + botUsername) || receivedMessage.equals("/numbers") && chatType != ChatType.PRIVATE) {
			if (!numbersActive.containsKey(chat)) {
				numbersActive.put(chat, false);
			}
			if (!numbers.containsKey(chat)) {
				numbers.put(chat, new HashMap<String, Integer>());
			}
			if (!numbersPrizes.containsKey(chat)) {
				numbersPrizes.put(chat, new HashMap<Integer, Integer>());
			}
			if (!numbersActive.get(chat)) {
				numbersActive.put(chat, true);
				numbersList(chat);
				message = SendableTextMessage.builder().message("These numbers contain random cash amounts behind them\n\n1    2    3    4    5\n6    7    8    9    10\n\nReply to this message with the number you want to choose").build();
				messageId.put(chat, telegramBot.sendMessage(event.getChat(), message).getMessageId());
				ExecutorService executor = Executors.newSingleThreadExecutor();
				executor.submit(new Runnable() {
						
					@Override
					public void run() {
						try {
							Thread.sleep(60000);
							SendableTextMessage m = SendableTextMessage.builder().message(numbersWinners(chat)).build();
							telegramBot.sendMessage(event.getChat(), m);
						} catch (Exception e) {
							e.getMessage();
						}
					}
				});
				executor.shutdown();
			} else {
				message = SendableTextMessage.builder().message("A numbers game is currently active").build();
				telegramBot.sendMessage(event.getChat(), message);
				
			}
		}
	}
	
	public void numbersList(String chat) {
		for (int i = 1; i <= 10; i++) {
			numbersPrizes.get(chat).put(i, new Random().nextInt(50000));
		}
	}
	
	public String numbersWinners(String chat) {
		String str = "Numbers game winners:\n";
		for (String player : numbers.get(chat).keySet()) {
			str += String.format("%s won $%,d%n", player, numbers.get(chat).get(player));
			if (!Cash.userCash.containsKey(player)) {
				Cash.userCash.put(player, Cash.startingCash);
			}
			Cash.userCash.put(player, Cash.userCash.get(player).add(BigInteger.valueOf(numbers.get(chat).get(player))));
		}
		if (str.equals("Numbers game winners:\n")) {
			str += "None";
		}
		numbersActive.put(chat, false);
		numbers.get(chat).clear();
		numbersPrizes.get(chat).clear();
		return str;
	}
}