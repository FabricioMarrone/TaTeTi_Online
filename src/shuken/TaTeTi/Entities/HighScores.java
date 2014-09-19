package shuken.TaTeTi.Entities;

import java.util.ArrayList;

import shuken.Engine.Resources.ResourceManager;
import shuken.TaTeTi.GameSession;
import shuken.TaTeTi.Renderable;
import shuken.TaTeTi.Updateable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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
		ResourceManager.fonts.UIlabelsFont.draw(batch, "Mejores puntajes", position.x - 10, position.y + ROW_SEPARATION);
		ResourceManager.fonts.UIlabelsFont.setColor(0, 0.8f, 0, 1);
		ResourceManager.fonts.UIlabelsFont.draw(batch, "G", position.x + 168, position.y + ROW_SEPARATION);
		ResourceManager.fonts.UIlabelsFont.setColor(0.9f, 0.9f, 0, 1);
		ResourceManager.fonts.UIlabelsFont.draw(batch, "E", position.x + 199, position.y + ROW_SEPARATION);
		ResourceManager.fonts.UIlabelsFont.setColor(0.8f, 0, 0, 1);
		ResourceManager.fonts.UIlabelsFont.draw(batch, "P", position.x + 230, position.y + ROW_SEPARATION);
		ResourceManager.fonts.UIlabelsFont.setColor(Color.WHITE);
		
		
		batch.end();
		shapeRender.begin(ShapeType.Filled);
		shapeRender.rect(position.x - 10, position.y - 5, 255, 1, Color.WHITE, new Color(0, 0, 0.8f, 1) , new Color(0, 0, 0.8f, 1), Color.WHITE);
		shapeRender.end();
		batch.begin();
		
		boolean clientIsNotInHighScore= true;
		
		//for(int i= records.size()-1; i >= 0; i--){
		for(int i= 0; i < records.size(); i++){
			//Resaltamos player local
			if(GameSession.getPlayer().getNick().compareToIgnoreCase(records.get(i).nick)== 0){
				ResourceManager.fonts.gameText.setColor(0,  0.7f, 0.88f, 1);
				clientIsNotInHighScore= false;
			}
			
			ResourceManager.fonts.gameText.draw(batch, (i+1) + "- " + records.get(i).nick, position.x, position.y - (i*ROW_SEPARATION) - 10);
			ResourceManager.fonts.gameText.draw(batch, records.get(i).won + "", position.x + 170, position.y - (i*ROW_SEPARATION) - 10);
			ResourceManager.fonts.gameText.draw(batch, records.get(i).draw + "", position.x + 200, position.y - (i*ROW_SEPARATION) - 10);
			ResourceManager.fonts.gameText.draw(batch, records.get(i).lose + "", position.x + 230, position.y - (i*ROW_SEPARATION) - 10);
			ResourceManager.fonts.gameText.setColor(Color.WHITE);
		}
		
		if(clientIsNotInHighScore){
			ResourceManager.fonts.gameText.setColor(0.9f, 0, 0, 1);
			Player p= GameSession.getPlayer();
			ResourceManager.fonts.gameText.draw(batch, ">> " + p.getNick(), position.x, position.y - ((records.size()+1)*ROW_SEPARATION));
			ResourceManager.fonts.gameText.draw(batch, p.getGanados() + "", position.x + 170, position.y - ((records.size()+1)*ROW_SEPARATION));
			ResourceManager.fonts.gameText.draw(batch, p.getEmpatados() + "", position.x + 200, position.y - ((records.size()+1)*ROW_SEPARATION));
			ResourceManager.fonts.gameText.draw(batch, p.getPerdidos() + "", position.x + 230, position.y - ((records.size()+1)*ROW_SEPARATION));
			ResourceManager.fonts.gameText.setColor(Color.WHITE);
		}
	}

	@Override
	public void update(float delta) {}
	
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
