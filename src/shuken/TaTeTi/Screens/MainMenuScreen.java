package shuken.TaTeTi.Screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
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
import shuken.TaTeTi.TaTeTi;
import shuken.TaTeTi.Updateable;
import shuken.TaTeTi.Entities.Ficha;
import shuken.TaTeTi.Entities.HighScores;
import shuken.TaTeTi.Entities.Partida;
import shuken.TaTeTi.Entities.Player;
import shuken.TaTeTi.Entities.TableOfPlayersOnline;
import shuken.TaTeTi.Entities.Player.States;
import shuken.TaTeTi.Network.InetMessage;
import shuken.TaTeTi.Transitions.FadeTransition;
import shuken.TaTeTi.Transitions.Transition;

public class MainMenuScreen extends ShukenScreen implements Updateable{

	
	private SpriteBatch batch;
	private ShapeRenderer shapeRender;
	
	private static enum ScreenStates{
		NORMAL,
		ESPERANDO_RESPUESTA_DE_OTRO_USUARIO,
		RESPONDIENDO_SOLICITUD_ENTRANTE
	}
	private ScreenStates state;
	
	/** Tiempo limite para responder una solicitud entrante antes de ser rechazada (medido en segundos)*/
	private static final int TIME_TO_ANSWER= 15;
	private static final int MAX_WAITING_TIME= TIME_TO_ANSWER + 5;
	private float timeOutCount;
	private float waitingTimeCount;
	
	/** Variable para generar animacion de flecha circular. */
	private float angle= 0;
	
	/** GUI */
	private SimpleButton btnInvitar, btnCerrarSesion, btnAceptarSolicitud, btnRechazarSolicitud, btnCancelarSolicitud;
	private TimeLabel lblErrorMsg;
	private SimpleTextBox txtOpponent;
	
	private HighScores highScores;
	private TableOfPlayersOnline tableOfPlayersOnline;
	
	/** Transitions */
	private ArrayList<Transition> transitions;
	private Transition transitionIn, transitionToLogginScreen, transitionToGameplay;
	
	
	/** ---------Flags para enviar mensajes al servidor.--------------- */
	protected boolean sendSolicitudParaJugar= false;
	String nickOponente= "";

	boolean sendRespuestaPlayerSolicitaJugarConVos= false;
	boolean respuestaSolicitudEntrante;
	boolean rechazoSolicitudForTimeOut;
	String nickSolicitante= "";
	
	boolean sendCancelarSolicitud= false;
	
	protected boolean sendIdle= false;
	
	protected boolean sendCerrarSesion= false;
	
	protected boolean sendObtenerHighScores= false;
	protected static final int HIGHSCORE_UPDATE_INTERVAL= 10;	//in seconds
	protected float updateHighScoreTime;
	
