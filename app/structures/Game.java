package structures;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;

//game logic will be stored here - contemplating just using GameState and making this whole concept redundant 
public class Game {
	
	
	
	
	public Game() {
		
	}
	
	
	
	//STATIC METHODS TO CALL DURING GAME - RECONSIDER NEW CLASS?
	
	//when the player selects a card this method is called - essentially has a highlight and dehighlight system in place 
	public static void selectCard(ActorRef out, GameState gameState, int handPosition) {
		if (!gameState.cardSelected) {
			BasicCommands.drawCard(out, GameState.player1.getPlayerHandCard(handPosition), handPosition, 1);
			gameState.currentCardSelected = handPosition;
			gameState.cardSelected = true;
			System.out.println("current selected card: " + String.valueOf(gameState.currentCardSelected));
		}
		
		else {
			Card currentSelectedCard = GameState.player1.getPlayerHandCard(gameState.currentCardSelected);
			System.out.println("current selected card: " + String.valueOf(gameState.currentCardSelected));
			BasicCommands.drawCard(out, currentSelectedCard, gameState.currentCardSelected, 0);
			BasicCommands.drawCard(out, GameState.player1.getPlayerHandCard(handPosition), handPosition, 1);
			gameState.currentCardSelected = handPosition;
			gameState.cardSelected = true;		
	
		}
	}
	
	public static void summonUnit(ActorRef out, GameState gameState, int x, int y) {
		Card cardToPlayer = GameState.player1.getPlayerHandCard(gameState.currentCardSelected);
		String cardJSONReference = cardToPlayer.getUnitConfig();
		//make unit id static public attribute - where to track this? gameState again? is this the right place? 
		
		//example tile but needs the board class with populated tiles to work with this 
		Tile tileSelected = BasicObjectBuilders.loadTile(3, 3);
		
		Unit unitTest = BasicObjectBuilders.loadUnit(cardJSONReference, 0, Unit.class);
		unitTest.setPositionByTile(tileSelected);
		
		BasicCommands.drawUnit(out, unitTest, tileSelected);
		//a delay is required from drawing to setting attack/hp or else it will not work
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitAttack(out, unitTest, 1);
		BasicCommands.setUnitHealth(out, unitTest, 2);
		
		GameState.player1.removeCardFromHand(gameState.currentCardSelected);
		BasicCommands.deleteCard(out, gameState.currentCardSelected);
	}
	

	

}
