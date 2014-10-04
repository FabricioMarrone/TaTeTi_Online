package shuken.Engine.Resources;

import shuken.TaTeTi.TaTeTi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;



/**
 * Clase singletos que posee toda la Musica y Sonidos del juego.
 * @author Shuken
 *
 */
public class AudioManager {

	private static AudioManager instance= null;
	
	/** Indicates if audio should be played */
	public boolean audioON;
	
	/** ------------------- SOUNDS --------------------- */
	public Sound select, ficha, checkbox, key;
	
	/** ------------------- MUSIC --------------------- */
	public Music loginScreenMusic, mainMenuMusic;
	
	public AudioManager(){
		//Loading config...
		audioON= TaTeTi.gamePreferences.getBoolean("musicON");

	}
	
	/**
	 * Returns an instance of AudioManager.
	 * @return
	 */
	protected static AudioManager getInstance(){
		if(instance == null) instance= new AudioManager();
		
		return instance;
	}

	
	/**
	 * Load into memory all the music songs.
	 */
	protected void loadMusic(){
		loginScreenMusic= Gdx.audio.newMusic(Gdx.files.internal("assets/sounds/loginScreenMusic_lostVillage.mp3"));
		loginScreenMusic.setLooping(true);
		
		mainMenuMusic= Gdx.audio.newMusic(Gdx.files.internal("assets/sounds/mainMenuMusic_interface.mp3"));
		mainMenuMusic.setLooping(true);
		
	}
	
	
	
	/**
	 * Load into memory all the sounds of the game.
	 */
	protected void loadSounds(){
	
		select= Gdx.audio.newSound(Gdx.files.internal("assets/sounds/select.mp3"));
		checkbox= Gdx.audio.newSound(Gdx.files.internal("assets/sounds/check.mp3"));
		ficha= Gdx.audio.newSound(Gdx.files.internal("assets/sounds/ficha.mp3"));
		key= Gdx.audio.newSound(Gdx.files.internal("assets/sounds/key.mp3"));
	}
	
	
	
	
	
	public void disposeAll(){
		//Dispose music...
		loginScreenMusic.dispose();
		mainMenuMusic.dispose();
		
		//Dispose sounds...
		select.dispose();
		checkbox.dispose();
		ficha.dispose();
		key.dispose();
	}
}//fin de clase
