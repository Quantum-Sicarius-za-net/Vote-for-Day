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

package za.net.quantumsicarius.voteforday.Logger;

import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;

import za.net.quantumsicarius.voteforday.Config.Config;

public class LogMain{
	
	// Define vfd (Plugin)
	Plugin vfd;
	
	// Create logger object
	private static final Logger log = Logger.getLogger("minecraft");
	
	// Define plugin prefix
	String pluginPrefix;
	
	// Define config object
	Config config;
	
	// Boolean to show debug
	private boolean active;
	
	public LogMain (Plugin plugin) {
		// Create prefix
		vfd = plugin;
		pluginPrefix = "[" + plugin.getName() + "] ";
		
	}
	
	// This method gets the config class so that it doesn't re-instantiate!
	public void initConfigClass(Config config_class) {
		config = config_class;
	}
	
	// Info log method
	public void logInfo(String string) {
		log.info(pluginPrefix + string);
	}
	
	// Debug log method
	public void logDebug(String string) {
		
		if (config != null) {
			active = config.getShowDebugLog();
		}
		
		// Check if config allowed this log
		if (active) {
			log.info(pluginPrefix + "[DEBUG] " + string);
		}
	}
	
	// Warning log method
	public void logWarning(String string) {
		log.warning(pluginPrefix + string);
	}
	
	// Severe log method
	public void logSevere(String string) {
		log.severe(pluginPrefix + string);
	}
}
