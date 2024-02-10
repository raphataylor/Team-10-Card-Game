package structures;

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
	
	
	
}
