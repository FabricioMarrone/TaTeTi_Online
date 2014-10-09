package shuken.TaTeTi.Network.Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Connector {

	/** Connection Data */
	private static Connection connection = null;
	private static String url = "jdbc:mysql://localhost:3306/";
    private static String dbName = "TaTeTi_Online";
    private static String driver = "com.mysql.jdbc.Driver";
    private static String userName = "root"; 
    private static String password = "shuriken";
    
    /**
	 * Establish connection with DB.
	 */
	public static void openConnection(){
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
	 * If the connection its close or null, this returns false. True otherwise.
	 * @return
	 */
	public static boolean isConnectionAlive(){
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
	public static ResultSet executeSelect(String string){
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
	public static void execute(String string){
		Statement stmt;
		try{
			stmt = connection.createStatement();
			stmt.execute(string);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}//end class
