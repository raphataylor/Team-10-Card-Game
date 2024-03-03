package structures.spells;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;

/*
 * Spell Name - True Strike
 * Player Deck - AI Player (Player 2)
 * Cost = 1
 * Effects: Deal 2 damage to an enemy unit.
 * */

public class TrueStrike extends Card implements SpellAbility{
	
	public void spellAbility(ActorRef out, GameState gameState, Tile tile){
    }

}
