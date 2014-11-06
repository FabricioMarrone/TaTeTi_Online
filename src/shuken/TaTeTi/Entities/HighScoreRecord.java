package shuken.TaTeTi.Entities;

public class HighScoreRecord{
	
	/** Testing only */
	protected String record;
	
	/** Data */
	protected String nick;
	protected int won, lose, draw;
	
	public HighScoreRecord(String nick, int won, int lose, int draw){
		this.record= getRecord(nick, won, lose, draw);
		this.nick= nick;
		this.won= won;
		this.lose= lose;
		this.draw= draw;
	}
	
	public static String getRecord(String nick, int won, int lose, int draw){
		return nick + " G:" + won + " P:" + lose + " E:" + draw;
	}
}//end class
