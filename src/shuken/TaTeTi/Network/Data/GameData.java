package shuken.TaTeTi.Network.Data;

import shuken.TaTeTi.Network.ServerConfig;

/**
 * Clase singleton que provee acceso a los datos.
 * @author F. Marrone
 *
 */
public class GameData {

	public static IPlayerData playerData;
	public static IMatchData matchData;
	
	public static void loadAll(){
		
		if(ServerConfig.PERSISTANCE_ON_DATABASE){
			playerData= new PlayerData_BD();
			matchData= new MatchData_BD();
		}else{
			playerData= new PlayerData_Memoria();
		}
		
		
	}
}//fin clase
