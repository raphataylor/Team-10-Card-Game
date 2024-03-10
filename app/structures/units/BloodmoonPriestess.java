package structures.units;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Game;
import structures.GameState;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.BattleHandler;
import utils.StaticConfFiles;

public class BloodmoonPriestess extends Unit implements DeathwatchAbilityUnit {

	public void deathwatchAbility(ActorRef out) {
		// summon wraithling logic will come with spell i hope
		Tile[][] board = Game.getBoard().getTiles();
		Position unitPosition = this.getPosition();
		int[][] array = { // array for obtaining tiles around and adjecent to the unit tile
				{ -1, -1 },
				{ -1, 0 },
				{ -1, 1 },
				{ 0, -1 },
				{ 0, 1 },
				{ 1, -1 },
				{ 1, 0 },
				{ 1, 1 },
		};
		int[][] shuffledArray = BattleHandler.shuffleArray(array);
		for (int i = 0; i < shuffledArray.length; i++) {
			int x = unitPosition.getTilex() + shuffledArray[i][0];
			int y = unitPosition.getTiley() + shuffledArray[i][1];
			Tile adjacentTile = board[x][y];
			if (!adjacentTile.hasUnit()) {
				BasicCommands.addPlayer1Notification(out, "drawUnit", 1);
				// summon wrathling if the tile on the adjacent tile has no unit present on it
				Game.summonToken(out, adjacentTile);
				return;
			}
		}
		return;
	}
}
