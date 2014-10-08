package shuken.TaTeTi.Network;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class ServerConfig {

private static boolean loadOK;
	
	public static boolean PERSISTANCE_ON_DATABASE;
	public static boolean ALLOW_MULTIPLES_CLIENT;
	public static int PORT;
	
	public static void loadConfig(){
		//Inicializamos objetos que se encargan de la lectura...
		FileReader fileReader= null;
		BufferedReader bufferedReader= null;
		try{
			fileReader= new FileReader("serverConfig.ttt");
			bufferedReader = new BufferedReader(fileReader);
		}catch(FileNotFoundException ex){
			System.err.println("\nArchivo server config no encontrado");
		}

		//Procedemos a leer el archivo...
		try{
			String line;
			while((line= bufferedReader.readLine()) != null){
				//Verificamos si se trata de un comentario
				if(line.startsWith("#")) continue;
				
				//Verificamos de que parametro se trata
				if(line.startsWith("UseDB")){
					PERSISTANCE_ON_DATABASE= false;
					if(line.substring(line.indexOf("=")+1).trim().compareToIgnoreCase("true")== 0) PERSISTANCE_ON_DATABASE= true;
				}
				if(line.startsWith("AllowMultipleClients")){
					ALLOW_MULTIPLES_CLIENT= false;
					if(line.substring(line.indexOf("=")+1).trim().compareToIgnoreCase("true")== 0) ALLOW_MULTIPLES_CLIENT= true;
				}
				if(line.startsWith("port")){
					PORT= Integer.parseInt(line.substring(line.indexOf("=")+1).trim());
				}
				
			}
			loadOK= true;
		}catch(Exception e){
			loadOK= false;
		}
		
	}//end load config file
	
	
	public static boolean loadOK(){
		return loadOK;
	}
	
}//end class
