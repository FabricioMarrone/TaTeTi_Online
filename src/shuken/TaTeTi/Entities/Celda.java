package shuken.TaTeTi.Entities;

import shuken.Engine.Resources.ResourceManager;
import shuken.TaTeTi.Renderable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Celda implements Renderable {

	private int nroCelda;
	
	/** Tipo de ficha contenida en la celda. Si la celda esta vacia, esto vale null. */
	private Ficha contenidoDeLaCelda;
	
	/** Zona del rectangulo. Esto es usado para detectar clicks sobre la celda. */
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
	 * Establece cual es el contenido de la celda. Se admite null.
	 * @param f
	 */
	public void setContenidoDeLaCelda(Ficha f){
		this.contenidoDeLaCelda= f;
	}
	
	/**
	 * Devuelve el tipo de ficha en la celda. Ojo que este valor puede ser null en caso de que este vacia.
	 * @return
	 */
	public Ficha getContenidoDeLaCelda(){
		return contenidoDeLaCelda;
	}
	
	/**
	 * Devuelve true si la celda contiene una ficha del tipo CRUZ
	 * @return
	 */
	public boolean celdaContainsX(){
		if(contenidoDeLaCelda == null) return false;
		
		if(contenidoDeLaCelda == Ficha.CIRCULO) return false;
		
		if(contenidoDeLaCelda == Ficha.CRUZ) return true;
		
		System.err.println("Celda: Ha ocurrido un error al constatar el tipo de ficha que posee una celda.");
		return false;
	}
	
	/**
	 * Devuelve true si la celda contiene una ficha del tipo CIRCULO
	 * @return
	 */
	public boolean celdaContainsO(){
		if(contenidoDeLaCelda == null) return false;
		
		if(contenidoDeLaCelda == Ficha.CRUZ) return false;
		
		if(contenidoDeLaCelda == Ficha.CIRCULO) return true;
		
		System.err.println("Celda: Ha ocurrido un error al constatar el tipo de ficha que posee una celda.");
		return false;
	}
	
	
	/**
	 * Devuelve true si la celda contiene la ficha especificada.
	 * @param f
	 * @return
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
	 * Devuelve true si el contenido de la celda no es ni CRUZ ni CIRCULO. Es decir, si es null.
	 * @return
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
}//fin clase
