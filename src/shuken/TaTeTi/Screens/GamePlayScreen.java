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
import shuken.TaTeTi.Config;
import shuken.TaTeTi.GameSession;
import shuken.TaTeTi.Localization;
import shuken.TaTeTi.TaTeTi;
import shuken.TaTeTi.Updateable;
import shuken.TaTeTi.Entities.Ficha;
import shuken.TaTeTi.Entities.Partida;
import shuken.TaTeTi.Entities.Partida.MatchStates;
import shuken.TaTeTi.Localization.Languages;
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
	
	/** Current gameplay state. */
	public States currentState;
	
	/** Transitions */
	private ArrayList<Transition> transitions;
	private Transition transitionIn, transitionToMainMenu;
	
	/** Flags to send message to the server. */
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
		
		btnBackToMenu= new SimpleButton(Localization.VolverAlMenu, 280, 35, ResourceManager.fonts.gameText, ResourceManager.textures.button, true);
		btnRendirse= new SimpleButton(Localization.Rendirse, 303, 10, ResourceManager.fonts.gameText, ResourceManager.textures.button, true);
		
		SimpleGUI.getInstance().addAreaNoActive(btnBackToMenu);
		SimpleGUI.getInstance().addAreaNoActive(btnRendirse);
	}//fin constructor
	
	@Override
	public void postCreate() {
		transitions= new ArrayList<Transition>();
		
		float transitionTime= 0.45f;
				
		transitionIn= new FadeTransition(transitionTime, null, true);
		transitionToMainMenu= new FadeTransition(transitionTime, TaTeTi.getInstance().mainMenuScreen, false);
		
		transitions.add(transitionIn);
		transitions.add(transitionToMainMenu);
	}
	
	public void prepareForPartida(Partida partida, boolean localPlayerPlaysFirst){
		this.partida= partida;

		if(localPlayerPlaysFirst) currentState= States.Local_Player_playing;
		else currentState= States.Opponent_Player_playing;
		
		resultado= MatchStates.PARTIDA_EN_CURSO;
	}
	
	@Override
	public void update(float delta) {
		//Checkeamos por perdida de conexion, en cuyo caso redirigimos al loggin screen que es quien se encarga de reconectar.
		if(!GameSession.getInstance().isConnectedToServer()){
			TaTeTi.getInstance().setScreen(TaTeTi.getInstance().loginScreen);
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
		
		//Actualizamos partida (y tablero)
		partida.update(delta);
		
		updateTalkToServer();
	}//end update
	
	@Override
	public void render(float delta) {
		//Limpiamos screen...
		Gdx.gl.glClearColor(0.88f,0.93f,0.88f,0f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		
		//Graficamos fondo
		batch.draw(ResourceManager.textures.backgroundGamePlay, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				
		if(Config.DEBUG_MODE){
			ResourceManager.fonts.defaultFont.draw(batch, "DEBUG MODE", 10, Gdx.graphics.getHeight()-5);
			ResourceManager.fonts.defaultFont.draw(batch, "Latency: " + GameSession.getInstance().getLatency() + "ms", 10, Gdx.graphics.getHeight() - 20);
			ResourceManager.fonts.defaultFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, Gdx.graphics.getHeight() - 35);
			ResourceManager.fonts.defaultFont.draw(batch, "GamePlay State: " + currentState, 10, Gdx.graphics.getHeight() - 50);
			ResourceManager.fonts.defaultFont.draw(batch, "Match State: " + partida.getPartidaState(), 10, Gdx.graphics.getHeight() - 65);
		}
		
		//Graficamos la partida, tablero y demas...
		partida.render(batch, shapeRender);
				
		int posX1=0, posX2=0, pos2X1=0, pos2X2=0, pos3X=0, pos4X1=0, pos4X2=0;
		String forcad= "";
		if(Localization.getCurrentLanguage() == Languages.ES){
			posX1= 260; posX2= 318;
			pos2X1= 260; pos2X2= 330;
			pos3X= 240;
			pos4X1=100; pos4X2=160;
		}
		if(Localization.getCurrentLanguage() == Languages.EN){
			posX1= 265; posX2= 315;
			pos2X1= 240; pos2X2= 295; forcad= "FOR ";
			pos3X= 280;
			pos4X1=120; pos4X2=180;
		}
		
		switch(currentState){
		case Local_Player_playing:
			ResourceManager.fonts.UIlabelsFont.setColor(0, 0.88f, 0, 1);
			ResourceManager.fonts.UIlabelsFont.draw(batch, Localization.Juege, posX1, 420);
			ResourceManager.fonts.UIlabelsFont.setColor(Color.WHITE);
			ResourceManager.fonts.UIlabelsFont.draw(batch, Localization.SuTurno, posX2, 420);
			break;
		case Opponent_Player_playing:
			ResourceManager.fonts.UIlabelsFont.setColor(0.9f, 0f, 0, 1);
			ResourceManager.fonts.UIlabelsFont.draw(batch, Localization.Espere, pos2X1, 420);
			ResourceManager.fonts.UIlabelsFont.setColor(Color.WHITE);
			ResourceManager.fonts.UIlabelsFont.draw(batch, forcad+Localization.SuTurno, pos2X2, 420);
			break;
		case Partida_terminada:
			ResourceManager.fonts.UIlabelsFont.draw(batch, Localization.PartidaFinalizada, pos3X, 430);
			
			switch(resultado){
			case EMPATE:
				ResourceManager.fonts.UIlabelsBIGFont.setColor(0.88f, 0.88f, 0, 1);
				ResourceManager.fonts.UIlabelsBIGFont.draw(batch, "\""+Localization.Empate+"\"", 255, Gdx.graphics.getHeight() - 160);
				break;
			case GANADOR_O:
				if(GameSession.getPlayer().equals(partida.getPlayerO())) {
					ResourceManager.fonts.UIlabelsBIGFont.setColor(0, 0.88f, 0, 1);
					ResourceManager.fonts.UIlabelsBIGFont.draw(batch, "¡¡"+Localization.Ganaste+"!!", 235, Gdx.graphics.getHeight() - 160); 
				}
				else {
					ResourceManager.fonts.UIlabelsBIGFont.setColor(0.88f, 0, 0, 1);
					ResourceManager.fonts.UIlabelsBIGFont.draw(batch, "¡¡"+Localization.Perdiste+"!!", 235, Gdx.graphics.getHeight() - 160);
				}
				break;
			case GANADOR_X:
				if(GameSession.getPlayer().equals(partida.getPlayerX())) {
					ResourceManager.fonts.UIlabelsBIGFont.setColor(0, 0.88f, 0, 1);
					ResourceManager.fonts.UIlabelsBIGFont.draw(batch, "¡¡"+Localization.Ganaste+"!!", 235, Gdx.graphics.getHeight() - 160);
				}
				else {
					ResourceManager.fonts.UIlabelsBIGFont.setColor(0.88f, 0, 0, 1);
					ResourceManager.fonts.UIlabelsBIGFont.draw(batch, "¡¡"+Localization.Perdiste+"!!", 235, Gdx.graphics.getHeight() - 160); 
				}
				break;	
			case RENDICION_X:
				if(GameSession.getPlayer().equals(partida.getPlayerO())){
					ResourceManager.fonts.UIlabelsFont.setColor(0.88f, 0.88f, 0, 1);
					ResourceManager.fonts.UIlabelsFont.draw(batch, Localization.ElContrincanteSeRindio, 200, Gdx.graphics.getHeight() - 60);
				}
				else {
					ResourceManager.fonts.UIlabelsFont.setColor(0.88f, 0.88f, 0, 1);
					ResourceManager.fonts.UIlabelsFont.draw(batch, Localization.TeHasRendido, 260, Gdx.graphics.getHeight() - 60);
				}
				break;
			case RENDICION_O:
				if(GameSession.getPlayer().equals(partida.getPlayerX())) {
					ResourceManager.fonts.UIlabelsFont.setColor(0.88f, 0.88f, 0, 1);
					ResourceManager.fonts.UIlabelsFont.draw(batch, Localization.ElContrincanteSeRindio, 200, Gdx.graphics.getHeight() - 60);
				}
				else {
					ResourceManager.fonts.UIlabelsFont.setColor(0.88f, 0.88f, 0, 1);
					ResourceManager.fonts.UIlabelsFont.draw(batch, Localization.TeHasRendido, 260, Gdx.graphics.getHeight() - 60);
				}
				break;
			case INCONCLUSO:
				ResourceManager.fonts.gameText.draw(batch, Localization.FallaEnPartida1, pos4X1, Gdx.graphics.getHeight() - 60);
				ResourceManager.fonts.gameText.draw(batch, Localization.FallaEnPartida2, pos4X2, Gdx.graphics.getHeight() - 80);
				break;
			}
			
		}//fin switch
		ResourceManager.fonts.UIlabelsBIGFont.setColor(Color.WHITE);
		ResourceManager.fonts.UIlabelsFont.setColor(Color.WHITE);
		
		//Graficamos GUI
		SimpleGUI.getInstance().render(batch);
		
		//Graficamos transiciones (si hay)
		for(int i= 0; i < transitions.size(); i++){
			if(transitions.get(i).isRunning()) transitions.get(i).render(batch, shapeRender);
		}
				
		batch.end();
		
		//Realizamos update
		this.update(TaTeTi.confirmDelta(delta));
	}//end render

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
	}
	
	protected void updateOpponentPlayerPlaying(float delta){
		//Si hay clicks, los limpiamos derecho viejo
		if(ShukenInput.getInstance().hasBeenClicked(0)){
			ShukenInput.getInstance().consumeClick(0);
		}
	}
	
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
	}//end updateTalkToServer
	
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
	
	public void sendFichaColocada(int nroCelda, Ficha tipo){
		sendFichaColocada= true;
		fichaColocadaRecientemente= tipo;
		nroCeldaEnQueSeColocoFichaRecientemente= nroCelda;
	}
	
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
	}
	
	@Override
	public void resize(int width, int height) {}

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
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void dispose() {
		System.out.println("Gameplay screen dispose...");
		batch.dispose();
		shapeRender.dispose();
	}
}//end class
