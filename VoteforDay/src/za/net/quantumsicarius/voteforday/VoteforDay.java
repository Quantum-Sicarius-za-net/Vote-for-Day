/**
This file is part of VoteforDay.

VoteforDay is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

VoteforDay is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with VoteforDay.  If not, see http://www.gnu.org/licenses/.
**/

package za.net.quantumsicarius.voteforday;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.BindingExecutionDelegate;
import org.getspout.spoutapi.keyboard.KeyBindingManager;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.event.input.KeyBindingEvent;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;

import za.net.quantumsicarius.voteforday.Chat.Chat;
import za.net.quantumsicarius.voteforday.Config.Config;
import za.net.quantumsicarius.voteforday.GUI.VoteGUI;
import za.net.quantumsicarius.voteforday.GUI.VoteProgress;
import za.net.quantumsicarius.voteforday.Keyboard.KeyboardTranslate;
import za.net.quantumsicarius.voteforday.Language.LanguageHandler;
import za.net.quantumsicarius.voteforday.Logger.LogMain;
import za.net.quantumsicarius.voteforday.VoteStartHandler.VoteStartHandler;

public class VoteforDay extends JavaPlugin implements Listener{
	
	// Votes: Positive and Negative
	private HashMap<Player, Boolean> votes = new HashMap<Player, Boolean>();
	// Votes: voted boolean
	private HashMap<Player, Boolean> voted = new HashMap<Player, Boolean>();
	// Votes per world
	private final HashMap<Player, World> player_world = new HashMap<Player, World>();
	// Vote session world
	private HashMap<World, Boolean> vote_session_world = new HashMap<World, Boolean>();
	// Player Progress Object
	private HashMap<Player, VoteProgress> player_voteProgress_object = new HashMap<Player, VoteProgress>();
	// Player Vote starter boolean
	private HashMap<Player, Boolean> player_started_a_vote = new HashMap<Player, Boolean>();
	
	// World
	private World current_world;
	
	// Define Vote Progress object
	VoteProgress voteProgressGUI;
	
	// Define Logger object
	LogMain log;
	
	// Define Chat Object
	Chat chat;
	
	// Define configuration object
	Config config;
	
	// Define votestarthandler
	VoteStartHandler votestarthandler;
	
	// Define keyboardTranlate
	KeyboardTranslate keyboard_translate;
	
	// Define LanguageHandler
	LanguageHandler languagehandler;
	
	// Set up show debug log boolean
	boolean showLogDebug = false;
	
	public void onEnable() {
		// Create log object
		log = new LogMain(this);
		// Create configuration object
		config = new Config(this, log);
		
		// Parse configuration to logger
		log.initConfigClass(config);
		
		// Create chat object
		chat = new Chat("VoteforDay");
		
		// Create LanguageHandler object
		languagehandler = new LanguageHandler(config, log, this);
		
		// Create voteStartHandler object
		votestarthandler = new VoteStartHandler(this, config, log, chat, languagehandler);
	
		// Create keyboardTranslate object
		keyboard_translate = new KeyboardTranslate();
		
		// Set up show debug log boolean
		showLogDebug = config.getShowDebugLog();
		
		// Register event listeners
		getServer().getPluginManager().registerEvents(this, this);
		
		// Start key binding
		log.logDebug("Started setting up keybinding!");
		keyboard();
		log.logDebug("Finished setting up keybinding!");
		
		log.logInfo(languagehandler.enable());
	}
	
