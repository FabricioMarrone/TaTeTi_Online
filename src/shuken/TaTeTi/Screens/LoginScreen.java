package shuken.TaTeTi.Screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import shuken.Engine.Basic.ShukenScreen;
import shuken.Engine.Resources.ResourceManager;
import shuken.Engine.ShukenInput.ShukenInput;
import shuken.Engine.SimpleGUI.ClickableArea;
import shuken.Engine.SimpleGUI.SimpleButton;
import shuken.Engine.SimpleGUI.SimpleCheckBox;
import shuken.Engine.SimpleGUI.SimpleGUI;
import shuken.Engine.SimpleGUI.SimpleTextBox;
import shuken.Engine.SimpleGUI.TimeLabel;
import shuken.TaTeTi.Config;
import shuken.TaTeTi.GameSession;
import shuken.TaTeTi.Localization;
import shuken.TaTeTi.ServerMessages;
import shuken.TaTeTi.Localization.Languages;
import shuken.TaTeTi.TaTeTi;
import shuken.TaTeTi.Updateable;
import shuken.TaTeTi.Entities.Player;
import shuken.TaTeTi.Network.InetMessage;
import shuken.TaTeTi.Transitions.FadeTransition;
import shuken.TaTeTi.Transitions.Transition;

public class LoginScreen extends ShukenScreen implements Updateable{

	private SpriteBatch batch;
	private ShapeRenderer shapeRender;
	
	private float volume= 1f;
	
	/** Transitions */
	private ArrayList<Transition> transitions;
	private Transition transitionIn, transitionToCreateAccount, transitionToMainMenu;
	
	/** GUI */
	private SimpleButton btnLoggin, btnCrearCuenta, btnReintentar;
	private SimpleTextBox txtUser, txtPass;
	private TimeLabel lblErrorMsg;
	private SimpleCheckBox checkBoxUser, checkBoxMusic;
	
	/** ---------Flags to send message to the server. ----------------- */
	protected boolean sendLogginRequest= false;
	String nickIngresado= "";		//esto se obtiene de textboxs en el menu principal
	String passIngresado= "";
	
	/** --------------------------------------------------------------- */
	
	private int tab_index= 0;
	private int tab_index_max= 1;
	
	public LoginScreen(){
		//Cargamos todos los recursos...
		//ResourceManager.loadAllResources();
		//Inicializamos simple GUI...
		//SimpleGUI.getInstance();
		
		batch= new SpriteBatch();
		shapeRender= new ShapeRenderer();
		
		//Inicializamos componentes de la gui...
		btnLoggin= new SimpleButton(Localization.Ingresar, 310, 60, ResourceManager.fonts.gameText, ResourceManager.textures.button, false);
		btnCrearCuenta= new SimpleButton(Localization.CrearCuenta, 150, 60, ResourceManager.fonts.gameText, ResourceManager.textures.button, false);
		btnReintentar= new SimpleButton(Localization.ActualizarEstado, 350, 10, ResourceManager.fonts.gameText, ResourceManager.textures.button, false);
		txtUser= new SimpleTextBox(250, 203, 19, ResourceManager.fonts.gameText, ResourceManager.textures.textbox);
		txtPass= new SimpleTextBox(250, 153, 19, ResourceManager.fonts.gameText, ResourceManager.textures.textbox);
		txtPass.setTextboxForPassword(true);
		checkBoxUser= new SimpleCheckBox(410, 126, ResourceManager.textures.checkbox_checked, ResourceManager.textures.checkbox_unchecked);
		checkBoxMusic= new SimpleCheckBox(410, 103, ResourceManager.textures.checkbox_checked, ResourceManager.textures.checkbox_unchecked);
		lblErrorMsg= new TimeLabel("", 200, 100, ResourceManager.fonts.gameText, 10f, ResourceManager.textures.transition);
		SimpleGUI.getInstance().addAreaNoActive(btnLoggin);
		SimpleGUI.getInstance().addAreaNoActive(btnCrearCuenta);
		SimpleGUI.getInstance().addAreaNoActive(txtUser);
		SimpleGUI.getInstance().addAreaNoActive(txtPass);
		SimpleGUI.getInstance().addAreaNoActive(btnReintentar);
		SimpleGUI.getInstance().addAreaNoActive(checkBoxUser);
		SimpleGUI.getInstance().addAreaNoActive(checkBoxMusic);
		SimpleGUI.getInstance().addAreaNoActive(lblErrorMsg);
		
	}
	
