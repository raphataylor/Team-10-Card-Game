package structures.units;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Game;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class GloomChaser extends Unit implements OpeningGambitAbilityUnit {
    public void openingGambitAbility(ActorRef out, int tilex, int tiley, GameState gameState, Unit summonedUnit) {
        Tile[][] board = Game.getBoard().getTiles();
        int x = tilex - 1;
        BasicCommands.addPlayer1Notification(out, "drawUnit", 1);
        Unit unit2 = BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, 1, Unit.class);
        // getting left tile using clicked tile co-rodinates
        Tile leftTile = board[x][tiley];
        // summon wrathling if the tile on the left has no unit present on it
        if (!leftTile.hasUnit()) {

            unit2.setPositionByTile(leftTile);
            leftTile.setUnit(unit2);
            BasicCommands.drawUnit(out, unit2, leftTile);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
