package structures.spells;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Game;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import structures.units.*;

/*
 * Spell Name - Wraithling Swarm
 * Player Deck - Human Player (Player 1)
 * Cost = 3
 * Effects:
 *          Summon 3 Wraithlings in sequence.
 */

public class WraithlingSwarm extends Card implements Spell {

    public void spell(ActorRef out, GameState gameState, Tile tile) {
        for (int i = 0; i < 3; i++) { 
            // Check if the tile is empty before summoning
            if (!tile.hasUnit()) {
                Unit wraithling = createWraithling(tile); // Create and place a Wraithling on the tile.
                BasicCommands.drawUnit(out, wraithling, tile); // Draw the Wraithling unit on the board.
                try {
                    Thread.sleep(1000); 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Unit createWraithling(Tile tile) {
        Unit wraithling = new Unit();
        wraithling.setPositionByTile(tile); // Set the unit's position based on the tile.
        wraithling.setHealth(3); // Example attributes
        wraithling.setAttack(1);
        wraithling.setName("Wraithling");
        tile.setUnit(wraithling); // Assign the newly created wraithling to the tile.
        return wraithling;
    }
}