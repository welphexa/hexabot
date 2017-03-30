import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.send.InputFile;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendablePhotoMessage;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.keyboards.KeyboardButton;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardMarkup;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardRemove;

public class Pokemon implements Listener {

	private TelegramBot telegramBot;
	
	private static File pokemonJson = new File("pokemon");
	private static File pokemonCatchRate = new File("pokemoncatchrate");
	private static File pokemonHp = new File("pokemonhp");
	private static File pokemonMovesJson = new File("pokemonmoves");
	private static InputStream pokemonJsonInput;
	private static InputStream pokemonMovesJsonInput;
	
	public static HashMap<String, ArrayList<String>> pokemons = new HashMap<>();
	public static HashMap<String, Integer> pokeballs = new HashMap<>();
	private static HashMap<Long, HashMap<String, Integer>> encounter = new HashMap<>();
	private static HashMap<Long, HashMap<String, String>> requests = new HashMap<>();
	private static HashMap<String, String> chosenPokemon = new HashMap<>();
	private static HashMap<String, Integer> chosenPokemonHp = new HashMap<>();
	private static HashMap<String, ArrayList<String>> chosenPokemonMoves = new HashMap<>();
	private static HashMap<String, String> battling = new HashMap<>();
	private static HashMap<String, Boolean> choosing = new HashMap<>();
	private static HashMap<String, Boolean> currentTurn = new HashMap<>();
	public static HashMap<String, Integer> wins = new HashMap<>();
	public static HashMap<String, Integer> losses = new HashMap<>();
	
	private static final ArrayList<Integer> missing = new ArrayList<>();
		
	public Pokemon(TelegramBot telegramBot) {
		this.telegramBot = telegramBot;
		onStart();
	}

	@Override
	public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
		ChatType chatType = event.getChat().getType();
		String receivedMessage = event.getContent().getContent();
		String username = event.getMessage().getSender().getUsername();
		String botUsername = telegramBot.getBotUsername();
		SendableTextMessage message;
		SendablePhotoMessage photo;
		
		if (receivedMessage.startsWith("/use") && chatType != ChatType.PRIVATE) {
			putDefaultValues(username);
			if (battling.containsKey(username)) {
				if (currentTurn.get(username)) {
					String move = receivedMessage.substring(4).trim();
					boolean moveExists = false;
					for (String s : chosenPokemonMoves.get(username)) {
						if (s.equalsIgnoreCase(move)) {
							moveExists = true;
							break;
						}
					}
					if (moveExists) {
						try {
							pokemonMovesJsonInput = new FileInputStream(pokemonMovesJson);
							pokemonJsonInput = new FileInputStream(pokemonJson);
						} catch (Exception e) {
							e.printStackTrace();
						}
						JsonReader reader = Json.createReader(pokemonMovesJsonInput);
						JsonObject object = reader.readObject();
						reader.close();
						JsonReader reader1 = Json.createReader(pokemonJsonInput);
						JsonObject object1 = reader1.readObject();
						reader1.close();
						String pokemon = object1.getJsonObject(chosenPokemon.get(username)).getString("name");
						String opponentsPokemon = object1.getJsonObject(chosenPokemon.get(battling.get(username))).getString("name");
						int damage = object.getJsonObject(move.toLowerCase()).getInt("power");
						int accuracy = (int)((object.getJsonObject(move.toLowerCase()).getJsonNumber("accuracy").doubleValue()) * 100);
						int chance = new Random().nextInt(100) + 1;
						if (accuracy >= chance) {
							int dodged = new Random().nextInt(9);
							if (dodged == 0) {
								message = SendableTextMessage.builder().message(opponentsPokemon + " dodged " + pokemon + "'s <b>" + move.toUpperCase() + "</b> attack").parseMode(ParseMode.HTML).build();
								telegramBot.sendMessage(event.getChat(), message);
								currentTurn.put(username, false);
								currentTurn.put(battling.get(username), true);
							} else {
								chosenPokemonHp.put(battling.get(username), chosenPokemonHp.get(battling.get(username)) - damage);
								if (chosenPokemonHp.get(battling.get(username)) <= 0) {
									message = SendableTextMessage.builder().message(battling.get(username) + "'s " + opponentsPokemon + " fainted\nWinner is:\n" + username + " and " + pokemon + "\nPrize: 3 pokeballs").replyMarkup(ReplyKeyboardRemove.builder().selective(true).build()).build();
									telegramBot.sendMessage(event.getChat(), message);
									losses.put(battling.get(username), losses.get(battling.get(username)) + 1);
									wins.put(username, wins.get(username) + 1);
									pokeballs.put(username, pokeballs.get(username) + 3);
									chosenPokemon.remove(battling.get(username));
									chosenPokemon.remove(username);
									chosenPokemonHp.remove(battling.get(username));
									chosenPokemonHp.remove(username);
									chosenPokemonMoves.remove(battling.get(username));
									chosenPokemonMoves.remove(username);
									currentTurn.put(battling.get(username), false);
									currentTurn.put(username, false);
									battling.remove(battling.get(username));
									battling.remove(username);
								} else {
									message = SendableTextMessage.builder().message(pokemon + " dealt " + damage + " damage to " + opponentsPokemon).build();
									telegramBot.sendMessage(event.getChat(), message);
									currentTurn.put(username, false);
									currentTurn.put(battling.get(username), true);
								}
							}
						} else {
							message = SendableTextMessage.builder().message(pokemon + "'s <b>" + move.toUpperCase() + "</b> attack missed").parseMode(ParseMode.HTML).build();
							telegramBot.sendMessage(event.getChat(), message);
							currentTurn.put(username, false);
							currentTurn.put(battling.get(username), true);
						}
					}
				}
			}
		}
		
