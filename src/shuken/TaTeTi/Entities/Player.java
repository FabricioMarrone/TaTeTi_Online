package shuken.TaTeTi.Entities;

public class Player {

	/** name of the player. */
	private String nick;
	/** Password */
	private String password;
	/** player data (win, lose, draw)*/
	private int ganados, perdidos, empatados;
	
	/**
	 * Player states. This allows the server to know what is doing the user.
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
	
	/** Current player state.*/
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
	
	public int getGanados(){
		return ganados;
	}

	public int getPerdidos(){
		return perdidos;
	}

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

	public boolean isIdle(){
		if(state == States.IDLE) return true;
		else return false;
	}
	
	public boolean isWaitingForOpponent(){
		if(state == States.WAITING_FOR_OPPONENT) return true;
		else return false;
	}
	
	public boolean isPlaying(){
		if(state == States.PLAYING) return true;
		else return false;
	}
	
	public boolean isRespondiendoSolicitud(){
		if(state == States.RESPONDIENDO_SOLICITUD) return true;
		else return false;
	}
	
	@Override
	public String toString(){
		return nick + " " + state;
	}
}//end class
