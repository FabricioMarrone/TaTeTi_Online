package shuken.TaTeTi.Network;


import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;

import shuken.TaTeTi.Entities.HighScores;
import shuken.TaTeTi.Entities.Partida;
import shuken.TaTeTi.Entities.Partida.MatchStates;
import shuken.TaTeTi.Entities.Player;
import shuken.TaTeTi.Network.Data.GameData;
import shuken.TaTeTi.Network.Data.PlayerData_BD;
import shuken.WhatsMyIP.Get_IP;

import java.awt.Font;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTable;

public class Server extends JFrame {

	/** Instancia singleton del servidor para que los diferentes HandleClient puedan acceder al server. */
	public static Server instance= null;
	public static Random random= new Random();
	
	private static final String version= "v0.6";
	
	/** Socket del servidor. */
	private ServerSocket serverSocket;
	public static int PORT= 8081;	//default
	public boolean online= false;
	
	/** Total de conexiones establecidas. */
	public ArrayList<HandleClient> connections;
	/** Partidas en ejecucion. */
	public ArrayList<Partida> partidas;
	
	
	private JPanel contentPane;
	protected JLabel lblInicializando;
	protected JLabel lblConfigLoad;
	protected JLabel lblPort;
	protected JLabel lblCantidadUsuariosConectados;
	protected int socketsConnected= 0;
	private JTable tablePlayersOnline;
	private DefaultTableModel tablePlayersOnlineModel;
	private JLabel lblUsingDatabase;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		//Cargamos archivo de configuracion
		ServerConfig.loadConfig();
		if(ServerConfig.loadOK()){
			PORT= ServerConfig.PORT;
		}else{
			JOptionPane.showMessageDialog(null, "Error al cargar ServerConfig file.");
			System.exit(0);
		}
		
		//Inicializamos un thread para el server
		new Thread()
		{
		    public void run() {
		    	Server serverTaTeTi = new Server();
				serverTaTeTi.setVisible(true);
				
				instance= serverTaTeTi;
				serverTaTeTi.init();
		    }
		}.start();
		
		//Le damos tiempo a que se inicialice...
		while(instance == null){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//Inicializamos un sub-thread para el server loop
		new Thread()
		{
		    public void run() {
		    	instance.controlLoop();
		    }
		}.start();
	}//end main

