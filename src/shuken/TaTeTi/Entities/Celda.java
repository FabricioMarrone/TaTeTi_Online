package shuken.TaTeTi.Entities;

import shuken.Engine.Resources.ResourceManager;
import shuken.TaTeTi.Renderable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Celda implements Renderable {

	private int nroCelda;
	
	/** Type of record contained in the cell. Can be null (when the cell is empty). */
	private Ficha contenidoDeLaCelda;
	
	/** Cell zone (used to detect clicks). */
	private Rectangle zone;
	
	public Celda(int nroCelda, int x, int y, int width, int height){
		this.nroCelda= nroCelda;
		zone= new Rectangle(x, y, width, height);
		contenidoDeLaCelda= null;
	}
	
	@Override
	public void render(SpriteBatch batch, ShapeRenderer shapeRender) {
		//Se grafica una textura u otra dependiendo del contenido de la celda...
		if(celdaContainsX()){
			batch.draw(ResourceManager.textures.cruz, zone.x, zone.y);
		}else if(celdaContainsO()){
			batch.draw(ResourceManager.textures.circulo, zone.x, zone.y);
		}
		
		/*
		//Only for testing
		batch.end();
		shapeRender.begin(ShapeType.Line);
		shapeRender.setColor(Color.GREEN);
		shapeRender.rect(zone.x, zone.y, zone.width, zone.height);
		shapeRender.setColor(Color.WHITE);
		shapeRender.end();
		batch.begin();
		*/
	}

	/**
	 * Sets the content of the cell. 
	 * @param f Can be null.
	 */
	public void setContenidoDeLaCelda(Ficha f){
		this.contenidoDeLaCelda= f;
	}
	
	public Ficha getContenidoDeLaCelda(){
		return contenidoDeLaCelda;
	}
	
	/**
	 * @return true if the cells contains a X.
	 */
	public boolean celdaContainsX(){
		if(contenidoDeLaCelda == null) return false;
		
		if(contenidoDeLaCelda == Ficha.CIRCULO) return false;
		
		if(contenidoDeLaCelda == Ficha.CRUZ) return true;
		
		System.err.println("Celda: Ha ocurrido un error al constatar el tipo de ficha que posee una celda.");
		return false;
	}
	
	/**
	 * @return true if the cell contains a O.
	 */
	public boolean celdaContainsO(){
		if(contenidoDeLaCelda == null) return false;
		
		if(contenidoDeLaCelda == Ficha.CRUZ) return false;
		
		if(contenidoDeLaCelda == Ficha.CIRCULO) return true;
		
		System.err.println("Celda: Ha ocurrido un error al constatar el tipo de ficha que posee una celda.");
		return false;
	}
	
	/**
	 * @return true if the cell contains the record f
	 */
	public boolean contains(Ficha ficha){
		switch(ficha){
		case CIRCULO:
			return this.celdaContainsO();
		case CRUZ:
			return this.celdaContainsX();
		default:
			return false;
		}
	}
	
	/**
	 * @return true if the cell is empty
	 */
	public boolean isCeldaEmpty(){
		if(contenidoDeLaCelda == null) return true;
		
		return false;
	}
	
	public void setLocation(int x, int y){
		zone.setPosition(x, y);
	}
	
	public Rectangle getZone(){
		return zone;
	}
	
	public int getNroCelda(){
		return nroCelda;
	}
}//end class
