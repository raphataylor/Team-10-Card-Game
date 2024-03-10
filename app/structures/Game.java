package structures;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Board;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.spells.BeamShock;
import structures.spells.DarkTerminus;
import structures.spells.SundropElixir;
import structures.spells.TrueStrike;
import structures.spells.WraithlingSwarm;
import structures.units.Avatar;
import structures.units.DeathwatchAbilityUnit;
import structures.units.NightsorrowAssasin;
import structures.units.OpeningGambitAbilityUnit;
import structures.units.ProvokeAbilityUnit;
import utils.AILogic;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;
import utils.StaticConfFiles;
import utils.SubUnitCreator;
import utils.BattleHandler;

import java.util.*;

//game logic will be stored here - contemplating just using GameState and making this whole concept redundant 
public class Game {

	private static Board board;

	public Game(ActorRef out) {
		System.out.println("Game : inside game constructor");
		createBoard(out);
	}

	public static void createBoard(ActorRef out) {
		System.out.println("Game : inside createBoard ");

		board = new Board(out);
	}

	public static Board getBoard() {
		return board;
	}

	public static void createPlayerDeck(ActorRef out, GameState gameState) {
		System.out.println("Game : inside createPlayerDeck");

		gameState.player1.setPlayerDeck(OrderedCardLoader.getPlayer1Cards(2));
		System.out.println("Game : inside createPlayerDeck : " + gameState.player1.getPlayerDeck().size());

		gameState.player1.drawInitialHand(out);

		// get AI cards and draw initial cards for AI
		gameState.player2.setPlayerDeck(OrderedCardLoader.getPlayer2Cards(2));
		System.out.println("Game : inside createPlayerDeck : " + gameState.player2.getPlayerDeck().size());

		gameState.player2.drawInitialHandAI(out);
	}

	public static void setManaOnStartTurn(ActorRef out, GameState gameState) {

		if (gameState.gameInitalised) {
			System.out.println("updatign mana");

			System.out.println("Game : inside setManaOnStartTurn");

			// checks for turn number, if it is greater than 8 then every turn after will
			// give the current player 9 mana.
			// players must start with 2 mana as the lowest so this will ensure the lowest
			// mana possible is 2
			if (gameState.turn < 2) {
				gameState.currentPlayer.setMana(2);
			} else if (gameState.turn >= 2 && gameState.turn <= 8) {
				gameState.currentPlayer.setMana(gameState.turn + 1);
			}
			/*
			 * else if (gameState.turn >= 9){
			 * gameState.currentPlayer.setMana(9);
			 * }
			 */
			else {
				gameState.currentPlayer.setMana(9);
			}

			// utilises a method created for this purpose rather than making additional work
			updateManaVisual(out, gameState.currentPlayer, gameState);

			// if (gameState.currentPlayer == gameState.player1) {
			// BasicCommands.setPlayer1Mana(out, gameState.currentPlayer);
			// } else {
			// BasicCommands.setPlayer2Mana(out, gameState.currentPlayer);
			// }

		}

	}

