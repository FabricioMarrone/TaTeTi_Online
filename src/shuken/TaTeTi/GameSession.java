package shuken.TaTeTi;

import java.util.ArrayList;

import shuken.Engine.SimpleGUI.SimpleGUI;
import shuken.TaTeTi.Entities.Ficha;
import shuken.TaTeTi.Entities.Player;
import shuken.TaTeTi.Network.Client;
import shuken.TaTeTi.Network.InetMessage;

/**
 * Singleton class that contains the session of the user, and also is responsible for managing the Threads in charge of communicating with the server.
 * @author F. Marrone
 */
public class GameSession implements Updateable{

	private static GameSession instance= null;
	public static GameSession getInstance(){
		if(instance == null) instance= new GameSession();
		
		return instance;
	}
	
	/** Flag for login */
	private boolean sesionIniciada= false;
	
	/** Local player info. */
	private Player localPlayer;
	
	/** Threads in charge of communicating with the server.*/
	private Thread serverTalker, listenServer;
	
	/** Client object. It manages the socket and IPs. */
	private Client client;
	
	/** Errors */
	private boolean serverNotFoundError= false;
	private boolean serverConnectionLost= false;
	private boolean tryConnectToServer= false;	//var aux que permite intentar una vez mas conectar, incluso cuando los flag de control indican que no es posible
	
	/** Latency */
	private static final int LATENCY_COUNT_MAX= 5;
	private int promLatency;
	private long acumulatedLatency;
	private long latency;
	private int latencyCount;
	
	//**************************************************************************************************************//
	//*********** Mensajes e informacion que los SCREENS le piden al GameSession que envie al servidor *************//
	//**************************************************************************************************************//
	protected boolean sendheartBeat= false;
	private static final float HEARTBEAT_INTERVAL= 0.3f;	//en segundos
	private float heartBeatTime;
	
	//**************** LOGGIN SCREEN ****************//
	protected boolean sendLogginRequest= false;
	protected String nickIngresado= "";
	protected String passIngresado= "";
	
	//***********************************************//
	
	//*********** Create Account SCREEN ************//
	protected boolean sendCreateAccount= false;
	protected String newUserNick= "";
	protected String newUserPass= "";

	//***********************************************//
	
	//************** MAIN MENU SCREEN ***************//
	protected boolean sendSolicitudParaJugar= false;
	protected String nickOponenteSolicitado= "";
	//---
	protected boolean sendRespuestaPlayerSolicitaJugarConVos= false;
	protected String nickSolicitante= "";
	protected boolean respuestaSolicitudEntrante;
	protected boolean respuestaIsForTimeOut;
	//---
	protected boolean sendCancelarSolicitud= false;
	//---
	protected boolean sendIdle= false;
	//---
	protected boolean sendCerrarSesion= false;
	//---
	protected boolean sendObtenerHighScores= false;
	//---
	protected boolean sendObtenerPlayersOnline= false;
	//***********************************************//
	
	//************** GAMEPLAY SCREEN ****************//
	protected boolean sendFichaColocada= false;
	protected Ficha fichaColocadaRecientemente;
	protected int nroCeldaEnQueSeColocoFichaRecientemente;
	protected String nickOpponente;
	//---
	protected boolean sendPlayerSeRinde= false;
	protected boolean seRindioX;
	//***********************************************//	
	
	//**************************************************************************************************************//

	/**
	 * Private constructor.
	 */
	private GameSession(){
		//Inicializamos el Cliente...
		client= new Client();
		
		//Inicializamos threads de comunicación con el servidor...
		serverTalker= new Thread(new ServerTalker());
		listenServer= new Thread(new ListenServer());
	}
	
	public boolean isSessionOn(){
		return sesionIniciada;
	}

	/**
	 * @return the local player. Might be null if "isSessionOn()" returns false.
	 */
	public Player getLocalPlayer(){
		return localPlayer;
	}
	
	public void startSession(Player p){
		this.localPlayer= p;
		sesionIniciada= true;
	}
	
