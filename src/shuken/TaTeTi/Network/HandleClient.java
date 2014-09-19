package shuken.TaTeTi.Network;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import shuken.TaTeTi.Entities.Ficha;
import shuken.TaTeTi.Entities.Partida;
import shuken.TaTeTi.Entities.Player;
import shuken.TaTeTi.Entities.Partida.MatchStates;
import shuken.TaTeTi.Network.Data.GameData;
import shuken.TaTeTi.Network.InetMessage.InetMsgType;


/*
 * Esta clase gestiona los clientes conectados al server. Hay un thread por cada cliente, y en cada uno de ellos se ejecuta la lógica de esta
 * clase.
 * 
 * 
 * 
 * Esto es SERVER-SIDE!!!!!
 */
public class HandleClient implements Runnable{
	
	/** Socket del cliente con el que el server se va a comunicar. */
	private Socket clientSocket;
	
	/** Input and Output para intercambiar mensajes.*/
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	/** Cuando se loggea el player, se lo almacena en esta variable. */
	private Player player= null;
	private long lastHeartBeatTime;
	
	private boolean closeThread= false;
	
	
	public HandleClient(Socket s){
		clientSocket= s;
		
		try{
		clientSocket.setSoTimeout(20000);
		out = new ObjectOutputStream(clientSocket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(clientSocket.getInputStream());
		} catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	@Override
	public void run() {


		try{
			while(!closeThread){
				//Esperamos hasta recibir un mensaje del cliente...
				InetMessage msg= (InetMessage)in.readObject();

				//Analizamos el mensaje y enviamos respuesta (si corresponde)...
				this.analizeInetMessage(msg);
			}//fin while
		}catch(Exception exception){
			//exception.printStackTrace();
			closeThread= true;
		}

		//Cerramos sockets
		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Verificamos si este cliente se encontraba en una partida, en cuyo caso se elimina la misma y se le avisa al otro cliente de la finalizacion
		if(player != null){
			Partida partida= Server.instance.getPartida(player.getNick());
			if(partida != null){
				String otroNick= "";
				if(player.getNick().compareTo(partida.getPlayerO().getNick())== 0) otroNick= partida.getPlayerX().getNick();
				else otroNick= partida.getPlayerO().getNick();
				HandleClient otroClient= Server.instance.getHandleClient(otroNick);
				if(otroClient != null){
					otroClient.sendFinDePartida(MatchStates.INCONCLUSO);
					//Eliminamos la partida del array de partidas
					Server.instance.partidas.remove(partida);
				}
			}
		}
		//Quitamos player de la lista y quitamos tambien la conexion de la lista de conexiones...
		Server.instance.removeConnection(this);
		
	}//fin de run

	
	
	private void analizeInetMessage(InetMessage msg){
		
		//NOTA: Solamente consideramos aquellos mensajes "Cliente a Servidor" (CaS) porque aca lo que hacemos es ESCUCHAR AL CLIENTE.
		switch(msg.type){
		case CaS_HeartBeat: heartBeat(); break;
		case CaS_Loggin_request: logginRquest(msg); break;
		case CaS_Create_Account: createAccount(msg); break;
		case CaS_Ficha_Colocada: fichaColocada(msg); break;
		case CaS_Solicitud_para_jugar: solicitudParajugar(msg); break;
		case CaS_Respuesta_Player_Solicita_Jugar_con_vos: respuestaPlayerParaJugar(msg); break;
		case CaS_Cancelar_Solicitud_para_jugar: cancelarSolicitudParaJugar(msg); break;
		case CaS_Idle: playerTurnsIDLE(msg); break;
		case CaS_CerrarSesion: cerrarSesion(); break;
		case CaS_ObtenerHighScores: obtenerHighScores(); break;
		case CaS_ObtenerPlayersOnline: obtenerPlayersOnline(); break;
		case CaS_PlayerSeRinde: playerSeRinde(msg); break;
		
		default:
			break;
		
		}//fin switch
		
	}//fin analize inet message
	
	
	protected void heartBeat(){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.SaC_heartBeat_response);

		//Guardamos el tiempo
		lastHeartBeatTime= System.currentTimeMillis();
		
		//Lo enviamos...
		this.sendMessage(msg);
	}
	
	protected void createAccount(InetMessage msg){
		//Obtenemos data...
		String nick= msg.strings.get(0);
		String pass= msg.strings.get(1);
		
		//Verificamos si existe un player para ese nick
		if(GameData.playerData.getOne(nick) != null){
			//Existe un usuario con ese nombre, por lo tanto, no es posible crear una cuenta nueva.
			sendRespuestaCreateAccount(false, "El nombre de usuario se encuentra en uso.");
			return;
		}
		
		//Damos de alta al nuevo jugador
		Player newPlayer= new Player(nick, pass);
		GameData.playerData.addPlayer(newPlayer);
		
		//Avisamos al cliente de que la creación fue exitosa
		sendRespuestaCreateAccount(true, "");
	}
	
	protected void playerSeRinde(InetMessage msg){
		//El mensaje nos indica cual player se rindió (si el X o el O)
		boolean seRindioX= msg.booleans.get(0);
		
		//Obtenemos la partida y la actualizamos
		Partida partida= Server.instance.getPartida(this.player.getNick());
		if(seRindioX) partida.playerX_seRinde();
		else partida.playerO_seRinde();
		
		//Obtenemos el handleclient del otro participante
		HandleClient hClientOtro= null;
		if(this.player.equals(partida.getPlayerX())) hClientOtro= Server.instance.getHandleClient(partida.getPlayerO().getNick());
		else hClientOtro= Server.instance.getHandleClient(partida.getPlayerX().getNick());
		
		//Enviamos a ambos jugadores el mensaje de "fin de partida"
		MatchStates resultado= partida.getPartidaState();
		hClientOtro.sendFinDePartida(resultado);
		this.sendFinDePartida(resultado);
		
		//Se registra el resultado de la partida.
		Server.instance.registrarPartida(partida);
		
		//Quitamos la partida del array de partidas...
		Server.instance.partidas.remove(partida);
	}
	
	protected void cancelarSolicitudParaJugar(InetMessage msg){
		//ESTE cliente ha cancelado una solicitud. Dentro del mensaje esta el nombre del cliente al que le tenemos que avisar de que ESTE canceló.
		String nickSolicitante= msg.strings.get(0);
		HandleClient hclientOtro= Server.instance.getHandleClient(nickSolicitante);
		
		if(hclientOtro != null) hclientOtro.sendSeHaCanceladoLaSolicitud(this.player.getNick());
		
		//ESTE cliente vuelve al estado IDLE
		player.setState(Player.States.IDLE);
		Server.instance.updateTablaPlayerOnline();
	}
	
	protected void obtenerPlayersOnline(){
		//Le pedimos al servidor los players online
		ArrayList<Player> po= Server.instance.getPlayersOnline();
		
		this.sendPlayersOnline(po);
	}
	
	protected void obtenerHighScores(){
		//Le pedimos al servidor que nos mande los 10 mejores players
		ArrayList<Player> top10= Server.instance.getTop10();
		
		this.sendMejoresPuntajes(top10);
	}
	
	protected void cerrarSesion(){
		//closeThread= true;
		//No cerramos el thread, sino que lo mantenemos vivo por si el cliente quiere seguir usando la aplicacion.
		player= null;
		Server.instance.updateTablaPlayerOnline();
	}
	
	
	protected void respuestaPlayerParaJugar(InetMessage msg){
		//Obtenemos respuesta...
		boolean resp= msg.booleans.get(0);
		boolean isForTimeOut= msg.booleans.get(1);
		String nickSolicitante= msg.strings.get(0);
		
		HandleClient hClientPlayerSolicitante= Server.instance.getHandleClient(nickSolicitante);
		
		if(hClientPlayerSolicitante == null){
			//Error inesperado 002.
			//System.err.println("Server-side: Error inesperado 002. El cliente solicitante ha crasheado.");

			//Colocamos a este player como disponible
			player.setState(Player.States.IDLE);
			Server.instance.updateTablaPlayerOnline();
			if(resp) this.send_ErrorMessage("El usuario " + nickSolicitante + " ha perdido su conexión con el servidor.");
			
			return;
		}
		
		//Actuamos segun respuesta.
		if(resp){
			//ESTE jugador acepta jugar contra "nickSolicitante". Enviamos a AMBOS el mensaje de iniciar match.
			boolean playerXStarts= Server.random.nextBoolean();
			
			//Dejamos al azar quien es X y quien es O
			boolean thisClientIsX= Server.random.nextBoolean();
			Ficha thisClientFicha= null;
			
			if(thisClientIsX){
				//El jugador que ACEPTA, es el player X
				thisClientFicha= Ficha.CRUZ;
				
				//Creamos la partida en el servidor...
				Server.instance.nuevaPartida(player, hClientPlayerSolicitante.getPlayer(), playerXStarts);
				
				
			}else{
				//El jugador que ACEPTA, es el player O
				thisClientFicha= Ficha.CIRCULO;
				
				//Creamos la partida en el servidor...
				Server.instance.nuevaPartida(hClientPlayerSolicitante.getPlayer(), player, playerXStarts);
				
			}
			
			//Enviamos a ESTE cliente...
			this.sendInicioPartida(thisClientFicha, nickSolicitante, playerXStarts);
			
			//Enviamos al OTRO cliente...
			Ficha otherClientFicha= null;
			if(thisClientFicha == Ficha.CRUZ) otherClientFicha= Ficha.CIRCULO;
			else  otherClientFicha= Ficha.CRUZ;
			hClientPlayerSolicitante.sendInicioPartida(otherClientFicha, player.getNick(), playerXStarts);
			
		}else{
			//ESTE player rechazó la oferta de jugar contra "nickSolicitante". Le avisamos al mismo de la respuesta.
			hClientPlayerSolicitante.sendPlayerRechazoOfertaDeJugar(player.getNick(), isForTimeOut);
		}
		
		
	}//fin respuestaPlayerParaJugar
	
	
	
	protected void solicitudParajugar(InetMessage msg){
		//Obtenemos data...
		String nickOponente= msg.strings.get(0);
		
		//Verificamos que ese jugador este online y ademas que este "idle".
		Player p= Server.instance.getPlayer(nickOponente);
		if(p == null){
			//El player solicitado no esta online.
			sendRespuestaSolicitudParaJugar(false, "El jugador solicitado no se encuentra online.");
			return;
		}
		
		if(!p.isIdle()){
			//El player se encuentra jugando con otra persona, o esperando respuesta de otra persona.
			if(p.isPlaying()) sendRespuestaSolicitudParaJugar(false, "El jugador se encuentra actualmente en otra partida.");
			else sendRespuestaSolicitudParaJugar(false, "El jugador se encuentra ocupado.");
			return;
		}
		
		//El player esta online y disponible.
		//Avisamos al solicitante que su solicitud es valida, y hacemos que espere.
		sendRespuestaSolicitudParaJugar(true, "La solicitud ha sido enviada. Espere a que el usuario " + nickOponente + " confirme o rechaze su solicitud.");
		
		//Enviamos solicitud al otro jugador. Para ello obtenemos su HandleClient.
		HandleClient hClientOpponent= Server.instance.getHandleClient(nickOponente);
		if(hClientOpponent == null){
			//Error inesperado 001.
			System.err.println("Server-side: Error inesperado 001.");
			return;
		}
		hClientOpponent.sendPlayerSolicitaJugarConVos(player.getNick());
		
	}//fin solicitud para jugar
	
	
	
	protected void fichaColocada(InetMessage msg){
		//Obtenemos data...
		int nroCelda= msg.ints.get(0);
		Ficha ficha= Ficha.getFicha(msg.ints.get(1));
		String nickDelOtroJugadorEnPartida= msg.strings.get(0);
		
		//Aca se agrega la ficha en el tablero que mantiene el server (esto le permite analizar si la partida ha finalizado o no)
		Partida partida= Server.instance.getPartida(player.getNick());
		if(partida == null){
			System.err.println("Error inesperado 005");
			return;
		}
		partida.putFichaOnCelda(nroCelda, ficha);
		MatchStates resultado= partida.getPartidaState();
		
		//Obtenemos el handleClient del OTRO cliente...
		HandleClient hClient= Server.instance.getHandleClient(nickDelOtroJugadorEnPartida);
		if(hClient == null){
			//Error inesperado 003.
			System.err.println("Server-side: Error inesperado 003.");
			return;
		}
		
		//Verificamos si debemos avanzar al proximo turno, o si ya ha finalizado la partida.
		if(resultado == MatchStates.PARTIDA_EN_CURSO){
			//La partida sigue en curso. Avanzamos al proximo turno.
			
			//Avisamos al otro jugador de la ficha que se ha colocado (y avanza el turno).
			hClient.sendAvisoDe_FichaColocada_yPasarDeTurno(nroCelda, ficha);
			
			//Avisamos al jugador que colocó la ficha, que tambien avance de turno.
			this.sendAvanzarTurno();
		}else{
			//La partida ha finalizado en empate, ganador O o ganador X. Sea como sea, enviamos fin de partida junto a su resultado.
			
			//A ESTE cliente (el que coloco la ultima ficha) le enviamos simplemente fin de partida.
			this.sendFinDePartida(resultado);
			//Al OTRO cliente le enviamos ficha colocada y fin de partida.
			hClient.sendAvisoDe_FichaColocada_yFinDePartida(nroCelda, ficha, resultado);
			
			//Se registra el resultado de la partida.
			Server.instance.registrarPartida(partida);
			
			//Quitamos la partida del array de partidas...
			Server.instance.partidas.remove(partida);
		}
		
		
		
	}//fin ficha colocada
	
	
	
	
	
	
	protected void logginRquest(InetMessage msg){
		
		//Obtenemos data...
		String nick= msg.strings.get(0);
		String pass= msg.strings.get(1);
		
		System.out.println("Servidor: procesando loggin request (" + nick + ")...");
		
		//Verificamos nombre...
		Player p= GameData.playerData.getOne(nick);
		if(p == null){
			//No existe un usuario con ese nombre.
			sendRespuestaLogginRequest(false, "El nombre de usuario es incorrecto.");
			return;
		}
		
		//Verificamos password...
		if(p.getPassword().compareToIgnoreCase(pass)!= 0){
			//El password es incorrecto.
			sendRespuestaLogginRequest(false, "La clave es incorrecta.");
			return;
		}
		
		//Verificamos que este usuario no tenga ya la sesion iniciada...
		Player user= Server.instance.getPlayer(nick);
		if(user == null){
			//Este usuario NO ESTA LOGGEADO. Tenemos entonces un:

			//Loggin exitoso.
			player= GameData.playerData.getOne(nick);
			Server.instance.updateTablaPlayerOnline();

			sendRespuestaLogginRequest(true, "Loggin exitoso.", player.getGanados(), player.getPerdidos(), player.getEmpatados());
			return;

		}else{
			//Ya existe un usuario loggeado con ese mismo nick.
			sendRespuestaLogginRequest(false, "Esta cuenta ya posee una sesion abierta.");
			return;
		}
		
	}//fin logginRequest
	
	
	public void sendPlayersOnline(ArrayList<Player> playersOnline){
		//Creamos y cargamos el mensaje
		InetMessage msg= new InetMessage(InetMsgType.SaC_PlayersOnline);
		for(int i= 0; i < playersOnline.size(); i++){
			msg.strings.add(i*2, new String(playersOnline.get(i).getNick()));
			msg.strings.add((i*2)+1, new String(playersOnline.get(i).getState().toString()));
		}
		//Total de registros
		msg.ints.add(0, new Integer(playersOnline.size()));
		
		//Lo enviamos...
		this.sendMessage(msg);
	}
	
	public void sendMejoresPuntajes(ArrayList<Player> top10){
		//Creamos el mensaje (insertamos HighScoreRecords en objetos basicos)...
		InetMessage msg= new InetMessage(InetMsgType.SaC_MejoresPuntajes);
		for(int i= 0; i < top10.size(); i++){
			msg.strings.add(i, new String(top10.get(i).getNick()));
			msg.ints.add((i*3), new Integer(top10.get(i).getGanados())); //won
			msg.ints.add((i*3)+1, new Integer(top10.get(i).getPerdidos())); //lose
			msg.ints.add((i*3)+2, new Integer(top10.get(i).getEmpatados())); //draw
		}
		msg.floats.add(0, new Float(top10.size()));	//total de registros, en caso de que sean menos de 10

		//Lo enviamos...
		this.sendMessage(msg);
	}
	
	public void sendAvisoDe_FichaColocada_yPasarDeTurno(int nroCelda, Ficha tipoFicha){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.SaC_Ficha_Colocada_y_Avanzar_turno);
		msg.ints.add(0, new Integer(nroCelda));
		msg.ints.add(1, new Integer(tipoFicha.ordinal()));
		
		//Lo enviamos...
		this.sendMessage(msg);
	}
	
