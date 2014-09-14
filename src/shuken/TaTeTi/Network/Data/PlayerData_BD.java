package shuken.TaTeTi.Network.Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import shuken.TaTeTi.Entities.Player;

public class PlayerData_BD implements PlayerData {

	private static final String nick_column= "nick";
	private static final String pass_column= "password";
	private static final String won_column= "won";
	private static final String draw_column= "draw";
	private static final String lose_column= "lose";
	
	/** Connection Data */
	private Connection connection = null;
	private String url = "jdbc:mysql://localhost:3306/";
    private String dbName = "TaTeTi_Online";
    private String driver = "com.mysql.jdbc.Driver";
    private String userName = "root"; 
    private String password = "shuriken";
    
	public PlayerData_BD(){
		//Iniciamos conexion
		openConnection();
	}
	
	@Override
	public void loadAllData() {
		//Nothing. This was for memory manage of data. Only testing or PERSISTANCE MODE = memory.
	}

	@Override
	public Player getOne(String userNick) {
		ResultSet result= executeSelect("select * from Players where " + nick_column + "= '" + userNick + "'");

		try{
			while(result.next()){
				String nickName= result.getString(nick_column);
				String pass= result.getString(pass_column);
				int won= result.getInt(won_column);
				int draw= result.getInt(draw_column);
				int lose= result.getInt(lose_column);
				return new Player(nickName, pass, won, lose, draw);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public ArrayList<Player> getAllPlayers() {
		ResultSet result= executeSelect("select * from Players");
		
		ArrayList<Player> players= new ArrayList<Player>();
		try{
			while(result.next()){
				String nick= result.getString(nick_column);
				String pass= result.getString(pass_column);
				int won= result.getInt(won_column);
				int draw= result.getInt(draw_column);
				int lose= result.getInt(lose_column);
				players.add(new Player(nick, pass, won, lose, draw));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		return players;
	}

	@Override
	public void addPlayer(Player p) {
		String columns= nick_column + ", " + pass_column + ", " + won_column + ", " + draw_column + "," + lose_column;
		String values= "'"+ p.getNick() +"', '"+ p.getPassword() + "', 0, 0, 0";
		
		execute("insert into Players ("+columns+") values ("+values+")");
	}

	@Override
	public void incrementWon(Player p) {
		execute("update Players set " + won_column + "= " + won_column + "+1 where " + nick_column + "= '" + p.getNick() + "'");
	}

	@Override
	public void incrementLose(Player p) {
		execute("update Players set " + lose_column + "= " + lose_column + "+1 where " + nick_column + "= '" + p.getNick() + "'");
	}

	@Override
	public void incrementDraw(Player p) {
		execute("update Players set " + draw_column + "= " + draw_column + "+1 where " + nick_column + "= '" + p.getNick() + "'");
	}

	/**
	 * Establish connection with DB.
	 */
	private void openConnection(){
		try{
			//Inicializa la coneccion...
			Class.forName(driver).newInstance();
			connection = DriverManager.getConnection(url+dbName,userName,password);
		}catch (Exception e) {
			e.printStackTrace();
			connection= null;
		}	
	}
	
	/**
	 * Close the connection with the DB.
	 */
	private void closeConnection(){
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * If the connection its close or null, this returns false. True otherwise.
	 * @return
	 */
	public boolean isConnectionAlive(){
		if(connection == null) return false;
		
		try {
			if(connection.isClosed()) return false;
			else return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	/**
	 * Ejecuta un SELECT a la BD. Este metodo verifica que exista una conexion establecida, caso contrario arroja error.
	 * Este metodo devuelve el resultado de la consulta.
	 * @param string
	 * @return
	 */
	private ResultSet executeSelect(String string){
		if(!isConnectionAlive()) openConnection();
		
		Statement stmt;
		try {
			//Creamos un statement (una sentencia) que nos permite realizar la consulta...
			stmt = connection.createStatement();
			
			//Realizamos la consulta y guardamos el resultado en el ResultSet...
			ResultSet rs= stmt.executeQuery(string);
			
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Ejecuta ya sea un INSERT o un UPDATE, que en ambos casos no se recibe una respuesta.
	 * @param string
	 * @return
	 */
	private void execute(String string){
		Statement stmt;
		try{
			stmt = connection.createStatement();
			stmt.execute(string);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}//end class
