import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class BankRob implements Listener {

	private TelegramBot telegramBot;
	
	private static boolean bankOpen = true;
	public static int bankMoney = 500000;
	
	public BankRob(TelegramBot telegramBot) {
		this.telegramBot = telegramBot;
		addBankMoney();
	}

	@Override
	public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
		long id = event.getMessage().getSender().getId();
		String receivedMessage = event.getContent().getContent();
		String username = event.getMessage().getSender().getUsername();
		String botUsername = telegramBot.getBotUsername();
		SendableTextMessage message;

		if (receivedMessage.equals("/bankrob@" + botUsername) || receivedMessage.equals("/bankrob")) {
			if (bankOpen) {
				message = SendableTextMessage.builder().message("Do you want to rob a bank?\nWarning: If you get caught and go to jail you will lose all your money and stocks\n1 in 6 chance of getting caught\nThe bank currently has " + String.format("$%,d", bankMoney) + "\n\n/rob to continue").build();
				telegramBot.sendMessage(event.getChat(), message);
			} else {
				message = SendableTextMessage.builder().message("The bank has not reopened yet").build();
				telegramBot.sendMessage(event.getChat(), message);
			}
		}
		
		if (receivedMessage.equals("/rob@" + botUsername) || receivedMessage.equals("/rob")) {
			if (bankOpen) {
				if (!Cash.userCash.containsKey(username)) {
					Cash.userCash.put(username, Cash.startingCash);
				}
				bankOpen = false;
				ExecutorService executor = Executors.newSingleThreadExecutor();
				executor.submit(new Runnable() {
			
					@Override
					public void run() {
						try {
							Thread.sleep(30000);
							bankOpen = true;
						} catch (Exception e) {
							e.getMessage();
						}
					}
				});
				executor.shutdown();
				int chance = new Random().nextInt(6);
				if (chance == 0) {
					Cash.userCash.put(username, BigInteger.ZERO);
					Stocks.shares.put(username, BigInteger.ZERO);
					message = SendableTextMessage.builder().message("You got caught and lost all your money and stocks (property is safe)\nBank will reopen in 30 seconds").replyTo(event.getMessage()).build();
					telegramBot.sendMessage(event.getChat(), message);
				} else {
					int stolen = new Random().nextInt(bankMoney);
					Cash.userCash.put(username, Cash.userCash.get(username).add(BigInteger.valueOf(stolen))); 
					bankMoney -= stolen;
					message = SendableTextMessage.builder().message(String.format("You got away with $%,d%nBank will reopen in 30 seconds", stolen)).replyTo(event.getMessage()).build();
					telegramBot.sendMessage(event.getChat(), message);
				}
			} else {
				message = SendableTextMessage.builder().message("The bank has not reopened yet").replyTo(event.getMessage()).build();
				telegramBot.sendMessage(event.getChat(), message);
			}
		}
	
		if (receivedMessage.equals("/open") && (Main.admins.contains(username) || id == Main.id)) {
			bankOpen = true;
		}
	}
	
	public void addBankMoney() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1);
						bankMoney += 25;
					} catch (Exception e) {
						e.getMessage();
					}
				}
			}
		});
	}
}