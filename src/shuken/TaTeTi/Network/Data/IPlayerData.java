package shuken.TaTeTi.Network.Data;

import java.util.ArrayList;

import shuken.TaTeTi.Entities.Player;

public interface IPlayerData {

	public void loadAllData();
	
	public Player getOne(String nick);
	
	public ArrayList<Player> getAllPlayers();
	
	public void addPlayer(Player p);
	
	public void incrementWon(Player p);
	public void incrementLose(Player p);
	public void incrementDraw(Player p);
}
