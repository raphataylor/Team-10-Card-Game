package structures.spells;
 
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
 
/*
* Spell Name - BeamShock
* Player Deck - AI Player (Player 2)
* Cost = 0
* Effects: Stun (the target unit cannot move or attack next turn) target enemy non-avatar unit.
* */
 
public class BeamShock extends Card implements Spell, EnemySpell {
    public void spell(ActorRef out, GameState gameState, Tile tile) {
        // Check if there is a unit on the targeted tile
        if (tile.hasUnit()) {
            Unit targetUnit = tile.getUnit();
            // Stun the unit, preventing it from moving or attacking next turn
            targetUnit.setStunned(out, true);
            // Display notification that the unit is stunned
            BasicCommands.addPlayer1Notification(out, targetUnit.getName() + " is stunned!", 2);
        }
    }
}