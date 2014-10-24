package shuken.TaTeTi.Entities;

import shuken.TaTeTi.Localization;
import shuken.TaTeTi.Localization.Languages;

public class PlayersOnlineRecord {

	public String nick;
	public String state;

	public PlayersOnlineRecord(String nick, String state){
		this.nick= nick;
		
		if(Localization.getCurrentLanguage()==Languages.EN){
			if(state.compareToIgnoreCase("Disponible")==0) state= "Available";
			if(state.compareToIgnoreCase("Esperando respuesta")==0) state= "Waiting for answer";
			if(state.compareToIgnoreCase("Respondiendo solicitud")==0) state= "Answering solicitude";
			if(state.compareToIgnoreCase("Jugando")==0) state= "Playing";
		}
		
		 this.state= state;
	}

	@Override
	public String toString() {
		return nick + " - " + state;
	}
	
	
}//end class
