package shuken.Engine.Resources;

/**
 * The ResourceManager class contains all the resources of the game (textures, sounds, etc) so they can be accesed by any class in any moment.
 * 
 * Its a public Singleton class.
 * 
 * @author F. Marrone
 *
 */
public class ResourceManager {

	/** Total time required to load all the resources into memory. */
	public static long loadTime;
	public static boolean loadDone= false;
	public static int percentLoad= 0;
	
	//***************** LIST OF RESOURCES *******************//
	public static TextureManager textures;
	public static AudioManager audio;
	public static FontManager fonts;
	//*******************************************************//
	
	
	//Singleton class.
	private ResourceManager(){};
	
	/**
	 * Load ALL the resources into memory.
	 */
	public static void loadAllResources(){
		//Start load...
		loadTime= System.currentTimeMillis();
		
		//Fonts...
		fonts= FontManager.getInstance();
		fonts.loadFonts();
		
		percentLoad= 15;
		
		//Textures...
		textures= TextureManager.getInstance();
		textures.loadTextures();
		
		percentLoad= 65;
		
		//Music...
		audio= AudioManager.getInstance();
		audio.loadMusic();
		
		percentLoad= 80;
		
		//Sounds...
		audio.loadSounds();
		
		percentLoad= 100;
		
		//System.out.println("RAM usage on resource manager: " + (Runtime.getRuntime().totalMemory())/1024/1024 + "mb");
		loadTime= System.currentTimeMillis() - loadTime;
		System.out.println("ResourceManager: Load Complete (" + loadTime + "ms)");
		loadDone= true;
	}
	
	public static boolean isAudioOn(){
		return audio.audioON;
	}
	

	public static void disposeAllResources(){
		System.out.println("Disposing resources...");
		textures.disposeAll();
		audio.disposeAll();
		fonts.disposeAll();
	}
}//fin clase