	public static void resetMana(ActorRef out, GameState gameState) {
		try {
			Thread.sleep(300);
			if (gameState.currentPlayer == gameState.player1) {
				gameState.player2.setMana(0);
				BasicCommands.setPlayer2Mana(out, gameState.player2);
				System.out.println("setting player mana to: " + String.valueOf(gameState.currentPlayer.getMana()));
			} else {
				gameState.player1.setMana(0);
				BasicCommands.setPlayer1Mana(out, gameState.player1);
				System.out.println("setting player mana to: " + String.valueOf(gameState.currentPlayer.getMana()));
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

	// this method only does player 1 summoning?
	public static void summonUnit(ActorRef out, GameState gameState, int x, int y) {
		System.out.println("summoning unit");
		Card cardToPlayer = GameState.player1.getPlayerHandCard(gameState.currentCardSelected);
		String cardJSONReference = cardToPlayer.getUnitConfig();
		Tile tileSelected = board.getTile(x, y);
		System.out.println(tileSelected.getIsActionableTile());
		// ensures the card attempting to be played is a unit card
		if (tileSelected.getIsActionableTile() && cardToPlayer.getIsCreature()) {
			//
			// mana cost check to ensure the player attempting to summon the unit has enough
			// mana
			if (gameState.currentPlayer.getMana() - cardToPlayer.getManacost() < 0) {
				BasicCommands.addPlayer1Notification(out, "NOT ENOUGH MANA", 3);
			} else {
				// Unit unitSummon = BasicObjectBuilders.loadUnit(cardJSONReference, 0,
				// Unit.class);
				Unit unitSummon = SubUnitCreator.identifyUnitTypeAndSummon(cardToPlayer.getCardname(),
						cardJSONReference, x, y);
				
				System.out.println("unitSummon : "+unitSummon);
				// System.out.println(unitSummon.getPosition().getTilex() + "," +
				// unitSummon.getPosition().getTiley());
				unitSummon.setPositionByTile(tileSelected);
				tileSelected.setUnit(unitSummon);

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
				unitSummon.setMaxHealth(healthVal);
				unitSummon.setAttack(attackVal);
				unitSummon.setMaxAttack(attackVal);

				String name = cardToPlayer.getCardname();
				unitSummon.setName(name);
				// now grabs health and attack values from the card for drawing
				BasicCommands.setUnitHealth(out, unitSummon, cardToPlayer.getHealth());
				BasicCommands.setUnitAttack(out, unitSummon, cardToPlayer.getAttack());
				
				GameState.player1.removeCardFromHand(gameState.currentCardSelected);
				BasicCommands.deleteCard(out, gameState.currentCardSelected);
				
				

				if (unitSummon instanceof OpeningGambitAbilityUnit) {
					System.out.println("unit has opening gambit ability");
					((OpeningGambitAbilityUnit) unitSummon).openingGambitAbility(out, gameState);
				}
				//else if(unitSummon instanceof Spell) {
					
				//}

				GameState.player1.setMana(GameState.player1.getMana() - cardToPlayer.getManacost());
				updateManaVisual(out, gameState.player1, gameState);
			}

		} else {
			BasicCommands.addPlayer1Notification(out, "CANNOT PLACE UNIT THERE", 3);
		}
		gameState.cardSelected = false;
		gameState.currentCardSelected = -1;
		resetGameState(out, gameState);
	}

	public static void summonToken(ActorRef out, Tile tileToSummonOn) {
		SubUnitCreator.globalUnitID++;
		Unit wraithUnit = BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, SubUnitCreator.globalUnitID,
				Unit.class);
		wraithUnit.setHealth(1);
		wraithUnit.setAttack(1);
		wraithUnit.setPositionByTile(tileToSummonOn);
		tileToSummonOn.setUnit(wraithUnit);
		BasicCommands.drawUnit(out, wraithUnit, tileToSummonOn);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		BasicCommands.setUnitHealth(out, wraithUnit, 1);
		BasicCommands.setUnitAttack(out, wraithUnit, 1);

	}

	// requires the correct coordinates for tile locations for both avatars
	public static Unit[] avatarSummonSetup(ActorRef out, int x, int y, int x2, int y2) {

		System.out.println("Game : inside avatarSummonSetup");

		// stores an array of the two units
		Unit[] avatars = new Unit[2];
		Unit humanAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Avatar.class);
		Tile humanAvatarStartTile = board.getTile(x, y);
		humanAvatar.setPositionByTile(humanAvatarStartTile);
		humanAvatarStartTile.setUnit(humanAvatar);
		BasicCommands.drawUnit(out, humanAvatar, humanAvatarStartTile);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		humanAvatar.setAttack(2);
		humanAvatar.setHealth(20);

		// BasicCommands.setPlayer1Health(out, GameState.player1);
		BasicCommands.setUnitHealth(out, humanAvatar, 20);
		BasicCommands.setUnitAttack(out, humanAvatar, 2);
		avatars[0] = humanAvatar;
		// not sure if this is appropriate but its required for effect checking
		// "technically"
		board.addPlayer1Unit(humanAvatar);

		Unit aiAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 1, Avatar.class);
		Tile aiAvatarStartTile = board.getTile(x2, y2);
		aiAvatarStartTile.setUnit(aiAvatar);
		aiAvatar.setPositionByTile(aiAvatarStartTile);
		BasicCommands.drawUnit(out, aiAvatar, aiAvatarStartTile);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		aiAvatar.setAttack(2);
		aiAvatar.setHealth(20);

		// BasicCommands.setPlayer2Health(out, GameState.player2);
		BasicCommands.setUnitHealth(out, aiAvatar, 20);
		BasicCommands.setUnitAttack(out, aiAvatar, 2);
		avatars[1] = aiAvatar;
		// not sure if this is appropriate but its required for effect checking
		// "technically"
		board.addPlayer2Unit(aiAvatar);

		// humanAvatar.setHasMoved(true);

		return avatars;

	}

