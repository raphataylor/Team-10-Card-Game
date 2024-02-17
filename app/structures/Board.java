package structures;

import commands.BasicCommands;
import utils.BasicObjectBuilders;
import structures.basic.*;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;


//stores tiles
public class Board {
	private Tile[][] tiles;
	private int rows = 9;
	private int columns = 5;
	
	public Board(ActorRef out) {
		tiles = createTiles();
		drawBoard(out, tiles);
		highlightedTiles = new ArrayList<>();
	}
	
	public Tile[][] createTiles() {
		Tile[][] tiles = new Tile[rows][columns];
		for(int i=0;i<rows;i++) {
			for(int j=0;j<columns;j++) {
				tiles[i][j] = BasicObjectBuilders.loadTile(i,j);
			}
		}
		return tiles;
	}
	
	public void drawBoard(ActorRef out, Tile[][] tiles) {
		for(int i=0;i<rows;i++) {
			for(int j=0;j<columns;j++) {
				//multi thread this to prevent buffer overflow
				BasicCommands.drawTile(out, tiles[i][j], 0);
				try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();} 
			}
		}
	}
	
	public Tile getTile(int x, int y) {
		//double check x and y are in the right order
		return tiles[x][y];
	}
	
	public Tile[][] getTiles(){
		return this.tiles;
	}

	
}
