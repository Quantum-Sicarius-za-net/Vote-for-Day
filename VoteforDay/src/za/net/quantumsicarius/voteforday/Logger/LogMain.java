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

public class LogMain{
	
	// Create logger object
	private static final Logger log = Logger.getLogger("minecraft");
	String pluginPrefix;
	
	public LogMain (String plugin) {
		// Create prefix
		pluginPrefix = "[" + plugin + "] ";					
	}
	
	// Info log method
	public void logInfo(String string) {
		log.info(pluginPrefix + string);
	}
	
	// Debug log method
	public void logDebug(String string, boolean active) {
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
