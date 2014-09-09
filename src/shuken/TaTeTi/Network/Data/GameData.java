package shuken.TaTeTi.Network.Data;

/**
 * Clase singleton que provee acceso a los datos.
 * @author F. Marrone
 *
 */
public class GameData {

	
	public static PlayerData playerData;
	//public static PartidaRecordData partidaData;
	
	public static void loadAll(){
		playerData= new PlayerData_Memoria();
		//partidaData= new PartidaRecordData_memoria();
	}
}//fin clase
