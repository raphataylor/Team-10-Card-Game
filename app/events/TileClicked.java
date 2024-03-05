package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Board;
import structures.Game;
import structures.GameState;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BattleHandler;
import utils.UnitSummonTest;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile
 * that was
 * clicked. Tile indices start at 1.
 *
 * {
 * messageType = “tileClicked”
 * tilex = <x index of the tile>
 * tiley = <y index of the tile>
 * }
 *
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		Tile clickedTile = Game.getBoard().getTile(tilex, tiley);
		Tile[][] tiles = Game.getBoard().getTiles();
		Unit selectedUnit = clickedTile.getUnit();

		if (gameState.something == true) {
			// do some logic
		}

		// bens testing ground
		// UnitSummonTest.summonUnit(out, gameState, tilex, tiley);

		if (!gameState.gameOver) {
			// the new method for summoning a unit - does not consider potential move or
			// anything like that yet - be prepared to adjust to funnel to correct method
			Board board = Game.getBoard();
			Tile tileSelected = board.getTile(tilex, tiley);
			if (gameState.cardSelected) { // tile clicked after card is selected, summon that card unit on the tile
				BasicCommands.addPlayer1Notification(out, "card selected", 3);
				gameState.currentSelectedUnit = null;
				gameState.unitCurrentTile = null;
				// new changes to account for tracking whether a tile is clicked or not
				// previouslySelectedTile is used for combat under the assumption it is
				// referring to a unit thats
				// going to declare an attack
				gameState.isTileSelected = false;
				Game.summonUnit(out, gameState, tilex, tiley);
			} else if (tileSelected.hasUnit() && !gameState.isTileSelected) { // if the tile clicked has unit on it show
																				// valid moves
				BasicCommands.addPlayer1Notification(out, "move by clicking on tiles", 3);
				gameState.currentSelectedUnit = tileSelected.getUnit();
				gameState.unitCurrentTile = tileSelected;
				// the selected unit is now in a position to either move or perform combat
				gameState.isTileSelected = true;
				Game.showValidMovement(out, tiles, clickedTile, 2);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//Game.getBoard().drawBoard(out, tiles);
				board.resetAllTiles(out);
				return;
			} else if (gameState.currentSelectedUnit != null && selectedUnit == null) {// if a tile is clicked after a unit
																						// is clicked, move the
																						// unit to that tile (valid move not implemented, for now it
																						// just moves)
				if (tileSelected.getIsActionableTile()) {
					System.out.println("able to move");
					BasicCommands.addPlayer1Notification(out, "moved", 3);
					gameState.unitCurrentTile.setUnit(null); // remove unit reference from previous tile before moving to new
					// tile
					gameState.currentSelectedUnit.setPositionByTile(tileSelected);
					tileSelected.setUnit(gameState.currentSelectedUnit);
					BasicCommands.addPlayer1Notification(out, "moveUnitToTile", 3);
					BasicCommands.moveUnitToTile(out, gameState.currentSelectedUnit, tileSelected);
					try {
						Thread.sleep(4000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				// Reset conditions after performing a move
				gameState.currentSelectedUnit = null;
				gameState.unitCurrentTile = null;
				gameState.isTileSelected = false;
				board.resetAllTiles(out);
				return;
			}
			if (gameState.unitCurrentTile != tileSelected && gameState.isTileSelected
					&& gameState.unitCurrentTile != null && tileSelected.getIsActionableTile()) {
				BattleHandler.attackUnit(out, gameState.currentSelectedUnit, selectedUnit, gameState);

				// Reset conditions after combat
				gameState.isTileSelected = false;
				gameState.currentSelectedUnit = null;
				gameState.unitCurrentTile = null;
				board.resetAllTiles(out);
			} 
		}

	}

	// If a human unit is clicked, display potential movement tiles
	// need to add filter to exclude AI units
	// if (selectedUnit != null && selectedUnit.getHasMoved() == false) {

	// }

}
