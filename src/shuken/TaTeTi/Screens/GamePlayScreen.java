package shuken.TaTeTi.Screens;

import java.util.ArrayList;

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
import shuken.Engine.SimpleGUI.SimpleGUI;
import shuken.TaTeTi.Config;
import shuken.TaTeTi.GameSession;
import shuken.TaTeTi.TaTeTi;
import shuken.TaTeTi.Updateable;
import shuken.TaTeTi.Entities.Ficha;
import shuken.TaTeTi.Entities.Partida;
import shuken.TaTeTi.Entities.Partida.MatchStates;
import shuken.TaTeTi.Network.InetMessage;
import shuken.TaTeTi.Transitions.FadeTransition;
import shuken.TaTeTi.Transitions.Transition;


public class GamePlayScreen extends ShukenScreen implements Updateable{

	private SpriteBatch batch;
	private ShapeRenderer shapeRender;
	
	protected Partida partida;
	private SimpleButton btnBackToMenu, btnRendirse;
	private boolean backToMenu= false;
	
	public static enum States{
		Local_Player_playing,
		Opponent_Player_playing,
		Partida_terminada
	}
	
	/** Estado actual del gameplay del cliente. */
	public States currentState;
	
	/** Transitions */
	private ArrayList<Transition> transitions;
	private Transition transitionIn, transitionToMainMenu;
	
	
	/** Flags para enviar mensajes al servidor. */
	protected boolean sendFichaColocada= false;
	Ficha fichaColocadaRecientemente;
	int nroCeldaEnQueSeColocoFichaRecientemente;
	
	MatchStates resultado;
	
	protected boolean rendirse= false;
	/** --------------------------------------- */
	
	
	
	public GamePlayScreen(){
		//Cargamos todos los recursos...
		//ResourceManager.loadAllResources();		//se debe hacer en un screen anterior a este
		batch= new SpriteBatch();
		shapeRender= new ShapeRenderer();
		
		btnBackToMenu= new SimpleButton("Volver al menu", 200, 75, ResourceManager.fonts.defaultFont, ResourceManager.textures.button);
		btnRendirse= new SimpleButton("Rendirse", 200, 10, ResourceManager.fonts.defaultFont, ResourceManager.textures.button);
		
		SimpleGUI.getInstance().addAreaNoActive(btnBackToMenu);
		SimpleGUI.getInstance().addAreaNoActive(btnRendirse);
	}//fin constructor
	
	@Override
	public void postCreate() {
		transitions= new ArrayList<Transition>();
		
		//TODO testcode
		float transitionTime= 0.45f;
				
		transitionIn= new FadeTransition(transitionTime, null, true);
		transitionToMainMenu= new FadeTransition(transitionTime, TaTeTi.getInstance().mainMenuScreen, false);
		
		transitions.add(transitionIn);
		transitions.add(transitionToMainMenu);
		
	}
	/**
	 * Prepara al gameplay para gestionar la partida. Este metodo es llamado antes de cambiar de screen.
	 * @param partida
	 * @param localPlayer
	 * @param client
	 * @param localPlayerPlaysFirst
	 */
	public void prepareForPartida(Partida partida, boolean localPlayerPlaysFirst){
		this.partida= partida;

		if(localPlayerPlaysFirst) currentState= States.Local_Player_playing;
		else currentState= States.Opponent_Player_playing;
		
		resultado= MatchStates.PARTIDA_EN_CURSO;
	}//fin preparefor partida
	
	
	
	
	
