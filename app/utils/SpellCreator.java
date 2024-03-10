package utils;

import java.util.HashMap;

import structures.basic.Card;
import structures.spells.BeamShock;
import structures.spells.DarkTerminus;
import structures.spells.HornOfTheForsaken;
import structures.spells.Spell;
import structures.spells.SundropElixir;
import structures.spells.TrueStrike;

public class SpellCreator {
	
	private static final HashMap<String, Spell> spellMap = new HashMap<String, Spell>(){
		{
			
			put("Horn Of the Forsaken", new HornOfTheForsaken());
			put("Wraithling Swarm", new DarkTerminus());
			put("Dark Terminus", new DarkTerminus());
			
			put("Sundrop Elixir", new SundropElixir());
			put("True Strike", new TrueStrike());
			put("Beam Shock", new BeamShock());
		}
		
	};
	
	public static Spell returnSpell(String spellname){
		if (spellMap.containsKey(spellname)) {
			Spell spellToReturn = spellMap.get(spellname);
			return spellToReturn;
		}
		else {
			return null;
		}
	}

}