	protected boolean sendObtenerPlayersOnline= false;
	protected static final int PLAYERS_ONLINE_UPDATE_INTERVAL= 4;	//in seconds
	protected float updatePlayersOnlineTime;;
	/** --------------------------------------------------------------- */
	
	
	public MainMenuScreen(){
		batch= new SpriteBatch();
		shapeRender= new ShapeRenderer();
		
		timeOutCount= 0;
		
		highScores= new HighScores(370, 230);
		tableOfPlayersOnline= new TableOfPlayersOnline(45, 310);
		
		//Inicializamos componentes de la gui...
		btnInvitar= new SimpleButton("Invitar a Jugar", 220, 54, ResourceManager.fonts.gameText, ResourceManager.textures.button, false);
		btnCerrarSesion= new SimpleButton("Cerrar Sesion", 510, 15, ResourceManager.fonts.gameText, ResourceManager.textures.button, false);
		btnAceptarSolicitud= new SimpleButton("Aceptar", 360, 100, ResourceManager.fonts.gameText, ResourceManager.textures.button, false);
		btnRechazarSolicitud= new SimpleButton("Rechazar", 200, 100, ResourceManager.fonts.gameText, ResourceManager.textures.button, false);
		btnCancelarSolicitud= new SimpleButton("Cancelar", 277, 100, ResourceManager.fonts.gameText, ResourceManager.textures.button, false);
		lblErrorMsg= new TimeLabel(" ", 200, 20, ResourceManager.fonts.gameText, 6f, ResourceManager.textures.transition);
		txtOpponent= new SimpleTextBox(45, 50, 19, ResourceManager.fonts.gameText, ResourceManager.textures.textbox);
		txtOpponent.putStringIntoText("Player");
		
		SimpleGUI.getInstance().addAreaNoActive(btnInvitar);
		SimpleGUI.getInstance().addAreaNoActive(btnCerrarSesion);
		SimpleGUI.getInstance().addAreaNoActive(lblErrorMsg);
		SimpleGUI.getInstance().addAreaNoActive(txtOpponent);
		SimpleGUI.getInstance().addAreaNoActive(btnAceptarSolicitud);
		SimpleGUI.getInstance().addAreaNoActive(btnRechazarSolicitud);
		SimpleGUI.getInstance().addAreaNoActive(btnCancelarSolicitud);
		SimpleGUI.getInstance().addAreaNoActive(lblErrorMsg);
	}//fin constructor
	
	@Override
	public void postCreate() {
		transitions= new ArrayList<Transition>();
		
		float transitionTime= 0.45f;
		
		transitionIn= new FadeTransition(transitionTime, null, true);
		transitionToLogginScreen= new FadeTransition(transitionTime, TaTeTi.getInstance().loginScreen, false);
		transitionToGameplay= new FadeTransition(transitionTime, TaTeTi.getInstance().gameplay, false);
		
		transitions.add(transitionIn);
		transitions.add(transitionToLogginScreen);
		transitions.add(transitionToGameplay);
		
	}
	
