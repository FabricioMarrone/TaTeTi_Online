package shuken.TaTeTi.Entities;

public class HighScoreRecord{
	
	/** En formato de string para render de prueba*/
	protected String record;
	/** Datos del registro */
	protected String nick;
	protected int won, lose, draw;
	
	public HighScoreRecord(String nick, int won, int lose, int draw){
		this.record= getRecord(nick, won, lose, draw);
		this.nick= nick;
		this.won= won;
		this.lose= lose;
		this.draw= draw;
	}
	
	/**
	 * Devuelve un String rapido con formato para mostrar en tabla.
	 * @param nick
	 * @param won
	 * @param lose
	 * @param draw
	 * @return
	 */
	public static String getRecord(String nick, int won, int lose, int draw){
		return nick + " G:" + won + " P:" + lose + " E:" + draw;
	}
	
}//end class
