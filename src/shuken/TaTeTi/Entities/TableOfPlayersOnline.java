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


public class TableOfPlayersOnline implements Updateable, Renderable {

	public static final int CANT_OF_ROWS = 10;
	private static final int ROW_SEPARATION = 15;
	
	/** Place to be rendered */
	private Vector2 position;

	private ArrayList<PlayersOnlineRecord> rows;
	
	public TableOfPlayersOnline(float posX, float posY){
		position= new Vector2(posX, posY);
		rows= new ArrayList<PlayersOnlineRecord>(CANT_OF_ROWS);
	}

	@Override
	public void render(SpriteBatch batch, ShapeRenderer shapeRender) {
		ResourceManager.fonts.defaultFont.draw(batch, "Players Online", position.x, position.y + ROW_SEPARATION);
		
		int cantToRender= 10;
		if(rows.size() < 10) cantToRender= rows.size();
			
		for(int i= 0; i < cantToRender; i++){		
			ResourceManager.fonts.defaultFont.draw(batch, rows.get(i).toString(), position.x, position.y - (i*ROW_SEPARATION));
		}
		
		if(rows.size() > 10){
			ResourceManager.fonts.defaultFont.draw(batch, "+" + (rows.size()-10), position.x, position.y - (11*ROW_SEPARATION));
		}
		
		if(rows.size() == 0){
			ResourceManager.fonts.defaultFont.draw(batch, "No players available", position.x, position.y);
		}
		
	}

	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * No es posible agregar a la tabla al player local.
	 */
	public void addRow(String nick, String state){
		if(GameSession.getPlayer().getNick().compareToIgnoreCase(nick)== 0) return;
		
		rows.add(new PlayersOnlineRecord(nick, state));
	}
	
	public void clear(){
		rows.clear();
	}
	
	public void setPosition(float x, float y){
		this.position.set(x, y);
	}
}//end class
