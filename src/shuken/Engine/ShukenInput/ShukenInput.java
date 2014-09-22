package shuken.Engine.ShukenInput;


import shuken.Engine.SimpleGUI.SimpleGUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

/**
 * Customized class to support a kind of "polling".
 * 
 * @author F. Marrone
 *
 */
public class ShukenInput implements InputProcessor{
	
	private static ShukenInput instance= null;
	
	/** Keys currently pressed */
	public boolean[] keysPressed;
	
	/** Keys recently released */
	public boolean[] keysReleased;
	
	/** Mouse clicks */
	public MouseClick[] clicks;			//clicks[0] button LEFT
										//clicks[1] button RIGHT
										//clicks[2] button MIDLE
	
	/** Last keyCode dispatched by the keyDown method. */
	private int lastKeyCode;	//its an aux var, it changes all the time and never must be used directly
	
	/**
	 * Last value dispatched by the scroll method. 
	 * -1 and 1 depending of the direction
	 * 0 no movement (default value)
	 */
	private int lastScrollAmount= 0; 
	
	/**
	 * Singleton class.
	 */
	private ShukenInput(){
		clicks= new MouseClick[3];
		clicks[0]= new MouseClick();
		clicks[1]= new MouseClick();
		clicks[2]= new MouseClick();
		
		keysPressed= new boolean[600];
		keysReleased= new boolean[600];
		
	}
	
	public static ShukenInput getInstance(){
		if(instance == null) instance= new ShukenInput();
		
		return instance;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		//We save the keyCode to be used later on the keyTyped method.
		this.lastKeyCode= keycode;
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		keysPressed[keycode]= false;
		keysReleased[keycode]= true;
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		//Si esta activado el Keyboard.enableRepeatEvents(true) este metodo se llama continuamente mientras una tecla este presionada.
		
		//We send the key to the GUI first...
		boolean consumed= SimpleGUI.getInstance().keyTyped(lastKeyCode, character);
		
		//if the GUI didn't use the key, then is saved for the gameplay.
		if(!consumed){
			if(keysPressed[this.lastKeyCode]== false) keysPressed[this.lastKeyCode]= true;
		}
		
		//System.out.println("TYPED: (keycode)" + this.lastKeyCode + "           que es la letra: " + character);
		return false;
	}

	
	
	//************************ MOUSE INPUTS ***********************//
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		this.saveClick(button, screenX, Gdx.graphics.getHeight() - screenY, 0);
		//System.out.println("Click en " + screenX + ", " + screenY);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {

		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		lastScrollAmount= amount;
		//System.out.println("Scroll detected: " + lastScrollAmount);
		
		return false;
	}

	/**
	 * @param keyCode (Gdx.Input."keycodes") 
	 * @return Returns true if the parameter is being pressed.
	 */
	public boolean isKeyPressed(int keyCode){
		return this.keysPressed[keyCode];
	}
	
	/**
	 * If the key has been released, this method returns true but also cleans the release. So, this method must be called specifically when the input
	 * its going to be processed.
	 * 
	 * @param keyCode (Gdx.Input."keycodes") 
	 * @return true if the key hass been recently released
	 */
	public boolean isKeyReleased(int keyCode){
		if(this.keysReleased[keyCode]){
			this.keysReleased[keyCode]= false;
			return true;
		}
		return false;
	}
	
	public void cleanAll(){
		//Limpia las teclas...
		for(int i=0; i < 600; i++){
			this.keysPressed[i]= false;
			this.keysReleased[i]= false;
		}
		//Limpia los clicks...
		this.clicks[0].cleanAll();
		this.clicks[1].cleanAll();
		this.clicks[2].cleanAll();
	}
	
	/**
	 * Saves the click for later use.
	 * @param button
	 * @param x
	 * @param y
	 * @param count
	 */
	protected void saveClick(int button, int x, int y, int count){
		this.clicks[button].clicked= true;
		this.clicks[button].clickCount= count;
		this.clicks[button].setCoords(x, y);
	}
	
	public boolean hasBeenClicked(int button){
		if(clicks[button].clicked) return true;
		else return false;
	}
	
	/**
	 * Returns the last X-coord of a mouse click. Note that for a good use of this method its recommended to check if 
	 * <code>hasBeenClicked(int button)</code> returns true. And finally, clean up the click to be not processed again in the next frame.</br>
	 * 
	 * Example:</br></br>
	 * 
	 * if(Game.input.hasBeenClicked(0)){</br>
			x= Game.input.getClickX(0);</br>
			y= Game.input.getClickY(0);</br>
			//Do what you want here...</br>
			Game.input.consumeClick(0);</br>
		}</br>
	 */
	public float getClickX(int button){
		return clicks[button].x;
	}
	
	/**
	 * Returns the last Y-coord of a mouse click. Note that for a good use of this method its recommended to check if 
	 * <code>hasBeenClicked(int button)</code> returns true. And finally, clean up the click to be not processed again in the next frame.</br>
	 * 
	 * Example:</br></br>
	 * 
	 * if(Game.input.hasBeenClicked(0)){</br>
			x= Game.input.getClickX(0);</br>
			y= Game.input.getClickY(0);</br>
			//Do what you want here...</br>
			Game.input.consumeClick(0);</br>
		}</br>
	 */
	public float getClickY(int button){
		return clicks[button].y;
	}
	
	public void consumeKey(int keyCode){
		this.keysPressed[keyCode]= false;
		this.keysReleased[keyCode]= false;
	}
	
	public void consumeClick(int button){
		clicks[button].cleanAll();
	}
	
	public int getScrollValue(){
		return lastScrollAmount;
	}
	
	/**
	 * Returns true if the scroll of the mouse has been moved. False otherwise.
	 * @return
	 */
	public boolean hasBeenMouseScrolled(){
		if(lastScrollAmount== 0) return false;
		else return true;
	}
	
	public void clearScroll(){
		lastScrollAmount= 0;
	}
}//fin clase
