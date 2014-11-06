package shuken.TaTeTi.Entities;

import shuken.Engine.Resources.ResourceManager;
import shuken.TaTeTi.Renderable;
import shuken.TaTeTi.Updateable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

/**
 * A Tic-Tac-Toe board contains 9 cells where can be a X or a O (or nothing).
 * 
 * The distribution is as follows:
 * 
 * 		-------------------------
 * 		|	1	|	2	|	3	|
 * 		-------------------------
 * 		|	4	|	5	|	6	|
 * 		-------------------------
 * 		|	7	|	8	|	9	|
 * 		-------------------------
 * 
 * 	The "position" attribute corresponds to the upper left corner of the board.
 * 
 * @author F. Marrone
 */
public class Tablero implements Renderable, Updateable{

	/** Cells */
	private Celda[] celdas;
	
	/** Board position */
	private GridPoint2 position;
	
	private static final int CELDA_WIDTH= 100;
	private static final int CELDA_HEIGHT= 100;
	
	private Color boardColor;
	
	/**
	 * Creates a new board.
	 */
	public Tablero(int posX, int posY){
		position= new GridPoint2(posX, posY);
		
		celdas= new Celda[10];
		celdas[1]= new Celda(1, position.x, position.y + (2*CELDA_HEIGHT), CELDA_WIDTH, CELDA_HEIGHT);
		celdas[2]= new Celda(2, position.x + CELDA_WIDTH, position.y + (2*CELDA_HEIGHT), CELDA_WIDTH, CELDA_HEIGHT);
		celdas[3]= new Celda(3, position.x + (2*CELDA_WIDTH), position.y + (2*CELDA_HEIGHT), CELDA_WIDTH, CELDA_HEIGHT);
		celdas[4]= new Celda(4, position.x, position.y + CELDA_HEIGHT, CELDA_WIDTH, CELDA_HEIGHT);
		celdas[5]= new Celda(5, position.x + CELDA_WIDTH, position.y + CELDA_HEIGHT, CELDA_WIDTH, CELDA_HEIGHT);
		celdas[6]= new Celda(6, position.x + (2*CELDA_WIDTH), position.y + CELDA_HEIGHT, CELDA_WIDTH, CELDA_HEIGHT);
		celdas[7]= new Celda(7, position.x, position.y, CELDA_WIDTH, CELDA_HEIGHT);
		celdas[8]= new Celda(8, position.x + CELDA_WIDTH, position.y, CELDA_WIDTH, CELDA_HEIGHT);
		celdas[9]= new Celda(9, position.x + (2*CELDA_WIDTH), position.y, CELDA_WIDTH, CELDA_HEIGHT);

		boardColor= new Color(Color.WHITE);
	}
	
	@Override
	public void update(float delta) {
		//Obtenemos la posicion actual del mouse
		//float x= Gdx.input.getX();
		//float y= Gdx.input.getY();
		Vector2 mouse= new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
		//Vector2 center= new Vector2(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		
		//El ángulo determina r
		float r= 1- mouse.y/Gdx.graphics.getHeight();
		//El cateto vertical determina g
		float g= 1- mouse.x/Gdx.graphics.getWidth();
		//El cateto horizontal determina b
		float b= r+g;
		
		if(r < 0.1f) r= 0.1f;
		if(g < 0.1f) g= 0.1f;
		boardColor.set(r, g, b, 1);
	}

	@Override
	public void render(SpriteBatch batch, ShapeRenderer shapeRender) {
		batch.setColor(boardColor);
		
		//Se grafica el tablero...
		batch.draw(ResourceManager.textures.tablero, position.x, position.y);
		
		batch.setColor(Color.WHITE);
		
		//Se grafican las fichas jugadas (si hay alguna)...
		for(int i= 1; i < 10; i++) celdas[i].render(batch, shapeRender);
	}

