package shuken.Engine.Resources;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
	public boolean soundON, musicON;
	
	
	
	
	
	
	/** ------------------- SOUNDS --------------------- */
	/** Acceso a sonidos mediante keys. Se utilizan hashmaps para acelerar la busqueda.*/
	private HashMap<String, Sound> sounds= null;
	
	/** Sonidos de prueba. */
	public Sound cuak, groundTouch;
	
	
	
	
	
	/** ------------------- MUSIC --------------------- */
	/** Acceso a musica mediante keys. Se utilizan hashmaps para acelerar la busqueda.*/
	private HashMap<String, Music> songs= null;
	
	/** Musica de prueba. */
	public Music song;
	
	public Music menuMusic, gameplayMusic;
	
	
	public AudioManager(){
		//Cargamos las configuraciones...
		//soundON= Config.soundON;
		//musicON= Config.musicON;
		soundON= true;
		musicON= true;
		
		//Inicializamos hashmaps...
		sounds= new HashMap<String, Sound>();
		songs= new HashMap<String, Music>();
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
		
		
		//cuak= Gdx.audio.newSound(Gdx.files.internal("assets/Audio/SFX/cuak.mp3"));
		//sounds.put("cuak", cuak);
		
		//groundTouch= Gdx.audio.newSound(Gdx.files.internal("assets/Audio/SFX/groundTouch.mp3"));
		//sounds.put("groundTouch", groundTouch);
	}
	
	
	
	
	
	/**
	 * Devuelve el sonido asociado al key. Esto permite que scripts, por ejemplo, obtengan un sonido en particular para utilizarlo.
	 * @param key
	 * @return
	 */
	public Sound getSound(String key){
		return sounds.get(key);
	}
	
	/**
	 * Devuelve la musica asociada al key. Esto permite que scripts, por ejemplo, obtengan una canción en particular para utilizarla.
	 * @param key
	 * @return
	 */
	public Music getSong(String key){
		return songs.get(key);
	}
	
	
	
	public void disposeAll(){
		//Liberamos todos los sonidos...
		Iterator<Map.Entry<String, Sound>> soundIterator= sounds.entrySet().iterator();
		while(soundIterator.hasNext()) soundIterator.next().getValue().dispose();
		
		//Liberamos toda la musica...
		Iterator<Map.Entry<String, Music>> musicIterator= songs.entrySet().iterator();
		while(musicIterator.hasNext()) musicIterator.next().getValue().dispose();
	}
}//fin de clase
