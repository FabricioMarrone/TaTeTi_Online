package shuken.TaTeTi.Entities;

public class PlayersOnlineRecord {

	public String nick;
	public String state;

	public PlayersOnlineRecord(String nick, String state){
		this.nick= nick;
		this.state= state;
	}

	@Override
	public String toString() {
		return nick + " - " + state;
	}
	
	
}//end class
