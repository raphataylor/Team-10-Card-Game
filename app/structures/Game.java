package structures;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Board;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;

//game logic will be stored here - contemplating just using GameState and making this whole concept redundant 
public class Game {
	
	private static Board board;

	public Game(ActorRef out) {
		createBoard(out);
	}

	public static void createBoard(ActorRef out) {
		board = new Board(out);
	}
	
	public static Board getBoard() {
		return board;
	}

	public static void createPlayerDeck(ActorRef out, GameState gameState) {
		gameState.player1.setPlayerDeck(OrderedCardLoader.getPlayer1Cards(1));
		gameState.player1.drawInitialHand(out);
	}

	// STATIC METHODS TO CALL DURING GAME - RECONSIDER NEW CLASS?

	// when the player selects a card this method is called - essentially has a
	// highlight and dehighlight system in place
	public static void selectCard(ActorRef out, GameState gameState, int handPosition) {
		if (!gameState.cardSelected) {
			//creating a bandaid for now
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
	
	//work on later to handle non unit situations
	//DO NOT ATTEMPT TO SUMMON A SPELL OR USE ONE - NOT IMPLEMENTED AND WILL CAUSE CRASH
	// this method only does player 1 summoning?
	public static void summonUnit(ActorRef out, GameState gameState, int x, int y) {
		Card cardToPlayer = GameState.player1.getPlayerHandCard(gameState.currentCardSelected);
		String cardJSONReference = cardToPlayer.getUnitConfig();
		//make unit id static public attribute - where to track this? gameState again? is this the right place? 
		
		//example tile but needs the board class with populated tiles to work with this 
		Tile tileSelected = board.getTile(x, y);
		
		Unit unitSummon = BasicObjectBuilders.loadUnit(cardJSONReference, 0, Unit.class);
		unitSummon.setPositionByTile(tileSelected);
		tileSelected.setUnit(unitSummon);
		
		// add unit summon to player 1 unit array
		board.addPlayer1Unit(unitSummon);
		System.out.println(board.getPlayer1Units());

		BasicCommands.drawUnit(out, unitSummon, tileSelected);
		// stops tiles from highlighting after summon
		unitSummon.setHasMoved(true);
		
		// a delay is required from drawing to setting attack/hp or else it will not
		// work
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		int healthVal = cardToPlayer.getHealth();
		int attackVal = cardToPlayer.getAttack();
		
		
		unitSummon.setHealth(healthVal);
		unitSummon.setAttack(attackVal);
		
		//now grabs health and attack values from the card for drawing
		BasicCommands.setUnitHealth(out, unitSummon, cardToPlayer.getHealth());
		BasicCommands.setUnitAttack(out, unitSummon, cardToPlayer.getAttack());
		

		GameState.player1.removeCardFromHand(gameState.currentCardSelected);
		BasicCommands.deleteCard(out, gameState.currentCardSelected);
		
		gameState.cardSelected = false;
		gameState.currentCardSelected = -1;
	}
	
	
	public static void showValidMovement(ActorRef out, Tile[][] board, Tile tile, int distance) {
		
		int X = tile.getTilex();
		int Y = tile.getTiley();
		Unit clickedUnit = tile.getUnit();
		
	    // Iterate over the board to find tiles within the specified distance.
	    for (int x = Math.max(X - distance, 0); x <= Math.min(X + distance, board.length - 1); x++) {
	        for (int y = Math.max(Y - distance, 0); y <= Math.min(Y + distance, board[0].length - 1); y++) {
	            // Calculate the Manhattan distance to the unit.
	            int manhattanDistance = Math.abs(x - X) + Math.abs(y - Y);
	            if (manhattanDistance <= distance) {
	                // Highlight this tile.
					BasicCommands.drawTile(out, board[x][y], 1);
					try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
	            }
	        }
	    }
	}

}
