package za.net.quantumsicarius.voteforday.VoteStartHandler;

import java.util.HashMap;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import za.net.quantumsicarius.voteforday.Chat.Chat;
import za.net.quantumsicarius.voteforday.Config.Config;
import za.net.quantumsicarius.voteforday.Logger.LogMain;

public class VoteStartHandler {
	
	// Define vfd (Plugin)
	Plugin vfd;
	
	// Define chat object
	Chat chat;
	
	// Define logger object
	LogMain log;
	
	// Define configuration object
	Config config;
	
	// Define world object
	World current_world;
	
	// Create HashMaps
	HashMap <Player, Boolean> voted = new HashMap<Player, Boolean>();
	HashMap <Player, Boolean> votes = new HashMap<Player, Boolean>();	
	HashMap <World, Boolean> vote_session_world = new HashMap<World, Boolean>();

	public VoteStartHandler(Plugin plugin, Config config_class, LogMain log_class, Chat chat_class) {
		// Instantiate plugin object
		vfd = plugin;
		// Instantiate configuration object
		config = config_class;
		// Instantiate chat object
		chat = chat_class;
		// Instantiate log object
		log = log_class;
	}
	
	// Vote_session_world = World vote session active
	// Votes = Player's votes (yes,no)
	// Voted = Player has voted
	public boolean onCommand(Player player, HashMap<World, Boolean> hashmap_vote_session_world, HashMap<Player, Boolean> hashmap_votes, HashMap<Player, Boolean> hashmap_voted) {
		
		voted = hashmap_voted;
		votes = hashmap_votes;
		vote_session_world = hashmap_vote_session_world;
		
		if (player.hasPermission("") | player.isOp()) {
			if(testRunVote(player)) {
				// Check keys
				if (vote_session_world.containsKey(player.getWorld())) {
					// If current world is active ignore player's vote start
					if (vote_session_world.get(player.getWorld())) {
						player.sendMessage(chat.chatWarning("There is already an active vote!"));
					}
					else {
						log.logDebug("Player: " + player.getName() + " has started a vote!");
						player.sendMessage(chat.chatInfo("Started Vote!"));	
						
						vote_session_world.put(player.getWorld(), true);
						log.logDebug("Started a vote session in: " + player.getWorld().getName());
						
						return true;					
					}
				}
				else {
					log.logDebug("Player: " + player.getName() + " has started a vote!");
					player.sendMessage(chat.chatInfo("You Started the Vote!"));	
					
					vote_session_world.put(player.getWorld(), true);
					log.logDebug("Started a vote session in: " + player.getWorld().getName());
					
					return true;					
				}
			}
		}
		else {
			player.sendMessage(chat.chatSevere("You are not permitted to start a vote!"));
		}
		return false;
	}
	
	// Test to see if its night
	private boolean testRunVote(Player player) {
			
		current_world = player.getWorld();
			
		// Check if the world environment is allowed
		if(current_world.getEnvironment() == World.Environment.NORMAL) {
			if  (current_world.getTime() > config.getVoteAllowStartTime()) {
				return true;
			}
			else {
				log.logDebug("Player: " + player.getName() + " has tried to vote during day time!");
				player.sendMessage(chat.chatWarning("Can't vote when its day!"));
				return false;
			}
		}
		else {
			log.logDebug("Player: " + player.getName() + " has tried to start a vote in an illegal world environment! World Environment: " + current_world.getEnvironment());
			player.sendMessage(chat.chatSevere("You can only use this command in a Normal World!"));
			return false;
		}
	}
	
	public HashMap<Player, Boolean> voted() {
		return voted;
	}
	
	public HashMap<Player, Boolean> votes() {
		return votes;
	}
	
	public HashMap<World, Boolean> vote_session_world() {
		return vote_session_world;
	}
}