	public void onDisable() {
		log.logInfo(languagehandler.disable());
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		
		// Set player to sender
		if (sender instanceof Player) {
			player = (Player) sender;
		}
		
		// Check command voteforday (Start the vote)
		if (cmd.getName().equalsIgnoreCase("voteforday") && player != null){

			// Make sure that the player doesn't start another vote on a separate world
			if (player_started_a_vote.containsKey(player)) {
				if (!player_started_a_vote.get(player)) {
					if(votestarthandler.onCommand(player, vote_session_world, votes,voted)) {
						runVote(player.getWorld());
						
						vote_session_world = votestarthandler.vote_session_world();
						votes = votestarthandler.votes();
						voted = votestarthandler.voted();
						
						player_world.put(player, player.getWorld());
						player_started_a_vote.put(player, true);
					}
				}
				else {
					player.sendMessage(chat.chatWarning(languagehandler.AlreadyActiveVoteSession()));
				}
			}
			else {
				if(votestarthandler.onCommand(player, vote_session_world, votes,voted)) {
					runVote(player.getWorld());
					
					vote_session_world = votestarthandler.vote_session_world();
					votes = votestarthandler.votes();
					voted = votestarthandler.voted();
					
					player_world.put(player, player.getWorld());
					player_started_a_vote.put(player, true);
				}
			}
			
			return true;
			
		}
		// Check vote parameters
		else if (cmd.getName().equalsIgnoreCase("vote") && args.length > 0 && player != null) {
			if (args[0] != null && args.length < 2) {
				
				// Check for permission
				if (player.hasPermission("voteforday.vote")  | player.isOp()) {
					if(vote_session_world.containsKey(player.getWorld())) {
						if (vote_session_world.get(player.getWorld())) {
							
							// If parameter equal 'y'
							if (args[0].equalsIgnoreCase("y")) {
								
								// Check if player exists							
								if (voted.containsKey(player)) {
									// Check if player has voted
									if (voted.get(player) == false) {
										log.logDebug("Player: " + player.getName() + " voted YES");
										sender.sendMessage(chat.chatInfo(languagehandler.votedYes()));
										
										log.logDebug("Adding player's world: " + player.getWorld().getName() + " To HashMap");
										player_world.put(player, player.getWorld());
										voted.put(player, true);
										votes.put(player, true);
										
										log.logDebug("Calling updateGUI method!");
										updateGUI(player, true);
										
									}
									else {
										log.logDebug("Player: " + player.getName() + " Has already voted!");
										sender.sendMessage(chat.chatWarning(languagehandler.youHaveAlreadyVoted()));
									}
								}
								else {
									voted.put(player, false);
									
									if (voted.get(player) == false) {
										log.logDebug("Player: " + player.getName() + " voted YES");
										sender.sendMessage(chat.chatInfo(languagehandler.votedYes()));
										
										log.logDebug("Adding player's world: " + player.getWorld().getName() + " To HashMap");
										player_world.put(player, player.getWorld());
										voted.put(player, true);
										votes.put(player, true);
										
										log.logDebug("Calling updateGUI method!");
										updateGUI(player, true);
									}
									else {
										log.logDebug("Player: " + player.getName() + " Has already voted!");
										sender.sendMessage(chat.chatWarning(languagehandler.youHaveAlreadyVoted()));
									}
								}
							}
							
							// If parameter equal 'n'
							else if (args[0].equalsIgnoreCase("n")) {
								// Check if player exists
								if (voted.containsKey(player)) {
									// Check if player has voted
									if (voted.get(player) == false) {
										log.logDebug("Player: " + player.getName() + " voted NO");
										sender.sendMessage(chat.chatInfo(languagehandler.votedNo()));
										
										log.logDebug("Adding player's world: " + player.getWorld().getName() + " To HashMap");
										player_world.put(player, player.getWorld());
										voted.put(player, true);
										votes.put(player, false);
										
										updateGUI(player, false);
									}
									else {
										log.logDebug("Player: " + player.getName() + " Has already voted!");
										sender.sendMessage(chat.chatWarning(languagehandler.youHaveAlreadyVoted()));
									}
								}
								else {
									voted.put(player, false);
									
									if (voted.get(player) == false) {
										log.logDebug("Player: " + player.getName() + " voted NO");
										sender.sendMessage(chat.chatInfo(languagehandler.votedNo()));
										
										log.logDebug("Adding player's world: " + player.getWorld().getName() + " To HashMap");
										player_world.put(player, player.getWorld());
										voted.put(player, true);
										votes.put(player, false);
										
										updateGUI(player, false);
									}
									else {
										log.logDebug("Player: " + player.getName() + " Has already voted!");
										sender.sendMessage(chat.chatWarning(languagehandler.youHaveAlreadyVoted()));
									}
								}
							}
						
							// If none of the vote parameters passed then Warn player and return false
							else {
								sender.sendMessage(chat.chatSevere(languagehandler.IllegalVoteParam()));
								return false;
							}
						}
						else {
							player.sendMessage(chat.chatWarning(languagehandler.NoActiveVoteSession()));
						}
					}
					else {
						player.sendMessage(chat.chatWarning(languagehandler.NoActiveVoteSession()));
					}
					//----End of parameter check----//
					return true;
				}
				else {
					player.sendMessage(chat.chatSevere(languagehandler.YouDoNotHavePermissionToVote()));
				}
			}

		}
		else {
			
			if (player == null) {
				sender.sendMessage(chat.chatSevere("This command can only be run by a player!"));
				return true;
			}
			else {
				sender.sendMessage(chat.chatSevere(languagehandler.IllegalVoteParam()));
				return false;
			}
			
		}
		return true;
	}
	
