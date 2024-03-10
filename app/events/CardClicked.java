package events;


import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Board;
import structures.Game;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.spells.EnemySpell;
import structures.spells.FriendlySpell;
import structures.spells.Spell;
import structures.units.Avatar;
import utils.SpellCreator;
import utils.UnitSummonTest;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a card.
 * The event returns the position in the player's hand the card resides within.
 * 
 * { 
 *   messageType = “cardClicked”
 *   position = <hand index position [1-6]>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class CardClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		int handPosition = message.get("position").asInt();
		//the new method for dealing with card clicked
		Game.selectCard(out, gameState, handPosition);
		System.out.println("CardClicked : inside CardClicked");

		// get current card clicked
		Card clickedCard = GameState.player1.getPlayerHandCard(handPosition);

		if (clickedCard.getIsCreature()) {// replace with check to see if card is creature
			// Highlight potential summoning tiles
			
			System.out.println("CardClicked : inside if - clickedCard.getIsCreature()");

			
			Avatar player1Avatar = (Avatar) gameState.player1.getAvatar();
			player1Avatar.highlightAdjacentTiles(out, Game.getBoard().getTiles());
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//not sure why we reset the gameboard after selecting a card 
			//Game.getBoard().drawBoard(out, Game.getBoard().getTiles());
			//Game.getBoard().resetAllTiles(out);
		}
		
		//handles clicking a spell card
		if (!clickedCard.getIsCreature()) {
			
			System.out.println("CardClicked : inside if - !clickedCard.getIsCreature()");

			
			Spell spell = SpellCreator.returnSpell(clickedCard.getCardname());
			if (spell instanceof FriendlySpell) {
				System.out.println("CardClicked : inside spell instanceof FriendlySpell");

				Game.highlightFriendlyUnits(out, gameState);
			}
			else if (spell instanceof EnemySpell) {
				System.out.println("CardClicked : inside spell instanceof EnemySpell");

				Game.highlightEnemyUnits(out, gameState);
			}
			
			else {
				
			}
			
		}

		
		if (clickedCard.getCardname().equalsIgnoreCase("dark terminus")) {// replace to check if it's a dark terminus card
			System.out.println("clicked card is dark terminus");
			//Game.showDarkTerminusHighlighing(out);
		}
		
	}

}
