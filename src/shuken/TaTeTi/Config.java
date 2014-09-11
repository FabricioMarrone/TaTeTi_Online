package shuken.TaTeTi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;



/**
 * General configs for Tic-Tac-Toe Online
 * @author F.Marrone
 *
 */
public class Config {

	public static boolean DEBUG_MODE;
	
	public static boolean TRANSITIONS_ON= true;
	
	public static String SERVER_IP;
	public static int PORT;
	
	private static boolean loadOK;
	
	public static void loadConfig(){
		//Inicializamos objetos que se encargan de la lectura...
		FileReader fileReader= null;
		BufferedReader bufferedReader= null;
		try{
			fileReader= new FileReader("config.ttt");
			bufferedReader = new BufferedReader(fileReader);
		}catch(FileNotFoundException ex){
			System.err.println("\nArchivo config no encontrado");
		}

		//Procedemos a leer el archivo...
		try{
			String line;
			while((line= bufferedReader.readLine()) != null){
				//Verificamos si se trata de un comentario
				if(line.startsWith("#")) continue;
				
				//Verificamos de que parametro se trata
				if(line.startsWith("serverIP")){
					SERVER_IP= line.substring(line.indexOf("=")+1).trim();
					System.out.println("ServerIP: " + SERVER_IP);
				}
				if(line.startsWith("port")){
					PORT= Integer.parseInt(line.substring(line.indexOf("=")+1).trim());
					System.out.println("Port: " + PORT);
				}
				if(line.startsWith("debug")){
					DEBUG_MODE= false;
					if(line.substring(line.indexOf("=")+1).trim().compareToIgnoreCase("true")== 0) DEBUG_MODE= true;
					System.out.println("DEBUG_MODE: " + DEBUG_MODE);
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
