package structures;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Board;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.units.Avatar;
import structures.units.DeathwatchAbilityUnit;
import structures.units.NightsorrowAssasin;
import structures.units.OpeningGambitAbilityUnit;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;
import utils.StaticConfFiles;
import utils.SubUnitCreator;

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

	public static void setManaOnStartTurn(ActorRef out, GameState gameState) {

		if (gameState.gameInitalised) {

			gameState.currentPlayer.setMana(gameState.turn + 1);

			if (gameState.currentPlayer == gameState.player1) {
				BasicCommands.setPlayer1Mana(out, gameState.currentPlayer);
			} else {
				BasicCommands.setPlayer2Mana(out, gameState.currentPlayer);
			}

		}

	}

	public static void resetMana(ActorRef out, GameState gameState) {

		try {

			Thread.sleep(300);
			gameState.currentPlayer.setMana(0);

			if (gameState.currentPlayer == gameState.player1) {
				BasicCommands.setPlayer1Mana(out, gameState.currentPlayer);
			} else {
				BasicCommands.setPlayer2Mana(out, gameState.currentPlayer);
				gameState.turn += 1;

			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	// STATIC METHODS TO CALL DURING GAME - RECONSIDER NEW CLASS?

	// when the player selects a card this method is called - essentially has a
	// highlight and dehighlight system in place
	public static void selectCard(ActorRef out, GameState gameState, int handPosition) {
		if (!gameState.cardSelected) {
			// creating a bandaid for now
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

	// work on later to handle non unit situations
	// DO NOT ATTEMPT TO SUMMON A SPELL OR USE ONE - NOT IMPLEMENTED AND WILL CAUSE
	// CRASH
	// this method only does player 1 summoning?
	public static void summonUnit(ActorRef out, GameState gameState, int x, int y) {
		Card cardToPlayer = GameState.player1.getPlayerHandCard(gameState.currentCardSelected);
		String cardJSONReference = cardToPlayer.getUnitConfig();
		// make unit id static public attribute - where to track this? gameState again?
		// is this the right place?

		// example tile but needs the board class with populated tiles to work with this
		Tile tileSelected = board.getTile(x, y);

		// Unit unitSummon = BasicObjectBuilders.loadUnit(cardJSONReference, 0,
		// Unit.class);
		Unit unitSummon = SubUnitCreator.identifyUnitTypeAndSummon(cardToPlayer.getCardname(), cardJSONReference, x, y);
		System.out.println(unitSummon.getPosition().getTilex() + "," + unitSummon.getPosition().getTiley());
		unitSummon.setPositionByTile(tileSelected);
		tileSelected.setUnit(unitSummon);
		if (unitSummon instanceof DeathwatchAbilityUnit) {
			System.out.println(unitSummon.getName() + " is a deathwatch unit and its ability will go off");
			((DeathwatchAbilityUnit) unitSummon).deathwatchAbility(out);
		} else if (unitSummon instanceof OpeningGambitAbilityUnit) {
			((OpeningGambitAbilityUnit) unitSummon).openingGambitAbility(out);
		}

		// add unit summon to player 1 unit array
		board.addPlayer1Unit(unitSummon);
		// System.out.println(board.getPlayer1Units());

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

		// consider building upon existing constructor in Unit class when time permits
		// to strengthen and create a safer class with invariants!
		int healthVal = cardToPlayer.getHealth();
		int attackVal = cardToPlayer.getAttack();

		unitSummon.setHealth(healthVal);
		unitSummon.setAttack(attackVal);

		String name = cardToPlayer.getCardname();
		unitSummon.setName(name);

		// now grabs health and attack values from the card for drawing
		BasicCommands.setUnitHealth(out, unitSummon, cardToPlayer.getHealth());
		BasicCommands.setUnitAttack(out, unitSummon, cardToPlayer.getAttack());

		GameState.player1.removeCardFromHand(gameState.currentCardSelected);
		BasicCommands.deleteCard(out, gameState.currentCardSelected);

		gameState.cardSelected = false;
		gameState.currentCardSelected = -1;
	}

	// requires the correct coordinates for tile locations for both avatars
	public static Unit[] avatarSummonSetup(ActorRef out, int x, int y, int x2, int y2) {
		// stores an array of the two units
		Unit[] avatars = new Unit[2];
		Unit humanAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Avatar.class);
		Tile humanAvatarStartTile = board.getTile(2, 2);
		humanAvatar.setPositionByTile(humanAvatarStartTile);
		humanAvatarStartTile.setUnit(humanAvatar);
		BasicCommands.drawUnit(out, humanAvatar, humanAvatarStartTile);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		humanAvatar.setAttack(0);
		humanAvatar.setHealth(20);

		BasicCommands.setPlayer1Health(out, GameState.player1);
		BasicCommands.setUnitHealth(out, humanAvatar, 20);
		BasicCommands.setUnitAttack(out, humanAvatar, 0);
		avatars[0] = humanAvatar;
		// not sure if this is appropriate but its required for effect checking
		// "technically"
		board.addPlayer1Unit(humanAvatar);

		Unit aiAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 0, Avatar.class);
		Tile aiAvatarStartTile = board.getTile(6, 2);
		aiAvatarStartTile.setUnit(aiAvatar);
		aiAvatar.setPositionByTile(aiAvatarStartTile);
		BasicCommands.drawUnit(out, aiAvatar, aiAvatarStartTile);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		aiAvatar.setAttack(0);
		aiAvatar.setHealth(20);

		BasicCommands.setPlayer2Health(out, GameState.player2);
		BasicCommands.setUnitHealth(out, aiAvatar, 20);
		BasicCommands.setUnitAttack(out, aiAvatar, 0);
		avatars[1] = aiAvatar;
		// not sure if this is appropriate but its required for effect checking
		// "technically"
		board.addPlayer2Unit(aiAvatar);

		return avatars;

	}
	
	
	public static void showValidMovement(ActorRef out, Tile[][] grid, Tile tile, int distance) {
		
		int X = tile.getTilex();
		int Y = tile.getTiley();
		Unit clickedUnit = tile.getUnit();
		
	    // Iterate over the board to find tiles within the specified distance.
	    for (int x = Math.max(X - distance, 0); x <= Math.min(X + distance, grid.length - 1); x++) {
	        for (int y = Math.max(Y - distance, 0); y <= Math.min(Y + distance, grid[0].length - 1); y++) {
	        	Tile checkedTile = Game.getBoard().getTile(x, y);
	            System.out.println(checkedTile);
	            // Calculate the Manhattan distance to the unit.
	            int manhattanDistance = Math.abs(x - X) + Math.abs(y - Y);
	            if (manhattanDistance <= distance) {
	                // Highlight this tile.
	            	if(!checkedTile.hasUnit()) {
						BasicCommands.drawTile(out, grid[x][y], 1);
						try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
	            	}
                    Unit unitOnTile = checkedTile.getUnit();
                    // Check if the unit belongs to player 2
                    if(Game.getBoard().getPlayer2Units().contains(unitOnTile)) {
                    	BasicCommands.drawTile(out, grid[x][y], 2);
						try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
                    }
	            }
	        }
	    }
	}

	// Sprint 1 [VIS08] & [VIS07]
	// Method to update and display health for both players
	public static void updateHealthVisual(ActorRef out, Player player1, Player player2) {
		BasicCommands.setPlayer1Health(out, player1); // Update player 1's health
		BasicCommands.setPlayer2Health(out, player2); // Update player 2's health
	}

	// Method to update and display mana for both players
	public static void updateManaVisual(ActorRef out, Player player1, Player player2) {
		BasicCommands.setPlayer1Mana(out, player1); // Update player 1's mana
		BasicCommands.setPlayer2Mana(out, player2); // Update player 2's mana
	}

	//Sprint 2 [SPELL01]
	// This method handles the casting of spells by the player
	public static void castSpell(ActorRef out, GameState gameState, int x, int y) {		

		// Retrieve the selected card from the player's hand
		Card cardToCast = gameState.player1.getPlayerHandCard(gameState.currentCardSelected); 
		
		// Retrieve the targeted tile from the board
		Tile tileSelected = board.getTile(x, y); 
		
		// Check if the selected card is indeed a spell and the player has enough mana
		if (!cardToCast.isCreature() && gameState.player1.getMana() >= cardToCast.getManacost()) {
			
			// Implement spell effects based on the type of spell
			applySpellEffect(out, gameState, tileSelected, cardToCast);

			// Deduct the mana cost of the spell from the player's mana pool
			gameState.player1.setMana(gameState.player1.getMana() - cardToCast.getManacost());
			BasicCommands.setPlayer1Mana(out, gameState.player1);

			// Remove the spell card from the player's hand
			gameState.player1.removeCardFromHand(gameState.currentCardSelected);
			BasicCommands.deleteCard(out, gameState.currentCardSelected);

			// Reset selected card state in the game state
			gameState.cardSelected = false;
			gameState.currentCardSelected = -1;

			// Add a notification to indicate the spell has been successfully cast
			BasicCommands.addPlayer1Notification(out, "Casted " + cardToCast.getCardname(), 2);
		} else {
			// If the card is not a spell or not enough mana, notify the player
			BasicCommands.addPlayer1Notification(out, "Cannot cast " + cardToCast.getCardname(), 2);
		}
	}

	// This method would contain the logic for applying the effects of the spell
	private static void applySpellEffect(ActorRef out, GameState gameState, Tile tile, Card spellCard) {
	}

}
