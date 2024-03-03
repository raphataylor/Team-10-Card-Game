package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.Game;
import structures.GameState;
import structures.units.Avatar;
import utils.UnitSummonTest;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a card.
 * The event returns the position in the player's hand the card resides within.
 * 
 * { 
 *   messageType = “cardClicked”
 *   position = <hand index position [1-6]>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class CardClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		int handPosition = message.get("position").asInt();
		
		//bens testing ground
		//UnitSummonTest.cardClick(out, gameState, handPosition);
		//the new method for dealing with card clicked
		Game.selectCard(out, gameState, handPosition);
		

		if (!GameState.gameOver) {
			// Highlight potential tiles
			Avatar player1Avatar = (Avatar) gameState.player1.getAvatar();
			player1Avatar.highlightAdjacentTiles(out, Game.getBoard().getTiles());
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Game.getBoard().drawBoard(out, Game.getBoard().getTiles());
		}
	}

}
