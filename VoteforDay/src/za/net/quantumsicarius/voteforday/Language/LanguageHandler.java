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

package za.net.quantumsicarius.voteforday.Language;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import za.net.quantumsicarius.voteforday.Config.Config;
import za.net.quantumsicarius.voteforday.Logger.LogMain;

public class LanguageHandler {
	
	// Define plugin object
	Plugin vfd;
	
	// Define file
	File file;
	
	// Define FileConfig
	FileConfiguration yml;
	
	// Default file readers
	InputStream defConfigStream;
	
	YamlConfiguration defConfig;
	
	// Define arraylist
	ArrayList<String> list;
	
	@SuppressWarnings("unchecked")
	public LanguageHandler(Config config, LogMain log, Plugin plugin){
		
		// Instantiate vdf
		vfd = plugin;
		
		// Create YMLConfig
		yml = new YamlConfiguration();
		
		// Instantiate file
		file = new File("plugins" + File.separator + "VoteforDay" + File.separator + "Translations" + File.separator + config.getLanguage() + ".yml");
		
		if(!file.exists()) {
			log.logInfo("Creating new Language file!");
			log.logDebug("File path: " + file);
			
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			
			try {			
				OutputStream out = new FileOutputStream(file);				
				
				byte buf[]=new byte[1024];
				int len;
				
				defConfigStream = vfd.getResource("Translations" + File.separator + config.getLanguage() +".yml");
				
				if (defConfigStream == null) {
					defConfigStream = vfd.getResource("Translations" + File.separator + "English.yml");
				}
				
				while((len = defConfigStream.read(buf)) >0) {
					out.write(buf,0,len);
				}
				
				out.close();
				defConfigStream.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			log.logInfo("Found Language file: " + config.getLanguage());
		}
		
		
		try {
			yml.load(file);
			list = (ArrayList<String>) yml.getList("main");
			if (list.toArray().length <= 21) {
				log.logSevere("The Language file is invalid, using English!");
				File newfile = new File("plugins" + File.separator + "VoteforDay" + File.separator + "Translations" + File.separator + "English.yml");
				if (!newfile.exists()) {
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					
					try {			
						OutputStream out = new FileOutputStream(file);				
						
						byte buf[]=new byte[1024];
						int len;
						
						defConfigStream = vfd.getResource("Translations" + File.separator + "English.yml");
						
						while((len = defConfigStream.read(buf)) >0) {
							out.write(buf,0,len);
						}
						
						out.close();
						defConfigStream.close();

					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				yml.load(newfile);
				list = (ArrayList<String>) yml.getList("main");
				
			}
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
	}
	// Plugin enable
	public String enable() {
		return (String) list.toArray()[0];
	}
	// Plugin disable
	public String disable() {
		return (String) list.toArray()[1];
	}
	// Already started a vote
	public String voteAlreadyStarted() {
		return (String) list.toArray()[2];
		
	}
	// You started a vote
	public String YouStartedTheVote() {
		return (String) list.toArray()[3];
		
	}
	// Already active vote session
	public String AlreadyActiveVoteSession() {
		return (String) list.toArray()[4];
		
	}
	// Don't have permission
	public String NoPermissionToStartAVote() {
		return (String) list.toArray()[5];
		
	}
	// Vote during day
	public String canNotVoteDuringDay() {
		return (String) list.toArray()[6];
		
	}
	// Normal World Voting only
	public String canOnlyVoteOnNormalWorld() {
		return (String) list.toArray()[7];
		
	}
	public String votedYes() {
		return (String) list.toArray()[8];
		
	}
	public String votedNo() {
		return (String) list.toArray()[9];
		
	}
	public String youHaveAlreadyVoted() {
		return (String) list.toArray()[10];
		
	}
	public String YouDoNotHavePermissionToVote() {
		return (String) list.toArray()[11];
		
	}
	public String IllegalVoteParam() {
		return (String) list.toArray()[12];
		
	}
	public String NoActiveVoteSession() {
		return (String) list.toArray()[13];
		
	}
	public String StartVoting() {
		return (String) list.toArray()[14];
		
	}
	public String VoteCompletedOnWorld() {
		return (String) list.toArray()[15];
		
	}
	public String YESVoteWon() {
		return (String) list.toArray()[16];
		
	}
	public String NOVoteWon() {
		return (String) list.toArray()[17];
		
	}
	public String GUICastYourVote() {
		return (String) list.toArray()[18];
		
	}
	public String GUIYes() {
		return (String) list.toArray()[19];
		
	}
	public String GUINo() {
		return (String) list.toArray()[20];
		
	}
	public String GUIProgressYesVotes() {
		return (String) list.toArray()[21];
		
	}
	public String GUIProgressNoVotes() {
		return (String) list.toArray()[22];
		
	}
}