	// Initialize a vote session
	private void runVote(World current_world) {
		
		//vote_session = true;
		
		// Set the worlds vote session to true
		vote_session_world.put(current_world, true);
		
		Player players [] = current_world.getPlayers().toArray(new Player[0]);
		
		for (int i = 0;i < players.length; i++) {
			if (vote_session_world.containsKey(players[i].getWorld())) {
				
				// Check if players are on a voting world!
				if (vote_session_world.get(players[i].getWorld())) {
					
					// If player is a SpoutPlayer (Don't know if its a bug, but this seems to always return true)
					if (players[i] instanceof SpoutPlayer) {
						
						// Create the spoutplayer local object and parse it player
						SpoutPlayer spoutplayer = (SpoutPlayer) players[i];
						
						log.logDebug("Adding player: " + players[i].getName() + " to world Map: " + players[i].getWorld());
						player_world.put(players[i], players[i].getWorld());
						
						if (spoutplayer.isSpoutCraftEnabled()) {
							log.logDebug("Spout player is: " + spoutplayer.getName());
							
							// Check if player has permission
							if (spoutplayer.hasPermission("voteforday.vote") | spoutplayer.isOp()) {
								spoutplayer.sendNotification("Vote!",languagehandler.StartVoting() , Material.BONE);
								VoteGUI popup = new VoteGUI(spoutplayer);
								spoutplayer.getMainScreen().attachPopupScreen(popup);
								voteProgressGUI = new VoteProgress(spoutplayer, this);
								player_voteProgress_object.put(spoutplayer, voteProgressGUI);
								//voteProgressGUI.createGUI(spoutplayer, this);
								voteProgressGUI.reset_GUI(spoutplayer);
							}
						}
						else {
							// Check if player has permission
							if (players[i].hasPermission("voteforday.vote")) {
								players[i].sendMessage(chat.chatInfo(languagehandler.StartVoting()));
							}
						}
						
					}
				}
			}	
		}
		
		// Call end method to check results!
		voteEnd(players, current_world);
	}
	
	// Player Disconnect event
	@EventHandler
	public void onPlayerQuit (PlayerQuitEvent event) {
		
		log.logDebug("Player: " + event.getPlayer().getName() + " has disconnected! Cleaning his HashMap object");
		
		// This frees some memory (Or in theory it should..)
		player_voteProgress_object.remove(event.getPlayer());
		player_world.remove(event.getPlayer());
	}
	
