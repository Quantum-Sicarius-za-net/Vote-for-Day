package za.net.quantumsicarius.voteforday;

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

public class VoteGUI extends GenericPopup{
    
    protected VoteforDay vfd = (VoteforDay) getPlugin().getServer().getPluginManager().getPlugin("VoteforDay");
    
	public VoteGUI (SpoutPlayer player) {
		
		// Create a new container
		Container box = new GenericContainer();
		
		int widthScale = player.getMainScreen().getWidth() / 10;
	    int heightScale = player.getMainScreen().getHeight() / 5;
	    
	    //Label
	    String votestring = ChatColor.GREEN + "Cast your vote!";
	    GenericLabel label = new GenericLabel(votestring);
	    label.setAnchor(WidgetAnchor.CENTER_CENTER);
	    
	    // Yes Button
	    GenericButton acceptButton = new GenericButton("Yes");
	    acceptButton.setAnchor(WidgetAnchor.CENTER_CENTER);
	    acceptButton.setAlign(WidgetAnchor.CENTER_CENTER);
	    acceptButton.setHoverColor(new Color(0, 255, 0));
	     
	    // No Button
	    GenericButton declineButton = new GenericButton("No");
	    declineButton.setAnchor(WidgetAnchor.CENTER_CENTER);
	    declineButton.setAlign(WidgetAnchor.CENTER_CENTER);
	    declineButton.setHoverColor(new Color(255, 0, 0));
	     
	    // Attach Buttons to container
	    box.addChildren(label ,acceptButton, declineButton);
	    box.setLayout(ContainerType.VERTICAL);
	    box.setAnchor(WidgetAnchor.CENTER_CENTER);
	    box.setWidth(widthScale).setHeight(heightScale);
        //box.shiftYPos(40);
        //box.shiftXPos(-acceptButton.getWidth());
	    
	    // Attach container to widget
        this.setTransparent(true);
	    this.attachWidget(vfd, box);
        this.setDirty(true);
	}
	
	// Check button isAccept
    public boolean isAccept(Button button) {
    	if (button.getText().equalsIgnoreCase("yes")) {
    		return true;
    	}
    	else {
    		return false;
    	}    	
    }
    
    // Check button isDecline
    public boolean isDecline(Button button) {
        if (button.getText().equalsIgnoreCase("no")) {
        	return true;
        }
        else {
        	return false;
        }
    }
}
