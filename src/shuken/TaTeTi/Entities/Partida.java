package shuken.TaTeTi.Entities;

import shuken.Engine.Resources.ResourceManager;
import shuken.TaTeTi.GameSession;
import shuken.TaTeTi.Renderable;
import shuken.TaTeTi.TaTeTi;
import shuken.TaTeTi.Updateable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Esta clase encierra todo lo referido a una partida de TaTeTi: el tablero y sus celdas, los jugadores y el manejo de los turnos.
 * @author F. Marrone
 *
 */
public class Partida implements Renderable, Updateable{

	private Tablero tablero;
	private Player playerX, playerO, playerTurnoActual;
	private boolean rendicion_x, rendicion_o;
	
	public static enum MatchStates{
		GANADOR_X,			//ordinal 0
		GANADOR_O,			//ordinal 1
		EMPATE,				//ordinal 2
		PARTIDA_EN_CURSO,	//ordinal 3
		RENDICION_X,		//ordinal 4
		RENDICION_O,		//ordinal 5
		INCONCLUSO;			//ordinal 6 Esto ocurre cuando hay una falla
		
		public static MatchStates getMatchState(int ordinal){
			if(ordinal == GANADOR_X.ordinal()) return GANADOR_X;
			if(ordinal == GANADOR_O.ordinal()) return GANADOR_O;
			if(ordinal == EMPATE.ordinal()) return EMPATE;
			if(ordinal == PARTIDA_EN_CURSO.ordinal()) return PARTIDA_EN_CURSO;
			if(ordinal == RENDICION_X.ordinal()) return RENDICION_X;
			if(ordinal == RENDICION_O.ordinal()) return RENDICION_O;
			if(ordinal == INCONCLUSO.ordinal()) return INCONCLUSO;
			return null;
		}
	}
	
	
	public Partida(Player pX, Player pO, boolean playerXStarts, boolean forServer){
		//Creamos el tablero...
		if(!forServer) tablero= new Tablero((Gdx.graphics.getWidth()/2) - (ResourceManager.textures.tablero.getWidth()/2), 40);
		else tablero= new Tablero(40, 40);
		
		rendicion_x= false;
		rendicion_o= false;
		
		//Asignamos los players...
		this.playerX= pX;
		this.playerO= pO;

		//Establecemos quien inicia la partida...
		playerTurnoActual= null;
		if(playerXStarts) playerTurnoActual= playerX;
		else playerTurnoActual= playerO;
	}
	/**
	 * Crea una nueva partida entre dos jugadores.
	 * @param pX
	 * @param pO
	 */
	public Partida(Player pX, Player pO, boolean playerXStarts){
		this(pX, pO, playerXStarts, false);
	}
	
	
	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(SpriteBatch batch, ShapeRenderer shapeRender) {
		//Graficamos el tablero...
		tablero.render(batch, shapeRender);
		
		//Graficamos nombres de los jugadores...
		/*
		ResourceManager.fonts.defaultFont.draw(batch, "Jugadores:", 20, Gdx.graphics.getHeight() - 30);
		
		String p1Turno= "";
		String p2Turno= "";
		if(playerTurnoActual.equals(player1)) p1Turno= " <- TURNO ACTUAL";
		else if(playerTurnoActual.equals(player2)) p2Turno= " <- TURNO ACTUAL";
		ResourceManager.fonts.defaultFont.draw(batch, "1- " + player1.getNick() + "(" + player1.getTipoFicha() + ")" + p1Turno, 20, Gdx.graphics.getHeight() - 45);
		ResourceManager.fonts.defaultFont.draw(batch, "2- " + player2.getNick() + "(" + player2.getTipoFicha() + ")" + p2Turno, 20, Gdx.graphics.getHeight() - 60);
		*/
		//Graficamos info variada
		//ResourceManager.fonts.defaultFont.draw(batch, "Tablero full: " + tablero.isFull(), 20, 40);
	}//fin render

	
	
	
	/**
	 * Este metodo gestiona un click que realiza el jugador actual de la partida. Se debe verificar si recae en alguna celda del tablero, en
	 * cuyo caso se debe colocar (intentar colocar) su ficha alli.
	 * @param clickX
	 * @param clickY
	 */
	public void playerClicksOnScreen(float clickX, float clickY){
		//Verificamos si el click recae en alguna de las celdas del tablero...
		Celda cell= tablero.getCelda(clickX, clickY);
		
		if(cell == null) return;
		
		//Intentamos colocar la ficha del jugador actual...
		if(tablero.putFichaOnCelda(cell.getNroCelda(), this.getFichaPlayerActual())){
			//Se ha colocado la ficha de forma local. Se avisa al gameplay para que avise al servidor.
			TaTeTi.getInstance().getGamePlay().sendFichaColocada(cell.getNroCelda(), this.getFichaPlayerActual());
		}else{
			//No se ha podido colocar la ficha (por diferentes motivos, probablemente porque la celda ya contiene una ficha). No hacemos nada con este click.
		}
	}
	
	
	public void putFichaOnCelda(int nroCelda, Ficha tipoFicha){
		tablero.putFichaOnCelda(nroCelda, tipoFicha);
	}
	
	/**
	 * Actualiza el player del turno actual. Pasa al siguiente que corresponda.
	 */
	public void nextPlayer(){
		if(playerTurnoActual.equals(playerX)) playerTurnoActual= playerO;
		else if(playerTurnoActual.equals(playerO)) playerTurnoActual= playerX;
	}
	
	
	public void clearTablero(){
		tablero.clear();
	}
	
	/**
	 * Este metodo verifica el estado de la partida (ver MatchStates). Verifica si alguien gana, si se ha empatado o si aun la partida sigue
	 * en curso.
	 * @return
	 */
	public MatchStates getPartidaState(){
		//Si alguno se rindi�
		if(rendicion_x) return MatchStates.RENDICION_X;
		if(rendicion_o) return MatchStates.RENDICION_O;
		
		//Verificamos si gana X
		if(tablero.checkWinner(Ficha.CRUZ)){
			return MatchStates.GANADOR_X;
		}
		
		//Verificamos si gana O
		if(tablero.checkWinner(Ficha.CIRCULO)){
			return MatchStates.GANADOR_O;
		}
		
		//Verificamos si el tablero esta lleno, en cuyo caso la partida finaliza en empate
		if(tablero.isFull()){
			return MatchStates.EMPATE;
		}
		
		//Si no hay ganadores y el tablero aun no esta lleno, entonces la partida sigue en curso
		return MatchStates.PARTIDA_EN_CURSO;
	}//fin check end conditions
	
	
	public Player getPlayerX(){
		return playerX;
	}
	
	public Player getPlayerO(){
		return playerO;
	}
	
	/**
	 * Devuelve el tipo de ficha del jugador del turno actual.
	 */
	public Ficha getFichaPlayerActual(){
		if(playerTurnoActual.equals(playerX)) return Ficha.CRUZ;
		else return Ficha.CIRCULO;
	}
	
	/**
	 * Devuelve el oponente del cliente (este metodo es util s�lo del lado del cliente)
	 * @return
	 */
	public Player getOpponent(){
		if(GameSession.getPlayer().equals(getPlayerX())) return getPlayerO();
		else return getPlayerX();
	}
	
	public void playerX_seRinde(){
		rendicion_x= true;
	}
	public void playerO_seRinde(){
		rendicion_o= true;
	}
}//fin clase
