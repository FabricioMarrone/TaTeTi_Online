package shuken.TaTeTi.Network;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents "a message TCP/IP". It will travel from one side to another over the net.</br>
 * 
 * Contains basic data that will be used in specific ways, depending the message type.
 * 
 * @author F.Marrone
 */
public class InetMessage implements Serializable{
	
	//private static final long serialVersionUID = 1L;
	
	/** Internet Message type */
	public InetMsgType type;

	public static enum InetMsgType{
		//Del server al cliente (SaC)
		SaC_heartBeat_response,
		SaC_Start_Match,
		SaC_Respuesta_Loggin_request,
		SaC_Respuesta_Create_Account,
		SaC_Respuesta_Solicitud_para_jugar,
		SaC_Player_Solicita_Jugar_con_vos,
		SaC_Player_ha_rechazado_oferta,
		SaC_Ficha_Colocada_y_Avanzar_turno,
		SaC_Avanzar_de_turno,
		SaC_Fin_de_Partida,
		SaC_FichaColocada_y_FinDePartida,
		SaC_MejoresPuntajes,
		SaC_PlayersOnline,
		SaC_SeHaCanceladoSolicitudParaJugar,
		SaC_ErrorMessage,
		
		//Del cliente al server (CaS)
		CaS_HeartBeat,
		CaS_Loggin_request,
		CaS_Create_Account,
		CaS_Ficha_Colocada,
		CaS_Solicitud_para_jugar,
		CaS_Respuesta_Player_Solicita_Jugar_con_vos,
		CaS_Cancelar_Solicitud_para_jugar,
		CaS_Idle,
		CaS_ObtenerHighScores,
		CaS_ObtenerPlayersOnline,
		CaS_PlayerSeRinde,
		CaS_CerrarSesion
	}
	
	/** Informacion enviada en el mensaje. */
	public ArrayList<Boolean> booleans;
	public ArrayList<Integer> ints;
	public ArrayList<Float> floats;
	public ArrayList<String> strings;

	public InetMessage(InetMsgType type){
		this.type= type;
		
		//NOTA:   Dependiendo del tipo de mensajes deberiamos inicializar s�lo aquellos arrayList que van a ser usados.
		booleans= new ArrayList<Boolean>();
		ints= new ArrayList<Integer>();
		floats= new ArrayList<Float>();
		strings= new ArrayList<String>();
	}
}//end class

