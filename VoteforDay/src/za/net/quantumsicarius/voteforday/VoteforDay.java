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
import za.net.quantumsicarius.voteforday.Keyboard.KeyboardTranslate;
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
	// World
	private World current_world;
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
		
		// Create voteStartHandler object
		votestarthandler = new VoteStartHandler(this, config, log, chat);
	
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
		
		log.logInfo("Enabled!");
	}
	
	public void onDisable() {
		log.logInfo("Disabled!");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		
		// Set player to sender
		if (sender instanceof Player) {
			player = (Player) sender;
		}
		
		// Check command voteforday (Start the vote)
		if (cmd.getName().equalsIgnoreCase("voteforday") && player != null){

			if(votestarthandler.onCommand(player, vote_session_world, votes,voted)) {
				runVote(player.getWorld());
				
				vote_session_world = votestarthandler.vote_session_world();
				votes = votestarthandler.votes();
				voted = votestarthandler.voted();
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
										sender.sendMessage(chat.chatInfo("You voted: Yes"));
										
										log.logDebug("Adding player's world: " + player.getWorld().getName() + " To HashMap");
										player_world.put(player, player.getWorld());
										voted.put(player, true);
										votes.put(player, true);	
									}
									else {
										log.logDebug("Player: " + player.getName() + " Has already voted!");
										sender.sendMessage(chat.chatWarning("You have already voted!"));
									}
								}
								else {
									voted.put(player, false);
									
									if (voted.get(player) == false) {
										log.logDebug("Player: " + player.getName() + " voted YES");
										sender.sendMessage(chat.chatInfo("You voted: Yes"));
										
										log.logDebug("Adding player's world: " + player.getWorld().getName() + " To HashMap");
										player_world.put(player, player.getWorld());
										voted.put(player, true);
										votes.put(player, true);	
									}
									else {
										log.logDebug("Player: " + player.getName() + " Has already voted!");
										sender.sendMessage(chat.chatWarning("You have already voted!"));
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
										sender.sendMessage(chat.chatInfo("You voted: NO"));
										
										log.logDebug("Adding player's world: " + player.getWorld().getName() + " To HashMap");
										player_world.put(player, player.getWorld());
										voted.put(player, true);
										votes.put(player, false);
									}
									else {
										log.logDebug("Player: " + player.getName() + " Has already voted!");
										sender.sendMessage(chat.chatWarning("You have already voted!"));
									}
								}
								else {
									voted.put(player, false);
									
									if (voted.get(player) == false) {
										log.logDebug("Player: " + player.getName() + " voted NO");
										sender.sendMessage(chat.chatInfo("You voted: No"));
										
										log.logDebug("Adding player's world: " + player.getWorld().getName() + " To HashMap");
										player_world.put(player, player.getWorld());
										voted.put(player, true);
										votes.put(player, false);	
									}
									else {
										log.logDebug("Player: " + player.getName() + " Has already voted!");
										sender.sendMessage(chat.chatWarning("You have already voted!"));
									}
								}
							}
						
							// If none of the vote parameters passed then Warn player and return false
							else {
								sender.sendMessage(chat.chatSevere("You made an illegal vote! Retry!"));
								return false;
							}
						}
						else {
							player.sendMessage(chat.chatWarning("There isn't an active vote session in your world!"));
						}
					}
					else {
						player.sendMessage(chat.chatWarning("There isn't an active vote session in your world!"));
					}
					//----End of parameter check----//
					return true;
				}
				else {
					player.sendMessage(chat.chatSevere("You are not permitted to vote!"));
				}
			}

		}
		else {
			
			if (player == null) {
				sender.sendMessage(chat.chatSevere("This command can only be run by a player!"));
				return true;
			}
			else {
				sender.sendMessage(chat.chatSevere("You made an illegal arguement!"));
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
					
					if (players[i] instanceof SpoutPlayer) {
						
						SpoutPlayer spoutplayer = (SpoutPlayer) players[i];
						
						spoutplayer.sendNotification("Vote!","Start Voting!" , Material.BONE);
						
						log.logDebug("Spout player is: " + spoutplayer.getName());
						
						if (spoutplayer.isSpoutCraftEnabled()) {
							log.logDebug(spoutplayer.getTitle() + " Spout is enabled!");
							
							// Check if player has permission
							if (spoutplayer.hasPermission("voteforday.vote") | spoutplayer.isOp()) {
								VoteGUI popup = new VoteGUI(spoutplayer);
								spoutplayer.getMainScreen().attachPopupScreen(popup);
							}
						}
						
					}
					else {
						players[i].sendMessage(chat.chatInfo("Start voting!"));
					}
				}
			}	
		}
		
		// Call end method to check results!
		voteEnd(players, current_world);
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
					if (votestarthandler.onCommand(arg0.getPlayer(), vote_session_world, votes, voted)) {
						runVote(arg0.getPlayer().getWorld());
						
						vote_session_world = votestarthandler.vote_session_world();
						votes = votestarthandler.votes();
						voted = votestarthandler.voted();			
					}
					else {
						
					}
				}
			}

			@Override
			public void keyReleased(KeyBindingEvent arg0) {
				
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
										player.sendNotification("Vote", "You voted YES", Material.BONE);
										player.getMainScreen().getActivePopup().close();
									}
									else {
										log.logDebug("Player: " + player.getName() + " Has already voted!");
										player.sendNotification("Vote", "You have already voted!", Material.BONE);
										player.getMainScreen().getActivePopup().close();
									}
								}
								else {
									log.logDebug("Adding player's world: " + player.getWorld().getName() + " To HashMap");
									player_world.put(player, player.getWorld());
									
									voted.put(player, true);
									votes.put(player, true);
									player.getMainScreen().getActivePopup().close();
								}
							}
							else {
								player.sendMessage(chat.chatWarning("There isn't an active vote session!"));
							}
						}
						else {
							player.sendMessage(chat.chatWarning("There isn't an active vote session!"));
						}
					}
					// If player doesn't have permission
					else {
						player.sendMessage(chat.chatSevere("You are not permitted to vote!"));
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
										player.sendNotification("Vote", "You voted NO", Material.BONE);
										player.getMainScreen().getActivePopup().close();
									}
									else {
										log.logDebug("Player: " + player.getName() + " Has already voted!");
										player.sendNotification("Vote", "You have already voted!", Material.BONE);
										player.getMainScreen().getActivePopup().close();
									}
								}
								else {
									log.logDebug("Adding player's world: " + player.getWorld().getName() + " To HashMap");
									player_world.put(player, player.getWorld());
									voted.put(player, true);
									votes.put(player, false);
									player.getMainScreen().getActivePopup().close();
								}
							}
							else {
								player.sendMessage(chat.chatWarning("There isn't an active vote session in your world!"));
							}	
						}
						else {
							player.sendMessage(chat.chatWarning("There isn't an active vote session in your world!"));
						}	
					}
					else {
						log.logDebug("Player: " + player.getName() + "Is not permitted to vote!");
						player.sendMessage(chat.chatSevere("You are not permitted to vote!"));
					}
					
				}	
	}
	
	
	private void voteEnd(final Player[] players, final World world) {
		this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable () {

			@Override
			public void run() {
				
				Server server = getServer();
				server.broadcastMessage(chat.chatInfo("Voting Complete! On world: " + world.getName()));
				
				int positive_votes = 0;
				int negative_votes = 0;
				
				current_world = world;
				
				for (int i = 0; i < players.length; i++) {
					// Test keys
					if (votes.containsKey(players[i])) {
						if (player_world.containsKey(players[i]) && vote_session_world.containsKey(world)) {
							// First check if world has a voting session (Just to be safe...)
							if (vote_session_world.get(world)) {
								// Check if player is on same world of the one that has finished voting
								if (player_world.get(players[i]) == world) {
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
					log.logDebug("Positive votes won! On world: " + world.getName() +" Changing to day!");
					server.broadcastMessage(chat.chatInfo("'Yes' votes won! On world: " + world.getName() + " Changing to day!"));
					current_world.setTime(0);
				}
				else {
					log.logDebug("Negative votes won! On world: " + world.getName() + " Not changing to day!");
					server.broadcastMessage(chat.chatInfo("'No' votes won! On world: " + world.getName() + " Not changing to day!"));
				}
				
				// Reset players votes
				for (int i = 0; i < players.length; i++) {
					// First check if keys are in place
					if (voted.containsKey(players[i]) && votes.containsKey(players[i])) {
						// Check for more keys
						if (player_world.containsKey(players[i]) && vote_session_world.containsKey(world)) {
							// If players world is equal to the current world that ended a vote
							if (player_world.get(players[i]) == world) {
								if (vote_session_world.get(world)) {
									// Reset player
									log.logDebug("Resseting player: " + players[i].getName() + " vote!");
									voted.put(players[i], false);
									votes.put(players[i], false);
									
									// If a player is a spout player and that player has an open popup from the vote, close it
									if (players[i] instanceof SpoutPlayer) {
										SpoutPlayer spoutplayer = (SpoutPlayer) players[i];
										spoutplayer.getMainScreen().closePopup();
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
