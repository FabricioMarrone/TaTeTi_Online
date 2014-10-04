package shuken.Engine.Basic;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.math.Rectangle;


/**
 * Base class that every game will extend. Implements basic data and methods. 
 * Methods main() and create() are commented to be easily moved into the extended class.
 * 
 * @author F. Marrone
 *
 */
public abstract class ShukenGame extends Game {
	
	
	/** Name of the game */
	public static String gameTitle;
	/** Game current version */
	public static String gameVersion;
	/** Release date (current version) */
	public static String gameReleaseVersionDate;
	/** About...(optional, of course) */
	public static String gameAbout;
	
	/** Instance of the game */
	private static ShukenGame instanceOfGame;
	
	/** LWJGL Application. */
	public static LwjglApplication app;
	
	/** Game preferences (are located on "c:/users/<name>/prefs/") */
	public static Preferences gamePreferences;
	
	/** This is the maximum value of delta that will be passed to the update if the screen calls to "confirmDelta". Prevents tunneling. */
	protected static float maxDelta= 0.05f;	//50ms
	
	/** Screen size and mode */
	public static int width;
    public static int height;
    public static boolean fullscreen;
    
    /** Rectangle of the size of the screen */
    public static Rectangle screenSize;
    /** Rectangle a little bit greater than the size of the screen (usefully for particles outside screen) */
    public static Rectangle screenOffSize;
    protected static int offSize= 50;	//auxiliar, tamaño extra que tiene el screenOffSize con relacion a screenSize.
	
	
    
    public static ShukenGame getInstance(){
    	return instanceOfGame;
    }
    
    
    protected void setInstance(ShukenGame instance){
    	instanceOfGame= instance;
    }
    
	/**
	 * The main method must be implemented by the extended class.
	 * @param args
	 */
	/*
	public static void main(String args[]){
		//Seteamos variables static...
		gameTitle= "Title";
		gameVersion= "v0.0.0";
		gameReleaseVersionDate= "DD/MM/AA";
		gameAbout= "About this game...";
		setScreenSize(960, 600, false);
		
		//Creacion primero del config y luego de la aplicacion basada en el config
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();

		cfg.title = ShukenGame.gameTitle;
		//cfg.useGL20 = false;
		cfg.width = width;
		cfg.height = height;
		cfg.resizable= false;
		
		//Creamos la aplicacion...
		LwjglApplication app= new LwjglApplication(new ShukenGame(), cfg);	//De aca se llama al metodo "create()"
		
		System.out.println("LWJGL version: " + Sys.getVersion());
		System.out.println("LibGDX version: " + Version.VERSION);
		System.out.println("----------------------------------");
	}
	*/
	
	@Override
	public abstract void create();
	
	/*
	@Override
	public void create() {
		//Creamos los objetos singleton accesibles desde cualquier lado...
		instanceOfGame= this;
		input= new ShuKenInput(this);
		gui= new ShuKenGUI(this);

		//Configuramos input...
		Gdx.input.setInputProcessor(input);
		Gdx.input.setCatchBackKey(true);
		Keyboard.enableRepeatEvents(true);

		//Seteamos maximo valor de delta posible (para evitar deltas grandes que pueden generar bugs en colisiones y demas)...
		this.setMaxDelta(0.025f);		//25ms

		//Cargamos archivo de preferencias (o lo crea si es que no existe)...
		gamePreferences= app.getPreferences(".typingFighter");
		if(gamePreferences.getBoolean("Created", false)) { 
			// El archivo esta ya creado de antes y se presume, configurado por el usuario asique no se modifica.
		}
		else{
			//El archivo de preferencias a sido creado recientemente. Cargamos datos iniciales.
			gamePreferences.putBoolean("Created", true);
			gamePreferences.putBoolean("musicON", true);
			gamePreferences.putBoolean("showIntro", true);
			gamePreferences.flush();
		}
		
		//Creamos los screens...
		testScreen= new TestScreen(this);
		
		//Seteamos el screen inicial...
		this.setScreen(testScreen);
		
	}//fin create
	*/
	
	/**
	 * Returns the actual screen.
	 */
	public ShukenScreen getCurrentScreen(){
		return (ShukenScreen)this.getScreen();
	}
	
	/**
	 * Checks if the screen is the "current screen".
	 * @return 
	 */
	public boolean isCurrentScreen(ShukenScreen screen){
		if(this.getCurrentScreen().equals(screen)) return true;
		else return false;
	}
	
	
	/**
	 * Saves the screen info and calculates the "screenSize".
	 * @param width
	 * @param height
	 * @param fullscreen
	 */
	public static void setScreenSize(int width, int height, boolean fullscreen){
		ShukenGame.width= width;
		ShukenGame.height= height;
		ShukenGame.fullscreen= fullscreen;
		
		screenSize= new Rectangle(0, 0, width, height);
		screenOffSize= new Rectangle(0 - offSize, 0 - offSize, width + offSize*2, height + offSize*2);
	}
	
	/**
	 * Takes the delta parameter and returns a delta witch is not greater than "maxDelta".
	 * @param delta
	 * @return
	 */
	public static float confirmDelta(float delta){
		if(delta > maxDelta) return maxDelta;
		
		return delta;
	}
	
	
	/**
	 * Sets the max delta value. So, before every update, the delta value must be confirmed by <code> confirmDelta(float delta)</code> method.
	 * @param max
	 */
	protected void setMaxDelta(float max){
		maxDelta= max;
	}
	
}//fin clase
