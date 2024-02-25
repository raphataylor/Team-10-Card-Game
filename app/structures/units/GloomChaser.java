package structures.units;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Game;
import structures.GameState;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class GloomChaser extends Unit implements OpeningGambitAbilityUnit {

    public void openingGambitAbility(ActorRef out) {
        Tile[][] board = Game.getBoard().getTiles();
        Position unitPosition = this.getPosition();
        int x = unitPosition.getTilex() - 1;
        int y = unitPosition.getTiley();
        // getting left tile using clicked tile co-rodinates
        Tile leftTile = board[x][y];
        // summon wrathling if the tile on the left has no unit present on it
        System.out.println(leftTile.hasUnit());
        if (!leftTile.hasUnit()) {
            BasicCommands.addPlayer1Notification(out, "drawUnit", 1);
            Unit unit2 = BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, 1, Unit.class);
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