	@Override
	public void postCreate() {
		transitions= new ArrayList<Transition>();
		
		float transitionTime= 0.45f;
		
		transitionIn= new FadeTransition(transitionTime, null, true);
		transitionToCreateAccount= new FadeTransition(transitionTime, TaTeTi.getInstance().createAccountScreen, false);
		transitionToMainMenu= new FadeTransition(transitionTime, TaTeTi.getInstance().mainMenuScreen, false);
		
		transitions.add(transitionIn);
		transitions.add(transitionToCreateAccount);
		transitions.add(transitionToMainMenu);
	}
	
	@Override
	public void update(float delta) {
		
		if(!GameSession.getInstance().isConnectedToServer()){
			SimpleGUI.getInstance().turnAreaON(btnReintentar);
			GameSession.getInstance().connectToServer();
		}else{
			SimpleGUI.getInstance().turnAreaOFF(btnReintentar);
		}
		
		//Actualizamos gameSession...
		GameSession.getInstance().update(delta);
			
		//Si estamos en una transicion...
		for(int i= 0; i < transitions.size(); i++){
			if(transitions.get(i).isRunning()){
				transitions.get(i).update(delta);
				return;
			}
		}
		
		if(transitionToMainMenu.isRunning()){
			volume-= delta * 0.1f;
			if(volume < 0) volume= 0;
			if(ResourceManager.isAudioOn()) ResourceManager.audio.loginScreenMusic.setVolume(volume);
		}
		
		updateTalkToServer();
		
		//tab index
		if(ShukenInput.getInstance().isKeyReleased(Keys.TAB)){
			tab_index++;
			if(tab_index > tab_index_max) tab_index= 0;
			
			txtUser.clickOff();
			txtPass.clickOff();
			
			switch(tab_index){
			case 0: txtUser.clickOn(); break;
			case 1: txtPass.clickOn(); break;
			}
		}
		
		//Actualizamos gui...
		SimpleGUI.getInstance().update(delta);
		
	}//end update
	
