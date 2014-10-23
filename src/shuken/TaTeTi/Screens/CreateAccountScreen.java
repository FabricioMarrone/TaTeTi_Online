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
import shuken.Engine.SimpleGUI.SimpleGUI;
import shuken.Engine.SimpleGUI.SimpleTextBox;
import shuken.Engine.SimpleGUI.TimeLabel;
import shuken.TaTeTi.Config;
import shuken.TaTeTi.GameSession;
import shuken.TaTeTi.Localization;
import shuken.TaTeTi.Localization.Languages;
import shuken.TaTeTi.TaTeTi;
import shuken.TaTeTi.Updateable;
import shuken.TaTeTi.Network.InetMessage;
import shuken.TaTeTi.Transitions.FadeTransition;
import shuken.TaTeTi.Transitions.Transition;

public class CreateAccountScreen extends ShukenScreen implements Updateable{

	private SpriteBatch batch;
	private ShapeRenderer shapeRender;
	
	/** Transitions */
	private ArrayList<Transition> transitions;
	private Transition transitionIn, transitionToLogginScreen;
	
	/** GUI */
	private SimpleButton btnCrear, btnCancelar, btnAceptar;
	private SimpleTextBox txtUser, txtPass1, txtPass2;
	private TimeLabel lblErrorMsg;
	
	private static enum ScreenStates{
		Normal,
		AccountCreated
	}
	private ScreenStates state;
	
	/** ---------Flags para enviar mensajes al servidor.--------------- */
	protected boolean sendCreateAccount= false;
	String nickIngresado= "";		//esto se obtiene de textboxs
	String passIngresado1= "";
	String passIngresado2= "";
	
	/** --------------------------------------------------------------- */
	
	private int tab_index= 0;
	private int tab_index_max= 2;
	
	public CreateAccountScreen(){
		batch= new SpriteBatch();
		shapeRender= new ShapeRenderer();
		
		btnCrear= new SimpleButton(Localization.Crear, 325, 50, ResourceManager.fonts.gameText, ResourceManager.textures.button, false);
		btnCancelar= new SimpleButton(Localization.Cancelar, 180, 50, ResourceManager.fonts.gameText, ResourceManager.textures.button, false);
		btnAceptar= new SimpleButton(Localization.Aceptar, 280, 100, ResourceManager.fonts.gameText, ResourceManager.textures.button, false);
		txtUser= new SimpleTextBox(265, 230, 19, ResourceManager.fonts.gameText, ResourceManager.textures.textbox);
		txtPass1= new SimpleTextBox(265, 180, 19, ResourceManager.fonts.gameText, ResourceManager.textures.textbox);
		txtPass1.setTextboxForPassword(true);
		txtPass2= new SimpleTextBox(265, 130, 19, ResourceManager.fonts.gameText, ResourceManager.textures.textbox);
		txtPass2.setTextboxForPassword(true);
		lblErrorMsg= new TimeLabel("", 200, 90, ResourceManager.fonts.gameText, 4.5f, ResourceManager.textures.transition);
		
		SimpleGUI.getInstance().addAreaNoActive(btnCrear);
		SimpleGUI.getInstance().addAreaNoActive(btnCancelar);
		SimpleGUI.getInstance().addAreaNoActive(btnAceptar);
		SimpleGUI.getInstance().addAreaNoActive(txtUser);
		SimpleGUI.getInstance().addAreaNoActive(txtPass1);
		SimpleGUI.getInstance().addAreaNoActive(txtPass2);
		SimpleGUI.getInstance().addAreaNoActive(lblErrorMsg);
		
	}//end create
	
	@Override
	public void postCreate() {
		transitions= new ArrayList<Transition>();
		
		float transitionTime= 0.45f;
				
		transitionIn= new FadeTransition(transitionTime, null, true);
		transitionToLogginScreen= new FadeTransition(transitionTime, TaTeTi.getInstance().loginScreen, false);
		
		transitions.add(transitionIn);
		transitions.add(transitionToLogginScreen);
	}
	
