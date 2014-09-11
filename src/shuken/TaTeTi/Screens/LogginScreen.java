package shuken.TaTeTi.Screens;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import shuken.Engine.Basic.ShukenScreen;
import shuken.Engine.Resources.ResourceManager;
import shuken.Engine.ShukenInput.ShukenInput;
import shuken.Engine.SimpleGUI.ClickableArea;
import shuken.Engine.SimpleGUI.SimpleButton;
import shuken.Engine.SimpleGUI.SimpleCheckBox;
import shuken.Engine.SimpleGUI.SimpleErrorMessage;
import shuken.Engine.SimpleGUI.SimpleGUI;
import shuken.Engine.SimpleGUI.SimpleTextBox;
import shuken.Engine.SimpleGUI.TimeLabel;
import shuken.TaTeTi.Config;
import shuken.TaTeTi.GameSession;
import shuken.TaTeTi.TaTeTi;
import shuken.TaTeTi.Updateable;
import shuken.TaTeTi.Entities.Ficha;
import shuken.TaTeTi.Entities.Partida;
import shuken.TaTeTi.Entities.Player;
import shuken.TaTeTi.Network.Client;
import shuken.TaTeTi.Network.InetMessage;
import shuken.TaTeTi.Transitions.FadeTransition;
import shuken.TaTeTi.Transitions.Transition;



public class LogginScreen extends ShukenScreen implements Updateable{

	private SpriteBatch batch;
	private ShapeRenderer shapeRender;
	
	/** Transitions */
	private ArrayList<Transition> transitions;
	private Transition transitionIn, transitionToCreateAccount, transitionToMainMenu;
	
	/** GUI */
	private SimpleButton btnLoggin, btnCrearCuenta, btnReintentar;
	private SimpleTextBox txtUser, txtPass;
	private TimeLabel lblErrorMsg;
	private SimpleCheckBox checkBoxUser;
	
	/** ---------Flags para enviar mensajes al servidor.--------------- */
	protected boolean sendLogginRequest= false;
	String nickIngresado= "";		//esto se obtiene de textboxs en el menu principal
	String passIngresado= "";
	
	/** --------------------------------------------------------------- */
	
	
	
	public LogginScreen(){
		//Cargamos todos los recursos...
		ResourceManager.loadAllResources();
		//Inicializamos simple GUI...
		SimpleGUI.getInstance();
		
		batch= new SpriteBatch();
		shapeRender= new ShapeRenderer();
		
		//Inicializamos componentes de la gui...
		btnLoggin= new SimpleButton("Loggin", 150, 100, ResourceManager.fonts.defaultFont, ResourceManager.textures.button);
		btnCrearCuenta= new SimpleButton("Crear cuenta", 350, 100, ResourceManager.fonts.defaultFont, ResourceManager.textures.button);
		btnReintentar= new SimpleButton("Actualizar estado", 350, 10, ResourceManager.fonts.defaultFont, ResourceManager.textures.button);
		txtUser= new SimpleTextBox(150, 200, 150, 40, 10, ResourceManager.fonts.defaultFont, ResourceManager.textures.textbox);
		txtPass= new SimpleTextBox(150, 150, 150, 40, 10, ResourceManager.fonts.defaultFont, ResourceManager.textures.textbox);
		txtPass.setTextboxForPassword(true);
		lblErrorMsg= new TimeLabel("", 200, 70, ResourceManager.fonts.defaultFont, 2.5f, ResourceManager.textures.transition);
		checkBoxUser= new SimpleCheckBox(100, 200, 16, 16, ResourceManager.textures.checkbox_checked, ResourceManager.textures.checkbox_unchecked);
		SimpleGUI.getInstance().addAreaNoActive(btnLoggin);
		SimpleGUI.getInstance().addAreaNoActive(btnCrearCuenta);
		SimpleGUI.getInstance().addAreaNoActive(txtUser);
		SimpleGUI.getInstance().addAreaNoActive(txtPass);
		SimpleGUI.getInstance().addAreaNoActive(lblErrorMsg);
		SimpleGUI.getInstance().addAreaNoActive(btnReintentar);
		SimpleGUI.getInstance().addAreaNoActive(checkBoxUser);
		
	}//fin constructor
	
