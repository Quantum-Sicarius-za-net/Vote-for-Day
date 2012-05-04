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

package za.net.quantumsicarius.voteforday.GUI;

import org.bukkit.ChatColor;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.Container;
import org.getspout.spoutapi.gui.ContainerType;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

import za.net.quantumsicarius.voteforday.VoteforDay;
import za.net.quantumsicarius.voteforday.Language.LanguageHandler;


public class VoteGUI extends GenericPopup{
    
	protected VoteforDay vfd = (VoteforDay) getPlugin().getServer().getPluginManager().getPlugin("VoteforDay");
    
	LanguageHandler lang;
	
	public VoteGUI (SpoutPlayer player, LanguageHandler lang_class) {
		
		// Instantiate Language Handler
		lang = lang_class;
		
		// Create a new container
		Container box = new GenericContainer();
	    
		//Label
		String votestring = ChatColor.GREEN + lang.GUICastYourVote();
		GenericLabel label = new GenericLabel(votestring);
		label.setAnchor(WidgetAnchor.CENTER_CENTER);
	    
		// Yes Button
		GenericButton acceptButton = new GenericButton(lang.GUIYes());
		acceptButton.setAnchor(WidgetAnchor.CENTER_CENTER);
		acceptButton.setAlign(WidgetAnchor.CENTER_CENTER);
		acceptButton.setHoverColor(new Color(0, 255, 0));
	     
		// No Button
		GenericButton declineButton = new GenericButton(lang.GUINo());
		declineButton.setAnchor(WidgetAnchor.CENTER_CENTER);
		declineButton.setAlign(WidgetAnchor.CENTER_CENTER);
		declineButton.setHoverColor(new Color(255, 0, 0));
	     
		// Attach Buttons to container
		box.addChildren(label ,acceptButton, declineButton);
		box.setLayout(ContainerType.VERTICAL);
		box.setHeight(90);
		box.setWidth(120);
		box.setX(160);
		box.setY(70);
	    
		// Attach container to widget
		this.setTransparent(true);
		this.attachWidget(vfd, box);
		this.setDirty(true);
	}
	
	// Check button isAccept
    public boolean isAccept(Button button) {
    	if (button.getPlugin() == vfd && button.getText() == lang.GUIYes()) {
    		return true;
    	}
    	else {
    		return false;
    	}    	
    }
    
    // Check button isDecline
    public boolean isDecline(Button button) {
        if (button.getPlugin() == vfd && button.getText() == lang.GUINo()) {
        	return true;
        }
        else {
        	return false;
        }
    }
}
