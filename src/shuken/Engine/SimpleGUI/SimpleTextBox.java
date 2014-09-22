package shuken.Engine.SimpleGUI;

import java.util.ArrayList;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import shuken.Engine.Resources.ResourceManager;
import shuken.Engine.ShukenInput.ShukenInput;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.FloatArray;


public class SimpleTextBox extends ClickableArea {

	protected boolean textboxForPassword= false;
	
	/** Aspecto del textbox. */
	protected TextureRegion skin;
	
	/** Fuente con la que se grafica el texto. */
	protected BitmapFont font;
	
	protected boolean writingOnTextbox= false;
	
	/** Texto ingresado por el usuario en formato interno para manipularlo. */
	protected ArrayList<Character> text;
	
	/** texto con el unico fin de ayudar en la graficacion del cursor (en la pos adecuada) */
	private String textToRenderCursor= "";	//aux
	private char[] cs;						//aux: secuencia de caracteres utilizada en el proceso del renderCursor
	
	/** Cantidad de caracteres visibles dentro del textbox (depende del largo del textbox)*/
	//public int cantOfCharAvailableOnShape;

	
	/** Caracter del cursor (en formato string para facilitar las cosas)*/
	protected String cursorChar= "|";
	/** Posicion (index) del cursor. */
	protected int CursorPosition=0;
	protected int renderCursorPosition;	//aux: posicion en la q se renderiza (en pixeles), es diferente de la posicion "real" dentro del texto.
	
	/** Intervalo de intermitencia del cursor (en segundos)*/
	protected float intermitencia= 0.4f;
	/** Determina si se grafica o no el cursor. Necesario para realizar la intermitencia.*/
	protected boolean showCursor= true;
	private float elapsedTime= 0;		//aux
	
	/** Limite de caracteres q puede tener el texto ingresado.*/
	protected int maxTextLength= 50;
	
	private int marginX= 10;
	private int marginY= 30;
	
	public SimpleTextBox(int posx, int posy, int maxCantChars, BitmapFont font, TextureRegion skin){
		this(posx, posy, skin.getRegionWidth(), skin.getRegionHeight(), maxCantChars, font, skin);
	}
	
