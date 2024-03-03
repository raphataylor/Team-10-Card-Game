package events;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.Game;
import structures.GameState;
import structures.basic.Unit;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case
 * the end-turn button.
 * 
 * {
 * messageType = “endTurnClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		if(gameState.currentPlayer == gameState.player1) {
			
			Game.resetMana(out, gameState);
			gameState.currentPlayer = gameState.player2;
			
			Game.setManaOnStartTurn(out, gameState);
			
			Game.selectAICardToPlay(out, gameState);
			
			Game.selectAIUnitToAttack(out, gameState);
			
		}else {
			//for testing AI player
			
			//code logic for AI player end turn will be placed her 
			//Game.resetMana(out, gameState);
			//gameState.currentPlayer = gameState.player1;
			//Game.setManaOnStartTurn(out, gameState);
			
		}

		gameState.player1.drawCardAtTurnEnd(out);
		
		List<Unit> player1Units = Game.getBoard().getPlayer1Units();
		
		for (Unit unit : player1Units) {
		    unit.setHasMoved(false);
		    unit.setHasAttacked(false);
		}
		
		gameState.previousSelectedTile = null;
		gameState.isTileSelected = false;

		
	}
	
	

}
