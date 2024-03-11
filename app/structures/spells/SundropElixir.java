package structures.spells;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;

/*
 * Spell Name - Sundrop Elixir
 * Player Deck - AI Player (Player 2)
 * Cost = 1
 * Effects: Heal allied unit for 4 health (this does not increase its maximum health)
 * */

public class SundropElixir extends Spell implements FriendlySpell{
	
	public SundropElixir() {
		this.manaCost = 1;
		this.name = "Sundrop Elixir";
	}
	
	public void spell(ActorRef out, GameState gameState, Tile tile){
		Unit selectedUnit = tile.getUnit();
		int unitMaxHP = selectedUnit.getMaxHealth();
		int newHP = selectedUnit.getHealth() + 4;
		System.out.println(selectedUnit);
		System.out.println(unitMaxHP);
		System.out.println(newHP);
		
		if (newHP >= unitMaxHP) {
			newHP = unitMaxHP;
		}
		
		selectedUnit.setHealth(newHP);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		BasicCommands.setUnitHealth(out, selectedUnit, newHP);
    }

}
