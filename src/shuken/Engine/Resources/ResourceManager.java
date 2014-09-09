package shuken.Engine.Resources;

/**
 * El Resource Manager es una clase q concentra toda la carga de imagenes, sonidos, etc, de una sola vez y las hace accesibles a cualquier
 * clase en cualquier momento.
 * 
 * No es una clase que se pueda instanciar, y todos sus atributos son public static.
 * 
 * @author Shuken
 *
 */
public class ResourceManager {

	/** Tiempo total que demora el ResourceManager en cargar TODOS los recursos en memoria. */
	public static long loadTime;
	
	//***************** LISTADO DE RESOURCES *******************//
	
	/** Acceso a las texturas cargadas. */
	public static TextureManager textures;
	/** Acceso a las musicas y sonidos cargados. */
	public static AudioManager audio;
	/** Acceso a tipos de letras cargadas. */
	public static FontManager fonts;
	//**********************************************************//
	
	
	
	//Constructor privado. Clase singleton.
	private ResourceManager(){};
	
	/**
	 * Carga TODOS los recursos (imagenes, audio, etc.)
	 */
	public static void loadAllResources(){
		//Iniciamos carga...
		loadTime= System.currentTimeMillis();
		
		//Cargamos las fuentes...
		fonts= FontManager.getInstance();
		fonts.loadFonts();
		
		//Cargamos las texturas...
		textures= TextureManager.getInstance();
		textures.loadTextures();
		
		//Cargamos la musica y sonidos...
		audio= AudioManager.getInstance();
		audio.loadMusic();
		audio.loadSounds();
		
		
		
		
		
		//System.out.println("RAM usage on resource manager: " + (Runtime.getRuntime().totalMemory())/1024/1024 + "mb");
		loadTime= System.currentTimeMillis() - loadTime;
		System.out.println("ResourceManager: Load Complete (" + loadTime + "ms)");
	}
	
	
	/**
	 * Libera todos los recursos utilizados.
	 */
	public static void disposeAllResources(){
		textures.disposeAll();
		audio.disposeAll();
		fonts.disposeAll();
	}
}//fin clase
