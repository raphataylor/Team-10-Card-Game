package utils;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Game;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;

public class BattleHandler {
	
	//WHEN AI IS IMPLEMENTED PLEASE PLEASE DO A CHECK ON IF IT IS THE RIGHT PLAYERS TURN AND 
	//ENSURE FRIENDLY FIRE IS FIXED!!!!
	public static void attackUnit(ActorRef out, Unit attacker, Unit defender, GameState gameState) {
		//debugging code to ensure positions and values are intended 
		System.out.println("defender: " + String.valueOf(defender.getPosition()));
		System.out.println(String.valueOf(defender.getAttack()));
		System.out.println(String.valueOf(defender.getHealth()));
		System.out.println("attacker: " + String.valueOf(attacker.getPosition()));
		System.out.println(String.valueOf(attacker.getAttack()));
		System.out.println(String.valueOf(attacker.getHealth()));
	
		int defenderPostCombatHealth = defender.getHealth() - attacker.getAttack();
		
		//the basicCommands only seem to effecting the latest drawn unit but the units being referenced are correct
		BasicCommands.setUnitHealth(out, defender, defenderPostCombatHealth);
		BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		
	

		System.out.println(defender.getPosition());
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		

		
		if (defenderPostCombatHealth > 0) {
			counterAttack(out, defender, attacker);
			defender.setHealth(defenderPostCombatHealth);
		}
		else {
			BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.death);
			//1500 seems like an initial good time from animation to delete but experiment to find most
			//appropriate
			try {Thread.sleep(1500);} catch (InterruptedException e) {e.printStackTrace();}
			BasicCommands.deleteUnit(out, defender);
		}
		attacker.setHasAttacked(true);
	}
	
	public static void counterAttack(ActorRef out, Unit counterAttacker, Unit defender) {
		int defenderPostCombatHealth = defender.getHealth() - counterAttacker.getAttack();
		BasicCommands.setUnitHealth(out, defender, defenderPostCombatHealth);
		BasicCommands.playUnitAnimation(out, counterAttacker, UnitAnimationType.attack);
		
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		
		if (defenderPostCombatHealth <= 0 ) {
			BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.death);
			try {Thread.sleep(1500);} catch (InterruptedException e) {e.printStackTrace();}
			BasicCommands.deleteUnit(out, defender);
		}
		else {
			defender.setHealth(defenderPostCombatHealth);
		}
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
	}

}
