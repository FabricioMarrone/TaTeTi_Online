package shuken.TaTeTi;

import shuken.TaTeTi.Localization.Languages;

public class ServerMessages {

	public static String getMessage(String cod){
		System.out.println("Decodificando: " + cod);
		
		if(Localization.getCurrentLanguage() == Languages.ES){
			if(cod.compareToIgnoreCase("cod1")==0) return "El nombre de usuario se encuentra en uso.";
			if(cod.compareToIgnoreCase("cod2")==0) return "El nombre de usuario es incorrecto.";
			if(cod.compareToIgnoreCase("cod3")==0) return "La clave es incorrecta.";
			if(cod.compareToIgnoreCase("cod4")==0) return "Ya existe una sesión iniciada en esta máquina (sólo se permite una sesión por PC).";
			if(cod.compareToIgnoreCase("cod5")==0) return "Esta cuenta ya posee una sesion abierta.";
			if(cod.compareToIgnoreCase("cod6")==0) return "El jugador solicitado no se encuentra online.";
			if(cod.compareToIgnoreCase("cod7")==0) return "El jugador se encuentra actualmente en otra partida.";
			if(cod.compareToIgnoreCase("cod8")==0) return "El jugador se encuentra ocupado.";
			if(cod.compareToIgnoreCase("cod9")==0) return "El usuario con el que deseaba jugar ha perdido su conexión con el servidor.";
			
			
			
			return "Mensaje del servidor no decodificable.";
		}
		
		if(Localization.getCurrentLanguage() == Languages.EN){
			if(cod.compareToIgnoreCase("cod1")==0) return "Username is already on use.";
			if(cod.compareToIgnoreCase("cod2")==0) return "Incorrect username.";
			if(cod.compareToIgnoreCase("cod3")==0) return "Incorrect password.";
			if(cod.compareToIgnoreCase("cod4")==0) return "There is an account it's already logged on this machine (only one session is allowed per PC).";
			if(cod.compareToIgnoreCase("cod5")==0) return "This account it's already logged in.";
			if(cod.compareToIgnoreCase("cod6")==0) return "The player requested is not online.";
			if(cod.compareToIgnoreCase("cod7")==0) return "The player is currently in another game.";
			if(cod.compareToIgnoreCase("cod8")==0) return "The player is currently busy.";
			if(cod.compareToIgnoreCase("cod9")==0) return "The user you wanted to play has lost its connection to the server.";
			
			return "Server Message not decodable.";
		}
		
		return "";
	}//end getMessage
}//end class
