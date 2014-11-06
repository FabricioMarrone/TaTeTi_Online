package shuken.Engine.SimpleGUI;

import java.util.ArrayList;

import shuken.Engine.Basic.ShukenGame;
import shuken.Engine.Resources.ResourceManager;
import shuken.Engine.ShukenInput.ShukenInput;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

/**
 * A simple GUI its a class that contains a list of ClickableAreas. When a click falls inside one of those areas, tells the actual screen about it.
 * The SimpleGUI works with ShukenInput.
 * @author F.Marrone
 *
 */
public class SimpleGUI {

	private static SimpleGUI instance= null;
	
	/** List of areas on the GUI. */
	protected ArrayList<ClickableArea> areas;
	
	/** Error message for general purpose. */
	public SimpleErrorMessage errorMsg;
	
	protected Rectangle auxRect;	//aux (en lugar de usar pool usamos 1 y listo)
	
	private SimpleGUI(){
		areas= new ArrayList<ClickableArea>();
		auxRect= new Rectangle();
		
		ArrayList<String> text= new ArrayList<String>();
		text.add("(Click para quitar mensaje)");
		errorMsg= new SimpleErrorMessage(text,  ResourceManager.fonts.gameText, ResourceManager.textures.transition);
		addAreaNoActive(errorMsg);
	}
	
	public static SimpleGUI getInstance(){
		if(instance==null) instance= new SimpleGUI();
		
		return instance;
	}
	
	/**
	 * Check if has been a click over an area.
	 * @param delta
	 */
	public void update(float delta){
		float x= -10;
		float y= -10;
		
		//Llamamos al update de cada area visible...
		for(int i= 0; i < this.areas.size(); i++) if(areas.get(i).active) areas.get(i).update(delta);
		
		//Obtenemos el ultimo click realizado (si es que hubo alguno)...
		if(ShukenInput.getInstance().hasBeenClicked(0)){
			x= ShukenInput.getInstance().getClickX(0);
			y= ShukenInput.getInstance().getClickY(0);

			//Verificamos si el click recae sobre alguna clickable area...
			for (int i= 0; i <areas.size(); i++) {
				if(areas.get(i).active){
					if(areas.get(i).zone.contains(x, y)) {
						
						if(areas.get(i).consumeClick){
							areas.get(i).clickOn();
							
							ShukenGame.getInstance().getCurrentScreen().simpleGUI_Event(areas.get(i));
							
							//Consumimos click...
							ShukenInput.getInstance().consumeClick(0);
						}			
					}else{
						areas.get(i).clickOff();
					}
				}
			}
		}
	}//end update
	
	/**
	 * @param character
	 * @return true if the key is consumed.
	 */
	public boolean keyTyped(int key, char character){
		for(int i= 0; i <areas.size(); i++) {
			if(areas.get(i) instanceof SimpleTextBox){
				SimpleTextBox txtbox= (SimpleTextBox)areas.get(i);
				if(txtbox.active && txtbox.isWritingOn()) return txtbox.keyTyped(key, character);
			}
		}
		return false;
	}
	
	/**
	 * Renders every visible area on the gui.
	 * @param batch
	 */
	public void render(SpriteBatch batch){
		for (int i= 0; i <areas.size(); i++) {
			if(areas.get(i).equals(errorMsg)) continue;
			
			if(areas.get(i).active) areas.get(i).render(batch);
		}
		
		if(errorMsg.active) errorMsg.render(batch);
	}
	
	/**
	 * Renders the limits of the areas. Test only.
	 * @param shapeRender
	 */
	public void renderZones(ShapeRenderer shapeRender){
		shapeRender.begin(ShapeType.Line);
		for (int i= 0; i <areas.size(); i++) {
			if(areas.get(i).active) areas.get(i).renderZone(shapeRender);
		}
		shapeRender.end();
	}
	
	/**
	 * @param area
	 * @return true if the area is under the mouse current position.
	 */
	public boolean isAreaUnderTheMouse(Rectangle area){
		if(area.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) return true;
		else return false;
	}
	
	public boolean isAreaUnderTheMouse(float x, float y, float width, float height){
		auxRect.set(x, y, width, height);
		return this.isAreaUnderTheMouse(auxRect);
	}
	
	/**
	 * @return true if the mouse is over (any) active area.
	 */
	public boolean isMouseOverAnyArea(float mouseX, float mouseY){
		for(int i= 0; i < areas.size(); i++){
			if(areas.get(i).active){
				if(areas.get(i).zone.contains(mouseX,mouseY)) return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds the area into the GUI.
	 * @param area
	 */
	public void addArea(ClickableArea area){
		area.active= true;
		areas.add(area);
	}
	
	/**
	 * Adds the area into the GUI, but not visible.
	 * @param area
	 */
	public void addAreaNoActive(ClickableArea area){
		area.active= false;
		areas.add(area);
	}
	
	/**
	 * Turns the area visible.</br>
	 * It must be previously loaded.
	 * @param id
	 */
	public void turnAreaON(ClickableArea area){
		areas.get(areas.indexOf(area)).active= true;
	}
	
	/**
	 * Turns the area invisible.</br>
	 * It must be previously loaded.
	 * @param id
	 */
	public void turnAreaOFF(ClickableArea area){
		areas.get(areas.indexOf(area)).active= false;
	}
}//end class
