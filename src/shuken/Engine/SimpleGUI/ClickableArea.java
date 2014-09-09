package shuken.Engine.SimpleGUI;

import shuken.Engine.Resources.ResourceManager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

/**
 * Un area clickeable es un sector de la pantalla que, en caso de recibir un click del mouse, avisa al gameplay de turno del click.
 * 
 * @author Shuken
 *
 */
public abstract class ClickableArea {

	protected String label;
	public Rectangle zone;
	protected boolean active;
	
	
	/** Indica si esta area consume o no clicks cuando hay clicks sobre ella. */
	public boolean consumeClick= true;
	
	/**
	 * Crea una nueva zona clickable activa a partir de un rectangulo.
	 * @param zone
	 */
	public ClickableArea(Rectangle zone){
		this.zone= new Rectangle(zone.x, zone.y, zone.width, zone.height);
		active= true;
		label= "";
	}
	
	
	
	public abstract void render(SpriteBatch batch);
		
	public abstract void update(float delta);
	
	/**
	 * Grafica la zona de la ClickableArea (un rectangulo blanco).
	 * @param shapeRender
	 */
	public void renderZone(ShapeRenderer shapeRender){
		shapeRender.rect(zone.x, zone.y, zone.width, zone.height);
	}
	
	public void setLocation(float x, float y){
		zone.setPosition(x, y);
	}
	
	public void setLabel(String lbl){
		this.label= lbl;
	}
	
	/**
	 * Devuelve true si las coordenadas pasadas como parametro estan por encima del componente.
	 * @param coordX
	 * @param coordY
	 * @return
	 */
	public boolean coordOverArea(float coordX, float coordY){
		return zone.contains(coordX, coordY);
	}
	
	/**
	 * Este metodo es llamado por la SimpleGUI cuando recae un click sobre esta area.
	 */
	public void clickOn(){
		//Sub-clases must override this method
	}
	
	/**
	 * Este metodo es llamado por la SimpleGUI cuando NO recae un click sobre esta area.
	 */
	public void clickOff(){
		//Sub-clases must override this method
	}
	
	public void setActive(){
		active= true;
	}
	public void setDesActive(){
		active= false;
	}
}//fin clase
