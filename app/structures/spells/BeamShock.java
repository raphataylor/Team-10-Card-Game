package structures.spells;
 
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
 
/*
* Spell Name - BeamShock
* Player Deck - AI Player (Player 2)
* Cost = 0
* Effects: Stun (the target unit cannot move or attack next turn) target enemy non-avatar unit.
* */
 
public class BeamShock extends Card implements Spell {
	
	public void spell(ActorRef out, GameState gameState, Tile tile){
    }
 
}