package structures.spells;
 
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
 
/*
* Spell Name - Dark Terminus
* Player Deck - Human Player (Player 1)
* Cost = 4
* Effects:
* 			Destroy an enemy creature.
* 			Summon a Wraithling on the tile of the destroyed creature
* */
public class DarkTerminus extends Card implements Spell {
    public void spell(ActorRef out, GameState gameState, Tile tile){
        // Check if there is an enemy unit on the targeted tile
        if (tile.hasUnit()) {
            Unit targetUnit = tile.getUnit();
            // Destroy the enemy unit
            BasicCommands.deleteUnit(out, targetUnit);
            tile.setUnit(null); // Remove the unit from the tile
            // Summon a Wraithling on the same tile
            Unit wraithling = new Unit(); // Replace this with actual creation logic
            tile.setUnit(wraithling);
            BasicCommands.drawUnit(out, wraithling, tile);
        }
    }
}