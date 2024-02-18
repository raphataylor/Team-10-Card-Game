package structures;

import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

	
	public static boolean gameInitalised = false;
	
	public boolean something = false;
	
	//tracks if a player has a card current selected 
	public boolean cardSelected = false;
	
	//tracks the unit on tile
	public Tile unitCurrentTile = null;
	
	//tracks the turn of the game
	public static int turn = 0;

	//tracks the current tile the unit was on, before the movement
    public Unit currentSelectedUnit = null;

	//tracks which card is being highlighted - if -1 then no card is currently highlighted 
	public int currentCardSelected = -1;
	
	//keeping track of the state of the game statically here as required - maybe move over to game class however this really can be static as its the only instance of its kind
	public static Player player1 = new Player();
	public static Player player2 = new Player();
	
	public static Player currentPlayer = player1;

	
	
}
