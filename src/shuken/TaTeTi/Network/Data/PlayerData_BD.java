package shuken.TaTeTi.Network.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import shuken.TaTeTi.Entities.Player;

public class PlayerData_BD implements IPlayerData {

	private static final String nick_column= "nick";
	private static final String pass_column= "password";
	private static final String won_column= "won";
	private static final String draw_column= "draw";
	private static final String lose_column= "lose";
	
	public PlayerData_BD(){
		//Iniciamos conexion
		Connector.openConnection();
	}
	
	@Override
	public void loadAllData() {
		//Nothing. This was for memory manage of data. Only testing or PERSISTANCE MODE = memory.
	}

	@Override
	public Player getOne(String userNick) {
		ResultSet result= Connector.executeSelect("select * from Players where " + nick_column + "= '" + userNick + "'");

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
		ResultSet result= Connector.executeSelect("select * from Players");
		
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
		
		Connector.execute("insert into Players ("+columns+") values ("+values+")");
	}

	@Override
	public void incrementWon(Player p) {
		Connector.execute("update Players set " + won_column + "= " + won_column + "+1 where " + nick_column + "= '" + p.getNick() + "'");
	}

	@Override
	public void incrementLose(Player p) {
		Connector.execute("update Players set " + lose_column + "= " + lose_column + "+1 where " + nick_column + "= '" + p.getNick() + "'");
	}

	@Override
	public void incrementDraw(Player p) {
		Connector.execute("update Players set " + draw_column + "= " + draw_column + "+1 where " + nick_column + "= '" + p.getNick() + "'");
	}
	
}//end class
