package structures.spells;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;

/*
 * Spell Name - Horn of the Forsaken
 * Player Deck - Human Player (Player 1)
 * Cost = 1
 * Effects:
 * 			Artifact 3: When cast the artifact is equipped to the player’s avatar with 3
			robustness. Whenever the player’s avatar takes damage from any source,
			decrease this artifact’s robustness by 1 (regardless of the amount of damage
			taken). When this artifact’s robustness reaches 0, the artifact is destroyed
			and its effects no longer trigger.

 * 			On Hit (whenever this unit deals damage to an enemy unit): Summon a
			Wraithling on a randomly selected unoccupied adjacent tile. If there are no
			unoccupied tiles, then this ability has no effect.
 **/

public class HornOfTheForsaken extends Card implements Spell {
	
	public void spell(ActorRef out, GameState gameState, Tile tile){
    }
}
