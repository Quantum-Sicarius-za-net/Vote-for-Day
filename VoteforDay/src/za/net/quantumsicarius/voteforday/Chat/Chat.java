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