	/**
	 * Create the frame.
	 */
	public Server() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 397);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblTatetiOnlineServer = new JLabel("TaTeTi Online Server" + "(" + version + ")");
		lblTatetiOnlineServer.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		lblInicializando = new JLabel("Inicializando...");
		
		JLabel lblNewLabel = new JLabel("Local IP address: " + Get_IP.getLocalIpAddress());
		
		JLabel lblGlobalIpAddress = new JLabel("Global IP address: " + Get_IP.getPublicIpAddress());
		
		lblCantidadUsuariosConectados = new JLabel("Cantidad sockets conectados: " + socketsConnected);
		
		tablePlayersOnlineModel= new DefaultTableModel(null, new Object[]{"Nick", "State"});
		tablePlayersOnline = new JTable(tablePlayersOnlineModel);
		tablePlayersOnline.setShowGrid(true);
		
		lblPort = new JLabel("Port: " + PORT);
		
		lblConfigLoad = new JLabel("Config load: ");
		
		lblUsingDatabase = new JLabel("Using Database: ");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(29)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblPort)
						.addComponent(lblGlobalIpAddress)
						.addComponent(lblCantidadUsuariosConectados)
						.addComponent(tablePlayersOnline, GroupLayout.PREFERRED_SIZE, 380, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblNewLabel)
								.addComponent(lblTatetiOnlineServer))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblInicializando))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblConfigLoad)
							.addGap(113)
							.addComponent(lblUsingDatabase)))
					.addContainerGap(25, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTatetiOnlineServer)
						.addComponent(lblInicializando))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblConfigLoad)
						.addComponent(lblUsingDatabase))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblPort)
					.addGap(3)
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblGlobalIpAddress)
					.addGap(18)
					.addComponent(lblCantidadUsuariosConectados)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tablePlayersOnline, GroupLayout.PREFERRED_SIZE, 181, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);
		
		
		//*********** Codigo by me ****************//
		connections= new ArrayList<HandleClient>();
		partidas= new ArrayList<Partida>();
		
		this.setTitle("Server " + version);
		//*****************************************//
	}//fin constructor
	
	
	
	
	
	private void init(){
		
		try {
			//Creamos el socket del server...
			serverSocket = new ServerSocket(PORT, 10);
			
			//Cargamos la data...
			GameData.loadAll();
			
			String str= "Error";
			String connected= "";
			if(ServerConfig.loadOK()){
				str= "OK";
				if(ServerConfig.PERSISTANCE_ON_DATABASE){
					if(((PlayerData_BD)(GameData.playerData)).isConnectionAlive()){
						connected= "CONNECTED";
					}else{
						//TODO mejorar mensaje
						JOptionPane.showMessageDialog(this, "Error al conectar a la Base de Datos.\n" +
								"Si desea utilizar el servidor fuera de localhost,\n " +
								"coloque \"useDB= false\" en el archivo de configuracion del servidor.");
						System.exit(0);
					}
				}
			}
			
			online= true;
			lblInicializando.setText("ONLINE");
			lblConfigLoad.setText("Config load: " + str);
			lblUsingDatabase.setText("Using Database: " + ServerConfig.PERSISTANCE_ON_DATABASE + " [" + connected + "]");
			
			//Iniciamos el loop...
			while(true){
				this.run();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}//fin init
	
	
	
	private void run(){
		try {
			//Esperamos a que un cliente se conecte...
			Socket socketClientConnected = serverSocket.accept();
			socketsConnected++;
			lblCantidadUsuariosConectados.setText("Cantidad de sockets conectados: " + socketsConnected);
			
			//Creamos y guardamos un handleClient que se encargara del cliente...
			HandleClient hClient= new HandleClient(socketClientConnected);
			connections.add(hClient);
			
			//Inicializamos un nuevo thread que se hara cargo del cliente conectado...
			new Thread(hClient).start();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}//fin run (recordar que esta dentro de un while)
	
	
	/**
	 * Loop de control de usuarios conectados.
	 */
	private void controlLoop(){
		//Loop infinito
		while(true){
			try {
				if(online) lblInicializando.setText("ONLINE & ControlLoop ON");
				else lblInicializando.setText("OFFLINE");
				
				//Cada 5 segundos...
				Thread.sleep(2000);
				
				//Verificamos heartbeats de todos los players supuestamente conectados
				if(connections != null){
					ArrayList<Integer> blackList= new ArrayList<Integer>(); 
					for(int i= 0; i < connections.size(); i++){
						if(connections.get(i).getPlayer() != null){
							//Comparamos su ultimo heartbeat con el tiempo actual. Si transcurrieron más de 2.5 segundos, se lo quita del server.
							long elapsedTime= System.currentTimeMillis() - connections.get(i).getLastHeartBeatTime();
							if(elapsedTime > 2500) {
								blackList.add(i);
							}
						}
					}
					//Quitamos los que haya que quitar
					for(int i= 0; i < blackList.size(); i++){
						connections.get(blackList.get(i)).cerrarSesion();
						connections.get(blackList.get(i)).closeThread();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
	
	public void updateTablaPlayerOnline(){
		//Limpiamos tabla
		int rows= tablePlayersOnlineModel.getRowCount();
		for(int i= rows - 1; i >= 0; i--) tablePlayersOnlineModel.removeRow(i);
		
		
		//La volvemos a cargar completa
		for(int i= 0; i < connections.size(); i++){
			Player p= connections.get(i).getPlayer();
			if(p != null) tablePlayersOnlineModel.addRow(new Object[]{p.getNick(), p.getState()});
				
		}
		
		//Actualizamos label
		socketsConnected= connections.size();
		lblCantidadUsuariosConectados.setText("Cantidad de sockets conectados: " + socketsConnected);
	}
	
	
	/**
	 * Busca en el listado de conexiones y devuelve el player si es que algun HandleClient esta trabajando con ese player. Este metodo 
	 * devuelve null si el player no se encuentra en ninguna conexion.
	 * @param nick
	 * @return
	 */
	public Player getPlayer(String nick){
		/*
		for(int i= 0; i < playersOnline.size(); i++){
			if(playersOnline.get(i).getNick().compareToIgnoreCase(nick)== 0) return playersOnline.get(i);
		}
		*/
		for(int i= 0; i < connections.size(); i++){
			Player p= connections.get(i).getPlayer();
			if(p == null) continue;
			
			if(p.getNick().compareToIgnoreCase(nick)== 0) return p;
		}
		return null;
	}
	
	/**
	 * Devuelve el HandleClient que se encarga de gestionar al player especificado. Este metodo devuelve null si ningun HandleClient gestiona
	 * al player ese.
	 * @param nick
	 * @return
	 */
	public HandleClient getHandleClient(String nick){
		for(int i= 0; i < connections.size(); i++){
			Player p= connections.get(i).getPlayer();
			if(p == null) continue;
			
			if(p.getNick().compareToIgnoreCase(nick)== 0) return connections.get(i);
		}
		return null;
	}
	
	
	public void nuevaPartida(Player pX, Player pO, boolean pXStarts){
		partidas.add(new Partida(pX, pO, pXStarts, true));
	}
	
	/**
	 * Devuelve la partida en la que se encuentra el player cuyo nick se especifica. Esta claro que un jugador unicamente puede estar en una
	 * partida a la vez.
	 * Este metodo devuelve null si no hay ninguna partida para ese nick.
	 * @param nick
	 * @return
	 */
	public Partida getPartida(String nick){
		for(int i= 0; i < partidas.size(); i++){
			Player p1= partidas.get(i).getPlayerX();
			Player p2= partidas.get(i).getPlayerO();
			if(p1.getNick().compareToIgnoreCase(nick)== 0 || p2.getNick().compareToIgnoreCase(nick)== 0) return partidas.get(i);
		}
		
		return null;
	}
	
	
	/**
	 * Registra la partida en la capa de datos.
	 * @param p
	 */
	public void registrarPartida(Partida p){
		//Obtenemos resultado...
		MatchStates resultado= p.getPartidaState();
		
		//Registramos
		switch(resultado){
		case EMPATE:
			GameData.playerData.incrementDraw(p.getPlayerX());
			GameData.playerData.incrementDraw(p.getPlayerO());
			break;
		case GANADOR_O:
			GameData.playerData.incrementWon(p.getPlayerO());
			GameData.playerData.incrementLose(p.getPlayerX());
			break;
		case GANADOR_X:
			GameData.playerData.incrementWon(p.getPlayerX());
			GameData.playerData.incrementLose(p.getPlayerO());
			break;
		case RENDICION_X:
			GameData.playerData.incrementLose(p.getPlayerX());
			break;
		case RENDICION_O:
			GameData.playerData.incrementLose(p.getPlayerO());
			break;
		case PARTIDA_EN_CURSO:
			//Salimos del registro porque evidentemente no hay que registrar aun. En teoria, nunca se va a llegar a este punto.
			return;
		}//fin switch
	}
	
	
	
	public void removeConnection(HandleClient conn){
		connections.remove(conn);
		
		updateTablaPlayerOnline();
	}
	
	/**
	 * Devuelve los 10 mejores players
	 * @return
	 */
	public ArrayList<Player> getTop10(){
		
		//Primero, obtenemos todos los datos
		ArrayList<Player> playersInDB= GameData.playerData.getAllPlayers();
		
		//Paralelamente guardamos un array que nos permite calcular un indice para obtener los 10 mejores puntajes
		ArrayList<Integer> scores= new ArrayList<Integer>();
		for(int i= 0; i < playersInDB.size(); i++){
			Player p= playersInDB.get(i);
			scores.add(i, p.getTotalScore());
		}
		
		//Buscamos los 10 mejores
		ArrayList<Player> top10= new ArrayList<Player>();
		ArrayList<Integer> blackIndexs= new ArrayList<Integer>();
		
		int tot= HighScores.CANT_OF_ROWS;
		if(playersInDB.size() < HighScores.CANT_OF_ROWS) tot= playersInDB.size();
		
		for(int i= 0; i < tot; i++){
			
			//Buscamos el mejor de los que quedan
			int max= -10000000;
			int maxIndex= 0;
			for(int ii = 0; ii < scores.size(); ii++){
				if(blackIndexs.contains(ii)) continue;
				
				if(scores.get(ii) >= max){
					max= scores.get(ii); 
					maxIndex= ii;
				}
			}
			
			//Guardamos el player
			top10.add(playersInDB.get(maxIndex));
			
			//Agregamos el indice a la lista negra para que no vuelva a ser tenido en consideracion
			blackIndexs.add(maxIndex);
			
		}//fin for 10
		
		return top10;
	}
	
	protected ArrayList<Player> getPlayersOnline(){
		 ArrayList<Player> playersOnline= new  ArrayList<Player>();
		 
		 for(int i= 0; i < connections.size(); i++){
			 Player p= connections.get(i).getPlayer();
			 if(p != null){
				 playersOnline.add(p);
			 }
		 }
		 
		 return playersOnline;
	}//end get players online
}//end class
