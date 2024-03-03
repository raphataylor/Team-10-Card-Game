package structures.spells;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;

/*
 * Spell Name - Sundrop Elixir
 * Player Deck - AI Player (Player 2)
 * Cost = 1
 * Effects: Heal allied unit for 4 health (this does not increase its maximum health)
 * */

public class SundropElixir extends Card implements SpellAbility{
	
	public void spell(ActorRef out, GameState gameState, Tile tile){
    }

}
