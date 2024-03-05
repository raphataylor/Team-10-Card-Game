package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Game;
import structures.GameState;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * somewhere that is not on a card tile or the end-turn button.
 * 
 * { 
 *   messageType = “otherClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class OtherClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		//prevents any potential issues by resetting tile selection state
		
		
		Game.resetGameState(out, gameState);
		//moved to a resetBoardState but requires testing before we can remove this
//		gameState.previousSelectedTile = null;
//		gameState.isTileSelected = false;
//		gameState.cardSelected = false;
//		Game.getBoard().resetAllTiles(out);
//		Game.resetBoardState(out);
		//look at how to fix this
		//BasicCommands.drawCard(out, null, 2, 0);
		
	}

}