	@Override
	public void update(float delta) {
		//Checkeamos por perdida de conexion, en cuyo caso redirigimos al loggin screen que es quien se encarga de reconectar.
		if(!GameSession.getInstance().isConnectedToServer()){
			TaTeTi.getInstance().setScreen(TaTeTi.getInstance().loginScreen);
		}
				
		//Si se ha cerrado la sesion, nos vamos al loggin screen (esto puede ser intencionado, el usuario presiona el boton CERRAR SESION, por ejemplo)
		if(!GameSession.getInstance().isSessionOn()) {
			transitionToLogginScreen.start();
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
				
		switch(state){
		case NORMAL:
			//Update del highscore
			updateHighScoreTime+= delta;
			if(updateHighScoreTime > HIGHSCORE_UPDATE_INTERVAL){
				updateHighScoreTime= 0;
				System.out.println("Actualizando HIGHSCORES...");
				sendObtenerHighScores= true;
			}
			//Update de players online
			updatePlayersOnlineTime+= delta;
			if(updatePlayersOnlineTime > PLAYERS_ONLINE_UPDATE_INTERVAL){
				updatePlayersOnlineTime= 0;
				System.out.println("Actualizando PLAYERS ONLINE...");
				sendObtenerPlayersOnline= true;
			}
			this.tableOfPlayersOnline.update(delta);
			break;
			
		case ESPERANDO_RESPUESTA_DE_OTRO_USUARIO:
			waitingTimeCount+= delta;
			if(waitingTimeCount > MAX_WAITING_TIME){
				lblErrorMsg.reset();
				lblErrorMsg.setLabel("El usuario " + nickOponente + " perdió comunicación con el servidor.");
				SimpleGUI.getInstance().turnAreaON(lblErrorMsg);
				cancelarSolicitud();
			}
			
			angle-= delta * 300;
			if(angle <= 0) angle= 360;
			break;
	
		case RESPONDIENDO_SOLICITUD_ENTRANTE:
			timeOutCount+= delta;
			if(timeOutCount > TIME_TO_ANSWER){
				rechazarSolicitud(true);
			}
			
			break;
		}//fin switch

		updateTalkToServer();
		
		//Actualizamos gui...
		SimpleGUI.getInstance().update(delta);
		
		
	}//fin update

	
	
	
	@Override
	public void render(float delta) {
		//Limpiamos screen...
		Gdx.gl.glClearColor(0f,0.3f,0f,0f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		
		//Graficamos fondo
		batch.draw(ResourceManager.textures.backgroundMainMenu, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				
		//Graficamos title
		batch.draw(ResourceManager.textures.mainMenuTitle, 17, 320);
				
		if(Config.DEBUG_MODE){
			ResourceManager.fonts.defaultFont.draw(batch, "DEBUG MODE", 10, Gdx.graphics.getHeight()-5);
			ResourceManager.fonts.defaultFont.draw(batch, "Latency: " + GameSession.getInstance().getLatency() + "ms", 10, Gdx.graphics.getHeight() - 20);
			ResourceManager.fonts.defaultFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, Gdx.graphics.getHeight() - 35);
			ResourceManager.fonts.defaultFont.draw(batch, "MainMenu Screen (" + state + ")", 10, Gdx.graphics.getHeight() - 50);
		}
		
		switch(state){
		case NORMAL:
			//Usuario loggeado
			//ResourceManager.fonts.UIlabelsFont.draw(batch, "Bienvenido " + GameSession.getPlayer().getNick() + " :)", 50, 320);
			ResourceManager.fonts.gameText.draw(batch, "Seleccione el", 300, 315);
			ResourceManager.fonts.gameText.setColor(0,  0.7f, 0.88f, 1);
			ResourceManager.fonts.gameText.draw(batch, "jugador", 390, 315);
			ResourceManager.fonts.gameText.setColor(Color.WHITE);
			ResourceManager.fonts.gameText.draw(batch, "con el que desea jugar.", 447, 315);
			batch.draw(ResourceManager.textures.arrow, 253, 286);
			
			ResourceManager.fonts.gameText.draw(batch, "O bien ingrese el", 40, 140);
			ResourceManager.fonts.gameText.setColor(0,  0.7f, 0.88f, 1);
			ResourceManager.fonts.gameText.draw(batch, "nombre", 157, 140);
			ResourceManager.fonts.gameText.setColor(Color.WHITE);
			ResourceManager.fonts.gameText.draw(batch, "del jugador con", 215, 140);
			ResourceManager.fonts.gameText.draw(batch, "el que desea jugar: ", 40, 120);
			
			//Graficamos tablas
			highScores.render(batch, shapeRender);
			tableOfPlayersOnline.render(batch, shapeRender);
			break;
		case RESPONDIENDO_SOLICITUD_ENTRANTE:
			ResourceManager.fonts.UIlabelsFont.setColor(0, 0.88f, 0, 1);
			ResourceManager.fonts.UIlabelsFont.draw(batch, nickSolicitante.toUpperCase(), 300, 250);
			ResourceManager.fonts.UIlabelsFont.setColor(Color.WHITE);
			ResourceManager.fonts.UIlabelsFont.draw(batch,"¡Quiere jugar con vos!", 235, 210);
			
			//ResourceManager.fonts.defaultFont.draw(batch, timeOutCount + "s", 95, 240);
			batch.setColor(0, 0, 0, 0.5f);
			batch.draw(ResourceManager.textures.timeBar, 220, 147);
			float percent= timeOutCount/TIME_TO_ANSWER;
			int widthToRender= ResourceManager.textures.timeBar.getWidth()-(int)(ResourceManager.textures.timeBar.getWidth() * percent);
			batch.setColor(1, 1, 1, 1);
			batch.draw(ResourceManager.textures.timeBar, 220, 147, 0, 0, widthToRender, ResourceManager.textures.timeBar.getHeight());
			
			break;
		case ESPERANDO_RESPUESTA_DE_OTRO_USUARIO:
			ResourceManager.fonts.UIlabelsFont.draw(batch, "Esperando a que " + nickOponente.toUpperCase() + " responda a su solicitud...", 100, 250);
			batch.draw(ResourceManager.textures.circleArrow, 320f, 170f, (float)ResourceManager.textures.circleArrow.getRegionWidth()/2f, (float)ResourceManager.textures.circleArrow.getRegionHeight()/2f, ResourceManager.textures.circleArrow.getRegionWidth(), ResourceManager.textures.circleArrow.getRegionHeight(), 1f, 1f, angle, true);
			break;
		}//fin switch
		
		//Graficamos GUi...
		SimpleGUI.getInstance().render(batch);
		
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
		
		
		if(sendSolicitudParaJugar){
			System.out.println("Client-side: enviando solicitud para jugar contra " + nickOponente + "...");
			GameSession.getInstance().sendSolicitudParaJugar(nickOponente);
			sendSolicitudParaJugar= false;
		}
		
		if(sendRespuestaPlayerSolicitaJugarConVos){
			GameSession.getInstance().sendRespuestaPlayerSolicitaJugarConVos(nickSolicitante, respuestaSolicitudEntrante, rechazoSolicitudForTimeOut);
			sendRespuestaPlayerSolicitaJugarConVos= false;
		}
		
		if(sendCancelarSolicitud){
			GameSession.getInstance().sendCancelarSolicitud(nickOponente);
			sendCancelarSolicitud= false;
		}
		if(sendIdle){
			GameSession.getInstance().sendIdle();
			sendIdle= false;
		}
		
		if(sendCerrarSesion){
			GameSession.getInstance().sendCerrarSesion();
			sendCerrarSesion= false;
		}
		
		if(sendObtenerHighScores){
			GameSession.getInstance().sendObtenerHighScores();
			sendObtenerHighScores= false;
		}
		
		if(sendObtenerPlayersOnline){
			GameSession.getInstance().sendObtenerPlayersOnline();
			sendObtenerPlayersOnline= false;
		}
	}//fin updateTalkToServer
	
	
	
	
	public void SaC_Respuesta_Solicitud_para_jugar(InetMessage msg){
		//System.out.println(msg.strings.get(0));
		
		
		if(msg.booleans.get(0)){
			//El contrincante esta considerando jugar o no...
			GameSession.getPlayer().setState(Player.States.WAITING_FOR_OPPONENT);
			setState_EsperandoRespuesta();
		}else{
			//El contrincante deseado no se encuentra online o esta ocupado.
			GameSession.getPlayer().setState(Player.States.IDLE);
			lblErrorMsg.reset();
			lblErrorMsg.setLabel(msg.strings.get(0));
			SimpleGUI.getInstance().turnAreaON(lblErrorMsg);
		}
	}
	
	
	public void SaC_Player_Solicita_Jugar_con_vos(InetMessage msg){
		nickSolicitante= msg.strings.get(0);
		GameSession.getPlayer().setState(Player.States.RESPONDIENDO_SOLICITUD);
		setState_RespondiendoSolicitudEntrante();
	}
	
	
	public void SaC_Player_ha_rechazado_oferta(InetMessage msg){
		//Datos del mensaje
		String nick= msg.strings.get(0);
		boolean isForTimeOut= msg.booleans.get(0);
		waitingTimeCount= 0;
		timeOutCount= 0;
		
		String mensaje= "";
		if(isForTimeOut) mensaje= "El usuario "+ nick + " se ha demorado demasiado en responder.";
		else mensaje= "El jugador " + nick + " ha rechazado tu oferta de jugar.";
		
		lblErrorMsg.reset();
		lblErrorMsg.setLabel(mensaje);
		SimpleGUI.getInstance().turnAreaON(lblErrorMsg);
		
		GameSession.getPlayer().setState(Player.States.IDLE);
		setState_Normal();
	}
	
	public void SaC_ErrorMessage(InetMessage msg){
		waitingTimeCount=0;
		timeOutCount= 0;
		
		lblErrorMsg.reset();
		lblErrorMsg.setLabel(msg.strings.get(0));
		SimpleGUI.getInstance().turnAreaON(lblErrorMsg);
		
		GameSession.getPlayer().setState(Player.States.IDLE);
		setState_Normal();
	}
	
	public void SaC_Start_Match(InetMessage msg){
		//Obtenemos la data...
		Ficha fichaAsignada= Ficha.getFicha(msg.ints.get(0));
		String nickOponente= msg.strings.get(0);
		boolean playerXStarts= msg.booleans.get(0);
		
		//Obtenemos el player local
		Player localPlayer= GameSession.getInstance().getLocalPlayer();
		
		//Creamos un "fake player" de oponente. Su password no es real.
		Player opponent= new Player(nickOponente, "asdastryrbmbmbqwqw2326-");
		
		//Creamos la partida
		Partida partida= null;
		if(fichaAsignada == Ficha.CRUZ){
			partida= new Partida(localPlayer, opponent, playerXStarts);
		}
		if(fichaAsignada == Ficha.CIRCULO){
			partida= new Partida(opponent, localPlayer, playerXStarts);
		}
		
		//Preparamos al gameplay para ingresar en él.
		boolean juegoPrimero= false;
		if(fichaAsignada == Ficha.CRUZ && playerXStarts) juegoPrimero= true;
		if(fichaAsignada == Ficha.CIRCULO && !playerXStarts) juegoPrimero= true;
		TaTeTi.getInstance().getGamePlay().prepareForPartida(partida, juegoPrimero);
		
		//Cambiamos de screen
		//TaTeTi.getInstance().setScreen(TaTeTi.getInstance().gameplay);
		transitionToGameplay.start();
		
		
	}//fin startMatch
	
	
	public void SaC_PlayersOnline(InetMessage msg){
		//Limpiamos tabla vieja
		tableOfPlayersOnline.clear();
		
		//Obtenemos data y cargamos la tala
		int rows= msg.ints.get(0);
		for(int i=0; i < rows; i++){
			String nick= msg.strings.get(i*2);
			String state= msg.strings.get((i*2)+1);
			tableOfPlayersOnline.addRow(nick, state);
		}
	}
	
	public void SaC_MejoresPuntajes(InetMessage msg){
		//Limpiamos highscores viejos
		highScores.clear();
		
		//Obtenemos la data del mensaje y la agregamos a la tabla de highscores
		int rows= msg.floats.get(0).intValue();
		for(int i=0; i < rows; i++){
			String nick= msg.strings.get(i);
			int won= msg.ints.get((i*3));
			int lose= msg.ints.get((i*3)+1);
			int draw= msg.ints.get((i*3)+2);

			highScores.addRecord(nick, won, lose, draw);
		}
		
	}//fin mejores puntajes
	
	public void SaC_SeHaCanceladoSolicitudParaJugar(InetMessage msg){
		setState_Normal();
		GameSession.getPlayer().setState(States.IDLE);
		
		String opponent= msg.strings.get(0);
		lblErrorMsg.reset();
		lblErrorMsg.setLabel(opponent + " ha cancelado la solicitud.");
		SimpleGUI.getInstance().turnAreaON(lblErrorMsg);
	}//fin se cancelo solicitud
	
	public void setState_Normal(){
		state= ScreenStates.NORMAL;
		waitingTimeCount= 0;
		timeOutCount= 0;
		hideGUI();
		showGUI();
	}
	
	public void setState_EsperandoRespuesta(){
		state= ScreenStates.ESPERANDO_RESPUESTA_DE_OTRO_USUARIO;
		hideGUI();
		SimpleGUI.getInstance().turnAreaON(btnCancelarSolicitud);
	}
	
	public void setState_RespondiendoSolicitudEntrante(){
		state= ScreenStates.RESPONDIENDO_SOLICITUD_ENTRANTE;
		hideGUI();
		SimpleGUI.getInstance().turnAreaON(btnAceptarSolicitud);
		SimpleGUI.getInstance().turnAreaON(btnRechazarSolicitud);
	}
	
	public void invitarAJugar(String opponent){
		ShukenInput.getInstance().cleanAll();
		nickOponente= opponent;
		
		if(GameSession.getInstance().getLocalPlayer().getNick().compareToIgnoreCase(nickOponente)== 0){
			//System.out.println("Client-side: No puedes invitarte a ti mismo.");
			lblErrorMsg.reset();
			lblErrorMsg.setLabel("No puedes invitarte a ti mismo.");
			SimpleGUI.getInstance().turnAreaON(lblErrorMsg);
		}
		else {
			sendSolicitudParaJugar= true;
		}
	}
	
	@Override
	public void simpleGUI_Event(ClickableArea area) {

		if(area.equals(btnInvitar)){
			//Obtenemos el nick ingresado...
			nickOponente= txtOpponent.getTextNoConsume();
			
			this.invitarAJugar(nickOponente);
		}

		if(area.equals(btnCerrarSesion)){
			System.out.println("cerrar sesion");
			sendCerrarSesion= true;
		}
		
		if(area.equals(btnAceptarSolicitud)){
			aceptarSolicitud();
		}
		
		if(area.equals(btnRechazarSolicitud)){
			rechazarSolicitud(false);
		}
		
		if(area.equals(btnCancelarSolicitud)){
			cancelarSolicitud();
		}
	}//fin simple gui event
	
	
	protected void aceptarSolicitud(){
		respuestaSolicitudEntrante= true;
		sendRespuestaPlayerSolicitaJugarConVos= true;
		setState_Normal();
	}
	
	protected void rechazarSolicitud(boolean isTimeOut){
		respuestaSolicitudEntrante= false;
		sendRespuestaPlayerSolicitaJugarConVos= true;
		rechazoSolicitudForTimeOut= isTimeOut;
		timeOutCount= 0;
		setState_Normal();
	}
	
	protected void cancelarSolicitud(){
		sendCancelarSolicitud= true;
		sendSolicitudParaJugar= false;
		waitingTimeCount= 0;
		setState_Normal();
		GameSession.getPlayer().setState(States.IDLE);
	}
	
	@Override
	public void resize(int width, int height) {}

	@Override
	public void show() {
		sendIdle= true;
		if(GameSession.getInstance().isSessionOn()) {
			GameSession.getInstance().getLocalPlayer().setState(Player.States.IDLE);
		}
		
		//Si hay clicks, los limpiamos derecho viejo
		if(ShukenInput.getInstance().hasBeenClicked(0)){
			ShukenInput.getInstance().consumeClick(0);
		}
		
		//Mostramos GUI
		showGUI();
		
		state= ScreenStates.NORMAL;
		
		//Actualizamos tablas
		sendObtenerHighScores= true;
		sendObtenerPlayersOnline= true;
		
		//Inicializamos transicion de llegada
		transitionIn.start();
	}

	public void showGUI(){
		SimpleGUI.getInstance().turnAreaON(btnInvitar);
		SimpleGUI.getInstance().turnAreaON(btnCerrarSesion);
		SimpleGUI.getInstance().turnAreaON(txtOpponent);
	}
	
	
	@Override
	public void hide() {
		//Ocultamos GUi
		hideGUI();
		
		//Reseteamos transiciones
		for(int i= 0; i < transitions.size(); i++){
			transitions.get(i).clear();
		}
	}

	public void hideGUI(){
		SimpleGUI.getInstance().turnAreaOFF(btnInvitar);
		SimpleGUI.getInstance().turnAreaOFF(btnCerrarSesion);
		SimpleGUI.getInstance().turnAreaOFF(txtOpponent);
		SimpleGUI.getInstance().turnAreaOFF(btnAceptarSolicitud);
		SimpleGUI.getInstance().turnAreaOFF(btnRechazarSolicitud);
		SimpleGUI.getInstance().turnAreaOFF(btnCancelarSolicitud);
	}
	
	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void dispose() {}

	

}//fin clase