	@Override
	public void update(float delta) {
		//Checkeamos por perdida de conexion, en cuyo caso redirigimos al loggin screen que es quien se encarga de reconectar.
		if(!GameSession.getInstance().isConnectedToServer()){
			TaTeTi.getInstance().setScreen(TaTeTi.getInstance().logginScreen);
		}
				
		//Actualizamos gameSession...
		GameSession.getInstance().update(delta);
				
		//Actualizamos gui...
		SimpleGUI.getInstance().update(delta);
				
		//Si estamos en una transicion...
		for(int i= 0; i < transitions.size(); i++){
			if(transitions.get(i).isRunning()){
				transitions.get(i).update(delta);
				return;
			}
		}
				
		switch(currentState){
		case Local_Player_playing: updateLocalPlayerPlaying(delta); break;
		case Opponent_Player_playing: updateOpponentPlayerPlaying(delta); break;
		case Partida_terminada: updatePartidaTerminada(delta); break;
		}//fin switch
		
		updateTalkToServer();
		
		
	}//fin update
	
	@Override
	public void render(float delta) {
		//Limpiamos screen...
		Gdx.gl.glClearColor(0f,0f,0f,0f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		
		if(Config.DEBUG_MODE){
			ResourceManager.fonts.defaultFont.draw(batch, "DEBUG MODE", 10, Gdx.graphics.getHeight()-5);
			ResourceManager.fonts.defaultFont.draw(batch, "Latency: " + GameSession.getInstance().getLatency() + "ms", 10, Gdx.graphics.getHeight() - 20);
			ResourceManager.fonts.defaultFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, Gdx.graphics.getHeight() - 35);
			ResourceManager.fonts.defaultFont.draw(batch, "GamePlay State: " + currentState, 10, Gdx.graphics.getHeight() - 50);
			ResourceManager.fonts.defaultFont.draw(batch, "Match State: " + partida.getPartidaState(), 10, Gdx.graphics.getHeight() - 65);
		}
		
		ResourceManager.fonts.defaultFont.draw(batch, partida.getPlayerX().getNick() + " VS " + partida.getPlayerO().getNick(), 200, Gdx.graphics.getHeight()-20);
		
		switch(currentState){
		case Local_Player_playing:
			ResourceManager.fonts.defaultFont.draw(batch, "TU TURNO", 250, Gdx.graphics.getHeight() - 40);
			break;
		case Opponent_Player_playing:
			ResourceManager.fonts.defaultFont.draw(batch, "ESPERANDO A QUE JUEGUE EL CONTRINCANTE...", 200, Gdx.graphics.getHeight() - 40);
			break;
		case Partida_terminada:
			ResourceManager.fonts.defaultFont.draw(batch, "PARTIDA FINALIZADA", 250, Gdx.graphics.getHeight() - 40);
			
			switch(resultado){
			case EMPATE:
				ResourceManager.fonts.defaultFont.draw(batch, "LA PARTIDA FINALIZO EN EMPATE", 230, Gdx.graphics.getHeight() - 60);
				break;
			case GANADOR_O:
				if(GameSession.getPlayer().equals(partida.getPlayerO())) ResourceManager.fonts.defaultFont.draw(batch, "¡¡GANASTE!!", 270, Gdx.graphics.getHeight() - 60); 
				else ResourceManager.fonts.defaultFont.draw(batch, "¡¡PERDISTE!!", 270, Gdx.graphics.getHeight() - 60);
				break;
			case GANADOR_X:
				if(GameSession.getPlayer().equals(partida.getPlayerX())) ResourceManager.fonts.defaultFont.draw(batch, "¡¡GANASTE!!", 270, Gdx.graphics.getHeight() - 60);
				else ResourceManager.fonts.defaultFont.draw(batch, "¡¡PERDISTE!!", 270, Gdx.graphics.getHeight() - 60); 
				break;	
			case RENDICION_X:
				if(GameSession.getPlayer().equals(partida.getPlayerO())) ResourceManager.fonts.defaultFont.draw(batch, "El contrincante se ha RENDIDO.", 270, Gdx.graphics.getHeight() - 60);
				else ResourceManager.fonts.defaultFont.draw(batch, "TE HAS RENDIDO.", 270, Gdx.graphics.getHeight() - 60);
				break;
			case RENDICION_O:
				if(GameSession.getPlayer().equals(partida.getPlayerX())) ResourceManager.fonts.defaultFont.draw(batch, "El contrincante se ha RENDIDO.", 270, Gdx.graphics.getHeight() - 60);
				else ResourceManager.fonts.defaultFont.draw(batch, "TE HAS RENDIDO.", 270, Gdx.graphics.getHeight() - 60);
				break;
			}
			
			break;
		}//fin switch
		
		//Graficamos la partida...
		partida.render(batch, shapeRender);
		
		//Graficamos GUI
		SimpleGUI.getInstance().render(batch);
		
		//Graficamos transiciones (si hay)
		for(int i= 0; i < transitions.size(); i++){
			if(transitions.get(i).isRunning()) transitions.get(i).render(batch, shapeRender);
		}
				
		batch.end();
		
		//Realizamos update
		this.update(TaTeTi.confirmDelta(delta));
	
	}//fin render

	
	protected void updateLocalPlayerPlaying(float delta){
		//Gestionamos clicks del mouse
		if(ShukenInput.getInstance().hasBeenClicked(0)){
			float clickX= ShukenInput.getInstance().getClickX(0);
			float clickY= ShukenInput.getInstance().getClickY(0);
			
			//Le preguntamos a la partida si el click tiene alguna importancia...
			partida.playerClicksOnScreen(clickX, clickY);
			
			//Limpiamos click del buffer
			ShukenInput.getInstance().consumeClick(0);
		}
		
		//TODO: remove test code. Test only
		if(ShukenInput.getInstance().isKeyReleased(Keys.SPACE)) partida.clearTablero();
		
	}//fin update local player playing
	
	
	