	@Override
	public void render(float delta) {
		//Limpiamos screen...
		Gdx.gl.glClearColor(0f,0.2f,0.25f,0f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		
		//Graficamos fondo
		batch.draw(ResourceManager.textures.backgroundLoggin, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		//Graficamos title
		batch.draw(ResourceManager.textures.gameTitle, (Gdx.graphics.getWidth() - ResourceManager.textures.gameTitle.getRegionWidth())/2, 280);
		
		ResourceManager.fonts.gameText.draw(batch, "Fabricio Marrone - UTNFRRo - 2014", Gdx.graphics.getWidth() - 249, Gdx.graphics.getHeight() - 12);
		
		if(Config.DEBUG_MODE){
			ResourceManager.fonts.defaultFont.draw(batch, "DEBUG MODE", 10, Gdx.graphics.getHeight()-5);
			ResourceManager.fonts.defaultFont.draw(batch, "Latency: " + GameSession.getInstance().getLatency() + "ms", 10, Gdx.graphics.getHeight() - 20);
			ResourceManager.fonts.defaultFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, Gdx.graphics.getHeight() - 35);
			ResourceManager.fonts.defaultFont.draw(batch, "LoginScreen", 10, Gdx.graphics.getHeight() - 50);
		}
		
		ResourceManager.fonts.UIlabelsFont.draw(batch, Localization.Usuario_dp, 155, 230);
		ResourceManager.fonts.UIlabelsFont.draw(batch, Localization.Contrase�a_dp, 112, 180);
		
		ResourceManager.fonts.gameText.draw(batch, Localization.RecordarNombreUsuario, 210, 140);
		ResourceManager.fonts.gameText.draw(batch, Localization.MusicaON, 327, 115);
		
		//Graficamos GUi...
		SimpleGUI.getInstance().render(batch);
				
		//Estado del servidor
		ResourceManager.fonts.UIlabelsFont.draw(batch, Localization.EstadoDelServidor_dp, 10, 25);
		int posX= 0;
		if(Localization.getCurrentLanguage() == Languages.ES) posX= 195;
		if(Localization.getCurrentLanguage() == Languages.EN) posX= 130;
		if(GameSession.getInstance().isConnectedToServer()) {
			ResourceManager.fonts.UIlabelsFont.setColor(0, 0.88f, 0, 1);
			ResourceManager.fonts.UIlabelsFont.draw(batch, "online", posX, 25);
			ResourceManager.fonts.UIlabelsFont.setColor(Color.WHITE);
		}
		else {
			ResourceManager.fonts.UIlabelsFont.setColor(0.8f, 0, 0, 1);
			ResourceManager.fonts.UIlabelsFont.draw(batch, "offline", posX, 25);
			ResourceManager.fonts.UIlabelsFont.setColor(Color.WHITE);
			batch.setColor(1, 1, 1, 0.5f);
			batch.draw(ResourceManager.textures.transition, 0, 50, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - 130);
			batch.setColor(1, 1, 1, 1);
		}
		
		//Graficamos transiciones (si hay)
		for(int i= 0; i < transitions.size(); i++){
			if(transitions.get(i).isRunning()) transitions.get(i).render(batch, shapeRender);
		}
		
		batch.end();
		
		//Realizamos update
		this.update(TaTeTi.confirmDelta(delta));
	}//end render

	protected void updateTalkToServer(){
		if(sendLogginRequest){
			System.out.println("Client-side: Solicitando loggin...");
			GameSession.getInstance().sendLogginRequest(nickIngresado, passIngresado);
			sendLogginRequest= false;
		}
	}
	
	public void SaC_Respuesta_Loggin_request(InetMessage msg){
		//System.out.println(msg.strings.get(0));
		
		if(msg.booleans.get(0)){
			//Loggin exitoso.
			int won= msg.ints.get(0);
			int lose= msg.ints.get(1);
			int draw= msg.ints.get(2);
			GameSession.getInstance().startSession(new Player(nickIngresado, passIngresado, won, lose, draw));
			
			//Cambiamos de screen (nos vamos al main menu)
			transitionToMainMenu.start();
		}else{
			//Loggin fall�.
			GameSession.getInstance().closeSession();
			lblErrorMsg.reset();
			String cod= msg.strings.get(0);
			lblErrorMsg.setLabel(ServerMessages.getMessage(cod));
			SimpleGUI.getInstance().turnAreaON(lblErrorMsg);
		}
	}
	
	@Override
	public void simpleGUI_Event(ClickableArea area) {

		if(area.equals(btnLoggin)){
			if(!GameSession.getInstance().isConnectedToServer()) return;
			
			//Verificamos que ambos campos esten completos
			if(txtUser.getTextNoConsume().compareTo("")== 0 || txtPass.getTextNoConsume().compareTo("")== 0){
				lblErrorMsg.reset();
				lblErrorMsg.setLabel(Localization.ErrorMsg_CamposIncompletos);
				SimpleGUI.getInstance().turnAreaON(lblErrorMsg);
				return;
			}
			
			sendLogginRequest= true;
			nickIngresado= txtUser.getTextNoConsume();
			passIngresado= txtPass.getText();
			
			//Guardamos user si ha de guardarse
			if(TaTeTi.gamePreferences.getBoolean("rememberUserName")){
				TaTeTi.gamePreferences.putString("userName", nickIngresado);
				TaTeTi.gamePreferences.flush();
			}
			
			//Limpiamos Input
			ShukenInput.getInstance().cleanAll();
		}
		
		if(area.equals(btnCrearCuenta)){
			if(!GameSession.getInstance().isConnectedToServer()) return;
			
			transitionToCreateAccount.start();
		}
		
		if(area.equals(btnReintentar)){
			GameSession.getInstance().tryConnectToServer();
		}
		
		if(area.equals(checkBoxUser)){
			if(checkBoxUser.isChecked()){
				TaTeTi.gamePreferences.putBoolean("rememberUserName", true);
			}else{
				TaTeTi.gamePreferences.putBoolean("rememberUserName", false);
			}
			TaTeTi.gamePreferences.flush();
		}
		
		if(area.equals(checkBoxMusic)){
			if(checkBoxMusic.isChecked()){
				TaTeTi.gamePreferences.putBoolean("musicON", true);
				ResourceManager.audio.audioON= true;
				ResourceManager.audio.loginScreenMusic.play();
			}else{
				TaTeTi.gamePreferences.putBoolean("musicON", false);
				ResourceManager.audio.audioON= false;
				ResourceManager.audio.loginScreenMusic.stop();
			}
			TaTeTi.gamePreferences.flush();
		}
	}//end gui event
	
	@Override
	public void resize(int width, int height) {}

	@Override
	public void show() {
		//Mostramos user por defecto (o no)
		if(TaTeTi.gamePreferences.getBoolean("rememberUserName")){
			txtUser.putNewStringIntoText(TaTeTi.gamePreferences.getString("userName"));
			txtPass.clickOn();
			tab_index= 1;
			checkBoxUser.setChecked(true);
		}else{
			checkBoxUser.setChecked(false);
			txtUser.putNewStringIntoText("");
			txtUser.clickOn();
			tab_index= 0;
		}
		
		//Valor por defecto y con foco
		txtPass.putNewStringIntoText("");
		
		//music On off
		checkBoxMusic.setChecked(TaTeTi.gamePreferences.getBoolean("musicON"));
		
		//Si hay clicks, los limpiamos derecho viejo
		if(ShukenInput.getInstance().hasBeenClicked(0)){
			ShukenInput.getInstance().consumeClick(0);
		}
				
		//Mostramos componentes de la gui...
		SimpleGUI.getInstance().turnAreaON(btnLoggin);
		SimpleGUI.getInstance().turnAreaON(btnCrearCuenta);
		SimpleGUI.getInstance().turnAreaON(txtUser);
		SimpleGUI.getInstance().turnAreaON(txtPass);
		SimpleGUI.getInstance().turnAreaON(btnReintentar);
		SimpleGUI.getInstance().turnAreaON(checkBoxUser);
		SimpleGUI.getInstance().turnAreaON(checkBoxMusic);
		
		//Inicializamos transicion de llegada
		transitionIn.start();
		
		//Cortamos musica de otro screen...
		ResourceManager.audio.mainMenuMusic.stop();
		//Inicializamos musica de fondo...
		if(!ResourceManager.audio.loginScreenMusic.isPlaying() && ResourceManager.isAudioOn()){
			volume= 0.4f;
			ResourceManager.audio.loginScreenMusic.setVolume(volume);
			ResourceManager.audio.loginScreenMusic.play();
		}
	}

	@Override
	public void hide() {
		//Ocultamos gui
		SimpleGUI.getInstance().turnAreaOFF(btnLoggin);
		SimpleGUI.getInstance().turnAreaOFF(btnCrearCuenta);
		SimpleGUI.getInstance().turnAreaOFF(txtUser);
		SimpleGUI.getInstance().turnAreaOFF(txtPass);
		SimpleGUI.getInstance().turnAreaOFF(btnReintentar);
		SimpleGUI.getInstance().turnAreaOFF(checkBoxUser);
		SimpleGUI.getInstance().turnAreaOFF(checkBoxMusic);
		SimpleGUI.getInstance().turnAreaOFF(lblErrorMsg);
		
		//Reseteamos transiciones (just in case)
		for(int i= 0; i < transitions.size(); i++){
			transitions.get(i).clear();
		}
		
		//Cortamos musica...(no lo hacemos aca, dejamos que el otro screen la corte)
		//ResourceManager.audio.loginScreenMusic.stop();
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void dispose() {
		System.out.println("Login screen dispose...");
		batch.dispose();
		shapeRender.dispose();
	}

}//end class