	// Player Teleport event, this event checks for player world change. When a player changes a world, it detaches the vote progress GUI to eliminate graphical artifacts
	@EventHandler
	public void onPlayerChangeWorld(PlayerTeleportEvent event) {
		// Safety check
		if (player_world.containsKey(event.getPlayer())) {
			if (event.getPlayer().getWorld() != player_world.get(event.getPlayer())) {
				log.logDebug("Player: " + event.getPlayer().getName() + " has changed worlds, closing his GUI");
				player_voteProgress_object.get(event.getPlayer()).close_GUI((SpoutPlayer) event.getPlayer());
			}
		}
		else {
			player_world.put(event.getPlayer(), event.getPlayer().getWorld());	
			if (event.getPlayer().getWorld() != player_world.get(event.getPlayer())) {
				log.logDebug("Player: " + event.getPlayer().getName() + " has changed worlds, closing his GUI");
				player_voteProgress_object.get(event.getPlayer()).close_GUI((SpoutPlayer) event.getPlayer());
			}
		}
		
	}
	
	// Update Method
	public void updateGUI(Player player, boolean type_of_vote) {
		
		Player[] active_player_array = player.getWorld().getPlayers().toArray(new Player[0]);
		
		for (int i = 0; i < active_player_array.length; i++) {
			SpoutPlayer spoutplayer = (SpoutPlayer) active_player_array[i];
			if (spoutplayer.isSpoutCraftEnabled()) {
				log.logDebug("Updating GUI for: " + spoutplayer.getName());
				if (type_of_vote) {
					if (player_voteProgress_object.containsKey(spoutplayer)) {
						player_voteProgress_object.get(spoutplayer).update(1, 0, spoutplayer);
					}
				}
				else {
					if (player_voteProgress_object.containsKey(spoutplayer)) {
						player_voteProgress_object.get(spoutplayer).update(0, 1, spoutplayer);
					}
				}
				if (spoutplayer.getWorld() != player_world.get(spoutplayer)) {
					log.logDebug("Player: " + spoutplayer.getName() + " has changed worlds, closing his GUI");
					player_voteProgress_object.get(spoutplayer).close_GUI(spoutplayer);
				}
			}
		}
		
	}
	
	// Keyboard Listener
	public void keyboard() {
		
		String defualt_key = config.getKeyBinding();
		log.logDebug("The defualt keyboard key is: " + defualt_key);
		
		KeyBindingManager bindingmanager = SpoutManager.getKeyBindingManager();
		bindingmanager.registerBinding("Start vote!", keyboard_translate.Translate(defualt_key), "Starts a new voting session.", new BindingExecutionDelegate() {
			@Override
			public void keyPressed(KeyBindingEvent arg0) {
				
				// Check if player is in chat
				if (arg0.getScreenType() == ScreenType.GAME_SCREEN) {
					
					log.logDebug("Player: " + arg0.getPlayer().getName() + " has pressed the keybind!");
						
					// Make sure that the player doesn't start another vote on a separate world
					if (player_started_a_vote.containsKey(arg0.getPlayer())) {
						if (!player_started_a_vote.get(arg0.getPlayer())) {
							if (votestarthandler.onCommand(arg0.getPlayer(), vote_session_world, votes, voted)) {
								runVote(arg0.getPlayer().getWorld());
								
								vote_session_world = votestarthandler.vote_session_world();
								votes = votestarthandler.votes();
								voted = votestarthandler.voted();
								
								player_world.put(arg0.getPlayer(), arg0.getPlayer().getWorld());
								player_started_a_vote.put(arg0.getPlayer(), true);
							}
						}
						else {
							arg0.getPlayer().sendMessage(chat.chatWarning(languagehandler.voteAlreadyStarted()));
						}
					}
					else {
						if (votestarthandler.onCommand(arg0.getPlayer(), vote_session_world, votes, voted)) {
							runVote(arg0.getPlayer().getWorld());
							
							vote_session_world = votestarthandler.vote_session_world();
							votes = votestarthandler.votes();
							voted = votestarthandler.voted();
							
							player_world.put(arg0.getPlayer(), arg0.getPlayer().getWorld());
							player_started_a_vote.put(arg0.getPlayer(), true);
						}
					}
				}
			}

			@Override
			public void keyReleased(KeyBindingEvent arg0) {
				// Unused
			}
		}, this);
	}
	