	public void closeSession(){
		this.localPlayer= null;
		sesionIniciada= false;
	}
	
	public static Player getPlayer(){
		return instance.getLocalPlayer();
	}
	
	public int getLatency(){
		if(promLatency > 9999) return 9999;
		else if(promLatency < 0) return 0;
		else return promLatency;
	}
	
	@Override
	public void update(float delta) {
		//Si estamos conectados al socket del server, entonces no debe haber ningun mensaje de error relacionado
		if(this.isConnectedToServer()) SimpleGUI.getInstance().turnAreaOFF(SimpleGUI.getInstance().errorMsg);
		
		heartBeatTime+= delta;
		if(heartBeatTime > HEARTBEAT_INTERVAL){
			heartBeatTime= 0;
			sendheartBeat= true;
		}
		
		//Mantenemos vivos a los threads...
		if(!serverTalker.isAlive()){
			serverTalker= new Thread(new ServerTalker()); 
			serverTalker.start();
		}
		if(!listenServer.isAlive()){
			listenServer= new Thread(new ListenServer());
			listenServer.start();
		}
		
	}//end update
	
	/**
	 * The GameSession class ask to the Client class to connect to the server. If for any reason, the server is offline or something, this method will
	 * try to connect a few times more.
	 */
	public void connectToServer(){
		if(!tryConnectToServer){
			if(serverNotFoundError || serverConnectionLost) return;
		}
		
		tryConnectToServer= false;
		client.connectToServer();
	}
	
	public void tryConnectToServer(){
		tryConnectToServer= true;
	}
	
	//loggin screen
	public void sendLogginRequest(String nick, String pass){
		this.sendLogginRequest= true;
		this.nickIngresado= nick;
		this.passIngresado= pass;
	}
		
	//create account
	public void sendCreateAccount(String nick, String pass){
		this.sendCreateAccount= true;
		this.newUserNick= nick;
		this.newUserPass= pass;
	}
	
	//main menu screen
	public void sendSolicitudParaJugar(String nickOpponenteSolicitado){
		this.sendSolicitudParaJugar= true;
		this.nickOponenteSolicitado= nickOpponenteSolicitado;
	}
		
	//main menu screen
	public void sendRespuestaPlayerSolicitaJugarConVos(String nickSolicitante, boolean resp, boolean isForTimeOut){
		this.sendRespuestaPlayerSolicitaJugarConVos= true;
		this.respuestaSolicitudEntrante= resp;
		this.respuestaIsForTimeOut= isForTimeOut;
		this.nickSolicitante= nickSolicitante;
	}
	
	//main menu scren
	public void sendCancelarSolicitud(String nickOpponent){
		this.sendCancelarSolicitud= true;
		this.nickOponenteSolicitado= nickOpponent;
	}
	
	//main menu screen
	public void sendIdle(){
		this.sendIdle= true;
	}
	
	//main menu screen
	public void sendCerrarSesion(){
		sendCerrarSesion= true;
	}
	
	//main menu screen
	public void sendObtenerHighScores(){
		sendObtenerHighScores= true;
	}
	
	//main menu screen
	public void sendObtenerPlayersOnline(){
		sendObtenerPlayersOnline= true;
	}
	
	//Gameplay screen
	public void sendFichaColocada(int nroCelda, Ficha ficha, String nickOpponente){
		this.sendFichaColocada= true;
		this.nroCeldaEnQueSeColocoFichaRecientemente= nroCelda;
		this.fichaColocadaRecientemente= ficha;
		this.nickOpponente= nickOpponente;
	}
	
	//Gamelay screen
	public void sendPlayerSeRinde(boolean seRindioX){
		this.sendPlayerSeRinde= true;
		this.seRindioX= seRindioX;
	}
	
	public boolean isConnectedToServer(){
		return client.connectedToServer;
	}
	