	public SimpleTextBox(int posx, int posy, int width, int height, int maxCantChars, BitmapFont font, TextureRegion skin){
		super(new Rectangle(posx, posy, width, height));
		
		//Inicializamos array...
		text= new ArrayList<Character>();	
		
		//Guardamos skin
		this.skin= skin;
		
		//Guardamos font
		this.font= font;
		
		//Guardamos longitud maxima de caracteres...
		maxTextLength= maxCantChars;
	}

	
	@Override
	public void render(SpriteBatch batch) {
		//Graficamos el skin...
		if(skin != null){
			if(this.coordOverArea(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) batch.setColor(Color.GREEN);
			
			batch.draw(skin, zone.x, zone.y, zone.width, zone.height);
			batch.setColor(Color.WHITE);
		}
		

		//Graficamos el texto introducido hasta el momento...
		if(textboxForPassword) font.draw(batch, getPasswordText(), zone.x + marginX, zone.y + marginY - 8);
		else font.draw(batch, getTextNoConsume(), zone.x + marginX, zone.y + marginY - 5);

		//Graficamos el cursor intermitente...
		this.renderCursor(batch);
				
		//For debug
		//marginX= 10;
		//marginY= 30;
	}


	
	
	/**
	 * Grafica el cursor del textbox (unicamente si el componente esta onAction).
	 */
	protected void renderCursor(SpriteBatch batch){
		if(!this.writingOnTextbox) return;
		
		//El texto lo pasamos a un string de caracteres...
		if(textboxForPassword) this.cs= getPasswordText().toCharArray();
		else this.cs= getTextNoConsume().toCharArray();

		//Limpiamos variable...
		this.textToRenderCursor= "";

		if(getTextNoConsume().length() > 0){
			//Armamos un nuevo string de 0 hasta la posicion actual del cursor...
			for(int i=0; i < this.renderCursorPosition; i++){
				this.textToRenderCursor+= cs[i];
			}
		}
		
		//font.draw(batch, textToRenderCursor, 10, 300);
		
		//Graficamos cursor...
		if(showCursor) {
			font.draw(batch, cursorChar, zone.x + marginX + ResourceManager.fonts.calculateTextWidth(font, textToRenderCursor) - 2, zone.y + marginY - 4);
		}
	}//fin rendercursor
	

	
	@Override
	public void update(float delta) {	
		//Provocamos la intermitencia del cursor...
		elapsedTime+= delta;
		if(elapsedTime >= intermitencia){
			elapsedTime= 0;
			showCursor= !showCursor;
		}
	}//fin update
	
	@Override
	public void clickOn(){
		writingOnTextbox= true;
	}
	
	@Override
	public void clickOff(){
		writingOnTextbox= false;
	}
	
	
	public boolean isWritingOn(){
		return writingOnTextbox;
	}
	
	

	
	/**
	 * Este metodo lo llama la clase SimpleGUI cuando una tecla se presiona en la GUI, mas precisamente en un TextBox. Le pasa la tecla y
	 * su char asociado para q el textbox lo trabaje como mejor le parezca. La GUI no se responsabiliza mas de lo q debe. Solo llama a este
	 * metodo y si este metodo devuelve true consume el evento para q no pase al gamestate de turno.
	 */
	public boolean keyTyped(int key, char c){
		boolean keyConsumed= false;
		
		//System.out.println("LETRA: " + c + "      CODIGO: " + key);
		
		switch(key){
		
		//****************************
		//Teclas que no deben colocarse en el texto. Ejemplos: shift, ctrl, tab, F1, F2, etc...
		case(Keys.ENTER):
		case(Keys.SHIFT_LEFT):
		case(Keys.SHIFT_RIGHT):
		case(Keys.ALT_LEFT):
		case(Keys.ALT_RIGHT):
		case(Keys.CONTROL_LEFT):
		case(Keys.CONTROL_RIGHT):
				//case(Input.KEY_RWIN):
				//case(Input.KEY_LWIN):
		case(Keys.TAB):
				//case(Input.KEY_BACKSLASH):	//barra invertida "\"
		case(Keys.INSERT):
				//case(Input.KEY_CAPITAL):	//bloq mayus
				//case(Input.KEY_NUMLOCK):	//bloq num
		case(Keys.PAGE_UP):		//re pag en mi teclado (page up)
		case(Keys.PAGE_DOWN):		//av pag en mi teclado (page down)
		case(Keys.SYM):		//teclas de win (ambas)
		case(Keys.F1):
		case(Keys.F2):
		case(Keys.F3):
		case(Keys.F4):
		case(Keys.F5):
		case(Keys.F6):
		case(Keys.F7):
		case(Keys.F8):
		case(Keys.F9):
		case(Keys.F10):
		case(Keys.F11):
		case(Keys.F12):
		
		
				//case(221):					//la q tiene forma de menu contextual en mi teclado, a la izquierda del "right ctrol"
		
			
			break;
		//****************************
		
		//LCONTROL + "V" (pegar)
		case(Keys.V):
			
			//Verificamos q se este apretando el "left ctrl"...
			if(ShukenInput.getInstance().isKeyPressed(Keys.CONTROL_LEFT)){
				//Se estra presionando el LCONTROL. Al apretar tambien V, ejecutamos la accion de "pegar".
				this.pasteTextFromClipBoard();
			}else{
				//No se esta presionando el LCONTROL, por lo tanto debe colocarse la V como si de una letra cualquiera se tratara...
				this.putCharacterIntoText(c);
			}
			break;
			
		
		//Si es la de borrar (BACK) borramos el char a la izquierda del cursor...
		case(Keys.DEL):
			if(text.size() != 0) {
				if(this.CursorPosition > 0) {
					text.remove(this.CursorPosition-1);
					this.moveCursor(-1);
				}
			}
			keyConsumed= true;
			break;

		//Tecla DELETE, borramos el caracter a la derecha del cursor...
		case(Keys.FORWARD_DEL):
			if(text.size() != 0) {
				if(this.CursorPosition == text.size()) {
					//El cursor esta al final del texto, nada q borrar.
				}else{
					//El cursor esta en algun lugar del texto (menos al final). Borramos el caracter de la derecha...
					text.remove(this.CursorPosition);
				}
			}
			keyConsumed= true;
			break;
			
		//Tecla "fin". Posicionamos cursor al final del texto.
		case(Keys.END):
			this.moveCursor(text.size());
			keyConsumed= true;
			break;
			
		//Tecla "inicio" (numero 199). Posicionamos el cursor al inicio del texto.
		case(Keys.HOME):
			this.moveCursor(0);
			keyConsumed= true;
			break;
			
		//ESCAPE, provoca q nos vayamos del "foco" y no tengamos mas elegido al componente.
		case(Keys.ESCAPE):
			//TODO textbox no gestiona ESCAPE
			//this.disableAction();		//quitamos esto de momento porq genera un peque�o bug cuando el textbox pertenece a un MessageWindow...
			//keyConsumed= true;
			break;
		
		//Movemos el cursor derecha e izquierda...
		case(Keys.LEFT):
			this.moveCursor(-1);
			//if(this.CursorPosition < 0) this.CursorPosition= 0;
			keyConsumed= true;
			break;
			
		case(Keys.RIGHT):
			this.moveCursor(1);
			//if(this.CursorPosition > text.size()) this.CursorPosition= text.size();
			keyConsumed= true;	
			break;
		
		//Gestionamos "BACKSLASH" (tecla a la izq del 1) solo con alt_derecho apretado...
		case(Keys.BACKSLASH):
			if(ShukenInput.getInstance().isKeyPressed(Keys.ALT_RIGHT)){
				this.putCharacterIntoText(c);
				keyConsumed= true;
			}
			break;
			
		//El mapeo de keys de libGDX coloca como "UNKNOWN" a varias teclas. Algunas deben colocarse.
		case(Keys.UNKNOWN):
			if(c == '<' || c== '>'){
				this.putCharacterIntoText(c);
				keyConsumed= true;
			}
			break;
			
		//En cualquier otro caso agreamos la letra al texto...
		default:
			this.putCharacterIntoText(c);
			keyConsumed= true;
			break;

		}//fin switch	
			
		if(ResourceManager.isAudioOn() && keyConsumed) ResourceManager.audio.key.play();
		
		return keyConsumed;
	}//fin keyPressed
	
	
	
	/**
	 * Agrega una letra al texto.
	 * @param c
	 */
	public void putCharacterIntoText(char c){
		//Si el texto no supera el limite establecido, colocamos la letra y movemos una posicion el cursor...
		if(text.size() < maxTextLength){
			text.add(this.CursorPosition, c);
			this.moveCursor(1);
		}
	}
	
	/**
	 * Coloca el string pasado como parametro dentro del textbox. Previo a eso, el textbox convierte el string a un array de caracteres.
	 * @param s
	 */
	public void putStringIntoText(String s){
		//Convertimos a array de chars...
		char chars[]= s.toCharArray();
		
		//Colocamos todos los caracteres. Si se alcanza el limite, se pierden.
		for(int i=0; i < chars.length; i++){
			this.putCharacterIntoText(chars[i]);
		}
		
	}
	
	/**
	 * Coloca el string pasado como parametro dentro del textbox. Llamar a este metodo borra cualquier otro valor previo.
	 */
	public void putNewStringIntoText(String s){
		this.clearText();
		this.putStringIntoText(s);
	}
	
	
	/**
	 * Mueve el cursor "p" cantidad de lugares (incluyendo negativos hacia la izquierda). Verifica limites del renderCursorPosition.
	 * @param p
	 */
	public void moveCursor(int p){
		//Movemos el cursor p lugares...
		if(p == 0) CursorPosition= 0;
		
		this.CursorPosition+= p;
		
		//Verificamos limites...
		if(this.CursorPosition < 0) this.CursorPosition= 0;
		if(this.CursorPosition > this.maxTextLength) this.CursorPosition= this.maxTextLength;
		if(this.CursorPosition > this.text.size()) this.CursorPosition= this.text.size();
		
		//Movemos el renderCursorPosition...
		this.renderCursorPosition= this.CursorPosition;
		//if(this.CursorPosition >= this.cantOfCharAvailableOnShape) this.renderCursorPosition= this.cantOfCharAvailableOnShape;
		//if(this.CursorPosition < this.cantOfCharAvailableOnShape) this.renderCursorPosition= this.CursorPosition;

	}//fin moveCursor

	
	
	/**
	 * Devuelve el texto ingresado hasta el momento, en un formato leible (String). </br>
	 * Este metodo consume el texto! Solo puede ser leido 1 vez.
	 * @return
	 */
	public String getText(){
		String temp_text= "";
		for(int i=0; i < this.text.size(); i++){
			temp_text+= this.text.get(i);
		}
		
		//Limpiamos el texto
		this.clearText();

		return temp_text;
	}
	
	/**
	 * Devuelve el texto ingresado hasta el momento, en un formato leible (String).
	 * @return
	 */
	public String getTextNoConsume(){
		String temp_text= "";
		for(int i=0; i < this.text.size(); i++){
			temp_text+= this.text.get(i);
		}
		return temp_text;
	}
	
	/**
	 * Devuelve el texto ingresado hasta el momento, pero oculto.
	 * @return
	 */
	public String getPasswordText(){
		String temp_text= "";
		for(int i=0; i < this.text.size(); i++){
			temp_text+= "*";
		}
		return temp_text;
	}
	
	/**
	 * Coloca el texto del ClipBoard dentro del textbox.
	 */
	protected void pasteTextFromClipBoard(){
		String data;
		try {
			//Leemos del portapapeles...
			data = (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
			
			//Introducimos texto en textbox...
			this.putStringIntoText(data);
			
		} catch (HeadlessException e) {
			e.printStackTrace();
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//fin pasteText
	
	
	
	/**
	 * Limpia el texto dentro del textbox.
	 * @return
	 */
	protected void clearText(){
		//Limpiamos array...
		this.text.clear();
		//Re-posicionamos el cursor...
		this.moveCursor(0);
	}
	


	public void setTextboxForPassword(boolean b){
		this.textboxForPassword= b;
	}

	
}//fin de clase
