package shuken.TaTeTi.Network.Data;

import java.util.ArrayList;

import shuken.TaTeTi.Entities.Player;

public class PlayerData_Memoria implements IPlayerData{

	private ArrayList<Player> players= null;
	
	
	public PlayerData_Memoria(){
		players= new ArrayList<Player>();
		
		loadAllData();
	}
	
	@Override
	public void loadAllData() {
		players.clear();
		
		players.add(new Player("Player1", "123", 2, 3, 1));
		players.add(new Player("Player2", "123", 0, 2, 3));
		players.add(new Player("Player3", "123", 3, 0, 0));
		players.add(new Player("Player4", "123", 1, 1, 1));
		players.add(new Player("Player5", "123", 0, 0, 0));
		players.add(new Player("Player6", "123", 1, 0, 0));
		players.add(new Player("Player7", "123", 1, 0, 0));
		players.add(new Player("Player8", "123", 1, 0, 0));
		players.add(new Player("Player9", "123", 1, 0, 0));
		players.add(new Player("Player10", "123", 1, 0, 0));
		players.add(new Player("Player11", "123", 1, 0, 0));
		players.add(new Player("Player12", "123", 1, 0, 0));
		players.add(new Player("Player13", "123", 1, 0, 0));
		
	}
	
	@Override
	public Player getOne(String nick) {
		for(int i= 0; i < players.size(); i++){
			if(players.get(i).getNick().compareToIgnoreCase(nick)== 0) return players.get(i);
		}
		
		return null;
	}

	@Override
	public ArrayList<Player> getAllPlayers() {
		//loadAllData();
		return players;
	}

	@Override
	public void addPlayer(Player p) {
		players.add(p);
	}

	@Override
	public void incrementWon(Player p) {
		getOne(p.getNick()).incrementWon();
	}

	@Override
	public void incrementLose(Player p) {
		getOne(p.getNick()).incrementLose();
	}

	@Override
	public void incrementDraw(Player p) {
		getOne(p.getNick()).incrementDraw();
	}
}//end class
