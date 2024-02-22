package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import demo.CommandDemo;
import demo.Loaders_2024_Check;
import structures.Game;
import structures.GameState;
import structures.basic.Player;
import structures.basic.Tile;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;
import utils.UnitSummonTest;

import structures.basic.Player;


/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * 
 * {
 * messageType = “initalize”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Initalize implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		// hello this is a change

		gameState.gameInitalised = true;

		gameState.something = true;
		// Game currentGame = new Game();
				
		gameState.turn = 1; 
		
		//This is required for the mana system code to run successfully 
		Player player1 = new Player();
		gameState.player1 = player1;
		gameState.player2 = new Player();
		gameState.currentPlayer = player1;
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("begin");
		
		Game.createBoard(out);
		Game.setManaOnStartTurn(out, gameState);
		Game.createPlayerDeck(out, gameState);

		// User 1 makes a change
		// CommandDemo.executeDemo(out); // this executes the command demo, comment out
		// this when implementing your solution
		// Loaders_2024_Check.test(out);

		// replace this with the initialisation method from the game class
		// UnitSummonTest.givePlayerCard(out);
		
		UnitSummonTest.summonEnemyUnitTest(out);
	}

}