	public void serverNotFoundError(Exception e){
		serverNotFoundError= true;

		//JOptionPane.showMessageDialog(null, "No se encuentra el servidor. Uno de los siguientes errores pudo haber ocurrido:\n-El servidor está caido.\n-La direccón IP del servidor es incorrecta.", "Error de conexion", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
		
		//Mostramos mensaje de error
		ArrayList<String> text= new ArrayList<String>();
		text.add(Localization.ServerNoEncontrado1);
		text.add(Localization.ServerNoEncontrado2);
		text.add(Localization.ServerNoEncontrado3);
		text.add(Localization.ServerNoEncontrado4);
		SimpleGUI.getInstance().errorMsg.setText(text);
		SimpleGUI.getInstance().errorMsg.show();
	}
	
	public void connectionLost(Exception e){
		serverConnectionLost= true;

		//JOptionPane.showMessageDialog(null, "Se perdió la comunicación con el servidor.", "Connection LOST", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
		
		//Mostramos mensaje de error
		ArrayList<String> text= new ArrayList<String>();
		text.add(Localization.SePerdioComServer1);
		text.add(Localization.SePerdioComServer2);
		SimpleGUI.getInstance().errorMsg.setText(text);
		SimpleGUI.getInstance().errorMsg.show();
				
		TaTeTi.getInstance().setScreen(TaTeTi.getInstance().loginScreen);
	}
	
	/**
	 * Sub-class that send messages to the server.
	 * @author F.Marrone
	 *
	 */
	private class ServerTalker implements Runnable {

		@Override
		public void run() {

			if(sendheartBeat){
				client.sendHeartBeat();
				sendheartBeat= false;
				latency= System.currentTimeMillis();
			}
			
			//**************** LOGGIN SCREEN ****************//
			if(sendLogginRequest){
				System.out.println("Client-side: Solicitando loggin...");
				client.sendLogginRequest(nickIngresado, passIngresado);
				sendLogginRequest= false;
			}
			//***********************************************//
			
			//********** Create Account SCREEN **************//
			if(sendCreateAccount){
				client.sendCreateAccount(newUserNick, newUserPass);
				sendCreateAccount= false;
			}
			//***********************************************//
			
			//**************** MAIN MENU SCREEN *************//
			if(sendSolicitudParaJugar){
				System.out.println("Client-side: enviando solicitud para jugar contra " + nickOponenteSolicitado + "...");
				client.sendSolicitudParaJugar(nickOponenteSolicitado);
				sendSolicitudParaJugar= false;
			}
			
			if(sendRespuestaPlayerSolicitaJugarConVos){
				client.sendRespuestaPlayerSolicitaJugarConVos(respuestaSolicitudEntrante, nickSolicitante, respuestaIsForTimeOut);
				sendRespuestaPlayerSolicitaJugarConVos= false;
			}
			
			if(sendCancelarSolicitud){
				client.sendCancelarSolicitud(nickOponenteSolicitado);
				sendCancelarSolicitud= false;
			}
			if(sendIdle){
				client.sendIdle();
				sendIdle= false;
			}
			
			if(sendCerrarSesion){
				client.sendCerrarSesion();
				sesionIniciada= false;
				sendCerrarSesion= false;
			}
			
			if(sendObtenerHighScores){
				client.sendObtenerHighScores();
				sendObtenerHighScores= false;
			}
			
			if(sendObtenerPlayersOnline){
				client.sendObtenerPlayersOnline();
				sendObtenerPlayersOnline= false;
			}
			//***********************************************//
			
			//************** GAMEPLAY SCREEN ****************//
			if(sendFichaColocada){
				System.out.println("Client-side: Cliente avisa al servidor que se ha colocado una ficha en [" + nroCeldaEnQueSeColocoFichaRecientemente + ", " + fichaColocadaRecientemente + "]");
				client.sendFichaColocada(nroCeldaEnQueSeColocoFichaRecientemente, fichaColocadaRecientemente, nickOpponente);
				sendFichaColocada= false;
			}
			
			if(sendPlayerSeRinde){
				client.sendPlayerSeRinde(seRindioX);
				sendPlayerSeRinde= false;
			}
			//***********************************************//
		}//end run
	}//end servertalker
	
