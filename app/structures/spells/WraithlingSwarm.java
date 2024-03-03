package structures.spells;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;

/*
 * Spell Name - Wraithling Swarm
 * Player Deck - Human Player (Player 1)
 * Cost = 3
 * Effects:
 			Summon 3 Wraithlings in sequence.
 * */

public class WraithlingSwarm extends Card implements SpellAbility{
	
	public void spell(ActorRef out, GameState gameState, Tile tile){
    }

}
