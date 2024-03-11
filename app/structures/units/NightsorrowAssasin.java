package structures.units;

import java.util.List;

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
	
    // Method representing the opening gambit ability of the Nightsorrow Assassin
    public void openingGambitAbility(ActorRef out, GameState gameState) {
    	
        Tile[][] board = Game.getBoard().getTiles();
        Position unitPosition = this.getPosition();
        int tilex = unitPosition.getTilex();
        int tiley = unitPosition.getTiley();
        Unit clickedUnit = Game.getBoard().getTile(tilex, tiley).getUnit();
        List<Unit> player2Units = Game.getBoard().getPlayer2Units();
        
        // Arrays representing relative positions for adjacent tiles
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
                System.out.println("NighSOrrow: adjacentUnit: " + adjacentUnit);
                System.out.println(
                        "NighSOrrow: player2Units.contains(adjacentUnit)" + " " + player2Units.contains(adjacentUnit));

                if (adjacentUnit != null && player2Units.contains(adjacentUnit)
                        && adjacentUnit.getHealth() < adjacentUnit.getMaxHealth()) {
                    int unitMaxHealth = adjacentUnit.getMaxHealth();
                    int unitHealth = adjacentUnit.getHealth();
                    System.out.println("--------------" + unitMaxHealth + " " + unitHealth);
                    adjacentUnit.setHealth(0);
                    System.out.println("NighSOrrow: DEATH ANIMATION");
                    board[x][y].setUnit(null);
                    System.out.println("NighSOrrow: REMOVED UNIT REFERNECE FROM TILE");
                    BasicCommands.addPlayer1Notification(out, "NighSOrrow: playUnitAnimation [Death]", 3);
                    BasicCommands.playUnitAnimation(out, adjacentUnit, UnitAnimationType.death);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    BasicCommands.deleteUnit(out, adjacentUnit);
                }
            }
        }
    }

}