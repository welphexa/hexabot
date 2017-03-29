import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class Data implements Listener {

	private TelegramBot telegramBot;
	
	private static File userCash = new File("usercash");
	private static File stocks = new File("stocks");
	private static File stockPrice = new File("stockprice");
	private static File cars = new File("cars");
	private static File apartments = new File("apartments");
	private static File stores = new File("stores");
	private static File mansions = new File("mansions");
	private static File jets = new File("jets");
	private static File islands = new File("islands");
	private static File spaceships = new File("spaceships");
	private static File bankmoney = new File("bankmoney");
	private static File pokemonIndexes = new File("pokemonindexes");
	private static File pokeballs = new File("pokeballs");
	
	public Data(TelegramBot telegramBot) {
		this.telegramBot = telegramBot;
		readData();
	}

	@Override
	public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
		long id = event.getMessage().getSender().getId();
		String receivedMessage = event.getContent().getContent();
		SendableTextMessage message;
		
		if (receivedMessage.equals("/memory") && id == Main.id) {
			long memory = Runtime.getRuntime().totalMemory() / 1000000;
			message = SendableTextMessage.builder().message("Heap size:\n<b>" + memory + " MB</b>").parseMode(ParseMode.HTML).build();
			telegramBot.sendMessage(event.getChat(), message);
		}
		
		if (receivedMessage.equals("/all") && id == Main.id) {
			String str = "Cash:\n";
			for (String user : Cash.userCash.keySet()) {
				str += String.format("%s: $%,d%n", user, Cash.userCash.get(user));
			}
			message = SendableTextMessage.builder().message(str).build();
			telegramBot.sendMessage(event.getChat(), message);
			str = "Stocks:\n";
			for (String user : Stocks.shares.keySet()) {
				str += String.format("%s: %,d%n", user, Stocks.shares.get(user));
			}
			str += "\nStock Price:\n";
			str += String.format("$%,d%n", Stocks.stockPrice) + "\n";
			message = SendableTextMessage.builder().message(str).build();
			telegramBot.sendMessage(event.getChat(), message);
			str = "Cars:\n";
			for (String user : Properties.cars.keySet()) {
				str += String.format("%s: $%,d%n", user, Properties.cars.get(user));
			}
			str += "\nApartments:\n";
			for (String user : Properties.apartments.keySet()) {
				str += String.format("%s: $%,d%n", user, Properties.apartments.get(user));
			}
			str += "\nStores:\n";
			for (String user : Properties.stores.keySet()) {
				str += String.format("%s: $%,d%n", user, Properties.stores.get(user));
			}
			str += "\nMansions:\n";
			for (String user : Properties.mansions.keySet()) {
				str += String.format("%s: $%,d%n", user, Properties.mansions.get(user));
			}
			str += "\nJets:\n";
			for (String user : Properties.jets.keySet()) {
				str += String.format("%s: $%,d%n", user, Properties.jets.get(user));
			}
			str += "\nIslands:\n";
			for (String user : Properties.islands.keySet()) {
				str += String.format("%s: $%,d%n", user, Properties.islands.get(user));
			}
			str += "\nSpaceships:\n";
			for (String user : Properties.spaceships.keySet()) {
				str += String.format("%s: $%,d%n", user, Properties.spaceships.get(user));
			}
			str += "\nBank Money:\n";
			str += String.format("$%,d%n", BankRob.bankMoney);
			message = SendableTextMessage.builder().message(str).build();
			telegramBot.sendMessage(event.getChat(), message);
			str = "Pokemon indexes:\n";
			for (String user : Pokemon.pokemons.keySet()) {
				for (String pokemon : Pokemon.pokemons.get(user)) {
					str += String.format("%s: %s%n", user, pokemon);
				}
			}
			message = SendableTextMessage.builder().message(str).build();
			telegramBot.sendMessage(event.getChat(), message);
			str = "Pokeballs:\n";
			for (String user : Pokemon.pokeballs.keySet()) {
				str += String.format("%s: %,d%n", user, Pokemon.pokeballs.get(user));
			}
			str += "Wins:\n";
			for (String user : Pokemon.wins.keySet()) {
				str += String.format("%s: %,d%n", user, Pokemon.wins.get(user));
			}
			str += "Losses\n";
			for (String user : Pokemon.losses.keySet()) {
				str += String.format("%s: %,d%n", user, Pokemon.losses.get(user));
			}
			message = SendableTextMessage.builder().message(str).build();
			telegramBot.sendMessage(event.getChat(), message);
		}

		if (receivedMessage.equals("/allparsed") && id == Main.id) {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.submit(new Runnable() {
				
				@Override
				public void run() {
					while (true) {
						SendableTextMessage m;
						String str = "";
						int i = 0;
						for (String user : Cash.userCash.keySet()) {
							str += String.format("%s:%d%n", user, Cash.userCash.get(user));
							i++;
							if (i == Cash.userCash.keySet().size() / 4) {
								m = SendableTextMessage.builder().message(str).build();
								telegramBot.sendMessage(event.getChat(), m);
								str = "";
							}
							if (i == Cash.userCash.keySet().size() / 2) {
								m = SendableTextMessage.builder().message(str).build();
								telegramBot.sendMessage(event.getChat(), m);
								str = "";
							}
							if (i == 3 * Cash.userCash.keySet().size() / 4) {
								m = SendableTextMessage.builder().message(str).build();
								telegramBot.sendMessage(event.getChat(), m);
								str = "";
							}
						}
						m = SendableTextMessage.builder().message(str).build();
						telegramBot.sendMessage(event.getChat(), m);
						str = "";
						for (String user : Stocks.shares.keySet()) {
							str += String.format("%s:%d%n", user, Stocks.shares.get(user));
						}
						str += "\n" + Stocks.stockPrice + "\n\n" + BankRob.bankMoney;
						m = SendableTextMessage.builder().message(str).build();
						telegramBot.sendMessage(event.getChat(), m);
						str = "";
						for (String user : Properties.cars.keySet()) {
							str += String.format("%s:%d%n", user, Properties.cars.get(user));
						}
						m = SendableTextMessage.builder().message(str).build();
						telegramBot.sendMessage(event.getChat(), m);
						str = "";
						for (String user : Properties.apartments.keySet()) {
							str += String.format("%s:%d%n", user, Properties.apartments.get(user));
						}
						m = SendableTextMessage.builder().message(str).build();
						telegramBot.sendMessage(event.getChat(), m);
						str = "";
						for (String user : Properties.stores.keySet()) {
							str += String.format("%s:%d%n", user, Properties.stores.get(user));
						}
						m = SendableTextMessage.builder().message(str).build();
						telegramBot.sendMessage(event.getChat(), m);
						str = "";
						for (String user : Properties.mansions.keySet()) {
							str += String.format("%s:%d%n", user, Properties.mansions.get(user));
						}
						m = SendableTextMessage.builder().message(str).build();
						telegramBot.sendMessage(event.getChat(), m);
						str = "";
						for (String user : Properties.jets.keySet()) {
							str += String.format("%s:%d%n", user, Properties.jets.get(user));
						}
						m = SendableTextMessage.builder().message(str).build();
						telegramBot.sendMessage(event.getChat(), m);
						str = "";
						for (String user : Properties.islands.keySet()) {
							str += String.format("%s:%d%n", user, Properties.islands.get(user));
						}
						m = SendableTextMessage.builder().message(str).build();
						telegramBot.sendMessage(event.getChat(), m);
						str = "";
						for (String user : Properties.spaceships.keySet()) {
							str += String.format("%s:%d%n", user, Properties.spaceships.get(user));
						}
						m = SendableTextMessage.builder().message(str).build();
						telegramBot.sendMessage(event.getChat(), m);
						str = "";
						i = 0;
						for (String user : Pokemon.pokemons.keySet()) {
							str += user + ":";
							i++;
							for (String pokemon : Pokemon.pokemons.get(user)) {
								str += pokemon + ",";
							}
							str += "\n";
							if (i == Pokemon.pokemons.keySet().size() / 3) {
								m = SendableTextMessage.builder().message(str).build();
								telegramBot.sendMessage(event.getChat(), m);
								str = "";
							}
							if (i == 2 * Pokemon.pokemons.keySet().size() / 3) {
								m = SendableTextMessage.builder().message(str).build();
								telegramBot.sendMessage(event.getChat(), m);
								str = "";
							}
						}
						m = SendableTextMessage.builder().message(str).build();
						telegramBot.sendMessage(event.getChat(), m);
						str = "";
						i = 0;
						for (String user : Pokemon.pokeballs.keySet()) {
							str += user + ":" + Pokemon.pokeballs.get(user) + "," + Pokemon.wins.get(user) + "," + Pokemon.losses.get(user) + "\n";
							i++;
							if (i == Pokemon.pokeballs.keySet().size() / 3) {
								m = SendableTextMessage.builder().message(str).build();
								telegramBot.sendMessage(event.getChat(), m);
								str = "";
							}
							if (i == 2 * Pokemon.pokeballs.keySet().size() / 3) {
								m = SendableTextMessage.builder().message(str).build();
								telegramBot.sendMessage(event.getChat(), m);
								str = "";
							}
						}
						m = SendableTextMessage.builder().message(str).build();
						telegramBot.sendMessage(event.getChat(), m);
						try {
							Thread.sleep(600000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
			
		}
		
		
		if (receivedMessage.equals("/save") && Main.id == id) {
			try (BufferedWriter br = new BufferedWriter(new FileWriter(new File("Test")))) {
				for (String username : Cash.userCash.keySet()) {
					br.write(username + ":" + Cash.userCash.get(username) + "\n"); 
				}
				message = SendableTextMessage.builder().message("Test").build();
				telegramBot.sendMessage(event.getChat(), message);
			} catch (IOException e) {
				e.getMessage();
			}
		}
	}
	
	public void readData() {
		try (BufferedReader br = new BufferedReader(new FileReader(userCash))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] arr = line.split(":");
				Cash.userCash.put(arr[0], new BigInteger(arr[1]));
			}
		} catch (Exception e) {
			e.getMessage();
		}
		try (BufferedReader br = new BufferedReader(new FileReader(stocks))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] arr = line.split(":");
				Stocks.shares.put(arr[0], new BigInteger(arr[1]));
			}
		} catch (Exception e) {
			e.getMessage();
		}
		try (BufferedReader br = new BufferedReader(new FileReader(stockPrice))) {
			String line;
			while ((line = br.readLine()) != null) {
				Stocks.stockPrice = Integer.parseInt(line);
			}
		} catch (Exception e) {
			e.getMessage();
		}
		try (BufferedReader br = new BufferedReader(new FileReader(cars))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] arr = line.split(":");
				Properties.cars.put(arr[0], Long.parseLong(arr[1]));
			}
		} catch (Exception e) {
			e.getMessage();
		}
		try (BufferedReader br = new BufferedReader(new FileReader(apartments))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] arr = line.split(":");
				Properties.apartments.put(arr[0], Long.parseLong(arr[1]));
			}
		} catch (Exception e) {
			e.getMessage();
		}
		try (BufferedReader br = new BufferedReader(new FileReader(stores))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] arr = line.split(":");
				Properties.stores.put(arr[0], Long.parseLong(arr[1]));
			}
		} catch (Exception e) {
			e.getMessage();
		}
		try (BufferedReader br = new BufferedReader(new FileReader(mansions))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] arr = line.split(":");
				Properties.mansions.put(arr[0], Long.parseLong(arr[1]));
			}
		} catch (Exception e) {
			e.getMessage();
		}
		try (BufferedReader br = new BufferedReader(new FileReader(jets))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] arr = line.split(":");
				Properties.jets.put(arr[0], Long.parseLong(arr[1]));
			}
		} catch (Exception e) {
			e.getMessage();
		}
		try (BufferedReader br = new BufferedReader(new FileReader(islands))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] arr = line.split(":");
				Properties.islands.put(arr[0], Long.parseLong(arr[1]));
			}
		} catch (Exception e) {
			e.getMessage();
		}
		try (BufferedReader br = new BufferedReader(new FileReader(spaceships))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] arr = line.split(":");
				Properties.spaceships.put(arr[0], Long.parseLong(arr[1]));
			}
		} catch (Exception e) {
			e.getMessage();
		}
		try (BufferedReader br = new BufferedReader(new FileReader(bankmoney))) {
			String line;
			while ((line = br.readLine()) != null) {
				BankRob.bankMoney = Integer.parseInt(line);
			}
		} catch (Exception e) {
			e.getMessage();
		}
		try (BufferedReader br = new BufferedReader(new FileReader(pokemonIndexes))) {
			String line;
			while ((line = br.readLine()) != null) {
				String user = line.substring(0, line.indexOf(":"));
				String pokemon = line.substring(line.indexOf(":") + 1);
				String[] arr = pokemon.split(",");
				Pokemon.pokemons.put(user, new ArrayList<String>());
				for (String s : arr) {
					Pokemon.pokemons.get(user).add(s);
				}
			}
		} catch (Exception e) {
			e.getMessage();
		}
		try (BufferedReader br = new BufferedReader(new FileReader(pokeballs))) {
			String line;
			while ((line = br.readLine()) != null) {
				String user = line.substring(0, line.indexOf(":"));
				String pokemon = line.substring(line.indexOf(":") + 1);
				String[] arr = pokemon.split(",");
				Pokemon.pokeballs.put(user, Integer.parseInt(arr[0]));
				Pokemon.wins.put(user, Integer.parseInt(arr[1]));
				Pokemon.losses.put(user, Integer.parseInt(arr[2]));
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}
}