		if (receivedMessage.startsWith("/choose")) {
			putDefaultValues(username);
			if (choosing.get(username) && !chosenPokemon.containsKey(username)) {
				String choice = receivedMessage.substring(7).trim();
				int choiceIndex = 0;
				boolean pokemonExists = false;
				try {
						pokemonJsonInput = new FileInputStream(pokemonJson);
					} catch (Exception e) {
						e.printStackTrace();
					}
					JsonReader reader = Json.createReader(pokemonJsonInput);
					JsonObject object = reader.readObject();
					reader.close();
				if (choice.equalsIgnoreCase("RANDOM")) {
					choiceIndex = Integer.parseInt(pokemons.get(username).get(new Random().nextInt(pokemons.get(username).size() - 1)));
					pokemonExists = true;
				} else {
					for (String i : pokemons.get(username)) {
						if (choice.equalsIgnoreCase(object.getJsonObject(i).getString("name"))) {
							choiceIndex = Integer.parseInt(i);
							pokemonExists = true;
							break;
						}
					}
				}
				if (pokemonExists) {
					InputFile inputFile = null;
					try {
						inputFile = new InputFile(new URL("http://assets22.pokemon.com/assets/cms2/img/pokedex/detail/" + String.format("%03d", choiceIndex) + ".png"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					photo = SendablePhotoMessage.builder().photo(inputFile).caption(username + " chose " + object.getJsonObject(String.valueOf(choiceIndex)).getString("name")).build();
					telegramBot.sendMessage(event.getChat(), photo);
					choosing.put(username, false);
					chosenPokemon.put(username, String.valueOf(choiceIndex));
					try (BufferedReader br = new BufferedReader(new FileReader(pokemonHp))) {
						String line;
						while ((line = br.readLine()) != null) {
							String[] arr = line.split(":");
							if (arr[0].equals(String.valueOf(choiceIndex))) {
								chosenPokemonHp.put(username, Integer.parseInt(arr[1]));
								break;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					JsonArray array = object.getJsonObject(String.valueOf(choiceIndex)).getJsonArray("moves");
					ArrayList<String> moves = new ArrayList<>();
					for (int i = 0; i < array.size(); i++) {
						moves.add(array.getString(i));
					}
					chosenPokemonMoves.put(username, moves);
				}
			}
		}
		
		if (receivedMessage.startsWith("/challenge") && chatType != ChatType.PRIVATE) {
			String repliedToUsername = event.getMessage().getRepliedTo().getSender().getUsername();
			putDefaultValues(repliedToUsername);
			putDefaultValues(username);
			if (!battling.containsKey(repliedToUsername) && !choosing.get(repliedToUsername) && !(repliedToUsername.equals(username))) {
				if (!battling.containsKey(username) && !choosing.get(username)) {
					if (pokemons.get(username).size() > 0) {
						message = SendableTextMessage.builder().message(repliedToUsername + ", someone has challenged you\nReply to this message with\n/accept to battle").replyTo(event.getMessage()).build();
						long messageId = telegramBot.sendMessage(event.getChat(), message).getMessageId();
						HashMap<String, String> request = new HashMap<>();
						request.put(repliedToUsername, username);
						requests.put(messageId, request);
						ExecutorService executor = Executors.newSingleThreadExecutor();
						executor.submit(new Runnable() {
							
							@Override
							public void run() {
								try {
									Thread.sleep(30000);
									if (requests.containsKey(messageId)) {
										requests.remove(messageId);
										SendableTextMessage m = SendableTextMessage.builder().message(username + "'s challenge cancelled").build();
										telegramBot.sendMessage(event.getChat(), m);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					} else {
						message = SendableTextMessage.builder().message("You do not have any pokemon to battle with").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					}
				} else {
					message = SendableTextMessage.builder().message("You are currently battling someone").replyTo(event.getMessage()).build();
					telegramBot.sendMessage(event.getChat(), message);
				}
			} else {
				message = SendableTextMessage.builder().message(repliedToUsername + " is currently battling someone").replyTo(event.getMessage()).build();
				telegramBot.sendMessage(event.getChat(), message);
			}
		}
		
		if (receivedMessage.startsWith("/accept") && chatType != ChatType.PRIVATE) {
			long repliedToId = event.getMessage().getRepliedTo().getMessageId();
			putDefaultValues(username);
			if (requests.containsKey(repliedToId)) {
				if (requests.get(repliedToId).containsKey(username)) {
					String challenger = requests.get(repliedToId).get(username);
					putDefaultValues(challenger);
					if (!battling.containsKey(challenger) && !choosing.get(challenger)) {
						if (!battling.containsKey(username) && !choosing.get(username)) {
							if (pokemons.get(username).size() > 0) {
								try {
									pokemonJsonInput = new FileInputStream(pokemonJson);
								} catch (Exception e) {
									e.printStackTrace();
								}
								JsonReader reader = Json.createReader(pokemonJsonInput);
								JsonObject object = reader.readObject();
								reader.close();
								ListIterator<String> it = pokemons.get(username).listIterator();
								ArrayList<KeyboardButton> buttons = new ArrayList<>();
								buttons.add(KeyboardButton.builder().text("/choose RANDOM").build());
								while (it.hasNext()) {
									String s = it.next();
									buttons.add(KeyboardButton.builder().text("/choose " + object.getJsonObject(s).getString("name")).build());
								}
								ReplyKeyboardMarkupBuilder keyboard = ReplyKeyboardMarkup.builder();
								for (KeyboardButton button : buttons) {
									keyboard.addRow(button);
								}
								ListIterator<String> it2 = pokemons.get(challenger).listIterator();
								ArrayList<KeyboardButton> buttons2 = new ArrayList<>();
								buttons2.add(KeyboardButton.builder().text("/choose RANDOM").build());
								while (it2.hasNext()) {
									String s = it2.next();
									buttons2.add(KeyboardButton.builder().text("/choose " + object.getJsonObject(s).getString("name")).build());
								}
								ReplyKeyboardMarkupBuilder keyboard2 = ReplyKeyboardMarkup.builder();
								for (KeyboardButton button : buttons2) {
									keyboard2.addRow(button);
								}
								message = SendableTextMessage.builder().message(username + " you have 30 seconds to choose your pokemon\n/choose <pokemon>").replyMarkup(keyboard.selective(true).oneTime(true).build()).build();
								telegramBot.sendMessage(event.getChat(), message);
								message = SendableTextMessage.builder().message(challenger + " you have 30 seconds to choose your pokemon\n/choose <pokemon>").replyMarkup(keyboard2.selective(true).oneTime(true).build()).build();
								telegramBot.sendMessage(event.getChat(), message);
								requests.remove(repliedToId);
								choosing.put(username, true);
								choosing.put(challenger, true);
								ExecutorService executor = Executors.newSingleThreadExecutor();
								executor.submit(new Runnable() {
									
									@Override
									public void run() {
										try {
											Thread.sleep(30000);
											if (choosing.get(username) || choosing.get(challenger)) {
												choosing.put(username, false);
												choosing.put(challenger, false);
												chosenPokemon.remove(username);
												chosenPokemon.remove(challenger);
												chosenPokemonHp.remove(username);
												chosenPokemonHp.remove(challenger);
												chosenPokemonMoves.remove(username);
												chosenPokemonMoves.remove(challenger);
												SendableTextMessage m = SendableTextMessage.builder().message("Battle between " + username + " and " + challenger + " cancelled").replyMarkup(ReplyKeyboardRemove.builder().selective(true).build()).build();
												telegramBot.sendMessage(event.getChat(), m);
											} else {
												battling.put(username, challenger);
												battling.put(challenger, username);
												int chance = new Random().nextInt(1);
												if (chance == 1) {
													currentTurn.put(username, true);
												} else {
													currentTurn.put(challenger, true);
												}
												String pokemon1 = object.getJsonObject(chosenPokemon.get(challenger)).getString("name");
												String pokemon2 = object.getJsonObject(chosenPokemon.get(username)).getString("name");
												String hp1 = null;
												try (BufferedReader br = new BufferedReader(new FileReader(pokemonHp))) {
													String line;
													while ((line = br.readLine()) != null) {
														String[] arr = line.split(":");
														if (arr[0].equals(chosenPokemon.get(challenger))) {
															hp1 = arr[1];
															break;
														}
													}
												} catch (Exception e) {
													e.printStackTrace();
												}
												String hp2 = null;
												try (BufferedReader br = new BufferedReader(new FileReader(pokemonHp))) {
													String line;
													while ((line = br.readLine()) != null) {
														String[] arr = line.split(":");
														if (arr[0].equals(chosenPokemon.get(username))) {
															hp2 = arr[1];
															break;
														}
													}
												} catch (Exception e) {
													e.printStackTrace();
												}
												SendableTextMessage m = SendableTextMessage.builder().message("Match started:\n" + challenger + " vs. " + username + "\n" + pokemon1 + " - " + hp1 + " HP\n" + pokemon2 + " - " + hp2 + " HP").build();
												telegramBot.sendMessage(event.getChat(), m);
												ExecutorService executor1 = Executors.newSingleThreadExecutor();
												executor1.submit(new Runnable() {
													
													@Override
													public void run() {
														try {
															while (battling.containsKey(username) && battling.containsKey(challenger)) {
																String turn = null;
																if (currentTurn.get(username)) {
																	turn = username;
																} else if (currentTurn.get(challenger)) {
																	turn = challenger;
																}
																String info = "Current turn: " + turn + "\n";
																try {
																	pokemonMovesJsonInput = new FileInputStream(pokemonMovesJson);
																	pokemonJsonInput = new FileInputStream(pokemonJson);
																} catch (Exception e) {
																	e.printStackTrace();
																}
																JsonReader reader = Json.createReader(pokemonMovesJsonInput);
																JsonObject object = reader.readObject();
																reader.close();
																JsonReader reader1 = Json.createReader(pokemonJsonInput);
																JsonObject object1 = reader1.readObject();
																reader1.close();
																String pokemon = object1.getJsonObject(chosenPokemon.get(turn)).getString("name");
																info += pokemon + " - <b>HP: ";
																try (BufferedReader br = new BufferedReader(new FileReader(pokemonHp))) {
																	String line;
																	while ((line = br.readLine()) != null) {
																		String[] arr = line.split(":");
																		if (arr[0].equals(chosenPokemon.get(turn))) {
																			info += chosenPokemonHp.get(turn) + "/" + arr[1] + "</b>\n";
																			break;
																		}
																	}
																} catch (Exception e) {
																	e.printStackTrace();
																}
																info += "\nMoves:\n";
																ListIterator<String> it = chosenPokemonMoves.get(turn).listIterator();
																ArrayList<KeyboardButton> buttons = new ArrayList<>();
																while (it.hasNext()) {
																	String j = it.next();
																	info += "<b>" + j.toUpperCase() + "</b>" + " -\nDamage: " + object.getJsonObject(j.toLowerCase()).getInt("power") + ", Accuracy: " + (int)((object.getJsonObject(j.toLowerCase()).getJsonNumber("accuracy").doubleValue()) * 100) + "\n";
																	buttons.add(KeyboardButton.builder().text("/use " + j.toLowerCase()).build());
																}
																info += "/use &lt;move name&gt;";
																ReplyKeyboardMarkupBuilder keyboard = ReplyKeyboardMarkup.builder();
																for (KeyboardButton button : buttons) {
																	keyboard.addRow(button);
																}
																SendableTextMessage m1 = SendableTextMessage.builder().message(info).replyMarkup(keyboard.selective(true).resize(true).oneTime(true).build()).parseMode(ParseMode.HTML).build();
																telegramBot.sendMessage(event.getChat(), m1);
																boolean playerMoved = false;
																for (int i = 0; i < 240; i++) {
																	Thread.sleep(500);
																	if (!currentTurn.get(turn)) {
																		playerMoved = true;
																		break;
																	}
																}
																if (!playerMoved) {
																	m1 = SendableTextMessage.builder().message(turn + " has not moved\nPlayer forfeits and " + (currentTurn.get(username) ? challenger : username) + " wins 3 pokeballs").replyMarkup(ReplyKeyboardRemove.builder().selective(true).build()).build();
																	telegramBot.sendMessage(event.getChat(), m1);
																	pokeballs.put(battling.get(turn), pokeballs.get(battling.get(turn)) + 3);
																	wins.put(battling.get(turn), wins.get(battling.get(turn)) + 1);
																	losses.put(turn, losses.get(turn) + 1);
																	choosing.put(username, false);
																	choosing.put(challenger, false);
																	chosenPokemon.remove(username);
																	chosenPokemon.remove(challenger);
																	chosenPokemonHp.remove(username);
																	chosenPokemonHp.remove(challenger);
																	chosenPokemonMoves.remove(username);
																	chosenPokemonMoves.remove(challenger);
																	currentTurn.put(username, false);
																	currentTurn.put(challenger, false);
																	battling.remove(username);
																	battling.remove(challenger);
																}
															}
														} catch (Exception e) {
															e.printStackTrace();
														}
													}
												});
												executor1.shutdown();
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								});
								executor.shutdown();
							} else {
								message = SendableTextMessage.builder().message("You do not have any pokemon to battle with").replyTo(event.getMessage()).build();
								telegramBot.sendMessage(event.getChat(), message);
								requests.remove(repliedToId);
							}
						} else {
							message = SendableTextMessage.builder().message("You are currently battling someone").replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
							requests.remove(repliedToId);
						}
					} else {
						message = SendableTextMessage.builder().message(requests.get(repliedToId).get(username) + " is currently battling someone").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
						requests.remove(repliedToId);
					}
				}
			}
		}
		
		if (receivedMessage.startsWith("/hunt")) {
			if (event.getMessage().getSender().getUsername().equals("@")) {
				message = SendableTextMessage.builder().message("You need a username to play").replyTo(event.getMessage()).build();
				telegramBot.sendMessage(event.getChat(), message);
			} else {
				putDefaultValues(username);
				int randomIndex = new Random().nextInt(151) + 1;;
				while (missing.contains(randomIndex)) {
					randomIndex = new Random().nextInt(151) + 1;
				}
				String randomPokemon = String.valueOf(randomIndex);
				InputFile inputFile = null;
				try {
					pokemonJsonInput = new FileInputStream(pokemonJson);
					inputFile = new InputFile(new URL("http://assets22.pokemon.com/assets/cms2/img/pokedex/detail/" + String.format("%03d", randomIndex) + ".png"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				JsonReader reader = Json.createReader(pokemonJsonInput);
				JsonObject object = reader.readObject();
				reader.close();
				String hp = null;
				try (BufferedReader br = new BufferedReader(new FileReader(pokemonHp))) {
					String line;
					while ((line = br.readLine()) != null) {
						String[] arr = line.split(":");
						if (arr[0].equals(randomPokemon)) {
							hp = "\n" + arr[1] + " HP, ";
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				String catchRate = null;
				try (BufferedReader br = new BufferedReader(new FileReader(pokemonCatchRate))) {
					String line;
					while ((line = br.readLine()) != null) {
						String[] arr = line.split(":");
						if (arr[0].equals(randomPokemon)) {
							catchRate = (int)((Double.parseDouble(arr[1])/255) * 100) + "% catch probability\n";
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				photo = SendablePhotoMessage.builder().photo(inputFile).caption("You encountered a wild " + object.getJsonObject(String.valueOf(randomPokemon)).getString("name") + hp + catchRate + "Reply to this message with\n/catch to attempt a capture").replyTo(event.getMessage()).build();
				long encounterId = telegramBot.sendMessage(event.getChat(), photo).getMessageId();
				HashMap<String, Integer> pokeIndex = new HashMap<>();
				pokeIndex.put(username, randomIndex);
				encounter.put(encounterId, pokeIndex);
			}
		}
		
		if (receivedMessage.startsWith("/catch")) {
			long repliedToId = event.getMessage().getRepliedTo().getMessageId();
			if (encounter.containsKey(repliedToId)) {
				if (encounter.get(repliedToId).containsKey(username)) {
					putDefaultValues(username);
					int pokemonIndex = encounter.get(repliedToId).get(username);
					String pokemon = String.valueOf(pokemonIndex);
					if (!pokemons.get(username).contains(pokemon)) {
						try {
							pokemonJsonInput = new FileInputStream(pokemonJson);
						} catch (Exception e) {
							e.printStackTrace();
						}
						JsonReader reader = Json.createReader(pokemonJsonInput);
						JsonObject object = reader.readObject();
						reader.close();
						if (pokeballs.get(username) > 0) {
							int catchRate = 0;
							try (BufferedReader br = new BufferedReader(new FileReader(pokemonCatchRate))) {
								String line;
								while ((line = br.readLine()) != null) {
									String[] arr = line.split(":");
									if (arr[0].equals(pokemon)) {
										catchRate = Integer.parseInt(arr[1]);
										break;
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							int probability = new Random().nextInt(255) + 1;
							pokeballs.put(username, pokeballs.get(username) - 1);
							if (catchRate >= probability) {
								pokemons.get(username).add(pokemon);
								message = SendableTextMessage.builder().message("You caught a wild " + object.getJsonObject(pokemon).getString("name")).replyTo(event.getMessage()).build();
								telegramBot.sendMessage(event.getChat(), message);
							} else {
								int pokeballStumble = new Random().nextInt(9);
								if (pokeballStumble == 0) {
									int pokeballFind = new Random().nextInt(10) + 1;
									pokeballs.put(username, pokeballs.get(username) + pokeballFind);
									message = SendableTextMessage.builder().message("Your pokeball failed and the wild " + object.getJsonObject(pokemon).getString("name") + " fled\n\nYou also stumbled upon " + pokeballFind + (pokeballFind == 1 ? " pokeball" : " pokeballs")).replyTo(event.getMessage()).build();
									telegramBot.sendMessage(event.getChat(), message);
								} else {
									message = SendableTextMessage.builder().message("Your pokeball failed and the wild " + object.getJsonObject(pokemon).getString("name") + " fled").replyTo(event.getMessage()).build();
									telegramBot.sendMessage(event.getChat(), message);
								}
							}
							if (pokemons.get(username).isEmpty() && pokeballs.get(username) == 0) {
								pokeballs.put(username, pokeballs.get(username) + 1);
								message = SendableTextMessage.builder().message("You had no more pokeballs but a kind stranger gifted you one").replyTo(event.getMessage()).build();
								telegramBot.sendMessage(event.getChat(), message);
							}
						} else {
							if (pokemons.get(username).isEmpty()) {
								pokeballs.put(username, pokeballs.get(username) + 1);
								message = SendableTextMessage.builder().message("You had no more pokeballs but a kind stranger gifted you one").replyTo(event.getMessage()).build();
								telegramBot.sendMessage(event.getChat(), message);
							} else {
								message = SendableTextMessage.builder().message("Use your pokemon to battle and win more pokeballs").replyTo(event.getMessage()).build();
								telegramBot.sendMessage(event.getChat(), message);
							}
						}
					} else {
						message = SendableTextMessage.builder().message("You already have this pokemon").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					}
					encounter.remove(repliedToId);
				}
			}
		}
		
		if (receivedMessage.equals("/mypokemon@" + botUsername) || receivedMessage.equals("/mypokemon")) {
			putDefaultValues(username);
			String stats = "Battle statistics:\n<b>Wins: " + wins.get(username) + "</b> | <b>Losses: " + losses.get(username) + "</b>\n\nPokemon:\n";
			try {
				pokemonJsonInput = new FileInputStream(pokemonJson);
			} catch (Exception e) {
				e.printStackTrace();
			}
			JsonReader reader = Json.createReader(pokemonJsonInput);
			JsonObject object = reader.readObject();
			reader.close();
			for (String pokemon : pokemons.get(username)) {
				stats += object.getJsonObject(pokemon).getString("name") + " - ";
				try (BufferedReader br = new BufferedReader(new FileReader(pokemonHp))) {
					String line;
					while ((line = br.readLine()) != null) {
						String[] arr = line.split(":");
						if (arr[0].equals(pokemon)) {
							stats += arr[1] + " HP\n";
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			stats += "\n<b>Pokeballs: " + pokeballs.get(username) + "</b>";
			message = SendableTextMessage.builder().message(stats).replyTo(event.getMessage()).parseMode(ParseMode.HTML).build();
			telegramBot.sendMessage(event.getChat(), message);
		}
		
		if (receivedMessage.startsWith("/send")) {
			String arr[] = receivedMessage.split(" ");
			putDefaultValues(username);
			if (arr.length == 3) {
				try {
					String user = arr[1];
					String parsed = arr[2].replaceAll(",", "");
					int amount = Integer.parseInt(parsed);
					putDefaultValues(user);
					if (!user.equals(username)) {
						if (amount < 1) {
							message = SendableTextMessage.builder().message("Invalid amount").build();
							telegramBot.sendMessage(event.getChat(), message);
						} else if (pokeballs.get(username) >= amount) {
							pokeballs.put(username, pokeballs.get(username) - amount);
							pokeballs.put(user, pokeballs.get(user) + amount);
							message = SendableTextMessage.builder().message(String.format("Sent %,d " + (amount == 1 ? "pokeball" : "pokeballs") + " to %s", amount, user)).build();
							telegramBot.sendMessage(event.getChat(), message);
						} else {
							message = SendableTextMessage.builder().message("Not enough pokeballs").build();
							telegramBot.sendMessage(event.getChat(), message);
						}
					}
				} catch (Exception e) {
					e.getMessage();
				}
			} else if (arr.length == 2) {
				try {
					String user = event.getMessage().getRepliedTo().getSender().getUsername();
					String parsed = arr[1].replaceAll(",", "");
					int amount = Integer.parseInt(parsed);
					putDefaultValues(user);
					if (!user.equals(username)) {
						if (amount < 1) {
							message = SendableTextMessage.builder().message("Invalid amount").build();
							telegramBot.sendMessage(event.getChat(), message);
						} else if (pokeballs.get(username) >= amount) {
							pokeballs.put(username, pokeballs.get(username) - amount);
							pokeballs.put(user, pokeballs.get(user) + amount);
							message = SendableTextMessage.builder().message(String.format("Sent %,d " + (amount == 1 ? "pokeball" : "pokeballs") + " to %s", amount, user)).build();
							telegramBot.sendMessage(event.getChat(), message);
						} else {
							message = SendableTextMessage.builder().message("Not enough pokeballs").build();
							telegramBot.sendMessage(event.getChat(), message);
						}
					}
				} catch (Exception e) {
					e.getMessage();
				}
			}
		}
		
		if (receivedMessage.startsWith("/receive") && event.getMessage().getSender().getId() == Main.id) {
			try {
				String[] arr = receivedMessage.split(" ");
				pokeballs.put(username, pokeballs.get(username) + Integer.parseInt(arr[1]));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (receivedMessage.startsWith("/release")) {
			if (event.getMessage().getRepliedTo() == null) {
				if (!pokemons.get(username).isEmpty()) {
					String choice = receivedMessage.substring(8).trim();
					try {
						pokemonJsonInput = new FileInputStream(pokemonJson);
					} catch (Exception e) {
						e.printStackTrace();
					}
					JsonReader reader = Json.createReader(pokemonJsonInput);
					JsonObject object = reader.readObject();
					reader.close();
					ListIterator<String> pokemon = pokemons.get(username).listIterator();
					while (pokemon.hasNext()) {
						String i = pokemon.next();
						if (choice.equalsIgnoreCase(object.getJsonObject(i).getString("name"))) {
							pokemon.remove();
							message = SendableTextMessage.builder().message("You released " + object.getJsonObject(i).getString("name")).replyTo(event.getMessage()).build();
							telegramBot.sendMessage(event.getChat(), message);
							break;
						}
					}
				}
			}
		}
		
		if (receivedMessage.equals("/pokemon@" + botUsername) || receivedMessage.equals("/pokemon")) {
			String info = "How to play pokemon:\n/mypokemon - check your stats\n/hunt - hunt for new pokemon\n/challenge - challenge a player to a battle\nNote: the winner wins one pokeball\n/release - release a pokemon\n/send - send pokeballs to someone";
			message = SendableTextMessage.builder().message(info).build();
			telegramBot.sendMessage(event.getChat(), message);
		}
		
		if (receivedMessage.equals("/endall") && event.getMessage().getSender().getId() == Main.id) {
			chosenPokemon.clear();
			chosenPokemonHp.clear();
			chosenPokemonMoves.clear();
			battling.clear();
			choosing.clear();
			currentTurn.clear();
		}
		
		if (receivedMessage.startsWith("/show")) {
			try {
				String[] arr = receivedMessage.split(" ");
				pokemonJsonInput = new FileInputStream(pokemonJson);
				JsonReader reader = Json.createReader(pokemonJsonInput);
				JsonObject object = reader.readObject();
				reader.close();
			
				JsonObject index = object.getJsonObject(arr[1]);
			
				message = SendableTextMessage.builder().message(index.getString("name")).build();
				telegramBot.sendMessage(event.getChat(), message);
				
				photo = SendablePhotoMessage.builder().photo(new InputFile(new URL("http://assets22.pokemon.com/assets/cms2/img/pokedex/detail/" + String.format("%03d", Integer.parseInt(arr[1])) + ".png"))).build();
				telegramBot.sendMessage(event.getChat(), photo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void putDefaultValues(String username) {
		if (!pokemons.containsKey(username)) {
			pokemons.put(username, new ArrayList<String>());
		}
		if (!pokeballs.containsKey(username)) {
			pokeballs.put(username, 10);
		}
		if (!wins.containsKey(username)) {
			wins.put(username, 0);
		}
		if (!losses.containsKey(username)) {
			losses.put(username, 0);
		}
		if (!choosing.containsKey(username)) {
			choosing.put(username, false);
		}
		if (!currentTurn.containsKey(username)) {
			currentTurn.put(username, false);
		}
	}
	
	public void onStart() {
		missing.add(11);
		missing.add(14);
		missing.add(15);
		missing.add(30);
		missing.add(33);
		missing.add(35);
		missing.add(36);
		missing.add(37);
		missing.add(39);
		missing.add(40);
		missing.add(43);
		missing.add(44);
		missing.add(45);
		missing.add(58);
		missing.add(61);
		missing.add(63);
		missing.add(64);
		missing.add(67);
		missing.add(70);
		missing.add(75);
		missing.add(90);
		missing.add(93);
		missing.add(115);
		missing.add(120);
		missing.add(132);
		missing.add(134);
	}
}