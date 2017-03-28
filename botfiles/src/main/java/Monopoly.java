import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.send.InputFile;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendablePhotoMessage;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class Monopoly implements Listener {

	private TelegramBot telegramBot;
	
	private static HashMap<String, Integer> token = new HashMap<>();
	private static final int minPlayers = 2;
	private static final int maxPlayers = 8;
	private static final int startingCash = 1500;
	private static HashMap<String, Integer> dice1 = new HashMap<>();
	private static HashMap<String, Integer> dice2 = new HashMap<>();
	private static HashMap<String, StringBuilder> result = new HashMap<>();
	private static HashMap<String, HashMap<String, Integer>> kicked = new HashMap<>();
	private static HashMap<String, Boolean> joining = new HashMap<>();
	private static HashMap<String, Boolean> active = new HashMap<>();
	private static HashMap<String, Boolean> rolling = new HashMap<>();
	private static HashMap<String, ArrayList<String>> playersList = new HashMap<>();
	private static HashMap<String, ListIterator<String>> playersListIterator = new HashMap<>();
	private static HashMap<String, HashMap<String, Boolean>> currentTurn = new HashMap<>();
	private static HashMap<String, HashMap<String, Integer>> playersCash = new HashMap<>();
	private static HashMap<String, HashMap<String, Integer>> playersPosition = new HashMap<>();
	private static HashMap<String, ArrayList<Integer>> ownedProperties = new HashMap<>();
	private static HashMap<String, HashMap<String, ArrayList<Integer>>> playersProperties = new HashMap<>();
	private static HashMap<String, ArrayList<Integer>> mortgaged = new HashMap<>();
	private static HashMap<String, HashMap<Integer, Integer>> houses = new HashMap<>();
	private static HashMap<String, Integer> availableHouses = new HashMap<>();
	private static HashMap<String, Integer> availableHotels = new HashMap<>();
	private static HashMap<String, Boolean> passedGo = new HashMap<>();
	private static HashMap<String, HashMap<String, Boolean>> freeParking = new HashMap<>();
	private static HashMap<String, HashMap<String, Integer>> getOutOfJailFree = new HashMap<>();
	private static HashMap<String, ArrayList<Integer>> chanceList = new HashMap<>();
	private static HashMap<String, ArrayList<Integer>> communityChestList = new HashMap<>();
	
	private static final HashMap<Integer, String> propertyPositions = new HashMap<>(); 
	private static final HashMap<Integer, Integer> propertyInitialValue = new HashMap<>();
	private static final HashMap<Integer, Integer> housePrices = new HashMap<>();
	private static final HashMap<Integer, HashMap<Integer, Integer>> rent = new HashMap<>();
	private static final ArrayList<Integer> isGo = new ArrayList<Integer>();
	private static final ArrayList<Integer> isProperty = new ArrayList<Integer>();
	private static final ArrayList<Integer> isRailroad = new ArrayList<Integer>();
	private static final ArrayList<Integer> isUtility = new ArrayList<Integer>();
	private static final ArrayList<Integer> isCommunityChest = new ArrayList<Integer>();
	private static final ArrayList<Integer> isChance = new ArrayList<Integer>();
	private static final ArrayList<Integer> isTax = new ArrayList<Integer>();
	private static final ArrayList<Integer> isVisitingJail = new ArrayList<Integer>();
	private static final ArrayList<Integer> isGoToJail = new ArrayList<Integer>();
	private static final ArrayList<Integer> isFreeParking = new ArrayList<Integer>();
	private static final HashMap<Integer, String> chanceCards = new HashMap<>();
	private static final HashMap<Integer, String> communityChestCards = new HashMap<>();
	
	public Monopoly(TelegramBot telegramBot) {
		this.telegramBot = telegramBot;
		onStart();
	}

	@Override
	public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
		boolean inPrivate = event.getChat().getType() == ChatType.PRIVATE;
		String chat = event.getChat().getId();
		String username = event.getMessage().getSender().getUsername();
		String receivedMessage = event.getContent().getContent();
		String botUsername = telegramBot.getBotUsername();
		SendableTextMessage message;
		
		if (receivedMessage.equals("/endgame") && event.getMessage().getSender().getId() == Main.id) {
			if (!active.containsKey(chat)) {
				active.put(chat, false);
			}
			if (active.get(chat)) {
				endGame(chat);
			}
		}
		
		if (receivedMessage.equals("/newgame@" + botUsername) || receivedMessage.equals("/newgame") && !inPrivate) {
			if (!joining.containsKey(chat)) {
				joining.put(chat, false);
			}
			if (!active.containsKey(chat)) {
				active.put(chat, false);
			}
			if (active.get(chat)) {
				message = SendableTextMessage.builder().message("A game is currently active").build();
				telegramBot.sendMessage(event.getChat(), message);
			} else {
				if (joining.get(chat)) {
					message = SendableTextMessage.builder().message("A game is about to begin\nUse /joingame to join").build();
					telegramBot.sendMessage(event.getChat(), message);
				} else {
					joining.put(chat, true);
					message = SendableTextMessage.builder().message("A monopoly game has been started\nOne minute left to /joingame\n\nUse /monopoly for available commands").build();
					telegramBot.sendMessage(event.getChat(), message);
					ExecutorService executor = Executors.newSingleThreadExecutor();
					executor.submit(new Runnable() {
					
						@Override
						public void run() {
							try {
								Thread.sleep(60000);
								SendableTextMessage m;
								joining.put(chat, false);
								if (playersList.get(chat).size() < minPlayers) {
									m = SendableTextMessage.builder().message("Time is up\nNot enough players have joined\nCancelling game").build();
									telegramBot.sendMessage(event.getChat(), m);
									playersList.clear();
								} else {
									onStart(chat);
									m = SendableTextMessage.builder().message("Starting the monopoly game\nGame will last 30 minutes\nUse /monopoly for available commands").build();
									telegramBot.sendMessage(event.getChat(), m);
									ExecutorService executor1 = Executors.newSingleThreadExecutor();
									executor1.submit(new Runnable() {
										
										@Override
										public void run() {
											ExecutorService executor2 = Executors.newSingleThreadExecutor();
											executor2.submit(new Runnable() {
												
												@Override
												public void run() {
													try {
														int startToken = token.get(chat);
														SendableTextMessage m2;
														Thread.sleep(1500000);
														if (startToken == token.get(chat) && active.get(chat)) {
															m2 = SendableTextMessage.builder().message("You have 5 minutes left").build();
															telegramBot.sendMessage(event.getChat(), m2);
															Thread.sleep(300000);
														}
														if (startToken == token.get(chat) && active.get(chat)) {
															m2 = SendableTextMessage.builder().message("The game is over. Results:\n" + winners(chat)).build();
															telegramBot.sendMessage(event.getChat(), m2);
															endGame(chat);
														}
													} catch (Exception e) {
														e.getMessage();
													}
												}
											});
											executor2.shutdown();
											try {
												while (active.get(chat)) {
													SendableTextMessage m2;
													String turn = getCurrentTurn(chat);
													m2 = SendableTextMessage.builder().message("Current turn: " + turn + "\nYou have one minute to make your moves\n/roll when ready").build();
													telegramBot.sendMessage(event.getChat(), m2);
													rolling.put(chat, true);
													for (int i = 0; i < 120; i++) {
														Thread.sleep(500);
														if (!playersList.get(chat).contains(turn)) {
															break;
														}
														if (!currentTurn.get(chat).get(turn)) {
															break;
														}
													}
													if (rolling.get(chat) && playersList.get(chat).contains(turn)) {
														kicked.get(chat).put(turn, kicked.get(chat).get(turn) + 1);
														if (kicked.get(chat).get(turn) == 3) {
															m2 = SendableTextMessage.builder().message(turn + " has been kicked from the game for 3 turns of inactivity").build();
															telegramBot.sendMessage(event.getChat(), m2);
															currentTurn.get(chat).put(turn, false);
															if (playersListIterator.get(chat).hasNext()) {
																currentTurn.get(chat).put(playersListIterator.get(chat).next(), true);
															} else {
																playersListIterator.put(chat, playersList.get(chat).listIterator());
																currentTurn.get(chat).put(playersListIterator.get(chat).next(), true);
															}
															deletePlayer(chat, turn);
														} 
													}
													if (playersList.get(chat).size() < minPlayers && active.get(chat)) {
														m2 = SendableTextMessage.builder().message("Not enough players\nCancelling game").build();
														telegramBot.sendMessage(event.getChat(), m2);
														endGame(chat);
														break;
													}
													if (playersList.get(chat).contains(turn)) {
														currentTurn.get(chat).put(turn, false);
														if (playersListIterator.get(chat).hasNext()) {
															currentTurn.get(chat).put(playersListIterator.get(chat).next(), true);
														} else {
															playersListIterator.put(chat, playersList.get(chat).listIterator());
															currentTurn.get(chat).put(playersListIterator.get(chat).next(), true);
														}
													}
												} 
											} catch (Exception e) {
												e.getMessage();
											}
										}
									});
									executor1.shutdown();
								}
							} catch (Exception e) {
								e.getMessage();
							}
						}
					});
					executor.shutdown();
				}
			}
		}
		
		if (receivedMessage.equals("/joingame@" + botUsername) || receivedMessage.equals("/joingame") && !inPrivate) {
			if (!joining.containsKey(chat)) {
				joining.put(chat, false);
			}
			if (!active.containsKey(chat)) {
				active.put(chat, false);
			}
			if (active.get(chat)) {
				message = SendableTextMessage.builder().message("A game is currently active\nPlease wait for the next round").replyTo(event.getMessage()).build();
				telegramBot.sendMessage(event.getChat(), message);
			} else {
				if (joining.get(chat)) {
					if (!playersList.containsKey(chat)) {
						playersList.put(chat, new ArrayList<String>());
					}
					if (playersList.get(chat).contains(username)) {
						message = SendableTextMessage.builder().message("You are already in the game").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					} else if (playersList.get(chat).size() == maxPlayers) {
						message = SendableTextMessage.builder().message("Player limit reached\nPlease wait for the next round").replyTo(event.getMessage()).build();
						telegramBot.sendMessage(event.getChat(), message);
					} else {
						playersList.get(chat).add(username);
						message = SendableTextMessage.builder().message(username + " has joined the game\nTotal players: " + playersList.get(chat).size() + "\nMin players: " + minPlayers + "\nMax players: " + maxPlayers).build();
						telegramBot.sendMessage(event.getChat(), message);
					}
				} else {
					message = SendableTextMessage.builder().message("No game is currently active\nUse /newgame to start one").build();
					telegramBot.sendMessage(event.getChat(), message);
				}
			}	
		}
		
		if (receivedMessage.equals("/roll@" + botUsername) || receivedMessage.equals("/roll") && !inPrivate) {
			if (!active.containsKey(chat)) {
				active.put(chat, false);
			}
			if (active.get(chat)) {
				if (playersList.get(chat).contains(username)) {
					if (currentTurn.get(chat).get(username)) {
						if (rolling.get(chat)) {
							dice1.put(chat, new Random().nextInt(6) + 1);
							dice2.put(chat, new Random().nextInt(6) + 1);
							if ((dice1.get(chat) + dice2.get(chat)) == 8 || (dice1.get(chat) + dice2.get(chat)) == 11) {
								result.get(chat).append("You rolled an " + (dice1.get(chat) + dice2.get(chat)) + "\n\n");
							} else {
								result.get(chat).append("You rolled a " + (dice1.get(chat) + dice2.get(chat)) + "\n\n");
							}
							rolling.put(chat, false);
							int position = playersPosition.get(chat).get(username);
							for (int i = 0; i < (dice1.get(chat) + dice2.get(chat)); i++) {
								if (position == 39) {
									position = 0;
									passedGo.put(chat, true);
									playersPosition.get(chat).put(username, position);
								} else {
									playersPosition.get(chat).put(username, ++position);
								}
							}
							int newPosition = playersPosition.get(chat).get(username);
							if (!passedGo.containsKey(chat)) {
								passedGo.put(chat, false);
							}
							move(newPosition, username, chat);
							message = SendableTextMessage.builder().message(result.get(chat).toString()).build();
							telegramBot.sendMessage(event.getChat(), message);
							result.put(chat, new StringBuilder());
						} else {
							message = SendableTextMessage.builder().message("You already rolled").build();
							telegramBot.sendMessage(event.getChat(), message);
						}
					}
				}
			}
		}
		
		if (receivedMessage.equals("/buy@" + botUsername) || receivedMessage.equals("/buy") && !inPrivate) {
			if (!active.containsKey(chat)) {
				active.put(chat, false);
			}
			if (active.get(chat)) {
				if (playersList.get(chat).contains(username)) {
					if (currentTurn.get(chat).get(username)) {
						if (rolling.get(chat)) {
							message = SendableTextMessage.builder().message("Please use /roll").build();
							telegramBot.sendMessage(event.getChat(), message);
						} else {
							int position = playersPosition.get(chat).get(username);
							if (ownedProperties.get(chat).contains(position)) {
								if (playersProperties.get(chat).get(username).contains(position)) {
									message = SendableTextMessage.builder().message("You already own this property").build();
									telegramBot.sendMessage(event.getChat(), message);
								}
							} else {
								if (playersCash.get(chat).get(username) >= propertyInitialValue.get(position)) {
									ownedProperties.get(chat).add(position);
									playersProperties.get(chat).get(username).add(position);
									playersCash.get(chat).put(username, playersCash.get(chat).get(username) - propertyInitialValue.get(position));
									String pay = "";
									if (isProperty.contains(position)) {
										pay = "\nPlayers will pay you $" + rent.get(position).get(houses.get(chat).get(position)) + " when they land on this property\nBuy a /house for $" + housePrices.get(position) + " each to increase the rent";
									} else if (isRailroad.contains(position)) {
										pay = "\nPlayers will pay you more money for each railroad you own when they land on this property"; 
									} else if (isUtility.contains(position)) {
										pay = "\nPlayers will pay you:\n4 times what they roll if you own one utility\n10 times what they roll if you own two utilities"; 
									}
									message = SendableTextMessage.builder().message("You purchased " + propertyPositions.get(position) + " for $" + propertyInitialValue.get(position) + pay).build();
									telegramBot.sendMessage(event.getChat(), message);
								} else {
									message = SendableTextMessage.builder().message("Not enough money to purchase this property").build();
									telegramBot.sendMessage(event.getChat(), message);
								}
							}
						}
					}
				}
			}
		}
		
		if (receivedMessage.equals("/house@" + botUsername) || receivedMessage.equals("/house") && !inPrivate) {
			if (!active.containsKey(chat)) {
				active.put(chat, false);
			}
			if (active.get(chat)) {
				if (playersList.get(chat).contains(username)) {
					if (currentTurn.get(chat).get(username)) {
						if (rolling.get(chat)) {
							message = SendableTextMessage.builder().message("Please use /roll").build();
							telegramBot.sendMessage(event.getChat(), message);
						} else {
							int position = playersPosition.get(chat).get(username);
							if (isProperty.contains(position)) {
								if (playersProperties.get(chat).get(username).contains(position)) {
									if (mortgaged.get(chat).contains(position)) {
										message = SendableTextMessage.builder().message("This property is currently mortaged\n/unmortgage to be able to buy houses").build();
										telegramBot.sendMessage(event.getChat(), message);
									} else {
										if (availableHouses.get(chat) == 0) {
											message = SendableTextMessage.builder().message("There are no more houses available").build();
											telegramBot.sendMessage(event.getChat(), message);
										} else {
											int currentHouses = houses.get(chat).get(position);
											if (currentHouses == 5) {
												message = SendableTextMessage.builder().message("This property already has a hotel\nYou cannot buy any more houses for this property").build();
												telegramBot.sendMessage(event.getChat(), message);
											} else if (currentHouses == 4) {
												message = SendableTextMessage.builder().message("This property already has 4 houses\nUse /hotel to buy a hotel").build();
												telegramBot.sendMessage(event.getChat(), message);
											} else if (currentHouses < 4) {
												if (playersCash.get(chat).get(username) >= housePrices.get(position)) {
													playersCash.get(chat).put(username, playersCash.get(chat).get(username) - housePrices.get(position));
													houses.get(chat).put(position, currentHouses + 1);
													availableHouses.put(chat, availableHouses.get(chat) - 1);
													message = SendableTextMessage.builder().message("You bought a house for\n" + propertyPositions.get(position) + "\nPlayers will now pay you $" + rent.get(position).get(houses.get(chat).get(position)) + " when they land on this property").build();
													telegramBot.sendMessage(event.getChat(), message);
												} else {
													message = SendableTextMessage.builder().message("Not enough money to buy a house").build();
													telegramBot.sendMessage(event.getChat(), message);
												}
											}
										}
									}
								} else {
									message = SendableTextMessage.builder().message("This property is not owned\n/buy this property for $" + propertyInitialValue.get(position)).build();
									telegramBot.sendMessage(event.getChat(), message);
								}
							} else {
								message = SendableTextMessage.builder().message("You cannot buy a house here").build();
								telegramBot.sendMessage(event.getChat(), message);
							}
						}
					}
				}
			}
		}
		
		if (receivedMessage.equals("/hotel@" + botUsername) || receivedMessage.equals("/hotel") && !inPrivate) {
			if (!active.containsKey(chat)) {
				active.put(chat, false);
			}
			if (active.get(chat)) {
				if (playersList.get(chat).contains(username)) {
					if (currentTurn.get(chat).get(username)) {
						if (rolling.get(chat)) {
							message = SendableTextMessage.builder().message("Please use /roll").build();
							telegramBot.sendMessage(event.getChat(), message);
						} else {
							int position = playersPosition.get(chat).get(username);
							if (isProperty.contains(position)) {
								if (playersProperties.get(chat).get(username).contains(position)) {
									if (mortgaged.get(chat).contains(position)) {
										message = SendableTextMessage.builder().message("This property is currently mortaged\n/unmortgage to be able to buy a hotel").build();
										telegramBot.sendMessage(event.getChat(), message);
									} else {
										if (availableHotels.get(chat) == 0) {
											message = SendableTextMessage.builder().message("There are no more hotels available").build();
											telegramBot.sendMessage(event.getChat(), message);
										} else {
											int currentHouses = houses.get(chat).get(position);
											if (currentHouses == 5) {
												message = SendableTextMessage.builder().message("This property already has a hotel\nYou cannot buy any more houses for this property").build();
												telegramBot.sendMessage(event.getChat(), message);
											} else if (currentHouses == 4) {
												if (playersCash.get(chat).get(username) >= housePrices.get(position)) {
													playersCash.get(chat).put(username, playersCash.get(chat).get(username) - housePrices.get(position));
													houses.get(chat).put(position, currentHouses + 1);
													availableHotels.put(chat, availableHotels.get(chat) - 1);
													message = SendableTextMessage.builder().message("You bought a hotel for\n" + propertyPositions.get(position) + "\nPlayers will now pay you $" + rent.get(position).get(houses.get(chat).get(position)) + " when they land on this property").build();
													telegramBot.sendMessage(event.getChat(), message);
												} else {
													message = SendableTextMessage.builder().message("Not enough money to buy a hotel").build();
													telegramBot.sendMessage(event.getChat(), message);
												}
											} else if (currentHouses < 4) {
												message = SendableTextMessage.builder().message("This property only has " + currentHouses + "\nYou need to have 4 houses in order to buy a hotel").build();
												telegramBot.sendMessage(event.getChat(), message);
											}
										}
									}
								} else {
									message = SendableTextMessage.builder().message("This property is not owned\n/buy this property for $" + propertyInitialValue.get(position)).build();
									telegramBot.sendMessage(event.getChat(), message);
								}
							} else {
								message = SendableTextMessage.builder().message("You cannot buy a hotel here").build();
								telegramBot.sendMessage(event.getChat(), message);
							}
						}
					}
				}
			}
		}
		
		if (receivedMessage.equals("/unmortgage@" + botUsername) || receivedMessage.equals("/unmortgage") && !inPrivate) {
			if (!active.get(chat)) {
				active.put(chat, false);
			}
			if (active.get(chat)) {
				if (playersList.get(chat).contains(username)) {
					if (currentTurn.get(chat).get(username)) {
						if (rolling.get(chat)) {
							message = SendableTextMessage.builder().message("Please use /roll").build();
							telegramBot.sendMessage(event.getChat(), message);
						} else {
							String unmortgagedProperties = "";
							int position = playersPosition.get(chat).get(username);
							if (playersProperties.get(chat).get(username).contains(position)) {
								if (mortgaged.get(chat).contains(position)) {
									int cost = (int)((propertyInitialValue.get(position) / 2) * 1.1);
									if (playersCash.get(chat).get(username) >= cost) {
										playersCash.get(chat).put(username, playersCash.get(chat).get(username) - cost);
										ListIterator<Integer> iterator = mortgaged.get(chat).listIterator();
										while (iterator.hasNext()) {
											int i = iterator.next();
											if (i == position) {
												unmortgagedProperties += "\n" + propertyPositions.get(i);
												iterator.remove();
												break;
											}
										}
									}
								}
							}
							ArrayList<Integer> temp = mortgaged.get(chat);
							Collections.sort(temp);
							ListIterator<Integer> it = temp.listIterator();
							while (it.hasNext()) {
								int i = it.next();
								if (playersProperties.get(chat).get(username).contains(i)) {
									int cost = (int)((propertyInitialValue.get(i) / 2) * 1.1);
									if (playersCash.get(chat).get(username) >= cost) {
										playersCash.get(chat).put(username, playersCash.get(chat).get(username) - cost);
										unmortgagedProperties += "\n" + propertyPositions.get(i);
										it.remove();
									}
								}
							}
							if (unmortgagedProperties.equals("")) {
								message = SendableTextMessage.builder().message("Could not unmortgage any properties").build();
								telegramBot.sendMessage(event.getChat(), message);
							} else {
								message = SendableTextMessage.builder().message("Unmortgaged:" + unmortgagedProperties).build();
								telegramBot.sendMessage(event.getChat(), message);
							}
						}
					}
				}
			}
		}
		
		if (receivedMessage.equals("/myassets@" + botUsername) || receivedMessage.equals("/myassets") && !inPrivate) {
			if (!active.get(chat)) {
				active.put(chat, false);
			}
			if (active.get(chat)) {
				if (playersList.get(chat).contains(username)) {
					String assets = "Cash: $" + playersCash.get(chat).get(username) + "\n\n";
					assets += "Property:\n";
					for (int i : playersProperties.get(chat).get(username)) {
						if (mortgaged.get(chat).contains(i)) {
							assets += propertyPositions.get(i) + " (mortgaged) -\n";
						} else {
							assets += propertyPositions.get(i) + " -\n";
						}
						if (isProperty.contains(i)) {
							if (houses.get(chat).get(i) == 5) {
								assets += "Houses: 0, Hotels: 1, Worth: $" + rent.get(i).get(5);
							} else {
								assets += "Houses: " + houses.get(chat).get(i) + ", Hotels: 0, Worth: $" + rent.get(i).get(houses.get(chat).get(i));
							}
						} else {
							assets += "Worth: $" + propertyInitialValue.get(i); 
						}
						assets += "\n";
					}
					assets += "\nGet Out of Jail Free cards: " + getOutOfJailFree.get(chat).get(username);
					assets += "\nFree Parking: " + (freeParking.get(chat).get(username) ? "1" : "0");
					message = SendableTextMessage.builder().message(assets).build();
					telegramBot.sendMessage(event.getChat(), message);
				}
			}
		}
		
		if (receivedMessage.equals("/endturn@" + botUsername) || receivedMessage.equals("/endturn") && !inPrivate) {
			if (!active.containsKey(chat)) {
				active.put(chat, false);
			}
			if (active.get(chat)) {
				if (playersList.get(chat).contains(username)) {
					if (currentTurn.get(chat).get(username)) {
						if (!rolling.get(chat)) {
							currentTurn.get(chat).put(username, false);
						}
					}
				}
			}
		}
		
		if (receivedMessage.equals("/leavegame@" + botUsername) || receivedMessage.equals("/leavegame") && !inPrivate) {
			if (!joining.containsKey(chat)) {
				joining.put(chat, false);
			}
			if (!active.containsKey(chat)) {
				active.put(chat, false);
			}
			if (active.get(chat)) {
				if (playersList.get(chat).contains(username)) {
					message = SendableTextMessage.builder().message(username + " has left the game").build();
					telegramBot.sendMessage(event.getChat(), message);
					currentTurn.get(chat).put(username, false);
					if (playersListIterator.get(chat).hasNext()) {
						currentTurn.get(chat).put(playersListIterator.get(chat).next(), true);
					} else {
						playersListIterator.put(chat, playersList.get(chat).listIterator());
						currentTurn.get(chat).put(playersListIterator.get(chat).next(), true);
					}
					deletePlayer(chat, username);
				}
			} else if (joining.get(chat)) {
				if (playersList.get(chat).contains(username)) {
					playersList.get(chat).remove(username);
					message = SendableTextMessage.builder().message(username + " has left the game").build();
					telegramBot.sendMessage(event.getChat(), message);
				}
			}
		}
		
		if (receivedMessage.equals("/turn@" + botUsername) || receivedMessage.equals("/turn") && !inPrivate) {
			if (!active.containsKey(chat)) {
				active.put(chat, false);
			}
			if (active.get(chat)) {
					String c = "Turn: " + getCurrentTurn(chat);
					message = SendableTextMessage.builder().message(c).build();
					telegramBot.sendMessage(event.getChat(), message);
			}
		}
		
		if (receivedMessage.equals("/list@" + botUsername) || receivedMessage.equals("/list") && !inPrivate) {
			if (!active.containsKey(chat)) {
				active.put(chat, false);
			}
			if (active.get(chat)) {
					String c = "\nPlayers:\n";
					for (String i : playersList.get(chat)) {
						c += i + "\n";
					}
					message = SendableTextMessage.builder().message(c).build();
					telegramBot.sendMessage(event.getChat(), message);
			}
			
		}
		
		if (receivedMessage.equals("/monopoly@" + botUsername) || receivedMessage.equals("/monopoly")) {
			String monopoly = "Monopoly commands:\n\n/roll - roll the dice\n/buy - buy the house you landed on\n/house - buy a house for the property you are on\n/hotel - buy a hotel for the property you are on\n/unmortgage - unmortgage your properties\n/myassets - check your money and properties\nendturn - end your turn\n\n/newgame - start a new game of monopoly\n/joingame - join a game\n/leavegame - leave the game\n\n/board - show a picture of the board for reference\n/propertycards - send link for the property cards";
			message = SendableTextMessage.builder().message(monopoly).build();
			telegramBot.sendMessage(event.getChat(), message);
		}
		
		if (receivedMessage.equals("/board@" + botUsername) || receivedMessage.equals("/board")) {
			try {
				InputFile inputFile = new InputFile(new URL("https://images-na.ssl-images-amazon.com/images/I/81oC5pYhh2L._SL1500_.jpg"));
				SendablePhotoMessage photo = SendablePhotoMessage.builder().photo(inputFile).build();
				telegramBot.sendMessage(event.getChat(), photo);
			} catch (MalformedURLException e) {
				e.getMessage();
			}
		}
		
		if (receivedMessage.equals("/propertycards@" + botUsername) || receivedMessage.equals("/propertycards")) {
			message = SendableTextMessage.builder().message("<a href=\"http://researchmaniacs.com/Games/Monopoly/Properties.html\">Monopoly property cards</a>").parseMode(ParseMode.HTML).build();
			telegramBot.sendMessage(event.getChat(), message);
		}
	}
	
	public void move(int position, String username, String chat) {
		if (isGo.contains(position)) {
			result.get(chat).append("You landed on GO and collected $200");
			playersCash.get(chat).put(username, playersCash.get(chat).get(username) + 200);
			passedGo.put(chat, false);
			currentTurn.get(chat).put(username, false);
		} else if (isProperty.contains(position)) {
			if (passedGo.get(chat)) {
				result.get(chat).append("You passed GO and collected $200\n\n");
				playersCash.get(chat).put(username, playersCash.get(chat).get(username) + 200);
				passedGo.put(chat, false);
			}
			if (mortgaged.get(chat).contains(position)) {
				if (playersProperties.get(chat).get(username).contains(position)) {
					result.get(chat).append("You landed on your mortgaged property " + propertyPositions.get(position) + "\n/unmortgage to be able to buy houses and collect rent");
				} else {
					String user = "";
					for (String s : playersProperties.get(chat).keySet()) {
						if (playersProperties.get(chat).get(s).contains(position)) {
							user = s;
							break;
						}
					}
					result.get(chat).append("You landed on " + user + "'s mortgaged property " + propertyPositions.get(position) + " and did not have to pay rent");
					currentTurn.get(chat).put(username, false);
				}
			} else {
				if (ownedProperties.get(chat).contains(position)) {
					if (playersProperties.get(chat).get(username).contains(position)) {
						result.get(chat).append("You landed on your own property " + propertyPositions.get(position) + "\nBuy a /house for $" + housePrices.get(position));
					} else {
						String user = "";
						for (String s : playersProperties.get(chat).keySet()) {
							if (playersProperties.get(chat).get(s).contains(position)) {
								user = s;
								break;
							}
						}
						int rentAmount = rent.get(position).get(houses.get(chat).get(position));
						if (freeParking.get(chat).get(username)) {
							result.get(chat).append("You landed on " + user + "'s " + propertyPositions.get(position) + " and used your free parking\nYou did not have to pay $" + rentAmount);
							freeParking.get(chat).put(username, false);
						} else {
							if (playersCash.get(chat).get(username) >= rentAmount) {			
								result.get(chat).append("You landed on " + user + "'s " + propertyPositions.get(position) + " and paid them $" + rentAmount);
								playersCash.get(chat).put(username, playersCash.get(chat).get(username) - rentAmount);
								playersCash.get(chat).put(user, playersCash.get(chat).get(user) + rentAmount);
							} else {
								ArrayList<Integer> temp = playersProperties.get(chat).get(username);
								Collections.sort(temp);
								Collections.reverse(temp);
								ListIterator<Integer> it = temp.listIterator();
								String mortgagedProperties = "";
								while (it.hasNext()) {
									int i = it.next();
									if (!mortgaged.get(chat).contains(i)) {
										mortgaged.get(chat).add(i);												
										playersCash.get(chat).put(username, playersCash.get(chat).get(username) + (propertyInitialValue.get(i) / 2));
										mortgagedProperties += "\n" + propertyPositions.get(i);
									}
									if (playersCash.get(chat).get(username) >= rentAmount) {
										break;
									}
								}
								if (playersCash.get(chat).get(username) >= rentAmount) {
									result.get(chat).append("You landed on " + user + "'s " + propertyPositions.get(position) + " and paid them $" + rentAmount + "\nYou had to mortgage:" + mortgagedProperties + "\n/unmortgage your properties when you have enough money");
									playersCash.get(chat).put(username, playersCash.get(chat).get(username) - rentAmount);
									playersCash.get(chat).put(user, playersCash.get(chat).get(user) + rentAmount);
								} else {
									result.get(chat).append("You landed on " + user + "'s " + propertyPositions.get(position) + " and do not have enough money or properties to pay rent\nYou went bankrupt and lost everything to the bank");
									playersCash.get(chat).put(user, playersCash.get(chat).get(user) + playersCash.get(chat).get(username));
									bankrupt(chat, username);
								}
							}
						}
						currentTurn.get(chat).put(username, false);
					}
				} else {
					result.get(chat).append("You landed on " + propertyPositions.get(position) + "\nThis property is not owned and you may choose to /buy it for $" + propertyInitialValue.get(position));
				}
			}
		} else if (isRailroad.contains(position)) {
			if (passedGo.get(chat)) {
				result.get(chat).append("You passed GO and collected $200\n\n");
				playersCash.get(chat).put(username, playersCash.get(chat).get(username) + 200);
				passedGo.put(chat, false);
			}
			if (mortgaged.get(chat).contains(position)) {
				if (playersProperties.get(chat).get(username).contains(position)) {
					result.get(chat).append("You landed on your mortgaged property " + propertyPositions.get(position) + "\n/unmortgage to be able to collect rent");
				} else {
					String user = "";
					for (String s : playersProperties.get(chat).keySet()) {
						if (playersProperties.get(chat).get(s).contains(position)) {
							user = s;
							break;
						}
					}
					result.get(chat).append("You landed on " + user + "'s mortgaged property " + propertyPositions.get(position) + " and did not have to pay rent");
					currentTurn.get(chat).put(username, false);
				}
			} else {
				if (ownedProperties.get(chat).contains(position)) {
					if (playersProperties.get(chat).get(username).contains(position)) {
						result.get(chat).append("You landed on your own property " + propertyPositions.get(position));
					} else {
						String user = "";
						for (String s : playersProperties.get(chat).keySet()) {
							if (playersProperties.get(chat).get(s).contains(position)) {
								user = s;
								break;
							}
						}
						int owned = 0;
						for (int i : isRailroad) {
							if (playersProperties.get(chat).get(user).contains(i)) {
								owned++;
							}
						}
						int rentAmount = 0;
						switch (owned) {
						case 1:
							rentAmount = 25;
							break;
						case 2:
							rentAmount = 50;
							break;
						case 3:
							rentAmount = 100;
							break;
						case 4:
							rentAmount = 200;
							break;
						}
						if (freeParking.get(chat).get(username)) {
							result.get(chat).append("You landed on " + user + "'s " + propertyPositions.get(position) + " and used your free parking\nYou did not have to pay $" + rentAmount);
							freeParking.get(chat).put(username, false);
						} else {
							if (playersCash.get(chat).get(username) >= rentAmount) {			
								result.get(chat).append("You landed on " + user + "'s " + propertyPositions.get(position) + " and paid them $" + rentAmount);
								playersCash.get(chat).put(username, playersCash.get(chat).get(username) - rentAmount);
								playersCash.get(chat).put(user, playersCash.get(chat).get(user) + rentAmount);
							} else {
								ArrayList<Integer> temp = playersProperties.get(chat).get(username);
								Collections.sort(temp);
								Collections.reverse(temp);
								ListIterator<Integer> it = temp.listIterator();
								String mortgagedProperties = "";
								while (it.hasNext()) {
									int i = it.next();
									if (!mortgaged.get(chat).contains(i)) {
										mortgaged.get(chat).add(i);												
										playersCash.get(chat).put(username, playersCash.get(chat).get(username) + (propertyInitialValue.get(i) / 2));
										mortgagedProperties += "\n" + propertyPositions.get(i);
									}
									if (playersCash.get(chat).get(username) >= rentAmount) {
										break;
									}
								}
								if (playersCash.get(chat).get(username) >= rentAmount) {
									result.get(chat).append("You landed on " + user + "'s " + propertyPositions.get(position) + " and paid them $" + rentAmount + "\nYou had to mortgage:" + mortgagedProperties + "\n/unmortgage your properties when you have enough money");
									playersCash.get(chat).put(username, playersCash.get(chat).get(username) - rentAmount);
									playersCash.get(chat).put(user, playersCash.get(chat).get(user) + rentAmount);
								} else {
									result.get(chat).append("You landed on " + user + "'s " + propertyPositions.get(position) + " and do not have enough money or properties to pay rent\nYou went bankrupt and lost everything to the bank");
									playersCash.get(chat).put(user, playersCash.get(chat).get(user) + playersCash.get(chat).get(username));
									bankrupt(chat, username);
								}
							}
						}
						currentTurn.get(chat).put(username, false);
					}
				} else {
					result.get(chat).append("You landed on " + propertyPositions.get(position) + "\nThis property is not owned and you may choose to /buy it for $" + propertyInitialValue.get(position));
				}
			}
		} else if (isUtility.contains(position)) {
			if (passedGo.get(chat)) {
				result.get(chat).append("You passed GO and collected $200\n\n");
				playersCash.get(chat).put(username, playersCash.get(chat).get(username) + 200);
				passedGo.put(chat, false);
			}
			if (mortgaged.get(chat).contains(position)) {
				if (playersProperties.get(chat).get(username).contains(position)) {
					result.get(chat).append("You landed on your mortgaged property " + propertyPositions.get(position) + "\n/unmortgage to be able to collect rent");
				} else {
					String user = "";
					for (String s : playersProperties.get(chat).keySet()) {
						if (playersProperties.get(chat).get(s).contains(position)) {
							user = s;
							break;
						}
					}
					result.get(chat).append("You landed on " + user + "'s mortgaged property " + propertyPositions.get(position) + " and did not have to pay rent");
					currentTurn.get(chat).put(username, false);
				}
			} else {
				if (ownedProperties.get(chat).contains(position)) {
					if (playersProperties.get(chat).get(username).contains(position)) {
						result.get(chat).append("You landed on your own property " + propertyPositions.get(position));
					} else {
						String user = "";
						for (String s : playersProperties.get(chat).keySet()) {
							if (playersProperties.get(chat).get(s).contains(position)) {
								user = s;
								break;
							}
						}
						int owned = 0;
						for (int i : isUtility) {
							if (playersProperties.get(chat).get(user).contains(i)) {
								owned++;
							}
						}
						int rentAmount = 0;
						switch (owned) {
						case 1:
							rentAmount = (dice1.get(chat) + dice2.get(chat)) * 4;
							break;
						case 2:
							rentAmount = (dice1.get(chat) + dice2.get(chat)) * 10;
							break;
						}
						if (freeParking.get(chat).get(username)) {
							result.get(chat).append("You landed on " + user + "'s " + propertyPositions.get(position) + " and used your free parking\nYou did not have to pay $" + rentAmount);
							freeParking.get(chat).put(username, false);
						} else {
							if (playersCash.get(chat).get(username) >= rentAmount) {			
								result.get(chat).append("You landed on " + user + "'s " + propertyPositions.get(position) + " and paid them $" + rentAmount);
								playersCash.get(chat).put(username, playersCash.get(chat).get(username) - rentAmount);
								playersCash.get(chat).put(user, playersCash.get(chat).get(user) + rentAmount);
							} else {
								ArrayList<Integer> temp = playersProperties.get(chat).get(username);
								Collections.sort(temp);
								Collections.reverse(temp);
								ListIterator<Integer> it = temp.listIterator();
								String mortgagedProperties = "";
								while (it.hasNext()) {
									int i = it.next();
									if (!mortgaged.get(chat).contains(i)) {
										mortgaged.get(chat).add(i);												
										playersCash.get(chat).put(username, playersCash.get(chat).get(username) + (propertyInitialValue.get(i) / 2));
										mortgagedProperties += "\n" + propertyPositions.get(i);
									}
									if (playersCash.get(chat).get(username) >= rentAmount) {
										break;
									}
								}
								if (playersCash.get(chat).get(username) >= rentAmount) {
									result.get(chat).append("You landed on " + user + "'s " + propertyPositions.get(position) + " and paid them $" + rentAmount + "\nYou had to mortgage:" + mortgagedProperties + "\n/unmortgage your properties when you have enough money");
									playersCash.get(chat).put(username, playersCash.get(chat).get(username) - rentAmount);
									playersCash.get(chat).put(user, playersCash.get(chat).get(user) + rentAmount);
								} else {
									result.get(chat).append("You landed on " + user + "'s " + propertyPositions.get(position) + " and do not have enough money or properties to pay rent\nYou went bankrupt and lost everything to the bank");
									playersCash.get(chat).put(user, playersCash.get(chat).get(user) + playersCash.get(chat).get(username));
									bankrupt(chat, username);
								}
							}
						}
						currentTurn.get(chat).put(username, false);
					}
				} else {
					result.get(chat).append("You landed on " + propertyPositions.get(position) + "\nThis property is not owned and you may choose to /buy it for $" + propertyInitialValue.get(position));
				}
			}
		} else if (isChance.contains(position)) {	
			if (passedGo.get(chat)) {
				result.get(chat).append("You passed GO and collected $200\n\n");
				playersCash.get(chat).put(username, playersCash.get(chat).get(username) + 200);
				passedGo.put(chat, false);
			}
			result.get(chat).append("You landed on chance:\n");
			if (chanceList.isEmpty()) {
				for (int i = 1; i <= 16; i++) {
					chanceList.get(chat).add(i);
				}
				Collections.shuffle(chanceList.get(chat));
			}
			int card = chanceList.get(chat).get(0);
			if (card == 1) {
				result.get(chat).append(chanceCards.get(card) + "\n\n");
				int newPosition = position;
				while (newPosition != 0) {
					if (newPosition == 39) {
						newPosition = 0;
						passedGo.put(chat, true);
						playersPosition.get(chat).put(username, newPosition);
					} else {
						playersPosition.get(chat).put(username, ++newPosition);
					}
				}
				move(newPosition, username, chat);
			} else if (card == 2) {
				result.get(chat).append(chanceCards.get(card) + "\n\n");
				int newPosition = position;
				while (newPosition != 24) {
					if (newPosition == 39) {
						newPosition = 0;
						passedGo.put(chat, true);
						playersPosition.get(chat).put(username, newPosition);
					} else {
						playersPosition.get(chat).put(username, ++newPosition);
					}
				}
				move(newPosition, username, chat);
			} else if (card == 3) {
				result.get(chat).append(chanceCards.get(card) + "\n\n");
				int newPosition = position;
				while (newPosition != 11) {
					if (newPosition == 39) {
						newPosition = 0;
						passedGo.put(chat, true);
						playersPosition.get(chat).put(username, newPosition);
					} else {
						playersPosition.get(chat).put(username, ++newPosition);
					}
				}
				move(newPosition, username, chat);
			} else if (card == 4) {
				result.get(chat).append(chanceCards.get(card) + "\n\n");
				int newPosition = position;
				while (newPosition != 12 && newPosition != 28) {
					if (newPosition == 39) {
						newPosition = 0;
						passedGo.put(chat, true);
						playersPosition.get(chat).put(username, newPosition);
					} else {
						playersPosition.get(chat).put(username, ++newPosition);
					}
				}
				move(newPosition, username, chat);
			} else if (card == 5) {
				result.get(chat).append(chanceCards.get(card) + "\n\n");
				int newPosition = position;
				while (newPosition != 5 && newPosition != 15 && newPosition != 25 && newPosition != 35) {
					if (newPosition == 39) {
						newPosition = 0;
						passedGo.put(chat, true);
						playersPosition.get(chat).put(username, newPosition);
					} else {
						playersPosition.get(chat).put(username, ++newPosition);
					}
				}
				move(newPosition, username, chat);
			} else if (card == 6) {
				result.get(chat).append(chanceCards.get(card));
				playersCash.get(chat).put(username, playersCash.get(chat).get(username) + 50);
				currentTurn.get(chat).put(username, false);
			} else if (card == 7) {
				result.get(chat).append(chanceCards.get(card));
				getOutOfJailFree.get(chat).put(username, getOutOfJailFree.get(chat).get(username) + 1);
				currentTurn.get(chat).put(username, false);
			} else if (card == 8) {
				result.get(chat).append(chanceCards.get(card) + "\n\n");
				int newPosition = position;
				for (int i = 0; i < 3; i++) {
					if (newPosition == 0) {
						newPosition = 39;
						playersPosition.get(chat).put(username, newPosition);
					} else {
						playersPosition.get(chat).put(username, --newPosition);
					}
				}
				move(newPosition, username, chat);
			} else if (card == 9) {
				result.get(chat).append(chanceCards.get(card) + "\n\n");
				playersPosition.get(chat).put(username, 10);
				if (getOutOfJailFree.get(chat).get(username) > 0) {
					getOutOfJailFree.get(chat).put(username, getOutOfJailFree.get(chat).get(username) - 1);
					result.get(chat).append("You used a get out of jail free card to leave jail");
				} else {
					if (playersCash.get(chat).get(username) >= 50) {
						playersCash.get(chat).put(username, playersCash.get(chat).get(username) - 50);
						result.get(chat).append("You paid $50 to get out of jail");
					} else {
						ArrayList<Integer> temp = playersProperties.get(chat).get(username);
						Collections.sort(temp);
						Collections.reverse(temp);
						ListIterator<Integer> it = temp.listIterator();
						String mortgagedProperties = "";
						while (it.hasNext()) {
							int i = it.next();
							if (!mortgaged.get(chat).contains(i)) {
								mortgaged.get(chat).add(i);												
								playersCash.get(chat).put(username, playersCash.get(chat).get(username) + (propertyInitialValue.get(i) / 2));
								mortgagedProperties += "\n" + propertyPositions.get(i);
							}
							if (playersCash.get(chat).get(username) >= 50) {
								break;
							}
						}
						if (playersCash.get(chat).get(username) >= 50) {
							result.get(chat).append("You paid $50 to get out of jail\nYou had to mortgage:" + mortgagedProperties + "\n/unmortgage your properties when you have enough money");
							playersCash.get(chat).put(username, playersCash.get(chat).get(username) - 50);
						} else {
							result.get(chat).append("You do not have enough money or properties to pay $50 and get out of jail\nYou went bankrupt and lost everything to the bank");
							bankrupt(chat, username);
						}
					}
				}
				currentTurn.get(chat).put(username, false);
			} else if (card == 10) {
				result.get(chat).append(chanceCards.get(card) + "\n\n");
				int cost = 0;
				for (int i : playersProperties.get(chat).get(username)) {
					if (isProperty.contains(i)) {
						if (houses.get(chat).get(i) == 5) {
							cost += 100;
						} else {
							cost += houses.get(chat).get(i) * 25;
						}
					}
				}
				if (playersCash.get(chat).get(username) >= cost) {
					playersCash.get(chat).put(username, playersCash.get(chat).get(username) - cost);
					result.get(chat).append("You paid $" + cost + " for the repairs");
				} else {
					ArrayList<Integer> temp = playersProperties.get(chat).get(username);
					Collections.sort(temp);
					Collections.reverse(temp);
					ListIterator<Integer> it = temp.listIterator();
					String mortgagedProperties = "";
					while (it.hasNext()) {
						int i = it.next();
						if (!mortgaged.get(chat).contains(i)) {
							mortgaged.get(chat).add(i);												
							playersCash.get(chat).put(username, playersCash.get(chat).get(username) + (propertyInitialValue.get(i) / 2));
							mortgagedProperties += "\n" + propertyPositions.get(i);
						}
						if (playersCash.get(chat).get(username) >= cost) {
							break;
						}
					}
					if (playersCash.get(chat).get(username) >= cost) {
						result.get(chat).append("You paid $" + cost + " for the repairs\nYou had to mortgage:" + mortgagedProperties + "\n/unmortgage your properties when you have enough money");
						playersCash.get(chat).put(username, playersCash.get(chat).get(username) - cost);
					} else {
						result.get(chat).append("You do not have enough money or properties to pay for the repairs\nYou went bankrupt and lost everything to the bank");
						bankrupt(chat, username);
					}
				}
				currentTurn.get(chat).put(username, false);
			} else if (card == 11) {
				result.get(chat).append(chanceCards.get(card) + "\n\n");
				int cost = 15;
				if (playersCash.get(chat).get(username) >= cost) {
					playersCash.get(chat).put(username, playersCash.get(chat).get(username) - cost);
					result.get(chat).append("You paid $" + cost + " in taxes");
				} else {
					ArrayList<Integer> temp = playersProperties.get(chat).get(username);
					Collections.sort(temp);
					Collections.reverse(temp);
					ListIterator<Integer> it = temp.listIterator();
					String mortgagedProperties = "";
					while (it.hasNext()) {
						int i = it.next();
						if (!mortgaged.get(chat).contains(i)) {
							mortgaged.get(chat).add(i);												
							playersCash.get(chat).put(username, playersCash.get(chat).get(username) + (propertyInitialValue.get(i) / 2));
							mortgagedProperties += "\n" + propertyPositions.get(i);
						}
						if (playersCash.get(chat).get(username) >= cost) {
							break;
						}
					}
					if (playersCash.get(chat).get(username) >= cost) {
						result.get(chat).append("You paid $" + cost + " in taxes\nYou had to mortgage:" + mortgagedProperties + "\n/unmortgage your properties when you have enough money");
						playersCash.get(chat).put(username, playersCash.get(chat).get(username) - cost);
					} else {
						result.get(chat).append("You do not have enough money or properties to pay the tax\nYou went bankrupt and lost everything to the bank");
						bankrupt(chat, username);
					}
				}
				currentTurn.get(chat).put(username, false);
			} else if (card == 12) {
				result.get(chat).append(chanceCards.get(card) + "\n\n");
				int newPosition = position;
				while (newPosition != 5) {
					if (newPosition == 39) {
						newPosition = 0;
						passedGo.put(chat, true);
						playersPosition.get(chat).put(username, newPosition);
					} else {
						playersPosition.get(chat).put(username, ++newPosition);
					}
				}
				move(newPosition, username, chat);
			} else if (card == 13) {
				result.get(chat).append(chanceCards.get(card) + "\n\n");
				int newPosition = position;
				while (newPosition != 39) {
					if (newPosition == 39) {
						newPosition = 0;
						passedGo.put(chat, true);
						playersPosition.get(chat).put(username, newPosition);
					} else {
						playersPosition.get(chat).put(username, ++newPosition);
					}
				}
				move(newPosition, username, chat);
			} else if (card == 14) {
				result.get(chat).append(chanceCards.get(card) + "\n\n");
				int cost = 50 * (playersList.get(chat).size() - 1);
				if (playersCash.get(chat).get(username) >= cost) {
					playersCash.get(chat).put(username, playersCash.get(chat).get(username) - cost);
					for (String s : playersCash.get(chat).keySet()) {
						if (!s.equals(username)) {
							playersCash.get(chat).put(s, playersCash.get(chat).get(s) + 50);
						}
					}
					result.get(chat).append("You paid out $" + cost);
				} else {
					ArrayList<Integer> temp = playersProperties.get(chat).get(username);
					Collections.sort(temp);
					Collections.reverse(temp);
					ListIterator<Integer> it = temp.listIterator();
					String mortgagedProperties = "";
					while (it.hasNext()) {
						int i = it.next();
						if (!mortgaged.get(chat).contains(i)) {
							mortgaged.get(chat).add(i);												
							playersCash.get(chat).put(username, playersCash.get(chat).get(username) + (propertyInitialValue.get(i) / 2));
							mortgagedProperties += "\n" + propertyPositions.get(i);
						}
						if (playersCash.get(chat).get(username) >= cost) {
							break;
						}
					}
					if (playersCash.get(chat).get(username) >= cost) {
						result.get(chat).append("You paid out $" + cost + "\nYou had to mortgage:" + mortgagedProperties + "\n/unmortgage your properties when you have enough money");
						playersCash.get(chat).put(username, playersCash.get(chat).get(username) - cost);
						for (String s : playersCash.get(chat).keySet()) {
							if (!s.equals(username)) {
								playersCash.get(chat).put(s, playersCash.get(chat).get(s) + 50);
							}
						}
					} else {
						result.get(chat).append("You do not have enough money or properties to pay each player $50\nYou went bankrupt and lost everything to the bank");
						bankrupt(chat, username);
					}
				}
				currentTurn.get(chat).put(username, false);
			} else if (card == 15) {
				result.get(chat).append(chanceCards.get(card));
				playersCash.get(chat).put(username, playersCash.get(chat).get(username) + 150);
				currentTurn.get(chat).put(username, false);
			} else if (card == 16) {
				result.get(chat).append(chanceCards.get(card));
				playersCash.get(chat).put(username, playersCash.get(chat).get(username) + 100);
				currentTurn.get(chat).put(username, false);
			}
			chanceList.get(chat).remove(0);
		} else if (isCommunityChest.contains(position)) {
			if (passedGo.get(chat)) {
				result.get(chat).append("You passed GO and collected $200\n\n");
				playersCash.get(chat).put(username, playersCash.get(chat).get(username) + 200);
				passedGo.put(chat, false);
			}
			result.get(chat).append("You landed on community chest:\n");
			if (communityChestList.isEmpty()) {
				for (int i = 1; i <= 16; i++) {
					communityChestList.get(chat).add(i);
				}
				Collections.shuffle(communityChestList.get(chat));
			}
			int card = communityChestList.get(chat).get(0);
			if (card == 1) {
				result.get(chat).append(communityChestCards.get(card) + "\n\n");
				int newPosition = position;
				while (newPosition != 0) {
					if (newPosition == 39) {
						newPosition = 0;
						passedGo.put(chat, true);
						playersPosition.get(chat).put(username, newPosition);
					} else {
						playersPosition.get(chat).put(username, ++newPosition);
					}
				}
				move(newPosition, username, chat);
			} else if (card == 2) {
				result.get(chat).append(communityChestCards.get(card));
				playersCash.get(chat).put(username, playersCash.get(chat).get(username) + 200);
				currentTurn.get(chat).put(username, false);
			} else if (card == 3) {
				result.get(chat).append(communityChestCards.get(card) + "\n\n");
				int cost = 50;
				if (playersCash.get(chat).get(username) >= cost) {
					playersCash.get(chat).put(username, playersCash.get(chat).get(username) - cost);
					result.get(chat).append("You paid $" + cost + " for the doctor's fees");
				} else {
					ArrayList<Integer> temp = playersProperties.get(chat).get(username);
					Collections.sort(temp);
					Collections.reverse(temp);
					ListIterator<Integer> it = temp.listIterator();
					String mortgagedProperties = "";
					while (it.hasNext()) {
						int i = it.next();
						if (!mortgaged.get(chat).contains(i)) {
							mortgaged.get(chat).add(i);												
							playersCash.get(chat).put(username, playersCash.get(chat).get(username) + (propertyInitialValue.get(i) / 2));
							mortgagedProperties += "\n" + propertyPositions.get(i);
						}
						if (playersCash.get(chat).get(username) >= cost) {
							break;
						}
					}
					if (playersCash.get(chat).get(username) >= cost) {
						result.get(chat).append("You paid $" + cost + " for the doctor's fees\nYou had to mortgage:" + mortgagedProperties + "\n/unmortgage your properties when you have enough money");
						playersCash.get(chat).put(username, playersCash.get(chat).get(username) - cost);
					} else {
						result.get(chat).append("You do not have enough money or properties to pay the doctor's fees\nYou went bankrupt and lost everything to the bank");
						bankrupt(chat, username);
					}
				}
				currentTurn.get(chat).put(username, false);
			} else if (card == 4) {
				result.get(chat).append(communityChestCards.get(card));
				playersCash.get(chat).put(username, playersCash.get(chat).get(username) + 50);
				currentTurn.get(chat).put(username, false);
			} else if (card == 5) {
				result.get(chat).append(communityChestCards.get(card));
				getOutOfJailFree.get(chat).put(username, getOutOfJailFree.get(chat).get(username) + 1);
				currentTurn.get(chat).put(username, false);
				currentTurn.get(chat).put(username, false);
			} else if (card == 6) {
				result.get(chat).append(communityChestCards.get(card) + "\n\n");
				playersPosition.get(chat).put(username, 10);
				if (getOutOfJailFree.get(chat).get(username) > 0) {
					getOutOfJailFree.get(chat).put(username, getOutOfJailFree.get(chat).get(username) - 1);
					result.get(chat).append("You used a get out of jail free card to leave jail");
				} else {
					if (playersCash.get(chat).get(username) >= 50) {
						playersCash.get(chat).put(username, playersCash.get(chat).get(username) - 50);
						result.get(chat).append("You paid $50 to get out of jail");
					} else {
						ArrayList<Integer> temp = playersProperties.get(chat).get(username);
						Collections.sort(temp);
						Collections.reverse(temp);
						ListIterator<Integer> it = temp.listIterator();
						String mortgagedProperties = "";
						while (it.hasNext()) {
							int i = it.next();
							if (!mortgaged.get(chat).contains(i)) {
								mortgaged.get(chat).add(i);												
								playersCash.get(chat).put(username, playersCash.get(chat).get(username) + (propertyInitialValue.get(i) / 2));
								mortgagedProperties += "\n" + propertyPositions.get(i);
							}
							if (playersCash.get(chat).get(username) >= 50) {
								break;
							}
						}
						if (playersCash.get(chat).get(username) >= 50) {
							result.get(chat).append("You paid $50 to get out of jail\nYou had to mortgage:" + mortgagedProperties + "\n/unmortgage your properties when you have enough money");
							playersCash.get(chat).put(username, playersCash.get(chat).get(username) - 50);
						} else {
							result.get(chat).append("You do not have enough money or properties to pay $50 and get out of jail\nYou went bankrupt and lost everything to the bank");
							bankrupt(chat, username);
						}
					}
				}
				currentTurn.get(chat).put(username, false);
			} else if (card == 7) {
				result.get(chat).append(communityChestCards.get(card));
				playersCash.get(chat).put(username, playersCash.get(chat).get(username) + 100);
				currentTurn.get(chat).put(username, false);
			} else if (card == 8) {
				result.get(chat).append(communityChestCards.get(card));
				playersCash.get(chat).put(username, playersCash.get(chat).get(username) + 20);
				currentTurn.get(chat).put(username, false);
			} else if (card == 9) {
				result.get(chat).append(communityChestCards.get(card));
				playersCash.get(chat).put(username, playersCash.get(chat).get(username) + 10);
				currentTurn.get(chat).put(username, false);
			} else if (card == 10) {
				result.get(chat).append(communityChestCards.get(card));
				playersCash.get(chat).put(username, playersCash.get(chat).get(username) + 100);
				currentTurn.get(chat).put(username, false);
			} else if (card == 11) {
				result.get(chat).append(communityChestCards.get(card) + "\n\n");
				int cost = 100;
				if (playersCash.get(chat).get(username) >= cost) {
					playersCash.get(chat).put(username, playersCash.get(chat).get(username) - cost);
					result.get(chat).append("You paid $" + cost + " for the hospital fees");
				} else {
					ArrayList<Integer> temp = playersProperties.get(chat).get(username);
					Collections.sort(temp);
					Collections.reverse(temp);
					ListIterator<Integer> it = temp.listIterator();
					String mortgagedProperties = "";
					while (it.hasNext()) {
						int i = it.next();
						if (!mortgaged.get(chat).contains(i)) {
							mortgaged.get(chat).add(i);												
							playersCash.get(chat).put(username, playersCash.get(chat).get(username) + (propertyInitialValue.get(i) / 2));
							mortgagedProperties += "\n" + propertyPositions.get(i);
						}
						if (playersCash.get(chat).get(username) >= cost) {
							break;
						}
					}
					if (playersCash.get(chat).get(username) >= cost) {
						result.get(chat).append("You paid $" + cost + " for the hospital fees\nYou had to mortgage:" + mortgagedProperties + "\n/unmortgage your properties when you have enough money");
						playersCash.get(chat).put(username, playersCash.get(chat).get(username) - cost);
					} else {
						result.get(chat).append("You do not have enough money or properties to pay the hospital fees\nYou went bankrupt and lost everything to the bank");
						bankrupt(chat, username);
					}
				}
				currentTurn.get(chat).put(username, false);
			} else if (card == 12) {
				result.get(chat).append(communityChestCards.get(card) + "\n\n");
				int cost = 150;
				if (playersCash.get(chat).get(username) >= cost) {
					playersCash.get(chat).put(username, playersCash.get(chat).get(username) - cost);
					result.get(chat).append("You paid $" + cost + " for the school fees");
				} else {
					ArrayList<Integer> temp = playersProperties.get(chat).get(username);
					Collections.sort(temp);
					Collections.reverse(temp);
					ListIterator<Integer> it = temp.listIterator();
					String mortgagedProperties = "";
					while (it.hasNext()) {
						int i = it.next();
						if (!mortgaged.get(chat).contains(i)) {
							mortgaged.get(chat).add(i);												
							playersCash.get(chat).put(username, playersCash.get(chat).get(username) + (propertyInitialValue.get(i) / 2));
							mortgagedProperties += "\n" + propertyPositions.get(i);
						}
						if (playersCash.get(chat).get(username) >= cost) {
							break;
						}
					}
					if (playersCash.get(chat).get(username) >= cost) {
						result.get(chat).append("You paid $" + cost + " for the school fees\nYou had to mortgage:" + mortgagedProperties + "\n/unmortgage your properties when you have enough money");
						playersCash.get(chat).put(username, playersCash.get(chat).get(username) - cost);
					} else {
						result.get(chat).append("You do not have enough money or properties to pay the school fees\nYou went bankrupt and lost everything to the bank");
						bankrupt(chat, username);
					}
				}
				currentTurn.get(chat).put(username, false);
			} else if (card == 13) {
				result.get(chat).append(communityChestCards.get(card));
				playersCash.get(chat).put(username, playersCash.get(chat).get(username) + 250);
				currentTurn.get(chat).put(username, false);
			} else if (card == 14) {
				result.get(chat).append(communityChestCards.get(card) + "\n\n");
				int cost = 0;
				for (int i : playersProperties.get(chat).get(username)) {
					if (isProperty.contains(i)) {
						if (houses.get(chat).get(i) == 5) {
							cost += 115;
						} else {
							cost += houses.get(chat).get(i) * 40;
						}
					}
				}
				if (playersCash.get(chat).get(username) >= cost) {
					playersCash.get(chat).put(username, playersCash.get(chat).get(username) - cost);
					result.get(chat).append("You paid $" + cost + " for the repairs");
				} else {
					ArrayList<Integer> temp = playersProperties.get(chat).get(username);
					Collections.sort(temp);
					Collections.reverse(temp);
					ListIterator<Integer> it = temp.listIterator();
					String mortgagedProperties = "";
					while (it.hasNext()) {
						int i = it.next();
						if (!mortgaged.get(chat).contains(i)) {
							mortgaged.get(chat).add(i);												
							playersCash.get(chat).put(username, playersCash.get(chat).get(username) + (propertyInitialValue.get(i) / 2));
							mortgagedProperties += "\n" + propertyPositions.get(i);
						}
						if (playersCash.get(chat).get(username) >= cost) {
							break;
						}
					}
					if (playersCash.get(chat).get(username) >= cost) {
						result.get(chat).append("You paid $" + cost + " for the repairs\nYou had to mortgage:" + mortgagedProperties + "\n/unmortgage your properties when you have enough money");
						playersCash.get(chat).put(username, playersCash.get(chat).get(username) - cost);
					} else {
						result.get(chat).append("You do not have enough money or properties to pay for the repairs\nYou went bankrupt and lost everything to the bank");
						bankrupt(chat, username);
					}
				}
				currentTurn.get(chat).put(username, false);
			} else if (card == 15) {
				result.get(chat).append(communityChestCards.get(card));
				playersCash.get(chat).put(username, playersCash.get(chat).get(username) + 10);
				currentTurn.get(chat).put(username, false);
			} else if (card == 16) {
				result.get(chat).append(communityChestCards.get(card));
				playersCash.get(chat).put(username, playersCash.get(chat).get(username) + 100);
				currentTurn.get(chat).put(username, false);
			}
			communityChestList.get(chat).remove(0);
		} else if (isTax.contains(position)) {
			if (passedGo.get(chat)) {
				result.get(chat).append("You passed GO and collected $200\n\n");
				playersCash.get(chat).put(username, playersCash.get(chat).get(username) + 200);
				passedGo.put(chat, false);
			}
			if (position == 4) {
				result.get(chat).append("You landed on income tax\n");
				int cost = 100;
				if (playersCash.get(chat).get(username) >= cost) {
					playersCash.get(chat).put(username, playersCash.get(chat).get(username) - cost);
					result.get(chat).append("You paid $" + cost + " in taxes");
				} else {
					ArrayList<Integer> temp = playersProperties.get(chat).get(username);
					Collections.sort(temp);
					Collections.reverse(temp);
					ListIterator<Integer> it = temp.listIterator();
					String mortgagedProperties = "";
					while (it.hasNext()) {
						int i = it.next();
						if (!mortgaged.get(chat).contains(i)) {
							mortgaged.get(chat).add(i);												
							playersCash.get(chat).put(username, playersCash.get(chat).get(username) + (propertyInitialValue.get(i) / 2));
							mortgagedProperties += "\n" + propertyPositions.get(i);
						}
						if (playersCash.get(chat).get(username) >= cost) {
							break;
						}
					}
					if (playersCash.get(chat).get(username) >= cost) {
						result.get(chat).append("You paid $" + cost + " in taxes\nYou had to mortgage:" + mortgagedProperties + "\n/unmortgage your properties when you have enough money");
						playersCash.get(chat).put(username, playersCash.get(chat).get(username) - cost);
					} else {
						result.get(chat).append("You do not have enough money or properties to pay the tax\nYou went bankrupt and lost everything to the bank");
						bankrupt(chat, username);
					}
				}
				currentTurn.get(chat).put(username, false);
			} else if (position == 38) {
				result.get(chat).append("You landed on luxury tax\n");
				int cost = 200;
				if (playersCash.get(chat).get(username) >= cost) {
					playersCash.get(chat).put(username, playersCash.get(chat).get(username) - cost);
					result.get(chat).append("You paid $" + cost + " in taxes");
				} else {
					ArrayList<Integer> temp = playersProperties.get(chat).get(username);
					Collections.sort(temp);
					Collections.reverse(temp);
					ListIterator<Integer> it = temp.listIterator();
					String mortgagedProperties = "";
					while (it.hasNext()) {
						int i = it.next();
						if (!mortgaged.get(chat).contains(i)) {
							mortgaged.get(chat).add(i);												
							playersCash.get(chat).put(username, playersCash.get(chat).get(username) + (propertyInitialValue.get(i) / 2));
							mortgagedProperties += "\n" + propertyPositions.get(i);
						}
						if (playersCash.get(chat).get(username) >= cost) {
							break;
						}
					}
					if (playersCash.get(chat).get(username) >= cost) {
						result.get(chat).append("You paid $" + cost + " in taxes\nYou had to mortgage:" + mortgagedProperties + "\n/unmortgage your properties when you have enough money");
						playersCash.get(chat).put(username, playersCash.get(chat).get(username) - cost);
					} else {
						result.get(chat).append("You do not have enough money or properties to pay the tax\nYou went bankrupt and lost everything to the bank");
						bankrupt(chat, username);
					}
				}
				currentTurn.get(chat).put(username, false);
			}
		} else if (isVisitingJail.contains(position)) {
			if (passedGo.get(chat)) {
				result.get(chat).append("You passed GO and collected $200\n\n");
				playersCash.get(chat).put(username, playersCash.get(chat).get(username) + 200);
				passedGo.put(chat, false);
			}
			result.get(chat).append("You are just visiting jail");
			currentTurn.get(chat).put(username, false);
		} else if (isGoToJail.contains(position)) {
			result.get(chat).append("You landed on the go to jail square and went to jail\n\n");
			playersPosition.get(chat).put(username, 10);
			if (getOutOfJailFree.get(chat).get(username) > 0) {
				getOutOfJailFree.get(chat).put(username, getOutOfJailFree.get(chat).get(username) - 1);
				result.get(chat).append("You used a get out of jail free card to leave jail");
			} else {
				if (playersCash.get(chat).get(username) >= 50) {
					playersCash.get(chat).put(username, playersCash.get(chat).get(username) - 50);
					result.get(chat).append("You paid $50 to get out of jail");
				} else {
					ArrayList<Integer> temp = playersProperties.get(chat).get(username);
					Collections.sort(temp);
					Collections.reverse(temp);
					ListIterator<Integer> it = temp.listIterator();
					String mortgagedProperties = "";
					while (it.hasNext()) {
						int i = it.next();
						if (!mortgaged.get(chat).contains(i)) {
							mortgaged.get(chat).add(i);												
							playersCash.get(chat).put(username, playersCash.get(chat).get(username) + (propertyInitialValue.get(i) / 2));
							mortgagedProperties += "\n" + propertyPositions.get(i);
						}
						if (playersCash.get(chat).get(username) >= 50) {
							break;
						}
					}
					if (playersCash.get(chat).get(username) >= 50) {
						result.get(chat).append("You paid $50 to get out of jail\nYou had to mortgage:" + mortgagedProperties + "\n/unmortgage your properties when you have enough money");
						playersCash.get(chat).put(username, playersCash.get(chat).get(username) - 50);
					} else {
						result.get(chat).append("You do not have enough money or properties to pay $50 and get out of jail\nYou went bankrupt and lost everything to the bank");
						bankrupt(chat, username);
					}
				}
			}
			currentTurn.get(chat).put(username, false);
		} else if (isFreeParking.contains(position)) {
			if (passedGo.get(chat)) {
				result.get(chat).append("You passed GO and collected $200\n\n");
				playersCash.get(chat).put(username, playersCash.get(chat).get(username) + 200);
				passedGo.put(chat, false);
			}
			for (String s : playersList.get(chat)) {
				freeParking.get(chat).put(s, false);
			}
			result.get(chat).append("You landed on free parking\nNext time you land on someone else's property you won't have to pay rent");
			freeParking.get(chat).put(username, true);
			currentTurn.get(chat).put(username, false);
		}
	}
	
	public String getCurrentTurn(String chat) {
		String user = "";
		for (String username : currentTurn.get(chat).keySet()) {
			if (currentTurn.get(chat).get(username) == true) {
				user = username;
			}
		}
		return user;
	}
	
	public void bankrupt(String chat, String username) {
		playersCash.get(chat).put(username, 0);
		ListIterator<Integer> iterator = playersProperties.get(chat).get(username).listIterator();
		while (iterator.hasNext()) {
			int i = iterator.next();
			if (isProperty.contains(i)) {
				if (houses.get(chat).get(i) == 5) {
					availableHotels.put(chat, availableHotels.get(chat) + 1);
				} else {
					availableHouses.put(chat, availableHouses.get(chat) + houses.get(chat).get(i));
				}
				houses.get(chat).put(i, 0);
			}
		}
		iterator = mortgaged.get(chat).listIterator();
		while (iterator.hasNext()) {
			if (playersProperties.get(chat).get(username).contains(iterator.next())) {
				iterator.remove();
			}
		}
		iterator = ownedProperties.get(chat).listIterator();
		while (iterator.hasNext()) {
			if (playersProperties.get(chat).get(username).contains(iterator.next())) {
				iterator.remove();
			}
		}
		playersProperties.get(chat).get(username).clear();
		freeParking.get(chat).put(username, false);
		getOutOfJailFree.get(chat).put(username, 0);
	}
	
	public void deletePlayer (String chat, String username) {
		bankrupt(chat, username);
		playersCash.get(chat).remove(username);
		playersList.get(chat).remove(username);
		playersPosition.get(chat).remove(username);
		currentTurn.get(chat).remove(username);
	}
	
	public String winners(String chat) {
		String res = "";
		ArrayList<Integer> temp = new ArrayList<>();
		HashMap<String, Integer> assets = new HashMap<>();
		for (String i : playersList.get(chat)) {
			int worth = playersCash.get(chat).get(i);
			for (int j : playersProperties.get(chat).get(i)) {
				if (isProperty.contains(j)) {
					worth += rent.get(j).get(houses.get(chat).get(j));
				} else {
					worth += propertyInitialValue.get(j);
				}
			}
			assets.put(i, worth);
		}
		for (String s : assets.keySet()) {
			temp.add(assets.get(s));
		}
		Collections.sort(temp);
		int i = temp.size() - 1;
		int place = 1;
		while (temp.size() != 0) {
			for (String s : assets.keySet()) {
				if (assets.get(s) == temp.get(i)) {
					res += place + ". " + s + " - Total assets: $" + assets.get(s) + "\n";
					temp.remove(i);
					place++;
				}
			}
		}
		return res;
	}
	
	public void endGame(String chat) {
		active.put(chat, false);
		playersList.get(chat).clear();
	}
	
	public void onStart(String chat) {
		HashMap<String, Integer> kick = new HashMap<>();
		HashMap<String, Boolean> turn = new HashMap<>();
		HashMap<String, Integer> cash = new HashMap<>(); 
		HashMap<String, Integer> position = new HashMap<>();
		HashMap<String, ArrayList<Integer>> properties = new HashMap<>();
		HashMap<String, Boolean> parking = new HashMap<>();
		HashMap<String, Integer> jail = new HashMap<>();
		for (String username : playersList.get(chat)) {
			if (username.equals(playersList.get(chat).get(playersList.get(chat).size() - 1))) {
				turn.put(username, true);
			} else {
				turn.put(username, false);
			}
			kick.put(username, 0);
			cash.put(username, startingCash);
			position.put(username, 0);
			properties.put(username, new ArrayList<Integer>());
			parking.put(username, false);
			jail.put(username, 0);
		}
		if (!token.containsKey(chat)) {
			token.put(chat, 0);
		}
		token.put(chat, token.get(chat) + 1);
		result.put(chat, new StringBuilder());
		kicked.put(chat, kick);
		active.put(chat, true);
		playersListIterator.put(chat, playersList.get(chat).listIterator());
		currentTurn.put(chat, turn);
		playersCash.put(chat, cash);
		playersPosition.put(chat, position);
		ownedProperties.put(chat, new ArrayList<Integer>());
		playersProperties.put(chat, properties);
		mortgaged.put(chat, new ArrayList<Integer>());
		houses.put(chat, new HashMap<Integer, Integer>());
		for (int i : isProperty) {
			houses.get(chat).put(i, 0);
		}
		availableHouses.put(chat, 32);
		availableHotels.put(chat, 12);
		passedGo.put(chat, false);
		freeParking.put(chat, parking);
		getOutOfJailFree.put(chat, jail);
		chanceList.put(chat, new ArrayList<Integer>());
		for (int i = 1; i <= 16; i++) {
			chanceList.get(chat).add(i);
		}
		Collections.shuffle(chanceList.get(chat));
		communityChestList.put(chat, new ArrayList<Integer>());
		for (int i = 1; i <= 16; i++) {
			communityChestList.get(chat).add(i);
		}
		Collections.shuffle(chanceList.get(chat));
	}
	
	public void onStart() {
		propertyPositions.put(0, "GO");
		propertyPositions.put(1, "MEDITERRANEAN AVENUE");
		propertyPositions.put(2, "COMMUNITY CHEST");
		propertyPositions.put(3, "BALTIC AVENUE");
		propertyPositions.put(4, "INCOME TAX");
		propertyPositions.put(5, "READING RAILROAD");
		propertyPositions.put(6, "ORIENTAL AVENUE");
		propertyPositions.put(7, "CHANCE");
		propertyPositions.put(8, "VERMONT AVENUE");
		propertyPositions.put(9, "CONNECTICUT AVENUE");
		propertyPositions.put(10, "JAIL");
		propertyPositions.put(11, "ST. CHARLES PLACE");
		propertyPositions.put(12, "ELECTRIC COMPANY");
		propertyPositions.put(13, "STATES AVENUE");
		propertyPositions.put(14, "VIRGINIA AVENUE");
		propertyPositions.put(15, "PENNSYLVANIA RAILROAD");
		propertyPositions.put(16, "ST. JAMES PLACE");
		propertyPositions.put(17, "COMMUNITY CHEST");
		propertyPositions.put(18, "TENNESSEE AVENUE");
		propertyPositions.put(19, "NEW YORK AVENUE");
		propertyPositions.put(20, "FREE PARKING");
		propertyPositions.put(21, "KENTUCKY AVENUE");
		propertyPositions.put(22, "CHANCE");
		propertyPositions.put(23, "INDIANA AVENUE");
		propertyPositions.put(24, "ILLINOIS AVENUE");
		propertyPositions.put(25, "B&O RAILROAD");
		propertyPositions.put(26, "ATLANTIC AVENUE");
		propertyPositions.put(27, "VENTNOR AVENUE");
		propertyPositions.put(28, "WATER WORKS");
		propertyPositions.put(29, "MARVIN GARDENS");
		propertyPositions.put(30, "GO TO JAIL");
		propertyPositions.put(31, "PACIFIC AVENUE");
		propertyPositions.put(32, "NORTH CAROLINA AVENUE");
		propertyPositions.put(33, "COMMUNITY CHEST");
		propertyPositions.put(34, "PENNSYLVANIA AVENUE");
		propertyPositions.put(35, "SHORT LINE");
		propertyPositions.put(36, "CHANCE");
		propertyPositions.put(37, "PARK PLACE");
		propertyPositions.put(38, "LUXURY TAX");
		propertyPositions.put(39, "BOARDWALK");
		isGo.add(0);
		isProperty.add(1);
		isCommunityChest.add(2);
		isProperty.add(3);
		isTax.add(4);
		isRailroad.add(5);
		isProperty.add(6);
		isChance.add(7);
		isProperty.add(8);
		isProperty.add(9);
		isVisitingJail.add(10);
		isProperty.add(11);
		isUtility.add(12);
		isProperty.add(13);
		isProperty.add(14);
		isRailroad.add(15);
		isProperty.add(16);
		isCommunityChest.add(17);
		isProperty.add(18);
		isProperty.add(19);
		isFreeParking.add(20);
		isProperty.add(21);
		isChance.add(22);
		isProperty.add(23);
		isProperty.add(24);
		isRailroad.add(25);
		isProperty.add(26);
		isProperty.add(27);
		isUtility.add(28);
		isProperty.add(29);
		isGoToJail.add(30);
		isProperty.add(31);
		isProperty.add(32);
		isCommunityChest.add(33);
		isProperty.add(34);
		isRailroad.add(35);
		isChance.add(36);
		isProperty.add(37);
		isTax.add(38);
		isProperty.add(39);
		propertyInitialValue.put(1, 60);
		propertyInitialValue.put(3, 60);
		propertyInitialValue.put(5, 200);
		propertyInitialValue.put(6, 100);
		propertyInitialValue.put(8, 100);
		propertyInitialValue.put(9, 120);
		propertyInitialValue.put(11, 140);
		propertyInitialValue.put(12, 150);
		propertyInitialValue.put(13, 140);
		propertyInitialValue.put(14, 160);
		propertyInitialValue.put(15, 200);
		propertyInitialValue.put(16, 180);
		propertyInitialValue.put(18, 180);
		propertyInitialValue.put(19, 200);
		propertyInitialValue.put(21, 220);
		propertyInitialValue.put(23, 220);
		propertyInitialValue.put(24, 240);
		propertyInitialValue.put(25, 200);
		propertyInitialValue.put(26, 260);
		propertyInitialValue.put(27, 260);
		propertyInitialValue.put(28, 150);
		propertyInitialValue.put(29, 280);
		propertyInitialValue.put(31, 300);
		propertyInitialValue.put(32, 300);
		propertyInitialValue.put(34, 320);
		propertyInitialValue.put(35, 200);
		propertyInitialValue.put(37, 350);
		propertyInitialValue.put(39, 400);
		housePrices.put(1, 50);
		housePrices.put(3, 50);
		housePrices.put(6, 50);
		housePrices.put(8, 50);
		housePrices.put(9, 50);
		housePrices.put(11, 100);
		housePrices.put(13, 100);
		housePrices.put(14, 100);
		housePrices.put(16, 100);
		housePrices.put(18, 100);
		housePrices.put(19, 100);
		housePrices.put(21, 150);
		housePrices.put(23, 150);
		housePrices.put(24, 150);
		housePrices.put(26, 150);
		housePrices.put(27, 150);
		housePrices.put(29, 150);
		housePrices.put(31, 200);
		housePrices.put(32, 200);
		housePrices.put(34, 200);
		housePrices.put(37, 200);
		housePrices.put(39, 200);
		for (int i : isProperty) {
			rent.put(i, new HashMap<Integer, Integer>());
		}
		rent.get(1).put(0, 2);
		rent.get(1).put(1, 10);
		rent.get(1).put(2, 30);
		rent.get(1).put(3, 90);
		rent.get(1).put(4, 160);
		rent.get(1).put(5, 250);
		rent.get(3).put(0, 4);
		rent.get(3).put(1, 20);
		rent.get(3).put(2, 60);
		rent.get(3).put(3, 180);
		rent.get(3).put(4, 320);
		rent.get(3).put(5, 450);
		rent.get(6).put(0, 6);
		rent.get(6).put(1, 30);
		rent.get(6).put(2, 90);
		rent.get(6).put(3, 270);
		rent.get(6).put(4, 400);
		rent.get(6).put(5, 550);
		rent.get(8).put(0, 6);
		rent.get(8).put(1, 30);
		rent.get(8).put(2, 90);
		rent.get(8).put(3, 270);
		rent.get(8).put(4, 400);
		rent.get(8).put(5, 550);
		rent.get(9).put(0, 8);
		rent.get(9).put(1, 40);
		rent.get(9).put(2, 100);
		rent.get(9).put(3, 300);
		rent.get(9).put(4, 450);
		rent.get(9).put(5, 600);
		rent.get(11).put(0, 10);
		rent.get(11).put(1, 50);
		rent.get(11).put(2, 150);
		rent.get(11).put(3, 450);
		rent.get(11).put(4, 625);
		rent.get(11).put(5, 750);
		rent.get(13).put(0, 10);
		rent.get(13).put(1, 50);
		rent.get(13).put(2, 150);
		rent.get(13).put(3, 450);
		rent.get(13).put(4, 625);
		rent.get(13).put(5, 750);
		rent.get(14).put(0, 12);
		rent.get(14).put(1, 60);
		rent.get(14).put(2, 180);
		rent.get(14).put(3, 500);
		rent.get(14).put(4, 700);
		rent.get(14).put(5, 900);
		rent.get(16).put(0, 14);
		rent.get(16).put(1, 70);
		rent.get(16).put(2, 200);
		rent.get(16).put(3, 550);
		rent.get(16).put(4, 750);
		rent.get(16).put(5, 950);
		rent.get(18).put(0, 14);
		rent.get(18).put(1, 70);
		rent.get(18).put(2, 200);
		rent.get(18).put(3, 550);
		rent.get(18).put(4, 750);
		rent.get(18).put(5, 950);
		rent.get(19).put(0, 16);
		rent.get(19).put(1, 80);
		rent.get(19).put(2, 220);
		rent.get(19).put(3, 600);
		rent.get(19).put(4, 800);
		rent.get(19).put(5, 1000);
		rent.get(21).put(0, 18);
		rent.get(21).put(1, 90);
		rent.get(21).put(2, 250);
		rent.get(21).put(3, 700);
		rent.get(21).put(4, 875);
		rent.get(21).put(5, 1050);
		rent.get(23).put(0, 18);
		rent.get(23).put(1, 90);
		rent.get(23).put(2, 250);
		rent.get(23).put(3, 700);
		rent.get(23).put(4, 875);
		rent.get(23).put(5, 1050);
		rent.get(24).put(0, 20);
		rent.get(24).put(1, 100);
		rent.get(24).put(2, 300);
		rent.get(24).put(3, 750);
		rent.get(24).put(4, 925);
		rent.get(24).put(5, 1100);
		rent.get(26).put(0, 22);
		rent.get(26).put(1, 110);
		rent.get(26).put(2, 330);
		rent.get(26).put(3, 800);
		rent.get(26).put(4, 975);
		rent.get(26).put(5, 1150);
		rent.get(27).put(0, 22);
		rent.get(27).put(1, 110);
		rent.get(27).put(2, 330);
		rent.get(27).put(3, 800);
		rent.get(27).put(4, 975);
		rent.get(27).put(5, 1150);
		rent.get(29).put(0, 24);
		rent.get(29).put(1, 120);
		rent.get(29).put(2, 360);
		rent.get(29).put(3, 850);
		rent.get(29).put(4, 1025);
		rent.get(29).put(5, 1200);
		rent.get(31).put(0, 26);
		rent.get(31).put(1, 130);
		rent.get(31).put(2, 390);
		rent.get(31).put(3, 900);
		rent.get(31).put(4, 1100);
		rent.get(31).put(5, 1275);
		rent.get(32).put(0, 26);
		rent.get(32).put(1, 130);
		rent.get(32).put(2, 390);
		rent.get(32).put(3, 900);
		rent.get(32).put(4, 1100);
		rent.get(32).put(5, 1275);
		rent.get(34).put(0, 28);
		rent.get(34).put(1, 150);
		rent.get(34).put(2, 450);
		rent.get(34).put(3, 1000);
		rent.get(34).put(4, 1200);
		rent.get(34).put(5, 1400);
		rent.get(37).put(0, 35);
		rent.get(37).put(1, 175);
		rent.get(37).put(2, 500);
		rent.get(37).put(3, 1100);
		rent.get(37).put(4, 1300);
		rent.get(37).put(5, 1500);
		rent.get(39).put(0, 50);
		rent.get(39).put(1, 200);
		rent.get(39).put(2, 600);
		rent.get(39).put(3, 1400);
		rent.get(39).put(4, 1700);
		rent.get(39).put(5, 2000);
		chanceCards.put(1, "Advance to Go - Collect $200");
		chanceCards.put(2, "Advance to Illinois Ave - If you pass go, collect $200");
		chanceCards.put(3, "Advance to St. Charles Place - If you pass go, collect $200");
		chanceCards.put(4, "Advance to the nearest Utility. If unowned, you may buy it from the Bank. If owned, pay the owner rent");
		chanceCards.put(5, "Advance to the nearest Railroad and pay owner rent. If Railroad is unowned, you may buy it from the Bank");
		chanceCards.put(6, "Bank pays you dividend of $50");
		chanceCards.put(7, "Get Out of Jail Free - Use this card the next time you go to jail");
		chanceCards.put(8, "Go back 3 spaces");
		chanceCards.put(9, "Go to Jail - Go directly to Jail\nDo not pass go\nDo not collect $200");
		chanceCards.put(10, "Make general repairs on all your property - For each house pay $25/For each hotel pay $100");
		chanceCards.put(11, "Pay poor tax of $15");
		chanceCards.put(12, "Take a trip to Reading Railroad - If you pass Go, collect $200");
		chanceCards.put(13, "Take a walk on the Boardwalk - Advance to Boardwalk");
		chanceCards.put(14, "You have been elected Chairman of the board - Pay each player $50");
		chanceCards.put(15, "Your building and loan matures - Collect $150");
		chanceCards.put(16, "You have won a crossword competition - Collect $100");
		communityChestCards.put(1, "Advance to Go - Collect $200");
		communityChestCards.put(2, "Bank error in your favor - Collect $200");
		communityChestCards.put(3, "Doctor's fee - Pay $50");
		communityChestCards.put(4, "From sale of stock you get $50");
		communityChestCards.put(5, "Get Out of Jail Free - Use this card the next time you go to jail");
		communityChestCards.put(6, "Go to Jail - Go directly to Jail\nDo not pass go\nDo not collect $200");
		communityChestCards.put(7, "Holiday Fund matures - Receive $100");
		communityChestCards.put(8, "Income tax refund - Collect $20");
		communityChestCards.put(9, "It is your birthday - Collect $10");
		communityChestCards.put(10, "Life insurance matures - Collect $100");
		communityChestCards.put(11, "Pay hospital fees of $100");
		communityChestCards.put(12, "Pay school fees of $150");
		communityChestCards.put(13, "Recieve $25 consultancy fee");
		communityChestCards.put(14, "You are assessed for street repairs - $40 per house/$115 per hotel");
		communityChestCards.put(15, "You have won second prize in a beauty contest - Collect $10");
		communityChestCards.put(16, "You inherit $100");
	}
}
