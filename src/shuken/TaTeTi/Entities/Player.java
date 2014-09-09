package shuken.TaTeTi.Entities;

public class Player {


	/** Nombre del jugador. */
	private String nick;
	/** Password */
	private String password;
	/** Estadisticas del jugador (GANADOS, PERDIDOS, EMPATADOS)*/
	private int ganados, perdidos, empatados;
	
	/**
	 * Estados que puede adquirir un player. Esto es usado por el servidor para saber que esta haciendo el jugador.
	 */
	public static enum States{
		IDLE{
		      public String toString() {
		          return "Disponible";
		      }
		  },
		WAITING_FOR_OPPONENT{
		      public String toString() {
		          return "Esperando respuesta";
		      }
		  },
		RESPONDIENDO_SOLICITUD{
		      public String toString() {
		          return "Respondiendo solicitud";
		      }
		  },
		PLAYING{
		      public String toString() {
		          return "Jugando";
		      }
		  }
	}
	
	/** Estado del player. En principio, esto sólo es usado por el server.*/
	private States state;
	
	
	
	public Player(String nick, String pass){
		this(nick, pass, 0, 0 ,0);
	}
	
	public Player(String nick, String pass, int won, int lose, int draw){
		this.nick= nick;
		this.password= pass;
		state= States.IDLE;
		ganados= won;
		perdidos= lose;
		empatados= draw;
	}
	
	public String getNick(){
		return nick;
	}
	
	public String getPassword(){
		return password;
	}
	/**
	 * Devuelve la cantidad de partidas ganadas
	 */
	public int getGanados(){
		return ganados;
	}
	/**
	 * Devuelve la cantidad de partidas perdidas
	 */
	public int getPerdidos(){
		return perdidos;
	}
	/**
	 * Devuelve la cantidad de partidas empatadas
	 */
	public int getEmpatados(){
		return empatados;
	}
	
	public int getTotalPartidasJugadas(){
		return getGanados() + getEmpatados() + getPerdidos();
	}
	
	public int getTotalScore(){
		return getGanados() * 10 + getEmpatados() * 2 - getPerdidos();
	}
	
	
	public void incrementWon(){
		ganados+= 1;
	}
	
	public void incrementLose(){
		perdidos+= 1;
	}
	
	public void incrementDraw(){
		empatados+= 1;
	}
	
	public States getState(){
		return state;
	}
	
	public void setState(States state){
		this.state= state;
	}
	/**
	 * Devuelve true si el playe se encuentra en estado "idle".
	 * @return
	 */
	public boolean isIdle(){
		if(state == States.IDLE) return true;
		else return false;
	}
	
	/**
	 * Devuelve true si el playe se encuentra en estado "waiting for opponent".
	 * @return
	 */
	public boolean isWaitingForOpponent(){
		if(state == States.WAITING_FOR_OPPONENT) return true;
		else return false;
	}
	
	/**
	 * Devuelve true si el playe se encuentra en estado "playing", lo que significa que esta en medio de una partida con alguien.
	 * @return
	 */
	public boolean isPlaying(){
		if(state == States.PLAYING) return true;
		else return false;
	}
	
	/**
	 * Devuelve true si el player se encuentra en estado "respondiendo solicitud".
	 * @return
	 */
	public boolean isRespondiendoSolicitud(){
		if(state == States.RESPONDIENDO_SOLICITUD) return true;
		else return false;
	}
	
	@Override
	public String toString(){
		return nick + " " + state;
	}
}//fin clase