	protected void updateOpponentPlayerPlaying(float delta){
		//Si hay clicks, los limpiamos derecho viejo
		if(ShukenInput.getInstance().hasBeenClicked(0)){
			ShukenInput.getInstance().consumeClick(0);
		}
	}//fin opponent player playing
	
	
	protected void updatePartidaTerminada(float delta){
		//Si el que se rindio es ESTE cliente, nos vamos derecho al cambio de screen
		/*
		if((resultado == MatchStates.RENDICION_X && GameSession.getPlayer().equals(partida.getPlayerX()))
				||
				(resultado == MatchStates.RENDICION_O && GameSession.getPlayer().equals(partida.getPlayerO())))
		{
			backToMenu= true;
		}
		*/
		
		if(backToMenu){
			backToMenu= false;
			
			//Actualizamos LOCALMENTE las estadisticas. El servidor las actualiza tambien por su cuenta (no nos preocupamos de eso).
			switch(resultado){
			case EMPATE:
				GameSession.getPlayer().incrementDraw();
				break;
			case GANADOR_O:
				if(GameSession.getPlayer().equals(partida.getPlayerO())) GameSession.getPlayer().incrementWon(); 
				else GameSession.getPlayer().incrementLose(); 
				break;
			case GANADOR_X:
				if(GameSession.getPlayer().equals(partida.getPlayerX())) GameSession.getPlayer().incrementWon(); 
				else GameSession.getPlayer().incrementLose(); 
				break;	
			case RENDICION_X:
				if(GameSession.getPlayer().equals(partida.getPlayerX())) GameSession.getPlayer().incrementLose();
				break;
			case RENDICION_O:
				if(GameSession.getPlayer().equals(partida.getPlayerO())) GameSession.getPlayer().incrementLose(); 
				break;
			}
			
			//Cambiamos de screen
			//TaTeTi.getInstance().setScreen(TaTeTi.getInstance().mainMenuScreen);
			transitionToMainMenu.start();
		}
		
	}
	
	
	/**
	 * Este metodo verifica los flags de envio de mensajes y solicita al GameSession que retransmita los mensajes al servidor.
	 */
	protected void updateTalkToServer(){
		
		if(sendFichaColocada){
			System.out.println("Client-side: Cliente avisa al servidor que se ha colocado una ficha en [" + nroCeldaEnQueSeColocoFichaRecientemente + ", " + fichaColocadaRecientemente + "]");
			GameSession.getInstance().sendFichaColocada(nroCeldaEnQueSeColocoFichaRecientemente, fichaColocadaRecientemente, partida.getOpponent().getNick());
			sendFichaColocada= false;
		}
		
		if(rendirse){
			rendirse= false;
			boolean seRindioX= false;
			if(GameSession.getPlayer().equals(partida.getPlayerX())){
				partida.playerX_seRinde();
				seRindioX= true;
			}else{
				partida.playerO_seRinde();
			}
			GameSession.getInstance().sendPlayerSeRinde(seRindioX);
		}
	}//fin updateTalkToServer
	

	
	
