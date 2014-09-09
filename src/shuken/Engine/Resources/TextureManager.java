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
	
	public Texture tablero, cruz, circulo, button, textbox, transition;
	
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
		
		tablero.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		cruz.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		circulo.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		button.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		textbox.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		transition.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		
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
