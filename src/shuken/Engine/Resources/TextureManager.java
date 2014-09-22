package shuken.Engine.Resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


/**
 * Singleton class that contains all the textures of the game.
 * @author F. Marrone
 *
 */
public class TextureManager {

	private static TextureManager instance= null;
	
	public TextureRegion tablero, cruz, circulo, button, textbox, transition, checkbox_checked, checkbox_unchecked;
	public Texture backgroundLoggin, backgroundCreateAccount, backgroundMainMenu, backgroundGamePlay;
	public TextureRegion gameTitle, createAccountTitle, mainMenuTitle;
	public TextureRegion lineV, lineH, lineD1, lineD2, arrow, circleArrow;
	public Texture timeBar;	//no usar TextureRegion en esta
	
	protected static TextureManager getInstance(){
		if(instance == null) instance= new TextureManager();
		
		return instance;
	}
	
	
	/**
	 * Load all the textures into the memory.
	 */
	protected void loadTextures(){
		
		tablero= new TextureRegion(new Texture("assets/images/tablero.png"));
		cruz= new TextureRegion(new Texture("assets/images/cruz.png"));
		circulo= new TextureRegion(new Texture("assets/images/circulo.png"));
		button= new TextureRegion(new Texture("assets/images/button.png"));
		textbox= new TextureRegion(new Texture("assets/images/textbox.png"));
		transition= new TextureRegion(new Texture("assets/images/transition.png"));
		checkbox_checked= new TextureRegion(new Texture("assets/images/checkbox_check.png"));
		checkbox_unchecked= new TextureRegion(new Texture("assets/images/checkbox_uncheck.png"));
		
		backgroundLoggin = new Texture("assets/images/backgroundLoggin.png");
		backgroundCreateAccount = new Texture("assets/images/backgroundCreateAccount.png");
		backgroundMainMenu = new Texture("assets/images/backgroundMainMenu.png");
		backgroundGamePlay  = new Texture("assets/images/backgroundGamePlay.png");
		
		gameTitle = new TextureRegion(new Texture("assets/images/title.png"));
		createAccountTitle= new TextureRegion(new Texture("assets/images/createAccountTitle.png"));
		mainMenuTitle= new TextureRegion(new Texture("assets/images/mainMenuTitle.png"));
		
		lineV = new TextureRegion(new Texture("assets/images/lineV.png"));
		lineH = new TextureRegion(new Texture("assets/images/lineH.png"));
		lineD1 = new TextureRegion(new Texture("assets/images/lineD1.png"));
		lineD2 = new TextureRegion(new Texture("assets/images/lineD2.png"));
		arrow = new TextureRegion(new Texture("assets/images/arrow.png"));
		Texture circleArr= new Texture("assets/images/circlearrow.png");
		circleArr.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		circleArrow= new TextureRegion(circleArr);
		timeBar = new Texture("assets/images/timeBar.png");
		
		
		
		/*
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
		
		lineV.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		lineH.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		lineD1.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		lineD2.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		arrow.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		timeBar.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		*/
		
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
		
		
	}//end load
	
	
	
	public void disposeAll(){
		//TODO dispose textures
	}
}//fin clase
