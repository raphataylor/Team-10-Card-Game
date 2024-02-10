package utils;
import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Card;
import structures.basic.Tile;

public class UnitSummonTest {
	
	public static void givePlayerCard(ActorRef out) {
		List<Card> player1Cards = OrderedCardLoader.getPlayer1Cards(1);
		int handPosition = 1;
		BasicCommands.drawCard(out, player1Cards.get(0), handPosition, 0);
		handPosition++;
		BasicCommands.drawCard(out, player1Cards.get(0), handPosition, 1);
	
		Tile tile = BasicObjectBuilders.loadTile(3, 2);
		BasicCommands.drawTile(out, tile, 1);
	}
	
	
	public static void testTile(ActorRef out) {
		Tile tile = BasicObjectBuilders.loadTile(3, 2);
		BasicCommands.drawTile(out, tile, 1);
	}
	
	public static void cardClick(ActorRef out, int handPosition) {
		List<Card> player1Cards = OrderedCardLoader.getPlayer1Cards(1);
		BasicCommands.drawCard(out, player1Cards.get(0), handPosition, 1);
	}

}
