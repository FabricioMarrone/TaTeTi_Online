package shuken.TaTeTi;

import java.util.ArrayList;

import shuken.Engine.SimpleGUI.SimpleGUI;
import shuken.TaTeTi.Entities.Ficha;
import shuken.TaTeTi.Entities.Player;
import shuken.TaTeTi.Network.Client;
import shuken.TaTeTi.Network.InetMessage;


/**
 * Clase singleton que almacena informacion de la sesion del usuario, como asi tambien se encarga de gestionar los Threads encargados de la
 * comunicación con el servidor.
 * 
 * El GameSession es una clase que debe actualizarse en todos los screens.
 * 
 * @author F. Marrone
 *
 */
public class GameSession implements Updateable{

	
	private static GameSession instance= null;
	public static GameSession getInstance(){
		if(instance == null) instance= new GameSession();
		
		return instance;
	}
	
	/** Flag que indica si el usuario a iniciado o no su sesion (es decir, si esta loggeado o no). */
	private boolean sesionIniciada= false;
	
	/** Jugador local una vez que inició sesion. */
	private Player localPlayer;
	
	/** Threads encargados de enviar y recibir mensajes del servidor.*/
	private Thread serverTalker, listenServer;
	
	/** Objeto client que contiene el socket (direcciones ip y demas) y otras cosas mas para hablar con el servidor. */
	private Client client;
	
	/** Control de errores. */
	private boolean serverNotFoundError= false;
	private boolean serverConnectionLost= false;
	private boolean tryConnectToServer= false;	//var aux que permite intentar una vez mas conectar, incluso cuando los flag de control indican que no es posible
	
	/** Latency */
	private static final int LATENCY_COUNT_MAX= 3;
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
	//**************************************************************************************************************//
	//**************************************************************************************************************//
	
	/**
	 * Constructor privado.
	 */
	private GameSession(){
		
		//Inicializamos el Cliente...
		client= new Client();
		
		//Inicializamos threads de comunicación con el servidor...
		serverTalker= new Thread(new ServerTalker());
		listenServer= new Thread(new ListenServer());
	}//fin constructor
	
	
	/**
	 * Devuelve true si el usuario esta actualmente loggeado.
	 * @return
	 */
	public boolean isSessionOn(){
		return sesionIniciada;
	}

	/**
	 * Devuelve el player local. Este metodo puede devolver null si es que aun no se ha iniciado la sesion. Por las dudas, antes de llamar a
	 * este metodo cerciorarse con "isSessionOn()".
	 * @return
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
		
	}//fin update
	
	
	
	
	
	/**
	 * The GameSession class ask to the Client class to connect to the server. If for any reason, the server is offline or something, this method will
	 * try to connect a few times. If the problems persists, this method will do nothing then.
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
		text.add("No se encuentra el servidor. Uno de los siguientes errores pudo haber ocurrido:");
		text.add("-El servidor está caido.");
		text.add("-La direccón IP del servidor es incorrecta.");
		text.add("                                        (Click para quitar mensaje)");
		SimpleGUI.getInstance().errorMsg.setText(text);
		SimpleGUI.getInstance().errorMsg.show();
	}
	
	public void connectionLost(Exception e){
		serverConnectionLost= true;

		//JOptionPane.showMessageDialog(null, "Se perdió la comunicación con el servidor.", "Connection LOST", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
		
		//Mostramos mensaje de error
		ArrayList<String> text= new ArrayList<String>();
		text.add("Se perdió la comunicación con el servidor.");
		text.add("            (Click para quitar mensaje)");
		SimpleGUI.getInstance().errorMsg.setText(text);
		SimpleGUI.getInstance().errorMsg.show();
				
		TaTeTi.getInstance().setScreen(TaTeTi.getInstance().loginScreen);
	}
	
	/**
	 * Esta sub-clase se encarga de enviar mensajes al servidor.
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
		}//fin run

		

	}//fin servertalker
	
	
	
	
	
	
	
	/**
	 * Esta sub-clase continuamente escucha al servidor en espera de mensajes entrantes.
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
				//************************************************//
				
				
				
				//**************** GAMEPLAY **********************//
				case SaC_Ficha_Colocada_y_Avanzar_turno: TaTeTi.getInstance().getGamePlay().SaC_Ficha_Colocada_y_Avanzar_turno(msg); break;

				case SaC_Avanzar_de_turno:  TaTeTi.getInstance().getGamePlay().nextTurn(); break;

				case SaC_FichaColocada_y_FinDePartida: TaTeTi.getInstance().getGamePlay().SaC_FichaColocada_y_FinDePartida(msg); break;

				case SaC_Fin_de_Partida: TaTeTi.getInstance().getGamePlay().SaC_Fin_de_Partida(msg); break;
				//************************************************//


				default:
					break;

				}//fin switch
			}catch(NullPointerException e){
				
				if(!isSessionOn()) {
					//Si ocurre 1 solo error de este tipo, es posible que sea producto de que el usuario cerro sesion. No hacemos nada.
					//Pero si este error ocurre muchas veces en pocos segundos, el servidor ha sido dado de baja o se perdio conexion con el server.
					System.out.println("Tiro null pointer en GameSession listenServer thread. Posiblemente el server se cayo o el usuario cerro sesion en un momento justo.");
					return;
				}
			}

		}//fin run

	}//fin listen server
	
	
}//fin clase
