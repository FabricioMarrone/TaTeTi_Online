package shuken.TaTeTi.Entities;

import java.util.ArrayList;

import shuken.Engine.Resources.ResourceManager;
import shuken.Engine.ShukenInput.ShukenInput;
import shuken.Engine.SimpleGUI.SimpleGUI;
import shuken.TaTeTi.GameSession;
import shuken.TaTeTi.Renderable;
import shuken.TaTeTi.TaTeTi;
import shuken.TaTeTi.Updateable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class TableOfPlayersOnline implements Updateable, Renderable {

	public static final int CANT_OF_ROWS = 10;
	private static final int ROW_SEPARATION = 15;
	
	/** Place to be rendered */
	private Vector2 position;

	private ArrayList<PlayersOnlineRecord> rows;
	private ArrayList<Rectangle> rowsRectangles;
	
	public TableOfPlayersOnline(float posX, float posY){
		position= new Vector2();
		rows= new ArrayList<PlayersOnlineRecord>(CANT_OF_ROWS);
		rowsRectangles= new ArrayList<Rectangle>(CANT_OF_ROWS);
		for(int i= 0; i < CANT_OF_ROWS; i++) rowsRectangles.add(new Rectangle());
		
		this.setPosition(posX, posY);
	}

	@Override
	public void render(SpriteBatch batch, ShapeRenderer shapeRender) {
		ResourceManager.fonts.UIlabelsFont.draw(batch, "Jugadores en línea", position.x, position.y + ROW_SEPARATION);
		
		batch.end();
		shapeRender.begin(ShapeType.Filled);
		shapeRender.rect(position.x, position.y - 4, 173, 1, new Color(0, 0, 0.8f, 1), Color.WHITE, Color.WHITE , new Color(0, 0, 0.5f, 1));
		shapeRender.end();
		batch.begin();
		
		int cantToRender= 10;
		if(rows.size() < 10) cantToRender= rows.size();
			
		for(int i= 0; i < cantToRender; i++){	
			//Graficamos con color segun estado
			if(rows.get(i).state.compareToIgnoreCase(Player.States.IDLE.toString())== 0) ResourceManager.fonts.gameText.setColor(0, 0.88f, 0, 1);
			if(rows.get(i).state.compareToIgnoreCase(Player.States.PLAYING.toString())== 0) ResourceManager.fonts.gameText.setColor(0.85f, 0, 0, 1);
			if(rows.get(i).state.compareToIgnoreCase(Player.States.RESPONDIENDO_SOLICITUD.toString())== 0 || rows.get(i).state.compareToIgnoreCase(Player.States.WAITING_FOR_OPPONENT.toString())== 0) ResourceManager.fonts.gameText.setColor(Color.YELLOW);
			
			//Si esta el mouse encima
			if(SimpleGUI.getInstance().isAreaUnderTheMouse(rowsRectangles.get(i))) ResourceManager.fonts.gameText.setColor(Color.GREEN);
			
			ResourceManager.fonts.gameText.draw(batch, rows.get(i).toString(), position.x, position.y - (i*ROW_SEPARATION) - 8);
			ResourceManager.fonts.gameText.setColor(Color.WHITE);
		}
		
		//Si hay mas de 10 players online...
		if(rows.size() > 10){
			ResourceManager.fonts.gameText.draw(batch, "+" + (rows.size()-10), position.x, position.y - (11*ROW_SEPARATION));
		}
		
		//Si no hay players online...
		if(rows.size() == 0){
			ResourceManager.fonts.gameText.draw(batch, "No hay jugadores disponibles", position.x, position.y - 10);
		}
		
		/*
		//Graficando rectangulos, testing only
		batch.end();
		shapeRender.begin(ShapeType.Line);
		for(int i= 0; i < CANT_OF_ROWS; i++){
			Rectangle r= rowsRectangles.get(i);
			shapeRender.rect(r.x, r.y, r.width, r.height);
		}
		shapeRender.end();
		batch.begin();
		*/
	}

	@Override
	public void update(float delta) {
		//Verificamos si se hizo algun click sobre la tabla
		if(ShukenInput.getInstance().hasBeenClicked(0)){
			float x= ShukenInput.getInstance().getClickX(0);
			float y= ShukenInput.getInstance().getClickY(0);
			for(int i= 0; i < CANT_OF_ROWS; i++){
				if(rowsRectangles.get(i).contains(x, y)){
					try{
						TaTeTi.getInstance().getMainMenuScreen().invitarAJugar(rows.get(i).nick);
						ShukenInput.getInstance().consumeClick(0);
						if(ResourceManager.isAudioOn()) ResourceManager.audio.select.play();
						
					}catch(IndexOutOfBoundsException e){ /* Do nothing */}
				}
			}
		}
		
	}//end update
	
	/**
	 * Nota: No es posible agregar a la tabla al player local.
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
		for(int i= 0; i < CANT_OF_ROWS; i++){
			rowsRectangles.get(i).set(position.x, position.y - (i*ROW_SEPARATION) - ROW_SEPARATION + 1 - 8, 150, ROW_SEPARATION-2);
		}
	}
}//end class