	/**
	 * This sub-class waits for incoming messages from the server.
	 * @author F.Marrone
	 *
	 */
	private class ListenServer implements Runnable {

		@Override
		public void run() {
			//Si no estamos conectados al servidor (me refiero al socket) no escuchamos nada para evitar error.
			if(!isConnectedToServer()) return;
			
			try{
				//Verificamos si hay un nuevo mensaje proveniente del servidor...
				InetMessage msg= client.receiveMessage();

				//Verificamos de cual se trata. NOTA: solamente consideramos aquellos que son "Server a Cliente" (SaC)
				switch(msg.type){

				case SaC_heartBeat_response: 
					latency= System.currentTimeMillis() - latency; 
					acumulatedLatency+= latency;
					latencyCount++;
					if(latencyCount > LATENCY_COUNT_MAX){
						promLatency= (int)(acumulatedLatency/LATENCY_COUNT_MAX);
						acumulatedLatency= 0;
						latencyCount= 0;
					}
					break;

				//**************** LOGGIN SCREEN ****************//
				case SaC_Respuesta_Loggin_request: TaTeTi.getInstance().getLogginScreen().SaC_Respuesta_Loggin_request(msg); break;
				//***********************************************//
				
				//********** Create Account SCREEN **************//
				case SaC_Respuesta_Create_Account: TaTeTi.getInstance().getCreateAccountScreen().SaC_Respuesta_Create_Account(msg); break;
				//***********************************************//
				
				//**************** MAINMENU **********************//
				case SaC_Respuesta_Solicitud_para_jugar: TaTeTi.getInstance().getMainMenuScreen().SaC_Respuesta_Solicitud_para_jugar(msg); break;

				case SaC_Player_Solicita_Jugar_con_vos: TaTeTi.getInstance().getMainMenuScreen().SaC_Player_Solicita_Jugar_con_vos(msg); break;

				case SaC_Player_ha_rechazado_oferta: TaTeTi.getInstance().getMainMenuScreen().SaC_Player_ha_rechazado_oferta(msg); break;

				case SaC_Start_Match: TaTeTi.getInstance().getMainMenuScreen().SaC_Start_Match(msg); break;

				case SaC_MejoresPuntajes: TaTeTi.getInstance().getMainMenuScreen().SaC_MejoresPuntajes(msg); break;

				case SaC_PlayersOnline: TaTeTi.getInstance().getMainMenuScreen().SaC_PlayersOnline(msg); break;
					
				case SaC_SeHaCanceladoSolicitudParaJugar: TaTeTi.getInstance().getMainMenuScreen().SaC_SeHaCanceladoSolicitudParaJugar(msg); break;
				
				case SaC_ErrorMessage:  TaTeTi.getInstance().getMainMenuScreen().SaC_ErrorMessage(msg); break;
				//************************************************//
							
				//**************** GAMEPLAY **********************//
				case SaC_Ficha_Colocada_y_Avanzar_turno: TaTeTi.getInstance().getGamePlay().SaC_Ficha_Colocada_y_Avanzar_turno(msg); break;

				case SaC_Avanzar_de_turno:  TaTeTi.getInstance().getGamePlay().nextTurn(); break;

				case SaC_FichaColocada_y_FinDePartida: TaTeTi.getInstance().getGamePlay().SaC_FichaColocada_y_FinDePartida(msg); break;

				case SaC_Fin_de_Partida: TaTeTi.getInstance().getGamePlay().SaC_Fin_de_Partida(msg); break;
				//************************************************//

				default:
					break;

				}//end switch
			}catch(NullPointerException e){		
				if(!isSessionOn()) {
					//System.out.println("Tiro null pointer en GameSession listenServer thread. Posiblemente el server se cayo o el usuario cerro sesion en un momento justo.");
					return;
				}
			}
		}//end run
	}//end listen server
}//end class
