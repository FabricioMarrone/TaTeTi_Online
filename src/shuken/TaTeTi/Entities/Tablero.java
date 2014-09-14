package shuken.TaTeTi.Entities;

import shuken.Engine.Resources.ResourceManager;
import shuken.TaTeTi.Renderable;
import shuken.TaTeTi.Updateable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;


/**
 * Un tablero de tateti contiene 9 celdas donde puede existir una CRUZ o un CIRCULO o ninguno de los anteriores.
 * 
 * Las celdas se ubican de la siguiente manera:
 * 
 * 		-------------------------
 * 		|	1	|	2	|	3	|
 * 		-------------------------
 * 		|	4	|	5	|	6	|
 * 		-------------------------
 * 		|	7	|	8	|	9	|
 * 		-------------------------
 * 
 * La "posicion" del tablero corresponde a la esquina inferior izquierda.
 * 	
 * @author F. Marrone
 *
 */
public class Tablero implements Renderable, Updateable{

	/** Celdas del tablero. */
	private Celda[] celdas;
	
	/** Posicion del tablero en pantalla. Determina donde se grafica pero ademas, dónde se hacen los clicks. */
	private GridPoint2 position;
	
	private static final int CELDA_WIDTH= 100;
	private static final int CELDA_HEIGHT= 100;
	
	/**
	 * Crea un nuevo tablero con todas sus celdas libres.
	 */
	public Tablero(int posX, int posY){
		position= new GridPoint2(posX, posY);
		
		celdas= new Celda[10];
		//for(int i= 1; i < 10; i++) celdas[i]= new Celda();
		celdas[1]= new Celda(1, position.x, position.y + (2*CELDA_HEIGHT), CELDA_WIDTH, CELDA_HEIGHT);
		celdas[2]= new Celda(2, position.x + CELDA_WIDTH, position.y + (2*CELDA_HEIGHT), CELDA_WIDTH, CELDA_HEIGHT);
		celdas[3]= new Celda(3, position.x + (2*CELDA_WIDTH), position.y + (2*CELDA_HEIGHT), CELDA_WIDTH, CELDA_HEIGHT);
		celdas[4]= new Celda(4, position.x, position.y + CELDA_HEIGHT, CELDA_WIDTH, CELDA_HEIGHT);
		celdas[5]= new Celda(5, position.x + CELDA_WIDTH, position.y + CELDA_HEIGHT, CELDA_WIDTH, CELDA_HEIGHT);
		celdas[6]= new Celda(6, position.x + (2*CELDA_WIDTH), position.y + CELDA_HEIGHT, CELDA_WIDTH, CELDA_HEIGHT);
		celdas[7]= new Celda(7, position.x, position.y, CELDA_WIDTH, CELDA_HEIGHT);
		celdas[8]= new Celda(8, position.x + CELDA_WIDTH, position.y, CELDA_WIDTH, CELDA_HEIGHT);
		celdas[9]= new Celda(9, position.x + (2*CELDA_WIDTH), position.y, CELDA_WIDTH, CELDA_HEIGHT);
		
		//Metemos fichas a mano
		//celdas[2].setContenidoDeLaCelda(Ficha.CRUZ);
		//celdas[3].setContenidoDeLaCelda(Ficha.CRUZ);
		//celdas[8].setContenidoDeLaCelda(Ficha.CIRCULO);
	}
	
	
	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(SpriteBatch batch, ShapeRenderer shapeRender) {
		//Se grafica el tablero...
		batch.draw(ResourceManager.textures.tablero, position.x, position.y);
		//Se grafican las fichas jugadas (si hay alguna)...
		for(int i= 1; i < 10; i++) celdas[i].render(batch, shapeRender);
	}

	
	/**
	 * Devuelve true si todas las celdas del tablero se encuentran ocupadas con fichas.
	 * @return
	 */
	public boolean isFull(){
		boolean flag= true;
		for(int i= 1; i < 10; i++){
			if(celdas[i].isCeldaEmpty()) flag= false;
		}
		
		return flag;
	}
	
	/**
	 * Establece la posicion en pantalla del tablero. Esto tiene implicancia en el render del tablero como en la deteccion de clicks del
	 * jugador.
	 * @param x
	 * @param y
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
	 * Coloca una ficha en la celda especificada. No se permite null (una ficha una vez puesta no puede retirarse). Solamente pueden colocarse
	 * fichas en una celda si la misma esta vacia.
	 * Este metodo devuelve true si se ha podido colocar la ficha en la celda. False otherwise.
	 * @param nroCelda
	 * @param f
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
	 * Dadas unas coordenadas (x,y) se verifica si recae sobre alguna de las celdas del tablero, en cuyo caso devuelve la celda (la primera
	 * que encuentre). Si las coordenadas no recaen sobre ninguna celda, este metodo devuelve null.
	 * @param coordX
	 * @param coordY
	 * @return
	 */
	public Celda getCelda(float coordX, float coordY){
		for(int i= 1; i < 10; i++){
			if(celdas[i].getZone().contains(coordX, coordY)) return celdas[i];
		}
		
		return null;
	}
	
	/**
	 * Quita todas las fichas del tablero.
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
	}//fin check winner
	
	
	
}//fin clase
