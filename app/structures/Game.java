package structures;

import java.util.ArrayList;
import java.util.List;

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
import structures.spells.WraithlingSwarm;
import structures.units.Avatar;
import structures.units.DeathwatchAbilityUnit;
import structures.units.NightsorrowAssasin;
import structures.units.OpeningGambitAbilityUnit;
import structures.units.ProvokeAbilityUnit;
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
		
		//get AI cards and draw initial cards for AI
		gameState.player2.setPlayerDeck(OrderedCardLoader.getPlayer2Cards(1));
		gameState.player2.drawInitialHandAI(out);
	}

	public static void setManaOnStartTurn(ActorRef out, GameState gameState) {

		if (gameState.gameInitalised) {
			System.out.println("updatign mana");
			
			
			//checks for turn number, if it is greater than 8 then every turn after will give the current player 9 mana.
			if (gameState.turn <= 8) {
				gameState.currentPlayer.setMana(gameState.turn + 1);
			}
			else {
				gameState.currentPlayer.setMana(9);
			}
			
			//utilises a method created for this purpose rather than making additional work
			updateManaVisual(out, gameState.currentPlayer);

//			if (gameState.currentPlayer == gameState.player1) {
//				BasicCommands.setPlayer1Mana(out, gameState.currentPlayer);
//			} else {
//				BasicCommands.setPlayer2Mana(out, gameState.currentPlayer);
//			}

		}

	}

	public static void resetMana(ActorRef out, GameState gameState) {

		try {

			Thread.sleep(300);
			gameState.currentPlayer.setMana(0);
			updateManaVisual(out, gameState.currentPlayer);

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

		Tile tileSelected = board.getTile(x, y);
		System.out.println(tileSelected.getIsActionableTile());
		if (tileSelected.getIsActionableTile()) {
			//mana cost check to ensure the player attempting to summon the unit has enough mana 
			if (gameState.currentPlayer.getMana() - cardToPlayer.getManacost() < 0) {
				BasicCommands.addPlayer1Notification(out, "NOT ENOUGH MANA", 3);
			}
			else {
				// Unit unitSummon = BasicObjectBuilders.loadUnit(cardJSONReference, 0,
				// Unit.class);
				Unit unitSummon = SubUnitCreator.identifyUnitTypeAndSummon(cardToPlayer.getCardname(), cardJSONReference, x, y);
				System.out.println(unitSummon.getPosition().getTilex() + "," + unitSummon.getPosition().getTiley());
				unitSummon.setPositionByTile(tileSelected);
				tileSelected.setUnit(unitSummon);
				
				if (unitSummon instanceof OpeningGambitAbilityUnit) {
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
				GameState.player1.setMana(GameState.player1.getMana() - cardToPlayer.getManacost());
				updateManaVisual(out, gameState.player1);
			}

		}
		else {
			BasicCommands.addPlayer1Notification(out, "CANNOT PLACE UNIT THERE", 3);
		}

		gameState.cardSelected = false;
		gameState.currentCardSelected = -1;
		resetGameState(out, gameState);
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
			Thread.sleep(200);
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
		
		//checks adjacent tiles for any units with provoke and draws red square if present
		List<Tile> adjTiles = board.getAdjacentTiles(tile);
		for (int i = 0; i < adjTiles.size(); i++) {
			if (adjTiles.get(i).hasUnit()) {
				if (adjTiles.get(i).getUnit() instanceof ProvokeAbilityUnit) {
					BasicCommands.drawTile(out, adjTiles.get(i), 2);
				}
				else {
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
									//sets the tile to be actionable
						            grid[x][y].setIsActionableTile(true);
									try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
				            	}
			                    Unit unitOnTile = checkedTile.getUnit();
			                    // Check if the unit belongs to player 2
			                    if(Game.getBoard().getPlayer2Units().contains(unitOnTile)) {
			                    	BasicCommands.drawTile(out, grid[x][y], 2);
			                    	//sets the tile to be actionable
						            grid[x][y].setIsActionableTile(true);
									try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
			                    }
				            }
				            
				        }
				    }
					
					
				}
			}
		}
	}
	// highlight all enemy creatures on board
	public static void showDarkTerminusHighlighing(ActorRef out) {
		Board board = Game.getBoard();
		List<Unit> player2units = board.getPlayer2Units();
		Tile[][] tiles = board.getTiles();
		int rows = board.getRows();
		int columns = board.getColumns();
		for(int i=0;i<rows;i++) {
			for(int j=0;j<columns;j++) {
				Tile checkedTile = Game.getBoard().getTile(i, j);
				if(checkedTile.hasUnit()) {
					Unit unitOnTile = checkedTile.getUnit();
					if(player2units.contains(unitOnTile) && !(unitOnTile instanceof Avatar)) {
						BasicCommands.drawTile(out, tiles[i][j], 2);
						try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();} 
					}
				}
			}
		}
	}

	// Sprint 1 [VIS08] & [VIS07]
	// Method to update and display health for both players
	public static void updateHealthVisual(ActorRef out, Player player) {
		BasicCommands.setPlayer1Health(out, player); // Update player 1's health
	}

	// Method to update and display mana for both players
	public static void updateManaVisual(ActorRef out, Player player) {
		BasicCommands.setPlayer1Mana(out, player); // Update player 1's mana
	}
	
	/* method to update the attack of an unit or avatar summoned on the board
	
	* to be modified when dependent features will get implemented like cast spells/AI summoned units
	*/
	public static void gainAttack(ActorRef out, Unit unit) {
		BasicCommands.setUnitAttack(out, unit, 2); //assigning the value 2, the unit identification and depending on it the value of attack would vary
	}
	
	// method to update the health of an unit or avatar summoned on the board
	public static void gainHealthUnit(ActorRef out, Unit unit) {
		BasicCommands.setUnitHealth(out, unit, 2);	//assigning the value 2, the unit identification and depending on it the value of attack would vary
	}
	
    public static void selectAICardToPlay(ActorRef out, GameState gameState) {
        
    	// to find playable cards (creature)
    	List<Card> playableCards = getPlayableAICards(gameState);
    	System.out.println("playableAI cards : "+playableCards.size());
    	
        int remainingMana = gameState.player2.getMana();


        if (!playableCards.isEmpty()) {
        	
        	for(int i=0; i < playableCards.size(); i++) {
        		
        		if (remainingMana >= playableCards.get(i).getManacost()) {
        			Card cardToPlay = playableCards.get(i);
                    remainingMana -= playableCards.get(i).getManacost();
                    
                    Tile targetTile = selectTileForSummoning(gameState); 
                    
                    System.out.println("tile selected "+targetTile.getTilex());
                    System.out.println("tile selected "+targetTile.getTiley());
                    
                    summonAICard(out, gameState, targetTile, cardToPlay);
                    
                    gameState.player2.playerHand[i] = null;
                                        
                    
                } else {
                    break; 
                }	
        		
        	}
            
        }else {
        	//logic to move the avatar/other units already placed, attack(if it can attack) and end turn
        }

    }
    
    private static Tile selectTileForSummoning(GameState gameState) {
    	
        Position avatarPosition = gameState.player2.getAvatar().getPosition();
        
        int X = avatarPosition.getTilex();
        System.out.println("tile X avatar "+X);
        
		int Y = avatarPosition.getTiley();
		System.out.println("tile Y avatar "+Y);
		
        Tile avatarTile = getBoard().getTile(X, Y); 
        
        List<Tile> adjacentTiles = getBoard().getAdjacentTiles(avatarTile);

        // Assuming adjacentTiles is not empty
        Random random = new Random();
        Boolean findEmptyTile = false;
        int randomIndex = 0;
        
        while(!findEmptyTile){
        	
        	randomIndex = random.nextInt(adjacentTiles.size());
        	if(!adjacentTiles.get(randomIndex).hasUnit()) {
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
        
        for (Card card : gameState.player2.playerHand) {
        	//add playable creature cards
            if (card.isCreature() == true && gameState.player2.getMana() >= card.getManacost()) {
                playable.add(card);
            }
            
            //add playable spell cards
            if(card.isCreature() == false && gameState.player2.getMana() >= card.getManacost()) {
            	//placeholder for logic 
            }
        }
        return playable;
    }
    
    private static void summonAICard(ActorRef out, GameState gameState, Tile tile, Card card) {
    	
		String cardJSONReference = card.getUnitConfig();
		
		Unit unitSummon = SubUnitCreator.identifyUnitTypeAndSummon(card.getCardname(), cardJSONReference, tile.getTilex(), tile.getTiley());
		
		System.out.println(unitSummon.getPosition().getTilex() + "," + unitSummon.getPosition().getTiley());
		unitSummon.setPositionByTile(tile);
		tile.setUnit(unitSummon);
		
		// add unit summon to player 1 unit array
		board.addPlayer2Unit(unitSummon); 
		
		System.out.println(board.getPlayer2Units());

		BasicCommands.drawUnit(out, unitSummon, tile);
		// stops tiles from highlighting after summon
		unitSummon.setHasMoved(true);
		unitSummon.setName(card.getCardname());
		
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
		
		
		
    }
    
    public static void selectAIUnitToAttack(ActorRef out, GameState gameState) {
    	List<Unit> targetEnemy = board.getPlayer1Units();
    	List<Unit> friendlyUnits = board.getPlayer2Units();
        if (targetEnemy.size() > 0) {
            BattleHandler.attackUnit(out, friendlyUnits.get(0), targetEnemy.get(0), gameState); 
        }
    }
    

	//SPELL01 CAST SPELL
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
            updateManaVisual(out, gameState.currentPlayer);
           
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

	private static void applySpellEffect(ActorRef out, GameState gameState, Tile tile, Card spellCard) {
		// Check the type of spell and apply the corresponding effect
		if (spellCard instanceof BeamShock) {
			((BeamShock)spellCard).spell(out, gameState, tile);
		} else if (spellCard instanceof DarkTerminus) {
			((DarkTerminus)spellCard).spell(out, gameState, tile);
		} else if (spellCard instanceof WraithlingSwarm) {
			((WraithlingSwarm)spellCard).spell(out, gameState, tile);
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
		
		gameState.turn++; 
		if (gameState.currentPlayer == gameState.player1) {
			setManaOnStartTurn(out, gameState);
			gameState.player1.drawCardAtTurnEnd(out);
		}
		
		else {
			
		}
	}
}