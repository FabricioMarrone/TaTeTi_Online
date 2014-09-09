package shuken.TaTeTi.Entities;

import java.util.ArrayList;

import shuken.Engine.Resources.ResourceManager;
import shuken.TaTeTi.GameSession;
import shuken.TaTeTi.Renderable;
import shuken.TaTeTi.Updateable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class HighScores implements Updateable, Renderable{

	public static final int CANT_OF_ROWS = 10;
	private static final int ROW_SEPARATION = 15;
	
	/** Place to be rendered */
	private Vector2 position;

	private ArrayList<HighScoreRecord> records;
	
	
	public HighScores(float posX, float posY){
		position= new Vector2(posX, posY);
		records= new ArrayList<HighScoreRecord>(CANT_OF_ROWS);
	}
	
	
	@Override
	public void render(SpriteBatch batch, ShapeRenderer shapeRender) {
		ResourceManager.fonts.defaultFont.draw(batch, "HIGHSCORES", position.x, position.y + ROW_SEPARATION);
		
		boolean clientIsNotInHighScore= true;
		
		//for(int i= records.size()-1; i >= 0; i--){
		for(int i= 0; i < records.size(); i++){
			//Resaltamos player local
			if(GameSession.getPlayer().getNick().compareToIgnoreCase(records.get(i).nick)== 0){
				ResourceManager.fonts.defaultFont.setColor(Color.MAGENTA);
				clientIsNotInHighScore= false;
			}
			
			ResourceManager.fonts.defaultFont.draw(batch, (i+1) + "- " + records.get(i).record, position.x, position.y - (i*ROW_SEPARATION));
			
			ResourceManager.fonts.defaultFont.setColor(Color.WHITE);
		}
		
		if(clientIsNotInHighScore){
			ResourceManager.fonts.defaultFont.setColor(Color.MAGENTA);
			Player p= GameSession.getPlayer();
			String r= HighScoreRecord.getRecord(p.getNick(), p.getGanados(), p.getPerdidos(), p.getEmpatados());
			ResourceManager.fonts.defaultFont.draw(batch, "-->" + r, position.x, position.y - ((records.size()+1)*ROW_SEPARATION));
			ResourceManager.fonts.defaultFont.setColor(Color.WHITE);
		}
	}

	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub
		
	}
	
	public void addRecord(String nick, int won, int lose, int draw){
		records.add(new HighScoreRecord(nick, won, lose, draw));
	}
	
	public void clear(){
		records.clear();
	}
	
	public void setPosition(float x, float y){
		this.position.set(x, y);
	}
}//end class
