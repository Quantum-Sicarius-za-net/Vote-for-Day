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

import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

import za.net.quantumsicarius.voteforday.VoteforDay;
import za.net.quantumsicarius.voteforday.Language.LanguageHandler;

public class VoteProgress{
	
	private GenericContainer box = new GenericContainer();
	private GenericTexture backtexture = new GenericTexture("http://");
	private GenericLabel title = new GenericLabel();
	private GenericLabel progress_text_left = new GenericLabel();
	private GenericLabel progress_text_right = new GenericLabel();
	
	private int yes_votes = 0;
	private int no_votes = 0;
	
	LanguageHandler lang;
	
	public VoteProgress(SpoutPlayer player, VoteforDay vfd, LanguageHandler lang_class) {
		
		lang = lang_class;
		
		backtexture.setAnchor(WidgetAnchor.CENTER_CENTER).setWidth(50).setHeight(50);
		
		title.setText("Vote Progress:");
		title.setAnchor(WidgetAnchor.TOP_CENTER);
		
		progress_text_left.setText("0 - " + lang.GUIProgressYesVotes());
		progress_text_left.setAnchor(WidgetAnchor.CENTER_LEFT);
		
		progress_text_right.setText("0 - " + lang.GUIProgressNoVotes());
		progress_text_right.setAnchor(WidgetAnchor.CENTER_RIGHT);
		
		box.addChildren(title, progress_text_left, progress_text_right);
		box.setX(45).setY(50).setWidth(45).setHeight(50);
		player.getMainScreen().attachWidget(vfd, box);
	}
	
	public void update(int progress_yes, int progress_no, SpoutPlayer player) {
		
		yes_votes = yes_votes + progress_yes;
		no_votes = no_votes + progress_no;
		
		progress_text_left.setText(Integer.toString(yes_votes) + " - " + lang.GUIProgressYesVotes());
		progress_text_right.setText(Integer.toString(no_votes) + " - " + lang.GUIProgressNoVotes());
		
		box.setVisible(true);
		
		player.getMainScreen().updateWidget(box);		
	}
	
	public void close_GUI(SpoutPlayer player) {
		box.setVisible(false);
		player.getMainScreen().updateWidget(box);
	}
	
	public void reset_GUI(SpoutPlayer player) {
		progress_text_left.setText("0 - " + lang.GUIProgressYesVotes());
		progress_text_right.setText("0 - " + lang.GUIProgressNoVotes());
		
		box.setVisible(true);
		
		player.getMainScreen().updateWidget(box);
	}
}