	@Override
	public void update(float delta) {
		//Checkeamos por perdida de conexion, en cuyo caso redirigimos al loggin screen que es quien se encarga de reconectar.
		if(!GameSession.getInstance().isConnectedToServer()){
			TaTeTi.getInstance().setScreen(TaTeTi.getInstance().loginScreen);
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
		
		//tab index
		if(ShukenInput.getInstance().isKeyReleased(Keys.TAB)){
			tab_index++;
			if(tab_index > tab_index_max) tab_index= 0;
			
			txtUser.clickOff();
			txtPass1.clickOff();
			txtPass2.clickOff();
			
			switch(tab_index){
			case 0: txtUser.clickOn(); break;
			case 1: txtPass1.clickOn(); break;
			case 2: txtPass2.clickOn(); break;
			}
		}
		
		//Actualizamos gui...
		SimpleGUI.getInstance().update(delta);
		
	}
	
	@Override
	public void render(float delta) {
		//Limpiamos screen...
		Gdx.gl.glClearColor(0f,0f,0.35f,0f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		batch.begin();
		
		//Graficamos fondo
		batch.draw(ResourceManager.textures.backgroundCreateAccount, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				
		//Graficamos title
		batch.draw(ResourceManager.textures.createAccountTitle, 40, 280);
				
		if(Config.DEBUG_MODE){
			ResourceManager.fonts.defaultFont.draw(batch, "DEBUG MODE", 10, Gdx.graphics.getHeight()-5);
			ResourceManager.fonts.defaultFont.draw(batch, "Latency: " + GameSession.getInstance().getLatency() + "ms", 10, Gdx.graphics.getHeight() - 20);
			ResourceManager.fonts.defaultFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, Gdx.graphics.getHeight() - 35);
			ResourceManager.fonts.defaultFont.draw(batch, "CreateAccountScreen ("+ state + ")", 10, Gdx.graphics.getHeight() - 50);
		}
		
		switch(state){
		case Normal:
			ResourceManager.fonts.UIlabelsFont.draw(batch, Localization.Usuario_dp, 170, 257);
			ResourceManager.fonts.UIlabelsFont.draw(batch, Localization.Contrase�a_dp, 127, 208);
			ResourceManager.fonts.UIlabelsFont.draw(batch, Localization.ConfirmarContrase�a_dp, 5, 158);
			break;
		case AccountCreated:
			int posX1=0, posX2=0, posX3=0;
			if(Localization.getCurrentLanguage() == Languages.ES){
				posX1= 195; posX2= 277; posX3= 350;
			}
			if(Localization.getCurrentLanguage() == Languages.EN){
				posX1= 190; posX2= 283; posX3= 363;
			}
			ResourceManager.fonts.UIlabelsFont.draw(batch, "�"+Localization.Cuenta, posX1, 225);
			ResourceManager.fonts.UIlabelsFont.setColor(0, 0.88f, 0, 1);
			ResourceManager.fonts.UIlabelsFont.draw(batch, Localization.Creada, posX2, 225);
			ResourceManager.fonts.UIlabelsFont.setColor(Color.WHITE);
			ResourceManager.fonts.UIlabelsFont.draw(batch, Localization.Exitosamente+"!", posX3, 225);
			break;
		}//end switch
		
		//Graficamos GUi...
		SimpleGUI.getInstance().render(batch);
		
		//Graficamos transiciones (si hay)
		for(int i= 0; i < transitions.size(); i++){
			if(transitions.get(i).isRunning()) transitions.get(i).render(batch, shapeRender);
		}
		
		batch.end();

		//Realizamos update
		this.update(TaTeTi.confirmDelta(delta));
	}//end render

	public void updateTalkToServer(){
		if(sendCreateAccount){
			sendCreateAccount= false;
			GameSession.getInstance().sendCreateAccount(nickIngresado, passIngresado1);
		}
		
	}//end talk to server
	
	
	
	
	public void setState_Normal(){
		this.state= ScreenStates.Normal;
		//Mostramos gui
		showGUI();
	}
	
	public void setState_AccountCreated(){
		this.state= ScreenStates.AccountCreated;
		
		hideGUI();
		SimpleGUI.getInstance().turnAreaON(btnAceptar);
	}
	
	public void SaC_Respuesta_Create_Account(InetMessage msg){
		//Obtenemos datos
		boolean succes= msg.booleans.get(0);
		String mensaje= msg.strings.get(0);
		
		if(succes){
			this.setState_AccountCreated();
		}else{
			//TODO mensaje entrante del servidor!
			lblErrorMsg.reset();
			this.lblErrorMsg.setLabel(mensaje);
			SimpleGUI.getInstance().turnAreaON(lblErrorMsg);
		}
	}
	
	
	@Override
	public void simpleGUI_Event(ClickableArea area) {
		
		if(area.equals(btnCrear)){
			//Obtenemos datos del "formulario"
			nickIngresado= txtUser.getTextNoConsume();
			passIngresado1= txtPass1.getTextNoConsume();
			passIngresado2= txtPass2.getTextNoConsume();
			
			//Verificamos nullidez de campos completados
			if(nickIngresado.compareTo("")== 0 || passIngresado1.compareTo("")== 0 || passIngresado2.compareTo("")== 0){
				//Mostramos mensaje
				lblErrorMsg.reset();
				this.lblErrorMsg.setLabel(Localization.ErrorMSg_DebesCompletarTodosLosCampos);
				SimpleGUI.getInstance().turnAreaON(lblErrorMsg);
				return;
			}
			
			//Verificamos longitud del nombre de usuario (no debe ser menor a 4 caracteres)
			if(nickIngresado.length() < 4){
				//Mostramos mensaje
				lblErrorMsg.reset();
				this.lblErrorMsg.setLabel(Localization.ErrorMsg_NombreUsuarioDebeTenerCantCaracateres);
				SimpleGUI.getInstance().turnAreaON(lblErrorMsg);
				return;
			}
			
			//Verificamos si contrase�as coinciden
			if(passIngresado1.compareTo(passIngresado2)!= 0){
				//Mostramos mensaje
				lblErrorMsg.reset();
				this.lblErrorMsg.setLabel(Localization.ErrorMsg_Contrase�asDebenCoincidir);
				SimpleGUI.getInstance().turnAreaON(lblErrorMsg);
				return;
			}
			
			//Verificamos si la contrase�a tiene mas de 6 caracteres
			if(passIngresado1.length() < 6){
				//Mostramos mensaje
				lblErrorMsg.reset();
				this.lblErrorMsg.setLabel(Localization.ErrorMsg_Contrase�asDebenCantCaracteres);
				SimpleGUI.getInstance().turnAreaON(lblErrorMsg);
				return;
			}
			
			//Seteamos para enviar los datos al servidor
			sendCreateAccount= true;
		}
		
		if(area.equals(btnCancelar) || area.equals(btnAceptar)){
			transitionToLogginScreen.start();
		}
		
	}//end gui event
	
	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		this.setState_Normal();
		
		//Limpiamos los textboxs...
		txtUser.getText();
		txtPass1.getText();
		txtPass2.getText();
		
		//Hacemos foco
		txtUser.clickOn();
		tab_index= 0;
		
		//Inicializamos transicion de llegada
		transitionIn.start();
	}

	public void showGUI(){
		SimpleGUI.getInstance().turnAreaON(btnCrear);
		SimpleGUI.getInstance().turnAreaON(btnCancelar);
		//SimpleGUI.getInstance().turnAreaON(btnAceptar);
		SimpleGUI.getInstance().turnAreaON(txtUser);
		SimpleGUI.getInstance().turnAreaON(txtPass1);
		SimpleGUI.getInstance().turnAreaON(txtPass2);
	}
	
	@Override
	public void hide() {
		//Ocultamos gui
		hideGUI();
		
		//Reseteamos transiciones
		for(int i= 0; i < transitions.size(); i++){
			transitions.get(i).clear();
		}
	}

	public void hideGUI(){
		SimpleGUI.getInstance().turnAreaOFF(btnCrear);
		SimpleGUI.getInstance().turnAreaOFF(btnCancelar);
		SimpleGUI.getInstance().turnAreaOFF(btnAceptar);
		SimpleGUI.getInstance().turnAreaOFF(txtUser);
		SimpleGUI.getInstance().turnAreaOFF(txtPass1);
		SimpleGUI.getInstance().turnAreaOFF(txtPass2);
		SimpleGUI.getInstance().turnAreaOFF(lblErrorMsg);
	}
	
	@Override
	public void pause() {
	}

	@Override
	public void resume() {	
	}

	@Override
	public void dispose() {	
		System.out.println("Create account screen dispose...");
		batch.dispose();
		shapeRender.dispose();
	}


}//end class