	// Button Click Listener
	@EventHandler
	public void ButtononClickEvent(ButtonClickEvent event) {
		
		SpoutPlayer player = event.getPlayer();
		Button control = event.getButton();
		
		VoteGUI gui = new VoteGUI(player);
		
		log.logDebug("Button click! by: " + player.getName());
					
				// If button is YES
				if (gui.isAccept(control)) { 
					// Check permission
					if (player.hasPermission("voteforday.vote") | player.isOp()) {

						// Key checks (Multi-world check)
						if (vote_session_world.containsKey(player.getWorld())) {
							if (vote_session_world.get(player.getWorld())) {
								
								// Check more keys
								if (votes.containsKey(player) && voted.containsKey(player)) {
									if (voted.get(player) == false) {
										log.logDebug("Adding player's world: " + player.getWorld().getName() + " To HashMap");
										player_world.put(player, player.getWorld());
										votes.put(player, true);
										voted.put(player, true);
										log.logDebug("Player: " + player.getName() + " voted YES");
										player.sendNotification("Vote", languagehandler.votedNo(), Material.BONE);
										player.getMainScreen().getActivePopup().close();
										
										updateGUI(player, true);
									}
									else {
										log.logDebug("Player: " + player.getName() + " Has already voted!");
										player.sendNotification("Vote", languagehandler.youHaveAlreadyVoted(), Material.BONE);
										player.getMainScreen().getActivePopup().close();
									}
								}
								else {
									log.logDebug("Adding player's world: " + player.getWorld().getName() + " To HashMap");
									player_world.put(player, player.getWorld());
									
									voted.put(player, true);
									votes.put(player, true);
									player.getMainScreen().getActivePopup().close();
									
									player.sendNotification("Vote", languagehandler.votedYes(), Material.BONE);
									
									updateGUI(player, true);
								}
							}
							else {
								player.sendMessage(chat.chatWarning(languagehandler.NoActiveVoteSession()));
								player.getMainScreen().getActivePopup().close();
							}
						}
						else {
							player.sendMessage(chat.chatWarning(languagehandler.NoActiveVoteSession()));
							player.getMainScreen().getActivePopup().close();
						}
					}
					// If player doesn't have permission
					else {
						player.sendMessage(chat.chatSevere(languagehandler.YouDoNotHavePermissionToVote()));
					}
				}
				
				// If button is NO
				else if (gui.isDecline(control)) {
					
					// Check for permission
					if (player.hasPermission("voteforday.vote") | player.isOp()) {

						// Key checks (Multi-world check)
						if (vote_session_world.containsKey(player.getWorld())) {
							if (vote_session_world.get(player.getWorld())) {
								
								// More key checks
								if (votes.containsKey(player) && voted.containsKey(player)) {
									if (voted.get(player) == false) {
										log.logDebug("Adding player's world: " + player.getWorld().getName() + " To HashMap");
										player_world.put(player, player.getWorld());
										
										votes.put(player, false);
										voted.put(player, true);
										log.logDebug("Player: " + player.getName() + " voted NO");
										player.sendNotification("Vote", languagehandler.votedNo(), Material.BONE);
										player.getMainScreen().getActivePopup().close();
										
										updateGUI(player, false);
									}
									else {
										log.logDebug("Player: " + player.getName() + " Has already voted!");
										player.sendNotification("Vote", languagehandler.youHaveAlreadyVoted(), Material.BONE);
										player.getMainScreen().getActivePopup().close();
									}
								}
								else {
									log.logDebug("Adding player's world: " + player.getWorld().getName() + " To HashMap");
									player_world.put(player, player.getWorld());
									voted.put(player, true);
									votes.put(player, false);
									
									player.getMainScreen().getActivePopup().close();
									
									player.sendNotification("Vote", languagehandler.votedNo(), Material.BONE);
									updateGUI(player, false);
								}
							}
							else {
								player.sendMessage(chat.chatWarning(languagehandler.NoActiveVoteSession()));
								player.getMainScreen().getActivePopup().close();
							}	
						}
						else {
							player.sendMessage(chat.chatWarning(languagehandler.NoActiveVoteSession()));
							player.getMainScreen().getActivePopup().close();
						}	
					}
					else {
						log.logDebug("Player: " + player.getName() + "Is not permitted to vote!");
						player.sendMessage(chat.chatSevere(languagehandler.YouDoNotHavePermissionToVote()));
					}
					
				}	
	}
	
