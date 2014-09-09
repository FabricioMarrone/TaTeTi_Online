package shuken.Engine.Basic;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.math.Rectangle;


/**
 * Clase base que todo juego posee. Implementa datos y metodos básicos. Esta clase debe extenderse para formar lo que es el juego en si
 * mismo. Están comentados los metodos main() y create() para que puedan colocarse facilmente en la clase extendida.
 * @author Shuken
 *
 */
public abstract class ShukenGame extends Game {
	
	
	/** Nombre del Juego */
	public static String gameTitle;
	/** Version actual del juego. */
	public static String gameVersion;
	/** Fecha de lanzamiento de la version actual. */
	public static String gameReleaseVersionDate;
	/** Acerca de... */
	public static String gameAbout;
	
	/** Instancia del juego. */
	private static ShukenGame instanceOfGame;
	
	/** LWJGL Aplication. */
	public static LwjglApplication app;
	
	/** Preferencias del juego. */
	public static Preferences gamePreferences;
	
	/** Delta max limit. Este es el maximo valor de delta que se pasara como parametro a los screens que llamen a "confirmDelta". */
	protected static float maxDelta= 0.05f;	//50ms
	
	/** Tamaño de la pantalla. */
	public static int width;
    public static int height;
    public static boolean fullscreen;
    
    /** Rectangulo con el tamaño exacto de la pantalla. */
    public static Rectangle screenSize;
    /** Rectangulo con un tamaño un poco mayor al de la pantalla, utilizado fundamentalmente en la emision de particulas fuera de pantalla. */
    public static Rectangle screenOffSize;
    protected static int offSize= 50;	//auxiliar, tamaño extra que tiene el screenOffSize con relacion a screenSize.
	
	
    
    public static ShukenGame getInstance(){
    	return instanceOfGame;
    }
    
    
    protected void setInstance(ShukenGame instance){
    	instanceOfGame= instance;
    }
    
	/**
	 * El metodo main debe ser quitado de aqui e implementado por la clase que extienda a ShukenGame.
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
	 * Devuelve el ShukenScreen actual.
	 * @return
	 */
	public ShukenScreen getActualScreen(){
		return (ShukenScreen)this.getScreen();
	}
	
	/**
	 * Verifica si el screen pasado como parámetro es el "actual screen" del juego. En cuyo caso devuelve true, false otherwise.
	 * @param screen Algun ShukenScreen del juego
	 * @return
	 */
	public boolean isActualScreen(ShukenScreen screen){
		if(this.getActualScreen().equals(screen)) return true;
		else return false;
	}
	
	
	/**
	 * Almacena la información del tamaño de la pantalla y calcula el "screenSize".
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
	 * Toma el valor del delta pasado como parámetro y lo devuelve corregido luego de aplicar el limite de maxDelta. Si el delta es menor
	 * al limite maximo, entonces se devuelve sin cambios.
	 * @param delta
	 * @return
	 */
	public static float confirmDelta(float delta){
		if(delta > maxDelta) return maxDelta;
		
		return delta;
	}
	
	
	/**
	 * Setea el maximo valor de delta que se pasara a la logica del juego. Antes de cada update, el delta debe pasar por el metodo
	 *<code> confirmDelta(float delta)</code> de la clase ShukenGame para que devuelva un delta que respete los limites.
	 * @param max
	 */
	protected void setMaxDelta(float max){
		maxDelta= max;
	}
}//fin clase
