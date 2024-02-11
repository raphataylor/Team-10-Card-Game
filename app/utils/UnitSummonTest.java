package utils;
import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;

public class UnitSummonTest {
	static Player testPlayer = new Player();	
	static List<Card> player1Cards = OrderedCardLoader.getPlayer1Cards(1);
	
	static Tile tile = BasicObjectBuilders.loadTile(3, 2);
	static Tile tile2 = BasicObjectBuilders.loadTile(3, 3);
	static Tile tile3 = BasicObjectBuilders.loadTile(3, 4);
	
	public static void givePlayerCard(ActorRef out) {
		
		int handPosition = 0;
		BasicCommands.drawCard(out, player1Cards.get(0), handPosition, 0);
		testPlayer.setPlayerHandCard(handPosition, player1Cards.get(0));
		handPosition++;
		
		BasicCommands.drawCard(out, player1Cards.get(1), handPosition, 0);
		testPlayer.setPlayerHandCard(handPosition, player1Cards.get(1));
		handPosition++;
		
		BasicCommands.drawCard(out, player1Cards.get(2), handPosition, 0);
		testPlayer.setPlayerHandCard(handPosition, player1Cards.get(2));
		handPosition++;
		
		BasicCommands.drawCard(out, player1Cards.get(3), handPosition, 0);
		testPlayer.setPlayerHandCard(handPosition, player1Cards.get(3));
		handPosition++;
		
		BasicCommands.drawCard(out, player1Cards.get(4), handPosition, 0);
		testPlayer.setPlayerHandCard(handPosition, player1Cards.get(4));
		handPosition++;
	
		
		//tile mode guide
		//1 - highlight
		//0 - chilling
		//2 - attackable
		
		BasicCommands.drawTile(out, tile, 1);
		
		
		BasicCommands.drawTile(out, tile2, 0);
		
		
		BasicCommands.drawTile(out, tile3, 2);
	}
	
	
	public static void testTile(ActorRef out) {
		Tile tile = BasicObjectBuilders.loadTile(3, 2);
		BasicCommands.drawTile(out, tile, 1);
	}
	
	//card highlight unhighlight logic for merging into relevant class when built
	public static void cardClick(ActorRef out, GameState gameState, int handPosition) {
		if (!gameState.cardSelected) {
			BasicCommands.drawCard(out, testPlayer.getPlayerHandCard(handPosition), handPosition, 1);
			gameState.currentCardSelected = handPosition;
			gameState.cardSelected = true;
			System.out.println("current selected card: " + String.valueOf(gameState.currentCardSelected));
		}
		
		else {
			Card currentSelectedCard = testPlayer.getPlayerHandCard(gameState.currentCardSelected);
			System.out.println("current selected card: " + String.valueOf(gameState.currentCardSelected));
			BasicCommands.drawCard(out, currentSelectedCard, gameState.currentCardSelected, 0);
			BasicCommands.drawCard(out, testPlayer.getPlayerHandCard(handPosition), handPosition, 1);
			gameState.currentCardSelected = handPosition;
			gameState.cardSelected = true;
			
	
		}
		
	}
	
	//goes into board class
	public static void summonUnit(ActorRef out, GameState gameState, int x, int y)  {
		Card cardToPlayer = testPlayer.getPlayerHandCard(gameState.currentCardSelected);
		String cardJSONReference = cardToPlayer.getUnitConfig();
		//make unit id static public attribute
		//set tile occupied attribute
		Unit unitTest = BasicObjectBuilders.loadUnit(cardJSONReference, 0, Unit.class);
		unitTest.setPositionByTile(tile2);
		
		BasicCommands.drawUnit(out, unitTest, tile2);
		//a delay is required from drawing to setting attack/hp or else it will not work
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		updateHP(out, unitTest);
		testPlayer.removeCardFromHand(gameState.currentCardSelected);
		BasicCommands.deleteCard(out, gameState.currentCardSelected);
	}
	
	public static void updateHP(ActorRef out, Unit unitTest) {
		BasicCommands.addPlayer1Notification(out, "setUnitAttack", 2);
		BasicCommands.setUnitAttack(out, unitTest, 1);
		BasicCommands.setUnitHealth(out, unitTest, 2);
	}

}
