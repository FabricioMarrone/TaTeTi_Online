package shuken.TaTeTi.Network.Data;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import shuken.TaTeTi.Entities.Partida;

public class MatchData_BD implements IMatchData{

	private static final String nickX_column= "nickX";
	private static final String nickO_column= "nickO";
	private static final String fecha_column= "fecha";
	private static final String hora_column= "hora";
	private static final String result_column= "resultado";
	
	@Override
	public void saveMatch(Partida p) {
		String fecha = new SimpleDateFormat("dd-MM-yy").format(Calendar.getInstance().getTime());
		String hora = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
		String nickX= p.getPlayerX().getNick();
		String nickO= p.getPlayerO().getNick();
		String resultado= p.getPartidaState().name();
		
		String columns= fecha_column + ", " + hora_column + ", " + nickX_column + ", " + nickO_column + "," + result_column;
		String values= "'"+ fecha +"', '"+ hora + "', '"+ nickX +"', '"+ nickO +"', '" + resultado +"'";
		
		Connector.execute("insert into Partidas ("+columns+") values ("+values+")");
		
	}

}//end class