	public void send_ErrorMessage(String error){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.SaC_ErrorMessage);
		msg.strings.add(0, new String(error));
		
		//Lo enviamos...
		this.sendMessage(msg);
	}
	
	public void sendAvanzarTurno(){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.SaC_Avanzar_de_turno);
		
		//Lo enviamos...
		this.sendMessage(msg);
	}
	
	
	public void sendAvisoDe_FichaColocada_yFinDePartida(int nroCelda, Ficha tipoFicha, MatchStates resultado){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.SaC_FichaColocada_y_FinDePartida);
		msg.ints.add(0, new Integer(nroCelda));
		msg.ints.add(1, new Integer(tipoFicha.ordinal()));
		msg.ints.add(2, new Integer(resultado.ordinal()));
		
		
		//Lo enviamos...
		this.sendMessage(msg);	
	}
	
	
	
	public void sendFinDePartida(MatchStates resultado){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.SaC_Fin_de_Partida);
		msg.ints.add(0, new Integer(resultado.ordinal()));
		
		//Lo enviamos...
		this.sendMessage(msg);	
	}
	
	public void sendPlayerRechazoOfertaDeJugar(String nick_del_Rechazador, boolean isForTimeOut){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.SaC_Player_ha_rechazado_oferta);
		msg.strings.add(0, new String(nick_del_Rechazador));
		msg.booleans.add(0, new Boolean(isForTimeOut));
		
		//Lo enviamos...
		this.sendMessage(msg);
		
		//Colocamos a ambos players como IDLE
		player.setState(Player.States.IDLE);
		Server.instance.getPlayer(nick_del_Rechazador).setState(Player.States.IDLE);
		Server.instance.updateTablaPlayerOnline();
	}
	
	
	
	public void sendInicioPartida(Ficha tuFichaAsignada, String nickOponente, boolean playerXStarts){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.SaC_Start_Match);
		msg.ints.add(0, new Integer(tuFichaAsignada.ordinal()));
		msg.strings.add(0, new String(nickOponente));
		msg.booleans.add(0, new Boolean(playerXStarts));

		//Lo enviamos...
		this.sendMessage(msg);
		
		//Colocamos al player como "jugando"
		player.setState(Player.States.PLAYING);
		Server.instance.updateTablaPlayerOnline();
	}//fin sendInicioPartida
	
	
	
	
	public void sendPlayerSolicitaJugarConVos(String nickSolicitante){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.SaC_Player_Solicita_Jugar_con_vos);
		msg.strings.add(0, new String(nickSolicitante));

		//Colocamos al player como "respondiendo a solicitud"...
		player.setState(Player.States.RESPONDIENDO_SOLICITUD);
		Server.instance.updateTablaPlayerOnline();
		
		//Lo enviamos...
		this.sendMessage(msg);
	}
	
	
	public void sendSeHaCanceladoLaSolicitud(String nickQueCancela){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.SaC_SeHaCanceladoSolicitudParaJugar);
		msg.strings.add(0, new String(nickQueCancela));

		//Colocamos a ESTE player como "IDLE"...
		player.setState(Player.States.IDLE);
		Server.instance.updateTablaPlayerOnline();

		//Lo enviamos...
		this.sendMessage(msg);
	}
	
	
	public void sendRespuestaSolicitudParaJugar(boolean success, String mensajeInformativo){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.SaC_Respuesta_Solicitud_para_jugar);
		msg.booleans.add(0, new Boolean(success));
		msg.strings.add(0, new String(mensajeInformativo));

		//Lo enviamos...
		this.sendMessage(msg);
		
		//Colocamos al jugador como "esperando por oponente" si es que la solicitud para jugar que realizó es valida.
		if(success) player.setState(Player.States.WAITING_FOR_OPPONENT);
		else player.setState(Player.States.IDLE);
		
		Server.instance.updateTablaPlayerOnline();
	}//fin sendRespuestaSolicitudParaJugar
	
	
	
	
	public void sendRespuestaLogginRequest(boolean success, String mensajeInformativo){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.SaC_Respuesta_Loggin_request);
		msg.booleans.add(0, new Boolean(success));
		msg.strings.add(0, new String(mensajeInformativo));
		
		//Lo enviamos...
		this.sendMessage(msg);
	}//fin sendRespuestaLogginRequest
	
	public void sendRespuestaCreateAccount(boolean success, String mensajeInformativo){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.SaC_Respuesta_Create_Account);
		msg.booleans.add(0, new Boolean(success));
		msg.strings.add(0, new String(mensajeInformativo));
		
		//Lo enviamos...
		this.sendMessage(msg);
	}
	
	public void sendRespuestaLogginRequest(boolean success, String mensajeInformativo, int won, int lose, int draw){
		//Creamos el mensaje...
		InetMessage msg= new InetMessage(InetMsgType.SaC_Respuesta_Loggin_request);
		msg.booleans.add(0, new Boolean(success));
		msg.strings.add(0, new String(mensajeInformativo));
		msg.ints.add(0, new Integer(won));
		msg.ints.add(1, new Integer(lose));
		msg.ints.add(2, new Integer(draw));
		
		//Lo enviamos...
		this.sendMessage(msg);
	}
	
	public void playerTurnsIDLE(InetMessage msg){
		if(player != null){
			player.setState(Player.States.IDLE);
			Server.instance.updateTablaPlayerOnline();
		}
	}
	
	/**
	 * Envia el mensaje al cliente.
	 * @param msg
	 */
	void sendMessage(InetMessage msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	/**
	 * Devuelve el player que el handle client maneja. Este metodo puede devolver null si el handleclient esta corriendo pero aun el usuario
	 * no se ha loggeado.
	 * @return
	 */
	public Player getPlayer(){
		return player;
	}
	
	public long getLastHeartBeatTime(){
		return lastHeartBeatTime;
	}
	
	public void closeThread(){
		closeThread= true;
	}
}//fin clase
