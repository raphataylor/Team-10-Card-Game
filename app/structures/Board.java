package structures;

import commands.BasicCommands;
import utils.BasicObjectBuilders;
import structures.basic.*;
import akka.actor.ActorRef;


//stores tiles
public class Board {
	private Tile[][] tiles;
	
	public Board(ActorRef out) {
		tiles = createTiles();
		drawBoard(out, tiles);
	}
	
	public Tile[][] createTiles() {
		Tile[][] tiles = new Tile[9][5];
		for(int i=0;i<9;i++) {
			for(int j=0;j<5;j++) {
				tiles[i][j] = BasicObjectBuilders.loadTile(i,j);
			}
		}
		return tiles;
	}
	
	public void drawBoard(ActorRef out, Tile[][] tiles) {
		for(int i=0;i<9;i++) {
			for(int j=0;j<5;j++) {
				BasicCommands.drawTile(out, tiles[i][j], 0);
			}
		}
	}
	
}
