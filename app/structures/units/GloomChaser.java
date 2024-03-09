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
import utils.SubUnitCreator;

public class GloomChaser extends Unit implements OpeningGambitAbilityUnit {

    public void openingGambitAbility(ActorRef out, GameState gameState) {

        Tile[][] board = Game.getBoard().getTiles();
        Position unitPosition = this.getPosition();
        Position avatarPosition = GameState.player1.getAvatar().getPosition();

        int x = avatarPosition.getTilex() - 1;
        int y = avatarPosition.getTiley();
        // getting left tile using clicked tile co-rodinates
        if (x >= 0 && x < 9) {
            Tile leftTile = board[x][y];
            // summon wrathling if the tile on the left has no unit present on it
            // System.out.println(leftTile.hasUnit());
            if (!leftTile.hasUnit()) {
                BasicCommands.addPlayer1Notification(out, "drawUnit", 1);
                Game.summonToken(out, leftTile);
            }
        }

    }

}