	public static void showValidMovement(ActorRef out, Tile[][] grid, Tile tile, int distance, GameState gameState) {
		int X = tile.getTilex();
		int Y = tile.getTiley();
		Unit clickedUnit = tile.getUnit();
		System.out.println("getting valid moves");
		// checks adjacent tiles for any units with provoke and draws red square if
		// present
		List<Tile> adjTiles = board.getAdjacentTiles(tile);
		for (int i = 0; i < adjTiles.size(); i++) {
			if (adjTiles.get(i).hasUnit()) {
				Unit adjUnit = adjTiles.get(i).getUnit();
				if (adjUnit instanceof ProvokeAbilityUnit && (gameState.currentPlayer == gameState.player1 &&
						getBoard().getPlayer2Units().contains(adjUnit) ||
						gameState.currentPlayer == gameState.player2 &&
								getBoard().getPlayer1Units().contains(adjUnit))) {
					BasicCommands.drawTile(out, adjTiles.get(i), 2);
					adjTiles.get(i).setIsActionableTile(true);
				}
			} else {
				// Iterate over the board to find tiles within the specified distance.
				for (int x = Math.max(X - distance, 0); x <= Math.min(X + distance, grid.length - 1); x++) {
					for (int y = Math.max(Y - distance, 0); y <= Math.min(Y + distance, grid[0].length - 1); y++) {
						Tile checkedTile = Game.getBoard().getTile(x, y);
						// Calculate the Manhattan distance to the unit.
						int manhattanDistance = Math.abs(x - X) + Math.abs(y - Y);
						if (manhattanDistance <= distance) {
							// Highlight this tile.
							if (!checkedTile.hasUnit()) {
								BasicCommands.drawTile(out, grid[x][y], 1);
								// sets the tile to be actionable
								grid[x][y].setIsActionableTile(true);
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							Unit unitOnTile = checkedTile.getUnit();
							// Check if the unit belongs to player 2
							if (Game.getBoard().getPlayer2Units().contains(unitOnTile)
									&& GameState.currentPlayer == GameState.player1) {
								BasicCommands.drawTile(out, grid[x][y], 2);
								// sets the tile to be actionable
								grid[x][y].setIsActionableTile(true);
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							} else if (Game.getBoard().getPlayer1Units().contains(unitOnTile)
									&& GameState.currentPlayer == GameState.player2) {
								BasicCommands.drawTile(out, grid[x][y], 2);
								// sets the tile to be actionable
								grid[x][y].setIsActionableTile(true);
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}

					}
				}

			}
		}
	}

	// highlight all enemy creatures on board
	/*public static void showDarkTerminusHighlighing(ActorRef out) {
		Board board = Game.getBoard();
		List<Unit> player2units = board.getPlayer2Units();
		Tile[][] tiles = board.getTiles();
		int rows = board.getRows();
		int columns = board.getColumns();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				Tile checkedTile = Game.getBoard().getTile(i, j);
				if (checkedTile.hasUnit()) {
					Unit unitOnTile = checkedTile.getUnit();
					if (player2units.contains(unitOnTile) && !(unitOnTile instanceof Avatar)) {
						BasicCommands.drawTile(out, tiles[i][j], 2);
						tiles[i][j].setIsActionableTile(true);
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}*/

	// Sprint 1 [VIS08] & [VIS07]
	// Method to update and display health for both players
	public static void updateHealthVisual(ActorRef out, Player player, GameState gameState) {
		if (gameState.currentPlayer == gameState.player1) {
			BasicCommands.setPlayer1Health(out, player); // Update player 1's health
		} else {
			BasicCommands.setPlayer2Health(out, player); // Update player 2's health
		}

	}

	// Method to update and display mana for both players
	public static void updateManaVisual(ActorRef out, Player player, GameState gameState) {

		System.out.println("Game : inside updateManaVisual");

		if (gameState.currentPlayer == gameState.player1) {
			BasicCommands.setPlayer1Mana(out, player); // Update player 1's mana
		} else {
			BasicCommands.setPlayer2Mana(out, player); // Update player 2's mana
		}

	}

	/*
	 * method to update the attack of an unit or avatar summoned on the board
	 * 
	 * to be modified when dependent features will get implemented like cast
	 * spells/AI summoned units
	 */
	public static void gainAttack(ActorRef out, Unit unit) {
		BasicCommands.setUnitAttack(out, unit, 2); // assigning the value 2, the unit identification and depending on it
													// the value of attack would vary
	}

	// method to update the health of an unit or avatar summoned on the board
	public static void gainHealthUnit(ActorRef out, Unit unit) {
		BasicCommands.setUnitHealth(out, unit, 2); // assigning the value 2, the unit identification and depending on it
													// the value of attack would vary
	}

	public static void highlightEnemyUnits(ActorRef out, GameState gameState, Card card) {
		
		System.out.println("Game : inside highlightEnemyUnits");
		
		Tile[][] tiles = board.getTiles();
		int rows = board.getRows();
		int columns = board.getColumns();

		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				
				Tile checkedTile = board.getTile(i, j);
				
				if (checkedTile.hasUnit() && board.getPlayer2Units().contains(checkedTile.getUnit())) {
					
					if(card.getCardname().equalsIgnoreCase("dark terminus") && !(checkedTile.getUnit() instanceof Avatar) ) {
						BasicCommands.drawTile(out, checkedTile, 2); // 2 for enemy highlight
						tiles[i][j].setIsActionableTile(true);
						
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static void highlightFriendlyUnits(ActorRef out, GameState gameState, Card card) {
		for (Tile[] row : Game.getBoard().getTiles()) {
			for (Tile tile : row) {
				if (tile.hasUnit() && board.getPlayer1Units().contains(tile.getUnit())) {
					BasicCommands.drawTile(out, tile, 1); // 1 for friendly highlight
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	// SPELL01 CAST SPELL
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
			updateManaVisual(out, gameState.currentPlayer, gameState);

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

	// Apply the effect of the spell to the targeted tile or unit
	private static void applySpellEffect(ActorRef out, GameState gameState, Tile tile, Card spellCard) {
		// Check the type of spell and apply the corresponding effect and highlighting
		if (spellCard instanceof BeamShock) {
			((BeamShock) spellCard).spell(out, gameState, tile);
		} else if (spellCard instanceof DarkTerminus) {
			((DarkTerminus) spellCard).spell(out, gameState, tile);
		} else if (spellCard instanceof WraithlingSwarm) {
			((WraithlingSwarm) spellCard).spell(out, gameState, tile);
		}
	}

	public static void resetGameState(ActorRef out, GameState gameState) {
		if (gameState.currentPlayer == gameState.player1) {
			if (gameState.currentCardSelected != -1) {
				Card currentCardHighlighted = gameState.player1.getPlayerHandCard(gameState.currentCardSelected);
				BasicCommands.drawCard(out, currentCardHighlighted, gameState.currentCardSelected, 0);
			}

		}
		gameState.previousSelectedTile = null;
		gameState.isTileSelected = false;
		gameState.cardSelected = false;
		getBoard().resetAllTiles(out);

	}

	public static void beginNewTurn(ActorRef out, GameState gameState) {

		System.out.println("Game : inside beginNewTurn");
		System.out.println("Game : gameState.turn : " + gameState.turn);
		// gameState.turn++;
		System.out.println("Game : gameState.turn : " + gameState.turn);

		Game.resetMana(out, gameState);

		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// these actions occur if it is the players turn
		if (gameState.currentPlayer == gameState.player1) {
			setManaOnStartTurn(out, gameState);
			//gameState.player1.drawCardAtTurnEnd(out);
			
		}

		// these actions occur if it is the AIs turn
		else {
			setManaOnStartTurn(out, gameState);
			AILogic.playAITurn(out, gameState);
			gameState.player2.drawAICard(out, 1);

		}
		getBoard().resetAllTiles(out);
		resetGameState(out, gameState);
	}
}