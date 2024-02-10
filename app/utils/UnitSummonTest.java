package utils;
import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;

public class UnitSummonTest {
	static Player testPlayer = new Player();	
	static List<Card> player1Cards = OrderedCardLoader.getPlayer1Cards(1);
	
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
	
		Tile tile = BasicObjectBuilders.loadTile(3, 2);
		BasicCommands.drawTile(out, tile, 1);
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

}
