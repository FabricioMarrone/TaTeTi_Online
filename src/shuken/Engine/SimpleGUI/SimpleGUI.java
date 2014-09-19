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
 * Una GUI simple es una clase que posee un conjunto de ClickableAreas y que cuando detecta que un click cae en alguna de dichas areas,
 * avisa al screen de turno. Utiliza para ello el input processor personalizado ShukenInput.
 * @author Shuken
 *
 */
public class SimpleGUI {

	private static SimpleGUI instance= null;
	
	/** Conjunto de areas en la GUI. */
	protected ArrayList<ClickableArea> areas;
	
	/** Mensaje de error accesible a cualquier screen y que se graficará por encima de todo. */
	public SimpleErrorMessage errorMsg;
	
	protected Rectangle auxRect;	//aux (en lugar de usar pool usamos 1 y a la mierda)
	
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
	 * Verifica si hubo algun click en alguna de las areas activas, en cuyo caso avisa al screen de turno.
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
							
							ShukenGame.getInstance().getActualScreen().simpleGUI_Event(areas.get(i));
							
							//Consumimos click...
							ShukenInput.getInstance().consumeClick(0);
						}
						
					}else{
						areas.get(i).clickOff();
					}
				}
			}
		}
		
		
	}//fin update
	
	/**
	 * Devuelve true si se consume la tecla.
	 * @param character
	 * @return
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
	 * Grafica todas aquellas clickables areas que esten activas.
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
	 * Grafica las zonas de todas las ClickableAreas activas.
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
	 * Verifica si la POSICION actual del mouse esta por encima del area especificada. Devuele true en ese caso.
	 * @param area
	 * @return
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
	 * Devuelve true si el mouse se encuentra sobre alguna ClickeableArea, sea cual sea. 
	 * @return
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
	 * Coloca al area dentro de la simpleGUI. 
	 * @param area
	 */
	public void addArea(ClickableArea area){
		area.active= true;
		areas.add(area);
	}
	
	
	/**
	 * Coloca al area dentro de la simpleGUI pero desactivada. 
	 * @param area
	 */
	public void addAreaNoActive(ClickableArea area){
		area.active= false;
		areas.add(area);
	}
	
	
	
	/**
	 * Coloca al area deseada como area activa. Si hay un click en ella, se detecta y se avisa al screen de turno.</br>
	 * El area debe estar previamente cargada.
	 * @param id
	 */
	public void turnAreaON(ClickableArea area){
		areas.get(areas.indexOf(area)).active= true;
	}
	
	/**
	 * Coloca al area deseada como area desactivada. Si hay un click en ella, NO se lo detecta.</br>
	 * El area debe estar previamente cargada.
	 * @param id
	 */
	public void turnAreaOFF(ClickableArea area){
		areas.get(areas.indexOf(area)).active= false;
	}
	
	
	
	
	
	
}//fin clase
