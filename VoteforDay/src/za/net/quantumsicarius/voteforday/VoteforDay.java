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
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;


import za.net.quantumsicarius.voteforday.Chat.Chat;
import za.net.quantumsicarius.voteforday.Config.Config;
import za.net.quantumsicarius.voteforday.GUI.VoteGUI;
import za.net.quantumsicarius.voteforday.Logger.LogMain;

public class VoteforDay extends JavaPlugin implements Listener{
	
	// Votes: Positive and Negative
	private final HashMap<Player, Boolean> votes = new HashMap<Player, Boolean>();
	// Votes: voted boolean
	private final HashMap<Player, Boolean> voted = new HashMap<Player, Boolean>();
	// World
	private World current_world;
	// Current session
	private boolean vote_session = false;
	
	// Create Logger object
	LogMain log = new LogMain("VoteforDay");
	
	// Create Chat Object
	Chat chat = new Chat("VoteforDay");
	
	// Create config object
	Config config;
	
	// Set up show debug log boolean
	boolean showLogDebug = false;
	
	public void onEnable() {
		// Create config object
		config = new Config(this);
		
		// Set up show debug log boolean
		showLogDebug = config.getShowDebugLog(this);
		
		getServer().getPluginManager().registerEvents(this, this);
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
			if (testRunVote(player)) {
				if (vote_session) {
					sender.sendMessage(chat.chatWarning("There is already an active vote!"));
				}
				else {
					sender.sendMessage(chat.chatInfo("Started Vote!"));		
					
					runVote();					
				}
			}
			else {
				sender.sendMessage(chat.chatWarning("Can't run vote when its day!"));
			}
			return true;
			
		}
		// Check vote parameters
		else if (cmd.getName().equalsIgnoreCase("vote") && args.length > 0 && player != null) {
			if (args[0] != null && args.length < 2) {
				if (vote_session) {
					
					if (args[0].equalsIgnoreCase("y")) {
						// Check if player exists
						if (voted.containsKey(player)) {
							// Check if player has voted
							if (voted.get(player) == false) {
								sender.sendMessage(chat.chatInfo("You voted: Yes"));
								voted.put(player, true);
								votes.put(player, true);	
							}
							else {
								sender.sendMessage(chat.chatWarning("You have already voted!"));
							}
						}
						else {
							voted.put(player, false);
							
							if (voted.get(player) == false) {
								sender.sendMessage(chat.chatInfo("You voted: Yes"));
								//sender.sendMessage("[VoteforDay] You voted: Yes");
								voted.put(player, true);
								votes.put(player, true);	
							}
							else {
								sender.sendMessage(chat.chatWarning("You have already voted!"));
								//sender.sendMessage("[VoteforDay] You have already voted!");
							}
						}
					}
					else if (args[0].equalsIgnoreCase("n")) {
						// Check if player exists
						if (voted.containsKey(player)) {
							// Check if player has voted
							if (voted.get(player) == false) {
								sender.sendMessage(chat.chatInfo("You voted: NO"));
								voted.put(player, true);
								votes.put(player, false);
							}
							else {
								sender.sendMessage(chat.chatWarning("You have already voted!"));
							}
						}
						else {
							voted.put(player, false);
							
							if (voted.get(player) == false) {
								sender.sendMessage(chat.chatInfo("You voted: No"));
								voted.put(player, true);
								votes.put(player, false);	
							}
							else {
								sender.sendMessage(chat.chatWarning("You have already voted!"));
							}
						}
					}
					else {
						sender.sendMessage(chat.chatSevere("You made an illegal vote! Retry!"));
						return false;
					}
				}
				else {
					sender.sendMessage(chat.chatWarning("There isn't an active vote session!"));
				}
				return true;
			}
		}
		else {
			if (player == null) {
				sender.sendMessage(chat.chatSevere("This command can only be run by a player!"));
				return true;
			}
			else {
				sender.sendMessage(chat.chatSevere("You made an illegal arguement!"));
			}
		}
		return false;
	}
	
	// Test to see if its night
	private boolean testRunVote(Player player) {
		
		current_world = player.getWorld();
		if  (current_world.getTime() > config.getVoteAllowStartTime(this)) {
			return true;
		}
		return false;
	}
	
	// Initialize a vote session
	private void runVote() {
		
		vote_session = true;
		
		Player players [] = current_world.getPlayers().toArray(new Player[0]);
		
		for (int i = 0;i < players.length; i++) {
			
			if (players[i] instanceof SpoutPlayer) {
				
				SpoutPlayer spoutplayer = (SpoutPlayer) players[i];
				
				spoutplayer.sendNotification("Vote!","Start Voting!" , Material.BONE);
				
				log.logDebug("Spout player is: " + spoutplayer.getTitle(), showLogDebug);
				
				if (spoutplayer.isSpoutCraftEnabled()) {
					log.logDebug(spoutplayer.getTitle() + " Spout is enabled!" , showLogDebug);
					
					VoteGUI popup = new VoteGUI(spoutplayer);
					spoutplayer.getMainScreen().attachPopupScreen(popup);
				}
				
			}
			else {
				players[i].sendMessage(chat.chatInfo("Start voting!"));
			}
			
		}
		
		// Call end method to check results!
		voteEnd(players);
	}
	
	// Button Click Listener
	@EventHandler
	public void ButtononClickEvent(ButtonClickEvent event) {
		
		SpoutPlayer player = event.getPlayer();
		Button control = event.getButton();
		
		VoteGUI gui = new VoteGUI(player);
		
		log.logDebug("Button click! by: " + player.getTitle(), config.getShowDebugLog(this));
		
		// If button is YES
		if (gui.isAccept(control)) {
			if (votes.containsKey(player) && voted.containsKey(player)) {
				if (voted.get(player) == false) {
					votes.put(player, true);
					voted.put(player, true);
					player.sendNotification("Vote", "You voted YES", Material.BONE);
					player.getMainScreen().getActivePopup().close();
				}
				else {
					player.sendNotification("Vote", "You have already voted!", Material.BONE);
					player.getMainScreen().getActivePopup().close();
				}
			}
			else {
				voted.put(player, true);
				votes.put(player, true);
				player.getMainScreen().getActivePopup().close();
			}
		}
		
		// If button is NO
		else if (gui.isDecline(control)) {
			if (votes.containsKey(player) && voted.containsKey(player)) {
				if (voted.get(player) == false) {
					votes.put(player, false);
					voted.put(player, true);
					player.sendNotification("Vote", "You voted NO", Material.BONE);
					player.getMainScreen().getActivePopup().close();
				}
				else {
					player.sendNotification("Vote", "You have already voted!", Material.BONE);
					player.getMainScreen().getActivePopup().close();
				}
			}
			else {
				voted.put(player, true);
				votes.put(player, false);
				player.getMainScreen().getActivePopup().close();
			}
		}
		else {
			player.sendMessage(chat.chatSevere("Failed to detect button! Please contact developer!"));
		}
	}
	
	
	private void voteEnd(final Player[] players) {
		this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable () {

			@Override
			public void run() {
				
				Server server = getServer();
				server.broadcastMessage(chat.chatInfo("Voting Complete!"));
				
				int positive_votes = 0;
				int negative_votes = 0;
				
				for (int i = 0; i < players.length; i++) {
					if (votes.containsKey(players[i])) {
						if (votes.get(players[i]) == true) {
							positive_votes ++;
						}
						else {
							negative_votes++;
						}
					}
				}
				
				log.logDebug("Vote results are: " + positive_votes + " voted yes and: " + negative_votes + " voted no!" , showLogDebug);
				
				// Test which votes won
				if (positive_votes > negative_votes) {
					log.logDebug("Positive votes won! Changing to day!", showLogDebug);
					server.broadcastMessage(chat.chatInfo("'Yes' votes won! Changing to day!"));
					current_world.setTime(0);
				}
				else {
					log.logDebug("Negative votes won! Not changing to day!" , showLogDebug);
					server.broadcastMessage(chat.chatInfo("'No' votes won! Not changing to day!"));
				}
				
				// Reset vote session
				vote_session = false;
				
				// Reset players votes
				for (int i = 0; i < players.length; i++) {
					if (voted.containsKey(players[i]) && votes.containsKey(players[i])) {
						voted.put(players[i], false);
						votes.put(players[i], false);
					}
					if (players[i] instanceof SpoutPlayer) {
						SpoutPlayer spoutplayer = (SpoutPlayer) players[i];
						spoutplayer.getMainScreen().closePopup();
					}
				}
				
			}} , 400L);
	}
}
