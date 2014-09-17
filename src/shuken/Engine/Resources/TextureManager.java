package shuken.Engine.Resources;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;


/**
 * Clase singleton que contiene todas las texturas necesarias.
 * @author Shuken
 *
 */
public class TextureManager {

	private static TextureManager instance= null;
	
	
	
	//private TextureAtlas playerAtlas;
	//public Animation playerRunning;
	//public Animation playerJumpForward;
	
	public Texture tablero, cruz, circulo, button, textbox, transition, checkbox_checked, checkbox_unchecked;
	public Texture backgroundLoggin, backgroundCreateAccount, backgroundMainMenu, backgroundGamePlay;
	public Texture gameTitle, createAccountTitle, mainMenuTitle;
	
	protected static TextureManager getInstance(){
		if(instance == null) instance= new TextureManager();
		
		return instance;
	}
	
	
	/**
	 * Carga en memoria todas las texturas requeridas por el juego.
	 */
	protected void loadTextures(){
		
		
		tablero= new Texture("assets/images/tablero.png");
		cruz= new Texture("assets/images/cruz.png");
		circulo= new Texture("assets/images/circulo.png");
		button= new Texture("assets/images/button.png");
		textbox= new Texture("assets/images/textbox.png");
		transition= new Texture("assets/images/transition.png");
		checkbox_checked= new Texture("assets/images/checkbox_check.png");
		checkbox_unchecked= new Texture("assets/images/checkbox_uncheck.png");
		
		backgroundLoggin = new Texture("assets/images/backgroundLoggin.png");
		backgroundCreateAccount = new Texture("assets/images/backgroundCreateAccount.png");
		backgroundMainMenu = new Texture("assets/images/backgroundMainMenu.png");
		backgroundGamePlay  = new Texture("assets/images/backgroundGamePlay.png");
		
		gameTitle = new Texture("assets/images/title.png");
		createAccountTitle= new Texture("assets/images/createAccountTitle.png");
		mainMenuTitle= new Texture("assets/images/mainMenuTitle.png");
		
		tablero.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		cruz.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		circulo.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		button.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		textbox.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		transition.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		checkbox_checked.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		checkbox_unchecked.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		backgroundLoggin.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		backgroundCreateAccount.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		backgroundMainMenu.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		backgroundGamePlay.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		gameTitle.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		createAccountTitle.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		mainMenuTitle.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		/*
		//Cargamos el atlas del player...
		playerAtlas= new TextureAtlas(Gdx.files.internal("assets/images/playerAtlas.txt"));
		
		//Creamos animacion de RUNNING
		TextureRegion running1= playerAtlas.findRegion("playerRunning1");
		TextureRegion running2= playerAtlas.findRegion("playerRunning2");
		TextureRegion running3= playerAtlas.findRegion("playerRunning3");
		Array<TextureRegion> playerRunningTextures= new Array<TextureRegion>();
		playerRunningTextures.add(running1);
		playerRunningTextures.add(running2);
		playerRunningTextures.add(running3);
		playerRunning= new Animation(0.15f, playerRunningTextures);
		playerRunning.setPlayMode(Animation.LOOP);
		
		//Creamos animacion de JUMP FORWARD
		TextureRegion jumpForward1= playerAtlas.findRegion("playerJumpForward1");
		TextureRegion jumpForward2= playerAtlas.findRegion("playerJumpForward2");
		TextureRegion jumpForward3= playerAtlas.findRegion("playerJumpForward3");
		Array<TextureRegion> playerJumpForwardTextures= new Array<TextureRegion>();
		playerJumpForwardTextures.add(jumpForward1);
		playerJumpForwardTextures.add(jumpForward2);
		playerJumpForwardTextures.add(jumpForward3);
		playerJumpForward= new Animation(0.25f, playerJumpForwardTextures);
		playerJumpForward.setPlayMode(Animation.NORMAL);
		*/
		
		
		
		
	}//fin load
	
	
	
	public void disposeAll(){
		
	}
}//fin clase
