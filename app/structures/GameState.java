package structures;

import structures.basic.Player;
import structures.Board;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

	
	public boolean gameInitalised = false;
	
	public boolean something = false;
	
	//tracks if a player has a card current selected 
	public boolean cardSelected = false;
	
	//tracks which card is being highlighted - if -1 then no card is currently highlighted 
	public int currentCardSelected = -1;
	
	//keeping track of the state of the game statically here as required - maybe move over to game class however this really can be static as its the only instance of its kind
	public static Player player1 = new Player();
	public static Player player2 = new Player();
	
	
}
