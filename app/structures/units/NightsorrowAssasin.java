package structures.units;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import structures.Board;
import structures.Game;
import structures.GameState;
import structures.basic.Position;
import structures.basic.Tile;

public class NightsorrowAssasin extends Unit implements OpeningGambitAbilityUnit {

    public void openingGambitAbility(ActorRef out) {
        Tile[][] board = Game.getBoard().getTiles();
        Position unitPosition = this.getPosition();
        int tilex = unitPosition.getTilex();
        int tiley = unitPosition.getTiley();
        Unit clickedUnit = Game.getBoard().getTile(tilex, tiley).getUnit();
        int[] dx = { -1, -1, -1, 0, 0, 1, 1, 1 };
        int[] dy = { -1, 0, 1, -1, 1, -1, 0, 1 };
        // Iterate over adjacent tiles
        for (int i = 0; i < 8; i++) {
            int x = tilex + dx[i];
            int y = tiley + dy[i];

            // Check if the coordinates are within the board bounds
            if (x >= 0 && x < board.length && y >= 0 && y < board[0].length) {
                // Check if there's an AI unit on this tile with health less that player's unit
                Unit adjacentUnit = board[x][y].getUnit();
                if (adjacentUnit != null && adjacentUnit.getHealth() < this.getHealth()) {
                    adjacentUnit.setHealth(0);
                    System.out.println("DEATH ANIMATION");
                    board[x][y].setUnit(null);
                    System.out.println("REMOVED UNIT REFERNECE FROM TILE");
                    BasicCommands.addPlayer1Notification(out, "playUnitAnimation [Death]", 3);
                    BasicCommands.playUnitAnimation(out, adjacentUnit, UnitAnimationType.death);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    BasicCommands.deleteUnit(out, adjacentUnit);

                    break;
                }
            }
        }
    }

}