	public void renderWinner(SpriteBatch batch, ShapeRenderer shapeRender, Ficha ficha){
		if(ficha == Ficha.CIRCULO) batch.setColor(0, 1, 0, 0.9f);
		if(ficha == Ficha.CRUZ) batch.setColor(0, 0, 1, 0.9f);
		
		//-1
		if(celdas[1].contains(ficha) && celdas[4].contains(ficha) && celdas[7].contains(ficha)) batch.draw(ResourceManager.textures.lineV, position.x + 20, position.y + 22);
		//-2
		if(celdas[2].contains(ficha) && celdas[5].contains(ficha) && celdas[8].contains(ficha)) batch.draw(ResourceManager.textures.lineV, position.x + 120, position.y + 22);
		//-3
		if(celdas[3].contains(ficha) && celdas[6].contains(ficha) && celdas[9].contains(ficha)) batch.draw(ResourceManager.textures.lineV, position.x + 220, position.y + 22);
		//-4
		if(celdas[1].contains(ficha) && celdas[2].contains(ficha) && celdas[3].contains(ficha)) batch.draw(ResourceManager.textures.lineH, position.x + 20, position.y + 218);
		//-5
		if(celdas[4].contains(ficha) && celdas[5].contains(ficha) && celdas[6].contains(ficha)) batch.draw(ResourceManager.textures.lineH, position.x + 20, position.y + 118);
		//-6
		if(celdas[7].contains(ficha) && celdas[8].contains(ficha) && celdas[9].contains(ficha)) batch.draw(ResourceManager.textures.lineH, position.x + 20, position.y + 20);
		//-7
		if(celdas[3].contains(ficha) && celdas[5].contains(ficha) && celdas[7].contains(ficha)) batch.draw(ResourceManager.textures.lineD1, position.x, position.y + 10);
		//-8
		if(celdas[1].contains(ficha) && celdas[5].contains(ficha) && celdas[9].contains(ficha)) batch.draw(ResourceManager.textures.lineD2, position.x + 10, position.y);
		
		batch.setColor(Color.WHITE);
	}
	
	/**
	 * @return true if all the cells are taken.
	 */
	public boolean isFull(){
		boolean flag= true;
		for(int i= 1; i < 10; i++){
			if(celdas[i].isCeldaEmpty()) flag= false;
		}
		return flag;
	}
	
	/**
	 * Sets the position of the board. This also impacts on the "clickable zones" of the board.
	 * @param x new position x
	 * @param y new position y
	 */
	public void setPosition(int x, int y){
		this.position.set(x, y);
		
		celdas[1].setLocation(position.x, position.y + (2*CELDA_HEIGHT));
		celdas[2].setLocation(position.x + CELDA_WIDTH, position.y + (2*CELDA_HEIGHT));
		celdas[3].setLocation(position.x + (2*CELDA_WIDTH), position.y + (2*CELDA_HEIGHT));
		celdas[4].setLocation(position.x, position.y + CELDA_HEIGHT);
		celdas[5].setLocation(position.x + CELDA_WIDTH, position.y + CELDA_HEIGHT);
		celdas[6].setLocation(position.x + (2*CELDA_WIDTH), position.y + CELDA_HEIGHT);
		celdas[7].setLocation(position.x, position.y);
		celdas[8].setLocation(position.x + CELDA_WIDTH, position.y);
		celdas[9].setLocation(position.x + (2*CELDA_WIDTH), position.y);
	}
	
	/**
	 * Puts the record on the cell. if the cell is not empty, this method fails.
	 * @param nroCelda
	 * @param f
	 * @return true if the record has been properly added to the board.
	 */
	public boolean putFichaOnCelda(int nroCelda, Ficha f){
		if(f == null) return false;
		if(celdas[nroCelda].isCeldaEmpty()){
			celdas[nroCelda].setContenidoDeLaCelda(f);
			return true;
		}
		return false;	//la celda ya contiene una ficha
	}
	
	/**
	 * Given two coords (could be mouse position, for example), this method returns the cell that is under the coords (if any).
	 * @param coordX
	 * @param coordY
	 * @return a cell (might be null)
	 */
	public Celda getCelda(float coordX, float coordY){
		for(int i= 1; i < 10; i++){
			if(celdas[i].getZone().contains(coordX, coordY)) return celdas[i];
		}
		return null;
	}
	
	/**
	 * Removes all the records from the board.
	 */
	public void clear(){
		for(int i= 1; i < 10; i++) celdas[i].setContenidoDeLaCelda(null);
	}
	
	public boolean checkWinner(Ficha ficha){
		//Analizamos las 8 posibilidades de ganar (ver documentacion). Con que una se cumpla ya es suficiente.
		//-1
		if(celdas[1].contains(ficha) && celdas[4].contains(ficha) && celdas[7].contains(ficha)) return true;
		//-2
		if(celdas[2].contains(ficha) && celdas[5].contains(ficha) && celdas[8].contains(ficha)) return true;
		//-3
		if(celdas[3].contains(ficha) && celdas[6].contains(ficha) && celdas[9].contains(ficha)) return true;
		//-4
		if(celdas[1].contains(ficha) && celdas[2].contains(ficha) && celdas[3].contains(ficha)) return true;
		//-5
		if(celdas[4].contains(ficha) && celdas[5].contains(ficha) && celdas[6].contains(ficha)) return true;
		//-6
		if(celdas[7].contains(ficha) && celdas[8].contains(ficha) && celdas[9].contains(ficha)) return true;
		//-7
		if(celdas[3].contains(ficha) && celdas[5].contains(ficha) && celdas[7].contains(ficha)) return true;
		//-8
		if(celdas[1].contains(ficha) && celdas[5].contains(ficha) && celdas[9].contains(ficha)) return true;
		
		return false;
	}//end check winner
}//end class
