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
		gameState.player1 = new Player();
		gameState.player1 = new Player();

		Game.createBoard(out);
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