	public void SaC_Ficha_Colocada_y_Avanzar_turno(InetMessage msg){
		//El otro jugador colocó una ficha.
		putFichaOnTablero(msg);
		
		//Pasamos de turno
		nextTurn();
	}
	
	
	public void SaC_FichaColocada_y_FinDePartida(InetMessage msg){
		//El otro jugador colocó una ficha.
		putFichaOnTablero(msg);
		
		//Fin de partida
		MatchStates resultado= MatchStates.getMatchState(msg.ints.get(2));
		matchEnd(resultado);
	}
	
	
	public void SaC_Fin_de_Partida(InetMessage msg){
		matchEnd(MatchStates.getMatchState(msg.ints.get(0)));
	}
	
	
	
	
	
	public void matchEnd(MatchStates resultado){
		//Cambiamos gameplay state
		currentState= States.Partida_terminada;
		this.resultado= resultado;
		SimpleGUI.getInstance().turnAreaOFF(btnRendirse);
		SimpleGUI.getInstance().turnAreaON(btnBackToMenu);
	}
	
	
	
	public void putFichaOnTablero(InetMessage msg){
		//El otro jugador colocó una ficha.
		int nroCelda= msg.ints.get(0);
		Ficha f= Ficha.getFicha(msg.ints.get(1));
		
		//Colocamos la ficha
		partida.putFichaOnCelda(nroCelda, f);
	}
	
	
	/**
	 * Este metodo prepara al gameplay para avisarle al servidor de que se ha colocado una ficha en el tablero.
	 * @param nroCelda
	 * @param tipo
	 */
	public void sendFichaColocada(int nroCelda, Ficha tipo){
		sendFichaColocada= true;
		fichaColocadaRecientemente= tipo;
		nroCeldaEnQueSeColocoFichaRecientemente= nroCelda;
	}
	
	
	
	
	/**
	 * Avanza al proximo turno.
	 */
	public void nextTurn(){
		System.out.println("NEXT TURN (Client: " + GameSession.getPlayer().getNick() + ")");
		if(currentState == States.Local_Player_playing) currentState= States.Opponent_Player_playing;
		else if(currentState == States.Opponent_Player_playing) currentState= States.Local_Player_playing;
		
		partida.nextPlayer();
	}
	
	
	

	
	@Override
	public void simpleGUI_Event(ClickableArea area) {
		
		if(area.equals(btnBackToMenu)){
			System.out.println("Volver al menu");
			backToMenu= true;
		}
		
		if(area.equals(btnRendirse)){
			System.out.println("Rendirse");
			rendirse= true;
		}
	}//fin simple gui event
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		//Si hay clicks, los limpiamos derecho viejo
		if(ShukenInput.getInstance().hasBeenClicked(0)){
			ShukenInput.getInstance().consumeClick(0);
		}
		SimpleGUI.getInstance().turnAreaON(btnRendirse);
		
		//Inicializamos transicion de llegada
		transitionIn.start();
	}

	@Override
	public void hide() {
		SimpleGUI.getInstance().turnAreaOFF(btnBackToMenu);
		SimpleGUI.getInstance().turnAreaOFF(btnRendirse);
		
		//Reseteamos transiciones
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
