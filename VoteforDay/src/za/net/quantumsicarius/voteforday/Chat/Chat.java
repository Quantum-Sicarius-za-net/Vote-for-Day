package za.net.quantumsicarius.voteforday.Chat;

import org.bukkit.ChatColor;

public class Chat {
	
	String pluginPrefix;
	
	public Chat (String string) {
		pluginPrefix = ChatColor.GREEN + "[" + string + "] ";
	}
	
	public String chatInfo(String string) {
		return pluginPrefix + ChatColor.AQUA + string;	
	}
	
	public String chatWarning(String string) {
		return pluginPrefix + ChatColor.GOLD + string;	
	}
	
	public String chatSevere(String string) {
		return pluginPrefix + ChatColor.RED + string;	
	}
}
