package shuken.TaTeTi.Entities;

import shuken.TaTeTi.Entities.Partida.MatchStates;
import shuken.TaTeTi.Network.Data.GameData;

/**
 * Entidad que representa una partida almacenada en la base de datos.
 * @author F. Marrone
 *
 */
public class PartidaRecord {

	private int idPartida;
	private String fecha;
	private String hora;
	private Player playerX, playerO;
	private Partida.MatchStates resultado;
	

	
	
	
	/**
	 * Crea una partida a partir de los datos de la base de datos. En principio, unicamente el servidor deber� crear este tipo de objetos.
	 */
	public PartidaRecord(int id, String fecha, String hora, String fk_nickX, String fk_nickO, MatchStates resultado){
		this.idPartida= id;
		this.fecha= fecha;
		this.hora= hora;
		this.playerX= GameData.playerData.getOne(fk_nickX);
		this.playerO= GameData.playerData.getOne(fk_nickO);
		this.resultado= resultado;
	}
	
	
	public int getIdPartida(){
		return idPartida;
	}


	public String getFecha() {
		return fecha;
	}


	public void setFecha(String fecha) {
		this.fecha = fecha;
	}


	public String getHora() {
		return hora;
	}


	public void setHora(String hora) {
		this.hora = hora;
	}


	public Partida.MatchStates getResultado() {
		return resultado;
	}


	public void setResultado(Partida.MatchStates resultado) {
		this.resultado = resultado;
	}


	public Player getPlayerX() {
		return playerX;
	}


	public Player getPlayerO() {
		return playerO;
	}
	
	
}//fin clase
