package utils;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Game;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;

public class BattleHandler {
	
	public static void attackUnit(ActorRef out, Unit attacker, Unit defender, GameState gameState) {
		

		System.out.println("defender: " + String.valueOf(defender.getPosition()));
		System.out.println(String.valueOf(defender.getAttack()));
		System.out.println(String.valueOf(defender.getHealth()));
		System.out.println("attacker: " + String.valueOf(attacker.getPosition()));
		System.out.println(String.valueOf(attacker.getAttack()));
		System.out.println(String.valueOf(attacker.getHealth()));
	
		int defenderPostCombatHealth = defender.getHealth() - attacker.getAttack();
		
		//the basicCommands only seem to effecting the latest drawn unit but the units being referenced are correct
		BasicCommands.setUnitHealth(out, gameState.previousSelectedTile.getUnit(), defenderPostCombatHealth);
		BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		
	

		System.out.println(defender.getPosition());
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		

		
		if (defenderPostCombatHealth > 0) {
			//counterattack logic goes here
		}
		else {
			BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.death);
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			//BasicCommands.deleteUnit(out, defendingUnit);
		}
		
		attacker.setHasAttacked(true);
		
		
	}

}
