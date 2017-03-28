import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.ParticipantJoinGroupChatEvent;
import pro.zackpollard.telegrambot.api.event.chat.ParticipantLeaveGroupChatEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.VoiceMessageReceivedEvent;

public class Triggers implements Listener {
	
	private TelegramBot telegramBot;
    
    private static boolean triggers = true;

	public Triggers(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

	@Override
	public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
		ChatType chatType = event.getChat().getType();
		String username = event.getMessage().getSender().getUsername();
		String receivedMessage = event.getContent().getContent();
    	long id = event.getMessage().getSender().getId();
		SendableTextMessage message;
		
		if (receivedMessage.startsWith("/triggers") && (Main.admins.contains(username) || id == Main.id)) {
    		try {
    			String[] arr = receivedMessage.split(" ");
    			if (arr[1].equalsIgnoreCase("on")) {
    				triggers = true;
    				message = SendableTextMessage.builder().message("Triggers set to ON").build();
    				telegramBot.sendMessage(event.getChat(), message);
    			} else if (arr[1].equalsIgnoreCase("off")) {
    				triggers = false;
    				message = SendableTextMessage.builder().message("Triggers set to OFF").build();
    				telegramBot.sendMessage(event.getChat(), message);
    			}
    		} catch (Exception e) {
    			e.getMessage();
    		}
    	}

    	if (triggers && (receivedMessage.equals("/kickme") && !(chatType == ChatType.PRIVATE))) {
    		message = SendableTextMessage.builder().message("And dont come back!").build();
			telegramBot.sendMessage(event.getChat(), message);
    	}
    	
    	if (triggers && (receivedMessage.equals("/ban") && !(chatType == ChatType.PRIVATE))) {
    		message = SendableTextMessage.builder().message("Harsh").replyTo(event.getMessage()).build();
			telegramBot.sendMessage(event.getChat(), message);
    	}

		if (receivedMessage.startsWith("/admin") && id == Main.id) {
			String[] arr = receivedMessage.split(" ");
			Main.admins.add(arr[1]);
		}

		if (receivedMessage.startsWith("/unadmin") && id == Main.id) {
			String[] arr = receivedMessage.split(" ");
			Main.admins.remove(arr[1]);
		}
	}

	@Override
	public void onParticipantJoinGroupChat(ParticipantJoinGroupChatEvent event) {
		String name = event.getMessage().getSender().getFullName();
		if (triggers && !(event.getMessage().getSender().getId() == Main.id)) {
			SendableTextMessage message = SendableTextMessage.builder().message("Hi " + name + ". Welcome?").build();
			telegramBot.sendMessage(event.getChat(), message);
		}
	}

	@Override
	public void onParticipantLeaveGroupChat(ParticipantLeaveGroupChatEvent event) {
		if (triggers) {
			SendableTextMessage message = SendableTextMessage.builder().message("My favorite person left :(").build();
			telegramBot.sendMessage(event.getChat(), message);
		}
	}
	
	@Override
	public void onVoiceMessageReceived(VoiceMessageReceivedEvent event) {
		if (triggers) {
			SendableTextMessage message = SendableTextMessage.builder().message("This is cancer").replyTo(event.getMessage()).build();
			telegramBot.sendMessage(event.getChat(), message);
		}
	}
}