package shuken.TaTeTi;

import javax.swing.JOptionPane;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Version;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import shuken.Engine.Basic.ShukenGame;
import shuken.Engine.Basic.ShukenScreen;
import shuken.Engine.Resources.ResourceManager;
import shuken.Engine.ShukenInput.ShukenInput;
import shuken.TaTeTi.Screens.CreateAccountScreen;
import shuken.TaTeTi.Screens.GamePlayScreen;
import shuken.TaTeTi.Screens.LoadingScreen;
import shuken.TaTeTi.Screens.LoginScreen;
import shuken.TaTeTi.Screens.MainMenuScreen;

public class TaTeTi extends ShukenGame{

	/** Screens del juego. */
	public ShukenScreen loadingScreen;
	public ShukenScreen loginScreen;
	public ShukenScreen createAccountScreen;
	public ShukenScreen mainMenuScreen;
	public ShukenScreen gameplay;
	
	public static void main(String args[]){
		//Seteamos variables static...
		gameTitle= "TaTeTi Online";
		gameVersion= "v1.0";
		gameReleaseVersionDate= "";
		gameAbout= "About this game...";
		setScreenSize(680, 460, false);
		//setScreenSize(1440, 900, true);
		
		//Creacion primero del config y luego de la aplicacion basada en el config
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();

		cfg.title = ShukenGame.gameTitle + " " + gameVersion + " | " + width + "x" + height + " | F.Marrone | " + gameReleaseVersionDate;
		cfg.fullscreen= ShukenGame.fullscreen;
		cfg.width = ShukenGame.width;
		cfg.height = ShukenGame.height;
		cfg.useGL20 = true;
		cfg.resizable= false;
		
		//Creamos la aplicacion...
		app= new LwjglApplication(new TaTeTi(), cfg);	//De aca se llama al metodo "create()"
		
		System.out.println("LWJGL version: " + Sys.getVersion());
		System.out.println("LibGDX version: " + Version.VERSION);
		System.out.println("Game version: " + gameVersion + " (" + gameReleaseVersionDate + ")");
		System.out.println("----------------------------------");
	}
	
	
	
	public static TaTeTi getInstance(){
		return (TaTeTi)ShukenGame.getInstance();
	}

	 
	public GamePlayScreen getGamePlay(){
		return (GamePlayScreen)gameplay;
	}
	
	public LoginScreen getLogginScreen(){
		return (LoginScreen)loginScreen;
	}
	
	public CreateAccountScreen getCreateAccountScreen(){
		return (CreateAccountScreen)createAccountScreen;
	}
	
	public MainMenuScreen getMainMenuScreen(){
		return (MainMenuScreen)mainMenuScreen;
	}
	
	@Override
	public void create() {
		//Guardamos referencia a la instancia...
		setInstance(this);
		
		//Configuramos input...
		Gdx.input.setInputProcessor(ShukenInput.getInstance());
		Gdx.input.setCatchBackKey(true);
		Keyboard.enableRepeatEvents(true);

		//Seteamos maximo valor de delta posible (para evitar deltas grandes que pueden generar bugs en colisiones y demas)...
		this.setMaxDelta(0.025f);		//25ms
		
		//Cargamos el archivo config
		Config.loadConfig();
		if(!Config.loadOK()){
			JOptionPane.showMessageDialog(null, "El archivo de configuracion esta dañado o no existe.\n" +
					"The config file it's damage or not exists.", "Config File Error", JOptionPane.ERROR_MESSAGE);
			app.exit();
		}
		//We load the localization profile...
		Localization.loadLanguage(Config.LANGUAGE);
		
		//Cargamos archivo de preferencias (o lo crea si es que no existe)...
		gamePreferences= app.getPreferences(".ttt");
		if(gamePreferences.getBoolean("Created", false)) { 
			// El archivo esta ya creado de antes y se presume, configurado por el usuario asique no se modifica.
		}
		else{
			//El archivo de preferencias a sido creado recientemente. Cargamos datos iniciales.
			gamePreferences.putBoolean("Created", true);
			gamePreferences.putBoolean("rememberUserName", false);
			gamePreferences.putString("userName", "");
			gamePreferences.putBoolean("musicON", true);
			gamePreferences.flush();
		}
		
		//Instanciamos el GameSession...
		GameSession.getInstance();
		
		//Creamos los screens...
		loadingScreen= new LoadingScreen();
		loginScreen= new LoginScreen();
		createAccountScreen= new CreateAccountScreen();
		mainMenuScreen= new MainMenuScreen();
		gameplay= new GamePlayScreen();
		
		//Llamamos a sus respectivos post-create por si requieren hacer cosas que interactuan con esta clase u otros screens...
		loadingScreen.postCreate();
		loginScreen.postCreate();
		createAccountScreen.postCreate();
		mainMenuScreen.postCreate();
		gameplay.postCreate();
		
		//Seteamos el screen inicial...
		//this.setScreen(logginScreen);
		this.setScreen(loadingScreen);
	}//fin create



	@Override
	public void dispose() {
		ResourceManager.disposeAllResources();
		
		loadingScreen.dispose();
		loginScreen.dispose();
		createAccountScreen.dispose();
		mainMenuScreen.dispose();
		gameplay.dispose();
	}

	
}//fin clase
