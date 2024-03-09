package structures.units;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Game;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;

//currently using class instead of interface as behaviour does not matter, just typing does
public class Avatar extends Unit{
	
	public Avatar() {
		super();
		hasMoved = false;
		hasAttacked = false;
	}
	private boolean hasHornOfForsaken = false;
	
	public void highlightAdjacentTiles(ActorRef out, Tile[][] grid) {
		int X = this.getPosition().getTilex();
		int Y = this.getPosition().getTiley();
		
	    for (int x = Math.max(0, X - 1); x <= Math.min(X + 1, grid.length - 1); x++) {
	        for (int y = Math.max(0, Y - 1); y <= Math.min(Y + 1, grid[0].length - 1); y++) {
	            if (x != X || y != Y) { 
	            	// highlight tile if empty
	            	if (!Game.getBoard().getTile(x, y).hasUnit()) {
					BasicCommands.drawTile(out, grid[x][y], 1); 
					grid[x][y].setIsActionableTile(true);
					try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
	            	}
	            }
	        }
	    }
	}
	
	public boolean getHasHornOfForsaken() {
		return this.hasHornOfForsaken;
	}
	
	public void setHasHornOfForsaken(boolean hasHornOfForsaken) {
		this.hasHornOfForsaken = hasHornOfForsaken;
	}

}
