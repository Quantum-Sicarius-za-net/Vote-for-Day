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

package za.net.quantumsicarius.voteforday.Config;

import java.io.File;

import org.bukkit.plugin.Plugin;

import za.net.quantumsicarius.voteforday.Logger.LogMain;

public class Config {
	
	// Create logger object
	LogMain log = null;
	
	// Config File
	File file = new File("plugins" + File.separator + "VoteforDay" + File.separator + "config.yml");
	
	// Define plugin object
	Plugin vfd;
	
	// Constructor
	public Config(Plugin plugin , LogMain log_main) {
		
		// Instantiate vdf
		vfd = plugin;
		// Instantiate log
		log = log_main;
		
		// Check for config file!
		if (file.exists()) {
			log.logInfo("Found config file!");
			if (!vfd.getConfig().getString("configFileVersion").trim().equals(plugin.getDescription().getVersion())) {
				log.logInfo("Found a version change, Updating config file!");
				vfd.getConfig().set("configFileVersion", vfd.getDescription().getVersion());
				vfd.getConfig().options().copyDefaults(true);
				vfd.saveConfig();
			}
		}
		else {
			log.logWarning("Didn't find config file! Creating!");
			plugin.getConfig().options().copyDefaults(true);
			plugin.saveConfig();
		}
	}
	
	// Get the vote start time
	public int getVoteAllowStartTime() {
		return vfd.getConfig().getInt("voteAllowStartTime");
	}
	
	// Get the boolean of automatic vote
	public boolean getForceVoteAtNight() {
		return vfd.getConfig().getBoolean("forceVoteAtNight");
	}
	
	// Get the boolean of debug log
	public boolean getShowDebugLog() {
		return vfd.getConfig().getBoolean("showDebugLog");
		
	}
	
	// Get vote session time (Multiply by 20 ticks, since there is 20 ticks in a second!)
	public int getVoteSessionTime() {
		return vfd.getConfig().getInt("voteSessionTime") * 20;
	}
	
	// Get key that starts a vote session
	public String getKeyBinding() {
		return vfd.getConfig().getString("keyBinding");
	}
	
	// Get the language to use
	public String getLanguage() {
		return vfd.getConfig().getString("language");
	}
	
}