	private void voteEnd(final Player[] players, final World world) {
		this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable () {
			
			@Override
			public void run() {
				
				Server server = getServer();
				server.broadcastMessage(chat.chatInfo(languagehandler.VoteCompletedOnWorld() + " " + world.getName()));
				
				int positive_votes = 0;
				int negative_votes = 0;
				
				current_world = world;
				
				for (int i = 0; i < players.length; i++) {
					// Test keys
					if (votes.containsKey(players[i])) {
						if (player_world.containsKey(players[i]) && vote_session_world.containsKey(current_world)) {
							// First check if world has a voting session (Just to be safe...)
							if (vote_session_world.get(current_world)) {
								// Check if player is on same world of the one that has finished voting
								if (player_world.get(players[i]) == current_world) {
									if (votes.get(players[i]) == true) {
										positive_votes ++;
									}
									else {
										negative_votes++;
									}
								}
							}
						}
					}
				}
				
				log.logDebug("Vote results are: " + positive_votes + " voted yes and: " + negative_votes + " voted no!");
				
				// Test which votes won
				if (positive_votes > negative_votes) {
					log.logDebug("Positive votes won! On world: " + current_world.getName() +" Changing to day!");
					server.broadcastMessage(chat.chatInfo(languagehandler.YESVoteWon()));
					current_world.setTime(0);
				}
				else {
					log.logDebug("Negative votes won! On world: " + current_world.getName() + " Not changing to day!");
					server.broadcastMessage(chat.chatInfo(languagehandler.NOVoteWon()));
				}
				
				// Reset players votes
				for (int i = 0; i < players.length; i++) {
					// First check if keys are in place
					if (voted.containsKey(players[i]) && votes.containsKey(players[i])) {
						// Check for more keys
						if (player_world.containsKey(players[i]) && vote_session_world.containsKey(current_world)) {
							
							// If players world is equal to the current world that ended a vote
							if (player_world.get(players[i]) == current_world) {
								
								if (vote_session_world.get(current_world)) {
									// Reset player
									log.logDebug("Resseting player: " + players[i].getName() + " vote!");
									voted.put(players[i], false);
									votes.put(players[i], false);
								}
							}
						}
					}
					
					// Check for more keys
					if (player_world.containsKey(players[i]) && vote_session_world.containsKey(current_world)) {
						
						// If players world is equal to the current world that ended a vote
						if (player_world.get(players[i]) == current_world) {
							
							if (vote_session_world.get(current_world)) {	
								
								// Reset players vote start
								player_started_a_vote.put(players[i], false);
								
								// If a player is a spout player and that player has an open popup from the vote, close it
								if (players[i] instanceof SpoutPlayer) {
									SpoutPlayer spoutplayer = (SpoutPlayer) players[i];
									spoutplayer.getMainScreen().closePopup();
									if (player_voteProgress_object.containsKey(spoutplayer)) {
										log.logDebug("Closing Player: " + spoutplayer.getName() + " Vote Progress GUI");
										player_voteProgress_object.get(spoutplayer).close_GUI(spoutplayer);
									}
									else {
										log.logDebug("Player: " + spoutplayer.getName() + " is not in the HashMap (Progress GUI)");
									}
								}
							}
						}
					}
				}
				
				// Reset current world
				vote_session_world.put(world, false);
				
			}} , config.getVoteSessionTime()); //400L
	}
}
