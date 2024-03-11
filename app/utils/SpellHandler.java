package utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Game;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.spells.BeamShock;
import structures.spells.DarkTerminus;
import structures.spells.EnemySpell;
import structures.spells.FriendlySpell;
import structures.spells.HornOfTheForsaken;
import structures.spells.Spell;
import structures.spells.SundropElixir;
import structures.spells.TrueStrike;
import structures.units.Avatar;

public class SpellHandler {
	
	private static final HashMap<String, Class <? extends Spell>> spellMap = new HashMap<String, Class <? extends Spell>>(){
		{
			
			put("Horn of the Forsaken", HornOfTheForsaken.class);
			put("Wraithling Swarm", DarkTerminus.class);
			put("Dark Terminus", DarkTerminus.class);
			
			put("Sundrop Elixir", SundropElixir.class);
			put("Truestrike", TrueStrike.class);
			put("Beamshock", BeamShock.class);
			
		}
		
	};
	
	//identifies class type and through reflection gets an instance of the spell 
	public static Spell returnSpell(String spellname){
		//will need to test for all spells
		System.out.println("grabbingspell");
		System.out.println(spellname);
		if (spellMap.containsKey(spellname)) {
			Class<? extends Spell> spellOfInterest = spellMap.get(spellname);
			try {
				System.out.println("returning spell: " + spellOfInterest.getName());
				return (Spell)spellOfInterest.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
				
		}
		else {
			return null;
		}
		return null;
	}
	
	public static void HighlightTilesSpell(Spell spell, ActorRef out, GameState gameState) {
		
		if (spell instanceof FriendlySpell) {
			System.out.println("CardClicked : inside spell instanceof FriendlySpell");

			Game.highlightFriendlyUnits(out, gameState);
		}
		else if (spell instanceof EnemySpell) {
			System.out.println("CardClicked : inside spell instanceof EnemySpell");

			Game.highlightEnemyUnits(out, gameState);
		}
		
	}
	
	//performs the spell
	public static void performSpell(Spell spell, Tile selectedTile, ActorRef out, GameState gameState) {
		if (gameState.currentPlayer == gameState.player1) {
			BasicCommands.addPlayer1Notification(out, "I use this spell!", 5);
		}
		
		if (spell instanceof DarkTerminus && selectedTile.getUnit() instanceof Avatar) {
            	BasicCommands.addPlayer1Notification(out, "He seems to be protected by something", 3);
            	return;
		}
		
		gameState.currentPlayer.setMana(gameState.currentPlayer.getMana() - spell.getManaCost()); // Deducting mana cost
        BasicCommands.setPlayer1Mana(out, gameState.currentPlayer);
		spell.spell(out, gameState, selectedTile);	
		GameState.player1.removeCardFromHand(gameState.currentCardSelected);
		BasicCommands.deleteCard(out, gameState.currentCardSelected);
		Game.resetGameState(out, gameState);
	}

}
