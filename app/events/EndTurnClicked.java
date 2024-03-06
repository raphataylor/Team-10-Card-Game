package events;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Game;
import structures.GameState;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.Unit;

import java.util.*;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case
 * the end-turn button.
 * 
 * {
 * messageType = “endTurnClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		//commented out for now to troubleshoot stuff, please bring back when testing ai implementation 

		
		if(gameState.gameOver == false && gameState.isAITurnOver == false) {
			
			
			//Game.selectAICardToPlay(out, gameState);
			makeAIMoves(out, gameState);
			
//			if(gameState.currentPlayer == gameState.player1) {
//			
//			Game.resetMana(out, gameState);
//			gameState.currentPlayer = gameState.player2;
//			
//			Game.setManaOnStartTurn(out, gameState);
//			
//			Game.selectAICardToPlay(out, gameState);//working for summoning a creature card around ai avatar
//			
//			Game.selectAIUnitToAttack(out, gameState);
//			
//		}else {
//			//for testing AI player
//			
//			//code logic for AI player end turn will be placed her 
//			//Game.resetMana(out, gameState);
//			//gameState.currentPlayer = gameState.player1;
//			//Game.setManaOnStartTurn(out, gameState);
//			
//		}
		}
		


		//gameState.player1.drawCardAtTurnEnd(out);
		
		List<Unit> player1Units = Game.getBoard().getPlayer1Units();
		
		for (Unit unit : player1Units) {
		    unit.setHasMoved(false);
		    unit.setHasAttacked(false);
		}
		
		gameState.previousSelectedTile = null;
		gameState.isTileSelected = false;
		Game.beginNewTurn(out, gameState);

		
	}
	
    public void makeAIMoves(ActorRef out, GameState gameState) {
        List<Unit> player2Units = Game.getBoard().getPlayer2Units();
        int initialUnitsSize = player2Units.size();

        for (int i = 0; i < player2Units.size(); i++) {
            moveAIUnits(out, player2Units.get(i), gameState);
			
        }
    }

    private void moveAIUnits(ActorRef out, Unit unit, GameState gameState){
        
        Tile unitTile = Game.getBoard().getTile(unit.getPosition().getTilex(), unit.getPosition().getTiley());
                
        if(!unit.getHasMoved() && !unit.getHasAttacked()){
        	showTiles(out, unitTile, gameState);
        }
        
    }
   
    public void showTiles(ActorRef out, Tile tile, GameState gameState){
		int X = tile.getTilex();
		int Y = tile.getTiley();
		Unit tileUnit = tile.getUnit();
		
		if(tileUnit.getHasAttacked()) {
			tileUnit.setHasMoved(true);
		} 
		
		List<Tile>  validTiles = new ArrayList<Tile>(); 
		
		
		List<Unit> friendlyUnitsOnBoard = new ArrayList<Unit>();
		List<Unit> enemyUnitsOnBoard = new ArrayList<Unit>();
		
		if(Game.getBoard().getPlayer1Units().contains(tileUnit)){
			friendlyUnitsOnBoard = Game.getBoard().getPlayer1Units();
			enemyUnitsOnBoard = Game.getBoard().getPlayer2Units();
		}else {
			friendlyUnitsOnBoard = Game.getBoard().getPlayer2Units();
			enemyUnitsOnBoard = Game.getBoard().getPlayer1Units();
		}
				
		List<Tile> removeTiles = new ArrayList<>();
		List<Tile> addTiles = new ArrayList<>();

		
		if(!tileUnit.getHasMoved()){
			//Creating the default diamond shape highlight using points distance from center.
			for(int x = X - 2; x <= X+2; x++){
				for(int y = Y - 2; y <= Y+2; y++){
					int a = Math.abs(x-X);
					int b = Math.abs(y-Y);
					if(a + b <= 2) {
						try {
							Tile tileToAdd = Game.getBoard().getTile(x, y);
							validTiles.add(tileToAdd);
						} catch (Exception ignored) {
						}
					}
				}
			}

			// tile with no friendly unit in adjacent position
			for(int i = 0; i < validTiles.size(); i++){
				int dx = validTiles.get(i).getTilex();
				int dy = validTiles.get(i).getTiley();
				boolean hasNoFriendlyUnitInAdjacentPosition = false;
				boolean hasFriendlyUnitInAdjacentPosition = false;
				for(int x = dx-1; x <= dx+1; x++){
					for(int y = dy-1; y <= dy+1; y++){
						
						Tile t = Game.getBoard().getTile(x, y);
						int fx = Math.abs(dx - x);
						int fy = Math.abs(dy - y);
						if(fx + fy == 1){
							if (validTiles.contains(t) && t.getUnit() == null){
								hasNoFriendlyUnitInAdjacentPosition = true;
							}
							if (validTiles.contains(t) && friendlyUnitsOnBoard.contains(t.getUnit())) {
								hasFriendlyUnitInAdjacentPosition = true;
							}
						}
					}
				}
				if(!hasNoFriendlyUnitInAdjacentPosition && !hasFriendlyUnitInAdjacentPosition){
					removeTiles.add(validTiles.get(i));
				}
			}

			validTiles.removeAll(removeTiles);
			removeTiles.clear();

			// tiles with friendly unit
			for(int i = 0; i < validTiles.size(); i++){
				if(tileUnit != null){
					if(friendlyUnitsOnBoard.contains(validTiles.get(i).getUnit())){
						removeTiles.add(validTiles.get(i));
					}
				}
			}

			validTiles.removeAll(removeTiles);
			removeTiles.clear();

			//Highlight enemy
			for(int i = 0; i < validTiles.size(); i++){
				int dx = validTiles.get(i).getTilex();
				int dy = validTiles.get(i).getTiley();
				int fx = Math.abs(X - dx);
				int fy = Math.abs(Y - dy);
				if(validTiles.get(i).getUnit() == null){
					if(fx+fy == 2){
						for(int x = dx-1; x <= dx+1; x++){
							for(int y = dy-1; y <= dy+1; y++){
								
								Tile tiles = Game.getBoard().getTile(x, y);					
								if (!validTiles.contains(tiles) && enemyUnitsOnBoard.contains(tiles.getUnit())){
									addTiles.add(tiles);
								}
								
							}
						}
					}
				}

			}

			validTiles.addAll(addTiles);
			addTiles.clear();

		}

		for(Tile loopingtile: validTiles){
			if(enemyUnitsOnBoard.contains(loopingtile.getUnit())){
				BasicCommands.drawTile(out, loopingtile, 2);
				try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			
			}
		}

	}
	
	

}
