package utils;

import java.util.List;
import java.util.Random;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Game;
import structures.GameState;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import structures.units.DeathAbilityUnit;
import structures.units.DeathwatchAbilityUnit;
import structures.units.Avatar;

public class BattleHandler {

	// WHEN AI IS IMPLEMENTED PLEASE PLEASE DO A CHECK ON IF IT IS THE RIGHT PLAYERS
	// TURN AND
	// ENSURE FRIENDLY FIRE IS FIXED!!!!
	public static void attackUnit(ActorRef out, Unit attacker, Unit defender, GameState gameState) {
		List<Unit> player1Units = Game.getBoard().getPlayer1Units();
		List<Unit> player2Units = Game.getBoard().getPlayer2Units();
		System.out.println("Attacking");
		// a simple and somewhat ugly solution to friendly fire checks
		// MAY NOT BE NEEDED SO TEST THIS
		// if (player1Units.contains(attacker) && player1Units.contains(defender)) {
		// System.out.println("CANNOT ATTACK FRIENDLY FIRE");
		// return;
		// }
		//
		// else if (player2Units.contains(attacker) && player2Units.contains(defender))
		// {
		// System.out.println("CANNOT ATTACK FRIENDLY FIRE");
		// return;
		// }
		//

		// debugging code to ensure positions and values are intended
		System.out.println("defender: " + String.valueOf(defender.getPosition()));
		System.out.println("attacker: " + String.valueOf(attacker.getPosition()));
		System.out.println(defender.getName() + " is defending");
		System.out.println(attacker.getName() + " is attacking");
		int defenderPostCombatHealth = defender.getHealth() - attacker.getAttack();

		defender.setHealth(defenderPostCombatHealth);

		// the basicCommands only seem to effecting the latest drawn unit but the units
		// being referenced are correct
		System.out.println(attacker.getName() + " is playing the attack animation");
		System.out.println("post combat health " + defenderPostCombatHealth);

		BasicCommands.setUnitHealth(out, defender, defenderPostCombatHealth);

		// display defender player's health on top
		if ((defender instanceof Avatar) && gameState.currentPlayer == gameState.player1) {
			gameState.player2.setHealth(defenderPostCombatHealth);
			BasicCommands.setPlayer2Health(out, gameState.player2); // Update player 1's health
		} else if ((defender instanceof Avatar) && gameState.currentPlayer == gameState.player2) {
			gameState.player1.setHealth(defenderPostCombatHealth);
			BasicCommands.setPlayer1Health(out, gameState.player1); // Update player 2's health
		}
		BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println(defender.getPosition());
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (defenderPostCombatHealth > 0) {
			counterAttack(out, defender, attacker, gameState);
		} else {

			// check for end game condition
			if (defender instanceof Avatar) {
				if (defender == gameState.player1.getAvatar()) {
					Player player1 = gameState.player1;
					gameOver(out, player1, gameState);
				} else {
					Player player2 = gameState.player2;
					gameOver(out, player2, gameState);
				}
			}

			BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.death);
			// 1500 seems like an initial good time from animation to delete but experiment
			// to find most
			// appropriate
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			unitDeathwatchAbilityCheck(out);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			BasicCommands.deleteUnit(out, defender);
		}
		attacker.setHasAttacked(true);
	}

	public static void counterAttack(ActorRef out, Unit counterAttacker, Unit defender, GameState gameState) {
		System.out.println("COUNTER attacking");
		int defenderPostCombatHealth = defender.getHealth() - counterAttacker.getAttack();
		System.out.println("post combat health " + defenderPostCombatHealth);
		BasicCommands.setUnitHealth(out, defender, defenderPostCombatHealth);

		BasicCommands.playUnitAnimation(out, counterAttacker, UnitAnimationType.attack);

		// display defender player's health on top
		if ((defender instanceof Avatar) && gameState.currentPlayer == gameState.player1) {
			gameState.player1.setHealth(defenderPostCombatHealth);
			BasicCommands.setPlayer1Health(out, gameState.player1); // Update player 1's health
		} else if ((defender instanceof Avatar) && gameState.currentPlayer == gameState.player2) {
			gameState.player2.setHealth(defenderPostCombatHealth);
			BasicCommands.setPlayer2Health(out, gameState.player2); // Update player 2's health
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (defenderPostCombatHealth <= 0) {
			BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.death);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			unitDeathwatchAbilityCheck(out);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			BasicCommands.deleteUnit(out, defender);
		} else {
			defender.setHealth(defenderPostCombatHealth);
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// will now loop over all units on the board
	public static void unitDeathwatchAbilityCheck(ActorRef out) {
		System.out.println("checking for deathwatch ability");
		// logic for checking if it has death ability or not
		List<Unit> player1Units = Game.getBoard().getPlayer1Units();
		// List<Unit> player2Units = Game.getBoard().getPlayer2Units();

		for (int i = 0; i < player1Units.size(); i++) {
			Unit unit = player1Units.get(i);
			if (unit instanceof DeathwatchAbilityUnit) {
				System.out.println(unit.getName() + " is a deathwatch unit and its ability will go off");
				((DeathwatchAbilityUnit) unit).deathwatchAbility(out);
			}
		}

	}

	public static int[][] shuffleArray(int[][] array) {
		Random random = new Random();

		// Shuffle the array
		for (int i = 0; i < array.length; i++) {
			// Swap with a random row
			int randomIndex = random.nextInt(array.length);
			int[] temp = array[i];
			array[i] = array[randomIndex];
			array[randomIndex] = temp;
		}
		return array;
	}

	public static void gameOver(ActorRef out, Player winner, GameState gamestate) {
		if (winner == GameState.player1) {
			BasicCommands.addPlayer1Notification(out, "you win", 10000);
		} else {
			BasicCommands.addPlayer1Notification(out, "you lose", 10000);
		}
		GameState.gameOver = true;
	}

}
