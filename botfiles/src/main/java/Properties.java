import java.math.BigInteger;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class Properties implements Listener {
	
	private TelegramBot telegramBot; 
	
	private static long carPrice = 5000000L;
	private static long apartmentPrice = 25000000L;
	private static long storePrice = 250000000L;
	private static long mansionPrice = 1000000000L;
	private static long jetPrice = 250000000000L;
	private static long islandPrice = 1000000000000L;
	private static long spaceshipPrice = 500000000000000L;
	
	public static HashMap<String, Long> cars = new HashMap<>();
	public static HashMap<String, Long> apartments = new HashMap<>();
	public static HashMap<String, Long> stores = new HashMap<>();
	public static HashMap<String, Long> mansions = new HashMap<>();
	public static HashMap<String, Long> jets = new HashMap<>();
	public static HashMap<String, Long> islands = new HashMap<>();
	public static HashMap<String, Long> spaceships = new HashMap<>();

	public Properties(TelegramBot telegramBot) {
		this.telegramBot = telegramBot;
		readProperties();
	}

	@Override
	public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
		String receivedMessage = event.getContent().getContent();
		String username = event.getMessage().getSender().getUsername();
		String botUsername = telegramBot.getBotUsername();
		SendableTextMessage message;
		
		if (receivedMessage.startsWith("/purchase")) {
			if (!Cash.userCash.containsKey(username)) {
				Cash.userCash.put(username, Cash.startingCash);
			}
			String[] arr = receivedMessage.split(" ");
			if (arr.length != 2) {
				message = SendableTextMessage.builder().message(String.format("Purchase properties that will generate you money%n%nAvailable properties:%nCar - $%,d%nApartment - $%,d%nStore - $%,d%nMansion - $%,d%nJet - $%,d%nIsland - $%,d%nSpaceship - $%,d%n%nTo buy:%n/purchase <property>%n%nTo sell for half the original price:%n/forsale <property>", carPrice, apartmentPrice, storePrice, mansionPrice, jetPrice, islandPrice, spaceshipPrice)).build();
				telegramBot.sendMessage(event.getChat(), message);
			} else if (arr.length == 2) {
				String property = arr[1];
				if (property.equalsIgnoreCase("car")) {
					if (cars.containsKey(username)) {
						message = SendableTextMessage.builder().message("You already own a car").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					} else {
						if (Cash.userCash.get(username).compareTo(BigInteger.valueOf(carPrice)) == -1) {
							message = SendableTextMessage.builder().message("Not enough money to purchase a car").replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
						} else {
							Cash.userCash.put(username, Cash.userCash.get(username).subtract(BigInteger.valueOf(carPrice)));
							cars.put(username, (long)0);
							message = SendableTextMessage.builder().message("You purchased a car").replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
						}
					}
				} else if (property.equalsIgnoreCase("apartment")) {
					if (apartments.containsKey(username)) {
						message = SendableTextMessage.builder().message("You already own an apartment").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					} else {
						if (Cash.userCash.get(username).compareTo(BigInteger.valueOf(apartmentPrice)) == -1) {
							message = SendableTextMessage.builder().message("Not enough money to purchase an apartment").replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
						} else {
							Cash.userCash.put(username, Cash.userCash.get(username).subtract(BigInteger.valueOf(apartmentPrice)));
							apartments.put(username, (long)0);
							message = SendableTextMessage.builder().message("You purchased an apartment").replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
						}
					}
				} else if (property.equalsIgnoreCase("store")) {
					if (stores.containsKey(username)) {
						message = SendableTextMessage.builder().message("You already own a store").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					} else {
						if (Cash.userCash.get(username).compareTo(BigInteger.valueOf(storePrice)) == -1) {
							message = SendableTextMessage.builder().message("Not enough money to purchase a store").replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
						} else {
							Cash.userCash.put(username, Cash.userCash.get(username).subtract(BigInteger.valueOf(storePrice)));
							stores.put(username, (long)0);
							message = SendableTextMessage.builder().message("You purchased a store").replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
						}
					}
				} else if (property.equalsIgnoreCase("mansion")) {
					if (mansions.containsKey(username)) {
						message = SendableTextMessage.builder().message("You already own a mansion").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					} else {
						if (Cash.userCash.get(username).compareTo(BigInteger.valueOf(mansionPrice)) == -1) {
							message = SendableTextMessage.builder().message("Not enough money to purchase a mansion").replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
						} else {
							Cash.userCash.put(username, Cash.userCash.get(username).subtract(BigInteger.valueOf(mansionPrice)));
							mansions.put(username, (long)0);
							message = SendableTextMessage.builder().message("You purchased a mansion").replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
						}
					}
				} else if (property.equalsIgnoreCase("jet")) {
					if (jets.containsKey(username)) {
						message = SendableTextMessage.builder().message("You already own a jet").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					} else {
						if (Cash.userCash.get(username).compareTo(BigInteger.valueOf(jetPrice)) == -1) {
							message = SendableTextMessage.builder().message("Not enough money to purchase a jet").replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
						} else {
							Cash.userCash.put(username, Cash.userCash.get(username).subtract(BigInteger.valueOf(jetPrice)));
							jets.put(username, (long)0);
							message = SendableTextMessage.builder().message("You purchased a jet").replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
						}
					}
				} else if (property.equalsIgnoreCase("island")) {
					if (islands.containsKey(username)) {
						message = SendableTextMessage.builder().message("You already own an island").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					} else {
						if (Cash.userCash.get(username).compareTo(BigInteger.valueOf(islandPrice)) == -1) {
							message = SendableTextMessage.builder().message("Not enough money to purchase an island").replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
						} else {
							Cash.userCash.put(username, Cash.userCash.get(username).subtract(BigInteger.valueOf(islandPrice)));
							islands.put(username, (long)0);
							message = SendableTextMessage.builder().message("You purchased an island").replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
						}
					}
				} else if (property.equalsIgnoreCase("spaceship")) {
					if (spaceships.containsKey(username)) {
						message = SendableTextMessage.builder().message("You already own a spaceship").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					} else {
						if (Cash.userCash.get(username).compareTo(BigInteger.valueOf(spaceshipPrice)) == -1) {
							message = SendableTextMessage.builder().message("Not enough money to purchase a spaceship").replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
						} else {
							Cash.userCash.put(username, Cash.userCash.get(username).subtract(BigInteger.valueOf(spaceshipPrice)));
							spaceships.put(username, (long)0);
							message = SendableTextMessage.builder().message("You purchased a spaceship").replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
						}
					}
				}
			}
		}
		
		if (receivedMessage.startsWith("/forsale")) {
			if (!Cash.userCash.containsKey(username)) {
				Cash.userCash.put(username, Cash.startingCash);
			}
			String[] arr = receivedMessage.split(" ");
			if (arr.length == 2) {
				if (arr[1].equalsIgnoreCase("car")) {
					if (cars.containsKey(username)) {
						cars.remove(username);
						Cash.userCash.put(username, Cash.userCash.get(username).add(BigInteger.valueOf(carPrice / 2)));
						message = SendableTextMessage.builder().message("You sold your car").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					} else {
						message = SendableTextMessage.builder().message("You do not own a car").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					}
				} else if (arr[1].equalsIgnoreCase("apartment")) {
					if (apartments.containsKey(username)) {
						apartments.remove(username);
						Cash.userCash.put(username, Cash.userCash.get(username).add(BigInteger.valueOf(apartmentPrice / 2)));
						message = SendableTextMessage.builder().message("You sold your apartment").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					} else {
						message = SendableTextMessage.builder().message("You do not own an apartment").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					}
				} else if (arr[1].equalsIgnoreCase("store")) {
					if (stores.containsKey(username)) {
						stores.remove(username);
						Cash.userCash.put(username, Cash.userCash.get(username).add(BigInteger.valueOf(storePrice / 2)));
						message = SendableTextMessage.builder().message("You sold your store").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					} else {
						message = SendableTextMessage.builder().message("You do not own a store").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					}
				} else if (arr[1].equalsIgnoreCase("mansion")) {
					if (mansions.containsKey(username)) {
						mansions.remove(username);
						Cash.userCash.put(username, Cash.userCash.get(username).add(BigInteger.valueOf(mansionPrice / 2)));
						message = SendableTextMessage.builder().message("You sold your mansion").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					} else {
						message = SendableTextMessage.builder().message("You do not own a mansion").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					}
				} else if (arr[1].equalsIgnoreCase("jet")) {
					if (jets.containsKey(username)) {
						jets.remove(username);
						Cash.userCash.put(username, Cash.userCash.get(username).add(BigInteger.valueOf(jetPrice / 2)));
						message = SendableTextMessage.builder().message("You sold your jet").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					} else {
						message = SendableTextMessage.builder().message("You do not own a jet").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					}
				} else if (arr[1].equalsIgnoreCase("island")) {
					if (islands.containsKey(username)) {
						islands.remove(username);
						Cash.userCash.put(username, Cash.userCash.get(username).add(BigInteger.valueOf(islandPrice / 2)));
						message = SendableTextMessage.builder().message("You sold your island").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					} else {
						message = SendableTextMessage.builder().message("You do not own a island").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					}
				} else if (arr[1].equalsIgnoreCase("spaceship")) {
					if (spaceships.containsKey(username)) {
						spaceships.remove(username);
						Cash.userCash.put(username, Cash.userCash.get(username).add(BigInteger.valueOf(spaceshipPrice / 2)));
						message = SendableTextMessage.builder().message("You sold your spaceship").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					} else {
						message = SendableTextMessage.builder().message("You do not own a spaceship").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					}
				} 
			}
		}
		
		if (receivedMessage.equals("/myproperties@" + botUsername) || receivedMessage.equals("/myproperties")) {
			if (!cars.containsKey(username) && !apartments.containsKey(username) && !stores.containsKey(username) && !mansions.containsKey(username) && !jets.containsKey(username) && !islands.containsKey(username) && !spaceships.containsKey(username)) {
				message = SendableTextMessage.builder().message("You do not own any properties").replyTo(event.getMessage()).build();
				telegramBot.sendMessage(event.getChat(), message);
			} else {	
				String str = "Your properties and their total generated amount:\n\n";
				if (cars.containsKey(username)) {
					str += String.format("Car: $%,d%n", cars.get(username));
				}
				if (apartments.containsKey(username)) {
					str += String.format("Apartment: $%,d%n", apartments.get(username));
				}
				if (stores.containsKey(username)) {
					str += String.format("Store: $%,d%n", stores.get(username));
				}
				if (mansions.containsKey(username)) {
					str += String.format("Mansion: $%,d%n", mansions.get(username));
				}
				if (jets.containsKey(username)) {
					str += String.format("Jet: $%,d%n", jets.get(username));
				}
				if (islands.containsKey(username)) {
					str += String.format("Island: $%,d%n", islands.get(username));
				}
				if (spaceships.containsKey(username)) {
					str += String.format("Spaceship: $%,d%n", spaceships.get(username));
				}
				message = SendableTextMessage.builder().message(str).replyTo(event.getMessage()).build();
				telegramBot.sendMessage(event.getChat(), message);
			}
		}
	}
	
	public void readProperties() {
		ExecutorService executor1 = Executors.newSingleThreadExecutor();
		executor1.submit(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1);
						for (String username : cars.keySet()) {
							cars.put(username, cars.get(username) + 3);
							Cash.userCash.put(username, Cash.userCash.get(username).add(BigInteger.valueOf(3)));
						}
					} catch (Exception e) {
						e.getMessage();
					}
				}
			}
		});
		
		executor1.shutdown();
		ExecutorService executor2 = Executors.newSingleThreadExecutor();
		executor2.submit(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1);
						for (String username : apartments.keySet()) {
							apartments.put(username, apartments.get(username) + 7);
							Cash.userCash.put(username, Cash.userCash.get(username).add(BigInteger.valueOf(7)));
						}
					} catch (Exception e) {
						e.getMessage();
					}
				}
			}
		});
		executor2.shutdown();
		ExecutorService executor3 = Executors.newSingleThreadExecutor();
		executor3.submit(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1);
						for (String username : stores.keySet()) {
							stores.put(username, stores.get(username) + 57);
							Cash.userCash.put(username, Cash.userCash.get(username).add(BigInteger.valueOf(57)));
						}
					} catch (Exception e) {
						e.getMessage();
					}
				}
			}
		});
		executor3.shutdown();
		ExecutorService executor4 = Executors.newSingleThreadExecutor();
		executor4.submit(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1);
						for (String username : mansions.keySet()) {
							mansions.put(username, mansions.get(username) + 223);
							Cash.userCash.put(username, Cash.userCash.get(username).add(BigInteger.valueOf(223)));
						}
					} catch (Exception e) {
						e.getMessage();
					}
				}
			}
		});
		executor4.shutdown();
		ExecutorService executor5 = Executors.newSingleThreadExecutor();
		executor5.submit(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1);
						for (String username : jets.keySet()) {
							jets.put(username, jets.get(username) + 5432);
							Cash.userCash.put(username, Cash.userCash.get(username).add(BigInteger.valueOf(5432)));
						}
					} catch (Exception e) {
						e.getMessage();
					}
				}
			}
		});
		executor5.shutdown();
		ExecutorService executor6 = Executors.newSingleThreadExecutor();
		executor6.submit(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1);
						for (String username : islands.keySet()) {
							islands.put(username, islands.get(username) + 123456);
							Cash.userCash.put(username, Cash.userCash.get(username).add(BigInteger.valueOf(123456)));
						}
					} catch (Exception e) {
						e.getMessage();
					}
				}
			}
		});
		executor6.shutdown();
		ExecutorService executor7 = Executors.newSingleThreadExecutor();
		executor7.submit(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1);
						for (String username : spaceships.keySet()) {
							spaceships.put(username, spaceships.get(username) + 750000);
							Cash.userCash.put(username, Cash.userCash.get(username).add(BigInteger.valueOf(750000)));
						}
					} catch (Exception e) {
						e.getMessage();
					}
				}
			}
		});
		executor7.shutdown();
		/*
		for (String username : apartments.keySet()) {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.submit(new Runnable() {
				
				@Override
				public void run() {
					while (true) {
						try {
							if (!apartments.containsKey(username)) {
								break;
							}
							Thread.sleep(1);
							apartments.put(username, apartments.get(username) + 7);
							Cash.userCash.put(username, Cash.userCash.get(username) + 7);
						} catch (Exception e) {
							e.getMessage();
						}
					}
				}
			});
			executor.shutdown();
		}
		for (String username : stores.keySet()) {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.submit(new Runnable() {
				
				@Override
				public void run() {
					while (true) {
						try {
							if (!stores.containsKey(username)) {
								break;
							}
							Thread.sleep(1);
							stores.put(username, stores.get(username) + 57);
							Cash.userCash.put(username, Cash.userCash.get(username) + 57);
						} catch (Exception e) {
							e.getMessage();
						}
					}
				}
			});
			executor.shutdown();
		}
		for (String username : mansions.keySet()) {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.submit(new Runnable() {
				
				@Override
				public void run() {
					while (true) {
						try {
							if (!mansions.containsKey(username)) {
								break;
							}
							Thread.sleep(1);
							mansions.put(username, mansions.get(username) + 223);
							Cash.userCash.put(username, Cash.userCash.get(username) + 223);
						} catch (Exception e) {
							e.getMessage();
						}
					}
				}
			});
			executor.shutdown();
		}
		for (String username : jets.keySet()) {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.submit(new Runnable() {
				
				@Override
				public void run() {
					while (true) {
						try {
							if (!jets.containsKey(username)) {
								break;
							}
							Thread.sleep(1);
							jets.put(username, jets.get(username) + 5432);
							Cash.userCash.put(username, Cash.userCash.get(username) + 5432);
						} catch (Exception e) {
							e.getMessage();
						}
					}
				}
			});
			executor.shutdown();
		}
		for (String username : islands.keySet()) {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.submit(new Runnable() {
				
				@Override
				public void run() {
					while (true) {
						try {
							if (!islands.containsKey(username)) {
								break;
							}
							Thread.sleep(1);
							islands.put(username, islands.get(username) + 123456);
							Cash.userCash.put(username, Cash.userCash.get(username) + 123456);
						} catch (Exception e) {
							e.getMessage();
						}
					}
				}
			});
			executor.shutdown();
		}
		for (String username : spaceships.keySet()) {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.submit(new Runnable() {
				
				@Override
				public void run() {
					while (true) {
						try {
							if (!spaceships.containsKey(username)) {
								break;
							}
							Thread.sleep(1);
							spaceships.put(username, spaceships.get(username) + 750000);
							Cash.userCash.put(username, Cash.userCash.get(username) + 750000);
						} catch (Exception e) {
							e.getMessage();
						}
					}
				}
			});
			executor.shutdown();
		}
		*/
	}
}