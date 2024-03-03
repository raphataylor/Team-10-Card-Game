package structures.spells;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;

/*
 * Spell Name - Dark Terminus
 * Player Deck - Human Player (Player 1)
 * Cost = 4
 * Effects:
 * 			Destroy an enemy creature.
 * 			Summon a Wraithling on the tile of the destroyed creature
 * */

public class DarkTerminus extends Card implements SpellAbility {
	
	public void spell(ActorRef out, GameState gameState, Tile tile){
    }
	
}
