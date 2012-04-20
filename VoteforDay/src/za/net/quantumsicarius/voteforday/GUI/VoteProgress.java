package za.net.quantumsicarius.voteforday.GUI;

import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

import za.net.quantumsicarius.voteforday.VoteforDay;

public class VoteProgress{
	
	private GenericContainer box = new GenericContainer();
	private GenericTexture backtexture = new GenericTexture("http://");
	private GenericLabel title = new GenericLabel();
	private GenericLabel progress_text_left = new GenericLabel();
	private GenericLabel progress_text_right = new GenericLabel();
	
	private int yes_votes = 0;
	private int no_votes = 0;
	
	public VoteProgress(SpoutPlayer player, VoteforDay vfd) {
		
		backtexture.setAnchor(WidgetAnchor.CENTER_CENTER).setWidth(50).setHeight(50);
		
		title.setText("Vote Progress:");
		title.setAnchor(WidgetAnchor.TOP_CENTER);
		
		progress_text_left.setText("0");
		progress_text_left.setAnchor(WidgetAnchor.CENTER_LEFT);
		
		progress_text_right.setText("0");
		progress_text_right.setAnchor(WidgetAnchor.CENTER_RIGHT);
		
		box.addChildren(title, progress_text_left, progress_text_right);
		box.setX(45).setY(50).setWidth(45).setHeight(50);
		player.getMainScreen().attachWidget(vfd, box);
	}
	
	public void update(int progress_yes, int progress_no, SpoutPlayer player) {
		
		yes_votes = yes_votes + progress_yes;
		no_votes = no_votes + progress_no;
		
		progress_text_left.setText(Integer.toString(yes_votes) + " - Yes Votes");
		progress_text_right.setText(Integer.toString(no_votes) + " - No Votes");
		
		box.setVisible(true);
		
		player.getMainScreen().updateWidget(box);		
	}
	
	public void close_GUI(SpoutPlayer player) {
		box.setVisible(false);
		player.getMainScreen().updateWidget(box);
	}
	
	public void reset_GUI(SpoutPlayer player) {
		progress_text_left.setText("0");
		progress_text_right.setText("0");
		
		box.setVisible(true);
		
		player.getMainScreen().updateWidget(box);
	}
}
