package shuken.Engine.SimpleGUI;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * A clickable area is a sector of the screen that, in case of a mouse click above it, tells to the actual screen about it.
 * @author F.Marrone
 *
 */
public abstract class ClickableArea {

	protected String label;
	public Rectangle zone;
	protected boolean active;
	
	
	/** If muts or not consume the click. */
	public boolean consumeClick= true;
	
	/**
	 * Creates a new clickable area from a rectangle.
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
	 * Renders the borders of the area (white rectangle). Test only.
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
	 * @return true if the coords are over the area
	 */
	public boolean coordOverArea(float coordX, float coordY){
		return zone.contains(coordX, coordY);
	}
	
	/**
	 * This method is called by the SimpleGUI when a click falls into this area.
	 */
	public void clickOn(){
		//Sub-clases must override this method
	}
	
	/**
	 * This method is called by the SimpleGUI when a click do not falls into this area.
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
