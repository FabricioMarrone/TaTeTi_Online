package shuken.TaTeTi.Network.Data;

import shuken.TaTeTi.Network.ServerConfig;

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
}
