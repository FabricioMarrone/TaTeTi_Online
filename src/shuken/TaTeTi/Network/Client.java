package shuken.TaTeTi.Network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import shuken.TaTeTi.Config;
import shuken.TaTeTi.GameSession;
import shuken.TaTeTi.Entities.Ficha;
import shuken.TaTeTi.Network.InetMessage.InetMsgType;

public class Client {

	/** Client Socket */
	Socket clientSocket;

	public boolean connectedToServer= false;
	
	/** Input and Output to communicate with server. */
	ObjectOutputStream out;
	ObjectInputStream in;

	/** Incoming message.*/
	InetMessage message;
	
	public void sendHeartBeat(){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.CaS_HeartBeat);

		//Lo enviamos...
		this.sendMessage(msg);
	}
	
	public void sendLogginRequest(String nick, String pass){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.CaS_Loggin_request);
		msg.strings.add(0, new String(nick));
		msg.strings.add(1, new String(pass));
		
		//Lo enviamos...
		this.sendMessage(msg);
	}
	
	public void sendCreateAccount(String nick, String pass){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.CaS_Create_Account);
		msg.strings.add(0, new String(nick));
		msg.strings.add(1, new String(pass));

		//Lo enviamos...
		this.sendMessage(msg);
	}
	
	public void sendFichaColocada(int nroCelda, Ficha tipo, String nickOpponent){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.CaS_Ficha_Colocada);
		msg.ints.add(0, new Integer(nroCelda));
		msg.ints.add(1, new Integer(tipo.ordinal()));
		msg.strings.add(0, new String(nickOpponent));
		
		//Lo enviamos...
		this.sendMessage(msg);
	}
	
	public void sendPlayerSeRinde(boolean seRindioX){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.CaS_PlayerSeRinde);
		msg.booleans.add(0, new Boolean(seRindioX));

		//Lo enviamos...
		this.sendMessage(msg);
	}
	
	public void sendSolicitudParaJugar(String nickOponente){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.CaS_Solicitud_para_jugar);
		msg.strings.add(0, new String(nickOponente));
		
		//Lo enviamos...
		this.sendMessage(msg);
	}
	
	public void sendRespuestaPlayerSolicitaJugarConVos(boolean success, String nickSolicitante, boolean isForTimeOut){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.CaS_Respuesta_Player_Solicita_Jugar_con_vos);
		msg.booleans.add(0, new Boolean(success));
		msg.booleans.add(1, new Boolean(isForTimeOut));
		msg.strings.add(0, new String(nickSolicitante));
		
		//Lo enviamos...
		this.sendMessage(msg);
	}
	
	public void sendCancelarSolicitud(String nickOpponent){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.CaS_Cancelar_Solicitud_para_jugar);
		msg.strings.add(0, new String(nickOpponent));
		
		//Lo enviamos...
		this.sendMessage(msg);
	}
	
	public void sendIdle(){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.CaS_Idle);
		
		//Lo enviamos...
		this.sendMessage(msg);
	}
	
	public void sendCerrarSesion(){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.CaS_CerrarSesion);

		//Lo enviamos...
		this.sendMessage(msg);
		
		//Cerramos socket
		//closeConnection();
	}
	
	public void sendObtenerHighScores(){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.CaS_ObtenerHighScores);

		//Lo enviamos...
		this.sendMessage(msg);
	}
	
	public void sendObtenerPlayersOnline(){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.CaS_ObtenerPlayersOnline);

		//Lo enviamos...
		this.sendMessage(msg);
	}
	
	/**
	 * Attempt to connect sockets. This is NOT a login.
	 * 
	 */
	public void connectToServer(){
		try{
			System.out.println("Client-side: Intentando conectar sockets.");
			//1. Intentamos conectar...
			clientSocket = new Socket(Config.SERVER_IP, Config.PORT);
			clientSocket.setTcpNoDelay(true);
			//clientSocket.setKeepAlive(true);
			//clientSocket.setSoTimeout(0);
			
			//2. get Input and Output streams
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(clientSocket.getInputStream());

			connectedToServer= true;
			
		} catch (UnknownHostException e) {
			connectedToServer= false;
			GameSession.getInstance().serverNotFoundError(e);
			
		} catch(ConnectException e){
			//Connection timed out o connection refused. Sea como sea, no encontro nada en la direccion IP.
			connectedToServer= false;
			GameSession.getInstance().serverNotFoundError(e);
		}catch (IOException e) {
			System.out.println("Client-side: error al intentar conectar con el socket del servidor.");
			connectedToServer= false;
			e.printStackTrace();
		}	

	}//end connect to server
	
	public void sendMessage(InetMessage msg)
	{
		try{
			if(!GameSession.getInstance().isConnectedToServer()) return;
			
			//Enviamos el mensaje...
			out.writeObject(msg);
			out.flush();
		}catch(SocketException s){
			connectedToServer= false;
			GameSession.getInstance().connectionLost(s);
		}catch(IOException ioException){
			ioException.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}//end sendMessage

	public InetMessage receiveMessage(){
		try {
			return (InetMessage)in.readObject();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Close this socket.
	 */
	public void closeConnection(){
		try {
			this.clientSocket.close();
			connectedToServer= false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}//end class