	@Override
	public void postCreate() {
		transitions= new ArrayList<Transition>();
		
		//TODO testcode
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
		
		updateTalkToServer();
		
		//Actualizamos gui...
		SimpleGUI.getInstance().update(delta);
		
		//TODO test code
		/*
		if(ShukenInput.getInstance().isKeyReleased(Keys.E)){
			ArrayList<String> text= new ArrayList<String>();
			text.add("Error inventado en logginnnnnnn");
			text.add("(Click para quitar mensaje)");
			SimpleGUI.getInstance().errorMsg.setText(text);
			SimpleGUI.getInstance().errorMsg.show();
		}
		*/
	}//fin update
	
	
	@Override
	public void render(float delta) {
		//Limpiamos screen...
		Gdx.gl.glClearColor(0f,0.2f,0.25f,0f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		
		if(Config.DEBUG_MODE){
			ResourceManager.fonts.defaultFont.draw(batch, "DEBUG MODE", 10, Gdx.graphics.getHeight()-5);
			ResourceManager.fonts.defaultFont.draw(batch, "Latency: " + GameSession.getInstance().getLatency() + "ms", 10, Gdx.graphics.getHeight() - 20);
			ResourceManager.fonts.defaultFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, Gdx.graphics.getHeight() - 35);
			ResourceManager.fonts.defaultFont.draw(batch, "LogginScreen", 10, Gdx.graphics.getHeight() - 50);
		}
		
		//Usuario no loggeado
		ResourceManager.fonts.defaultFont.draw(batch, "Nick: ", 100, 230);
		ResourceManager.fonts.defaultFont.draw(batch, "Pass: ", 100, 180);
		
		//Graficamos GUi...
		SimpleGUI.getInstance().render(batch);
				
		//Estado del servidor
		if(GameSession.getInstance().isConnectedToServer()) {
			ResourceManager.fonts.defaultFont.draw(batch, "Estado del servidor: ONLINE", 10, 20);
		}
		else {
			ResourceManager.fonts.defaultFont.draw(batch, "Estado del servidor: OFFLINE", 10, 20);
			batch.setColor(1, 1, 1, 0.5f);
			batch.draw(ResourceManager.textures.transition, 0, 75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - 150);
			batch.setColor(1, 1, 1, 1);
		}
		
		//Graficamos transiciones (si hay)
		for(int i= 0; i < transitions.size(); i++){
			if(transitions.get(i).isRunning()) transitions.get(i).render(batch, shapeRender);
		}
		
		batch.end();
		
		
		//Realizamos update
		this.update(TaTeTi.confirmDelta(delta));
	}//fin render


	
	
	/**
	 * Este metodo verifica los flags de envio de mensajes y solicita al GameSession que retransmita los mensajes al servidor.
	 */
	protected void updateTalkToServer(){
		
		if(sendLogginRequest){
			System.out.println("Client-side: Solicitando loggin...");
			GameSession.getInstance().sendLogginRequest(nickIngresado, passIngresado);
			sendLogginRequest= false;
		}
		
		
	}//fin updateTalkToServer
	
	
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
			//Loggin falló.
			GameSession.getInstance().closeSession();
			
			lblErrorMsg.setLabel(msg.strings.get(0));
			SimpleGUI.getInstance().turnAreaON(lblErrorMsg);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public void simpleGUI_Event(ClickableArea area) {

		if(area.equals(btnLoggin)){
			if(!GameSession.getInstance().isConnectedToServer()) return;
			
			//Verificamos que ambos campos esten completos
			if(txtUser.getTextNoConsume().compareTo("")== 0 || txtPass.getTextNoConsume().compareTo("")== 0){
				lblErrorMsg.setLabel("Campos incompletos.");
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
		
	}//fin simple gui event
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		//Mostramos user por defecto (o no)
		if(TaTeTi.gamePreferences.getBoolean("rememberUserName")){
			txtUser.putNewStringIntoText(TaTeTi.gamePreferences.getString("userName"));
		}else{
			//TODO test code
			txtUser.putNewStringIntoText("Player");
			txtPass.putNewStringIntoText("123");
		}
		
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
		
		//Inicializamos transicion de llegada
		transitionIn.start();
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
		
		//Reseteamos transiciones (just in case)
		for(int i= 0; i < transitions.size(); i++){
			transitions.get(i).clear();
		}
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}



	

	

}//fin clase
