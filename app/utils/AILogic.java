package utils;

import java.util.ArrayList;


import java.util.Comparator;
import java.util.List;
import java.util.Random;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Game;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.Card;


//AI Specific logic 
public class AILogic {

	public static void playAITurn(ActorRef out, GameState gameState) {
		identifyValidMoves(out, gameState);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		selectAICardToPlay(out, gameState);// working for summoning a creature card around ai avatar
		endAITurn(out, gameState);
	}

	public static void identifyValidMoves(ActorRef out, GameState gameState) {
		System.out.println("AILogic : inside identifyValidMoves");
		
		ArrayList<Tile> moveTiles = new ArrayList<Tile>();
		ArrayList<Tile> attackTiles = new ArrayList<Tile>();
		List<Unit> aiUnits = new ArrayList<Unit>();

		aiUnits = Game.getBoard().getPlayer2Units();

		for (Unit unitOfInterest : aiUnits) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Tile tileOfInterest = Game.getBoard().getTile(unitOfInterest.getPosition().getTilex(),
					unitOfInterest.getPosition().getTiley());
			Game.showValidMovement(out, Game.getBoard().getTiles(), tileOfInterest, 2, gameState);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			performUnitBattle(unitOfInterest, out, gameState);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			performUnitMove(unitOfInterest, out, gameState);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Game.getBoard().resetAllTiles(out);
		}
	}

	// we will need a list of valid tiles to choose an action from
	// There needs to be a clear distinction between what tiles are considered
	// movement and which are considered attack
	public static void performUnitBattle(Unit unitOfInterest, ActorRef out, GameState gameState) {
		ArrayList<Tile> allTiles = Game.getBoard().getTileList();
		// we will first check all potential abilities for an easy kill with this loop
		for (Tile attackTile : allTiles) {
			if (attackTile.getIsActionableTile() && !unitOfInterest.getHasAttacked() && attackTile.hasUnit()) {
				Unit unitToAttack = attackTile.getUnit();
				// if the selected unit is able to kill the defending unit in 1 blow this
				// happens
				if (unitOfInterest.getAttack() > unitToAttack.getHealth()) {
					BattleHandler.attackUnit(out, unitOfInterest, attackTile, gameState);
					return;
				}
			}
		}
		// if no easy kills are found this for loop is run instead for potential chip
		// damage
//		for (Tile attackTile : allTiles) {
//			if (attackTile.getIsActionableTile() && !unitOfInterest.getHasAttacked() && attackTile.hasUnit()) {
//				Unit unitToAttack = attackTile.getUnit();
//				// if the selected unit will survive a counter attack then it will attack for
//				// the sake of attacking to chip the enemy unit
//				if ((unitOfInterest.getHealth() > unitToAttack.getAttack()) && unitOfInterest.getAttack() > 0) {
//					BattleHandler.attackUnit(out, unitOfInterest, attackTile, gameState);
//					return;
//				}
//			}
//		}
	}

	public static void performUnitMove(Unit unitOfInterest, ActorRef out, GameState gameState) {
		System.out.println("AILogic : inside performUnitMove");

		ArrayList<Tile> allTiles = Game.getBoard().getTileList();
		ArrayList<Tile> moveableTiles = new ArrayList<Tile>();
		int currentTileX = unitOfInterest.getPosition().getTilex();
		int currentTileY = unitOfInterest.getPosition().getTiley();
		Tile currentTile = Game.getBoard().getTile(currentTileX, currentTileY);

		// Collects all moveable tiles
		for (Tile tile : allTiles) {
			if (tile.getIsActionableTile() && !tile.hasUnit()) {
				moveableTiles.add(tile);
			}
		}

		// Logic for finding farthest moves towards the human player
		Tile tileToMoveTo = null;
		int counterForDistance = 0;
		while (tileToMoveTo == null && counterForDistance < 8) {
			tileToMoveTo = findIfMoveExist(moveableTiles, currentTile, counterForDistance, out);
			counterForDistance++;
		}

		if (tileToMoveTo != null) {
			currentTile.setUnit(null);
			BasicCommands.moveUnitToTile(out, unitOfInterest, tileToMoveTo);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			tileToMoveTo.setUnit(unitOfInterest);
			unitOfInterest.setPositionByTile(tileToMoveTo);
			unitOfInterest.setHasMoved(true);
		}
		return;
		// logic for AI to choose which position to move to
	}

	// logic to identify tiles that meet the distance threshhold
	private static Tile findIfMoveExist(ArrayList<Tile> potentialTiles, Tile currentTile, int distanceThreshhold,
			ActorRef out) {
		for (Tile moveableTile : potentialTiles) {
			// System.out.println("attempting tile movement");
			int xDiff = moveableTile.getTilex() - currentTile.getTilex();
			int yDiff = moveableTile.getTiley() - currentTile.getTiley();
			Unit unitOfInterest = currentTile.getUnit();
			if (xDiff <= distanceThreshhold) {
				try {
					Game.getBoard().getTile(moveableTile.getTilex(), moveableTile.getTiley());
					return Game.getBoard().getTile(moveableTile.getTilex(), moveableTile.getTiley());
				} catch (ArrayIndexOutOfBoundsException e) {
					continue;
				}
			}
		}

		return null;
	}

	private static void summonAICard(ActorRef out, GameState gameState, Tile tile, Card card) {

		String cardJSONReference = card.getUnitConfig();
		Unit unitSummon = SubUnitCreator.identifyUnitTypeAndSummon(card.getCardname(), cardJSONReference,
				tile.getTilex(), tile.getTiley());

		System.out.println(unitSummon.getPosition().getTilex() + "," + unitSummon.getPosition().getTiley());
		unitSummon.setPositionByTile(tile);
		tile.setUnit(unitSummon);

		
		// System.out.println(board.getPlayer2Units());

		BasicCommands.drawUnit(out, unitSummon, tile);
		// stops tiles from highlighting after summon
		int health = card.getHealth();
		int attack = card.getAttack();
		unitSummon.setHealth(health);
		unitSummon.setMaxHealth(health);
		unitSummon.setAttack(attack);
		unitSummon.setMaxAttack(attack);
		unitSummon.setHasMoved(true);
		unitSummon.setName(card.getCardname());
		
		Game.getBoard().addPlayer2Unit(unitSummon);

		// a delay is required from drawing to setting attack/hp or else it will not
		// work
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// now grabs health and attack values from the card for drawing
		BasicCommands.setUnitHealth(out, unitSummon, card.getHealth());
		BasicCommands.setUnitAttack(out, unitSummon, card.getAttack());
		try {
			
				for (int i = 0; i < gameState.player2.playerHand.length; i++) {
			        if (gameState.player2.playerHand[i] != null && gameState.player2.playerHand[i].getCardname().equals(card.getCardname())) {
			        	gameState.player2.removeCardFromHand(i);  // Remove by setting the reference to null
			            break; // Stop if you only need to remove the first match
			        }
			    }
			
		}catch(Exception e) {
			System.out.println("exception");
		}
		//gameState.player2.removeCardFromHand(gameState.currentCardSelected);
		Game.resetGameState(out, gameState);
	}

	private static Tile selectTileForSummoning(GameState gameState) {

		Position avatarPosition = gameState.player2.getAvatar().getPosition();

		int X = avatarPosition.getTilex();
		System.out.println("tile X avatar " + X);
		int Y = avatarPosition.getTiley();
		System.out.println("tile Y avatar " + Y);
		Tile avatarTile = Game.getBoard().getTile(X, Y);
		List<Tile> adjacentTiles = Game.getBoard().getAdjacentTiles(avatarTile);

		// Assuming adjacentTiles is not empty
		Random random = new Random();
		Boolean findEmptyTile = false;
		int randomIndex = 0;

		while (!findEmptyTile) {

			randomIndex = random.nextInt(adjacentTiles.size());
			if (!adjacentTiles.get(randomIndex).hasUnit()) {
				findEmptyTile = true;
				return adjacentTiles.get(randomIndex);
			}

		}

		return adjacentTiles.get(randomIndex);

	}

	private static List<Card> getPlayableAICards(GameState gameState) {

		List<Card> playableCreatures = getPlayableCreatureCards(gameState);

		// Sort by mana cost
		playableCreatures.sort(Comparator.comparing(Card::getManacost));

		return playableCreatures;
	}

	private static List<Card> getPlayableCreatureCards(GameState gameState) {

		List<Card> playable = new ArrayList<>();

		for (int i = 0; i < gameState.player2.playerHand.length; i++) {
			if (gameState.player2.playerHand[i] instanceof Card) {

				// add playable creature cards
				if (gameState.player2.playerHand[i].isCreature() == true
						&& gameState.player2.getMana() >= gameState.player2.playerHand[i].getManacost()) {
					playable.add(gameState.player2.playerHand[i]);
				}
				// add playable spell cards
				if (gameState.player2.playerHand[i].isCreature() == false
						&& gameState.player2.getMana() >= gameState.player2.playerHand[i].getManacost()) {
					// placeholder for logic
				}
			}
		}
		return playable;
	}

	public static void selectAICardToPlay(ActorRef out, GameState gameState) {

		// to find playable cards (creature)
		List<Card> playableCards = getPlayableAICards(gameState);
		System.out.println("playableAI cards #### : " + playableCards.size());
		int remainingMana = gameState.player2.getMana();

		if (!playableCards.isEmpty()) {

			for (int i = 0; i < playableCards.size(); i++) {

				if (remainingMana >= playableCards.get(i).getManacost()) {
					Card cardToPlay = playableCards.get(i);
					remainingMana -= playableCards.get(i).getManacost();
					Tile targetTile = selectTileForSummoning(gameState);
					// System.out.println("tile selected "+targetTile.getTilex());
					// System.out.println("tile selected "+targetTile.getTiley());

					summonAICard(out, gameState, targetTile, cardToPlay);
					gameState.player2.playerHand[i] = null;

				} else {
					break;
				}

			}

		} else {
			// logic to move the avatar/other units already placed, attack(if it can attack)
			// and end turn
		}

	}

	public static void endAITurn(ActorRef out, GameState gameState) {
		gameState.turn++;

		List<Unit> player2Units = Game.getBoard().getPlayer2Units();
		for (Unit unit : player2Units) {
			// System.out.println("setting unit to not moved");
			unit.setHasMoved(false);
			unit.setHasAttacked(false);
		}
		Game.getBoard().resetAllTiles(out);
		//gameState.player2.drawCardAtTurnEnd(out);
		Game.beginNewTurn(out, gameState);

	}

}
