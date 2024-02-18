package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.Game;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BattleHandler;
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
		Tile clickedTile = Game.getBoard().getTile(tilex, tiley);
		Tile[][] tiles = Game.getBoard().getTiles();
		Unit selectedUnit = clickedTile.getUnit();
		
		System.out.println(gameState.previousSelectedTile);
		System.out.println(clickedTile);
		
//		if (gameState.something == true) {
//			// do some logic
//		}
		
		//bens testing ground
		//UnitSummonTest.summonUnit(out, gameState, tilex, tiley);
		
		//the new method for summoning a unit - does not consider potential move or anything like that yet - be prepared to adjust to funnel to correct method
		if(gameState.cardSelected) {
			Game.summonUnit(out, gameState, tilex, tiley);	
			gameState.previousSelectedTile = null;
			gameState.isTileSelected = false;
		}
		
	
		//a large if statement checking if a previous tile has been selected and if the clicked tile contains an enemy unit
		//will also check if the attacking unit has attacked yet
		//need to find a way to check if unit occupies square (.getunit() gives error)
		//Game.getBoard().getPlayer2Units().contains(selectedUnit) does not work - why? 
		if (selectedUnit != null && gameState.previousSelectedTile != null && gameState.isTileSelected && gameState.previousSelectedTile != clickedTile) {
			BattleHandler.attackUnit(out, gameState.previousSelectedTile.getUnit(), selectedUnit);
			gameState.previousSelectedTile = null;
			gameState.isTileSelected = false;
		}
		
	
		//If a human unit is clicked, display potential movement tiles
		//need to add filter to exclude AI units
		else if(selectedUnit != null && selectedUnit.getHasMoved() == false) {
			Game.showValidMovement(out, tiles, clickedTile, 2);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();} 
			Game.getBoard().drawBoard(out, tiles);
			gameState.previousSelectedTile = clickedTile;
			gameState.isTileSelected = true;
		}
		
		else {
			gameState.previousSelectedTile = clickedTile;
			gameState.isTileSelected = true;
		}

		
		
		
	}

}
