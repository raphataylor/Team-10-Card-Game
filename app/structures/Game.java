package structures;

import structures.basic.Player;

//game logic will be stored here - contemplating just using GameState and making this whole concept redundant 
public class Game {
	
	
	private Player player1;
	private Player player2;
	private Board gameBoard;
	
	public Game() {
		//this is mandatory to start game - whoever is in charge of expanding on this please replace this code
		this.player1 = new Player();
		this.player2 = new Player();
		this.gameBoard = new Board();
	}

}
