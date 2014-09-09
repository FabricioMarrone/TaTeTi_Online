package shuken.Engine.ShukenInput;


import shuken.Engine.SimpleGUI.SimpleGUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

/**
 * Input solamente empleado para hacer una especie de polling.
 * 
 * @author Shuken
 *
 */
public class ShukenInput implements InputProcessor{
	
	private static ShukenInput instance= null;
	
	/** Keys presionadas actualmente. */
	public boolean[] keysPressed;
	
	/** Keys liberadas recientemente. */
	public boolean[] keysReleased;
	
	/** Clicks del mouse. Cada click contiene información acerca de si se trata de un press o un release. */
	public MouseClick[] clicks;			//clicks[0] button IZQ
										//clicks[1] button DER
										//clicks[2] button del medio
	
	/** Ultimo keyCode enviado como parametro en el metodo keyDown. Se lo guarda para utilizarlo luego en keyTyped junto a su char asociado. */
	private int lastKeyCode;	//podriamos decir q es una variable auxiliar cuyo valor cambia todo el tiempo y nunca debe ser accedida directamente.
	
	/** Ultimo valor enviado desde el metodo "scroll" cuando se hace girar la rueda del mouse. -1 o 1 dependiendo la direccion. 0 si no se ha
	 * realizado ningun movimiento (valor por defecto).*/
	private int lastScrollAmount= 0; 
	
	
	/**
	 * Constructor privado. Clase singleton.
	 * @param game
	 */
	private ShukenInput(){
		clicks= new MouseClick[3];
		clicks[0]= new MouseClick();
		clicks[1]= new MouseClick();
		clicks[2]= new MouseClick();
		
		keysPressed= new boolean[600];		//el indice se determina por los KeyCode (son int de 0 a 600 mas o menos)
		keysReleased= new boolean[600];
		
	}
	
	
	public static ShukenInput getInstance(){
		if(instance == null) instance= new ShukenInput();
		
		return instance;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		//Guardamos el keycode para usarlo luego en keyTyped junto a su char asociado. En este metodo no hacemos nada con la tecla.
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
		
		//Enviamos la tecla a la GUI por si algun componente la utiliza...
		boolean consumed= SimpleGUI.getInstance().keyTyped(lastKeyCode, character);
		

		//Si no ha sido consumida por la GUI, se almacena por si algun state del juego la utiliza.
		if(!consumed){
			if(keysPressed[this.lastKeyCode]== false) keysPressed[this.lastKeyCode]= true;
		}
		
		
		//System.out.println("TYPED: (keycode)" + this.lastKeyCode + "           que es la letra: " + character);
		return false;
	}

	
	
	//************************ INPUTS DEL MOUSE ***********************//
	
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
	 * Devuelve true si la tecla pasada como parámetro está siendo presionada.
	 * @param keyCode (Gdx.Input."keycodes") 
	 * @return
	 */
	public boolean isKeyPressed(int keyCode){
		return this.keysPressed[keyCode];
	}
	
	/**
	 * Si una key ha sido "released" y se llama a este metodo, la respuesta es true pero además se limpia el registro del "release". Esto es para
	 * que un release se gestione solo 1 vez. Por lo tanto, llamar a este método implica limpiar tambien el registro. Se lo debe llamar especificamente
	 * cuando va a gestionarse el input.
	 * 
	 * @param keyCode (Gdx.Input."keycodes") 
	 * @return true si la tecla ha sido recientemente liberada
	 */
	public boolean isKeyReleased(int keyCode){
		if(this.keysReleased[keyCode]){
			//Limpiamos el release para que sólo sea utilizado una vez...
			this.keysReleased[keyCode]= false;
			return true;
		}
		return false;
	}
	
	
	/**
	 * Limpia el input en su totalidad (polling, obvio).
	 */
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
	 * Guarda el click para su uso posterior.
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
	
	
	/**
	 * Devuelve true si ha habido un click con el boton del mouse especificado.
	 * @param button
	 * @return
	 */
	public boolean hasBeenClicked(int button){
		if(clicks[button].clicked) return true;
		else return false;
	}
	
	/**
	 * Devuelve la coordenada x del ultimo click del boton del mouse especificado. Notar que para un buen uso de este metodo, es 
	 * recomendable verificar primero de que haya habido un click, mediante el metodo <code>hasBeenClicked(int button)</code>.</br>
	 * Se recomienda limpiar el click luego de su uso.</br>
	 * 
	 * La forma correcta de utilizarlo es:</br></br>
	 * 
	 * if(Cavern.input.hasBeenClicked(0)){</br>
			x= Cavern.input.getClickX(0);</br>
			y= Cavern.input.getClickY(0);</br>
			Cavern.input.consumeClick(0);</br>
		}</br>
	 * @param button
	 * @return
	 */
	public float getClickX(int button){
		return clicks[button].x;
	}
	
	/**
	 * Devuelve la coordenada y (fixed al canvas del juego) del ultimo click del boton del mouse especificado. Notar que para un buen 
	 * uso de este metodo, es recomendable verificar primero de que haya habido un click, mediante el metodo 
	 * <code>hasBeenClicked(int button)</code>.</br>
	 *
	 * Se recomienda limpiar el click luego de su uso.</br>
	 * La forma correcta de utilizarlo es:</br></br>
	 * 
	 * if(Cavern.input.hasBeenClicked(0)){</br>
			x= Cavern.input.getClickX(0);</br>
			y= Cavern.input.getClickY(0);</br>
			Cavern.input.consumeClick(0);</br>
		}</br>
	 * @param button
	 * @return
	 */
	public float getClickY(int button){
		return clicks[button].y;
	}
	/**
	 * Consume la tecla para que ya no figure como presionada.
	 * @param keyCode
	 */
	public void consumeKey(int keyCode){
		this.keysPressed[keyCode]= false;
		this.keysReleased[keyCode]= false;
	}
	
	/**
	 * COnsume el click asociado al boton.
	 * @param button
	 */
	public void consumeClick(int button){
		clicks[button].cleanAll();
	}
	
	/**
	 * Devuelve el valor del scroll del mouse. -1 (hacia arriba), 1 (hacia abajo) o 0 si no hubo movimiento.</br>
	 * 
	 * @return
	 */
	public int getScrollValue(){
		return lastScrollAmount;
	}
	
	/**
	 * Devuelve true si ha habido un scroll del mouse, sea cual sea la direccion. Falso si no hubo movimientos.
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
