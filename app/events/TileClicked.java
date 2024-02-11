package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.Game;
import structures.GameState;
import utils.UnitSummonTest;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 1.
 * 
 * { 
 *   messageType = “tileClicked”
 *   tilex = <x index of the tile>
 *   tiley = <y index of the tile>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		
		if (gameState.something == true) {
			// do some logic
		}
		
		//bens testing ground
		//UnitSummonTest.summonUnit(out, gameState, tilex, tiley);
		
		//the new method for summoning a unit - does not consider potential move or anything like that yet - be prepared to adjust to funnel to correct method
		Game.summonUnit(out, gameState, tilex, tiley);
		
		
	}

}
