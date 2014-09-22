package shuken.Engine.Resources;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import shuken.TaTeTi.Config;
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
	
	/** Indica si los sonidos o musica estan activados.*/
	public boolean audioON;
	
	/** ------------------- SOUNDS --------------------- */
	public Sound select, ficha, checkbox, key;
	
	/** ------------------- MUSIC --------------------- */
	public Music song;
	
	public AudioManager(){
		//Cargamos las configuraciones...
		audioON= TaTeTi.gamePreferences.getBoolean("musicON");

	}
	
	/**
	 * Devuelve la instancia del AudioManager.
	 * @return
	 */
	protected static AudioManager getInstance(){
		if(instance == null) instance= new AudioManager();
		
		return instance;
	}

	
	/**
	 * Carga todos los archivos de musica.
	 */
	protected void loadMusic(){
		//song= Gdx.audio.newMusic(Gdx.files.internal("assets/Audio/Music/back to the future.mp3"));
		//songs.put("back to the", song);
		
		//menuMusic= Gdx.audio.newMusic(Gdx.files.internal("assets/music/menu.mp3"));
		//menuMusic.setLooping(true);
		//songs.put("menu", menuMusic);
		
		//gameplayMusic= Gdx.audio.newMusic(Gdx.files.internal("assets/music/gameplayMusic.mp3"));
		//gameplayMusic.setLooping(true);
		//songs.put("gameplay", gameplayMusic);
	}
	
	
	
	/**
	 * Carga todos los archivos de sonido.
	 */
	protected void loadSounds(){
	
		select= Gdx.audio.newSound(Gdx.files.internal("assets/sounds/select.mp3"));
		checkbox= Gdx.audio.newSound(Gdx.files.internal("assets/sounds/check.mp3"));
		ficha= Gdx.audio.newSound(Gdx.files.internal("assets/sounds/ficha.mp3"));
		key= Gdx.audio.newSound(Gdx.files.internal("assets/sounds/key.mp3"));
	}
	
	
	
	
	
	public void disposeAll(){
		//Liberamos todos los sonidos...
		
		
		//Liberamos toda la musica...
		
	}
}//fin de clase
