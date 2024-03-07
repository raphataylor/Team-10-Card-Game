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
			put("Beam Shock", new BeamShock());
			put("Dark Terminus", new DarkTerminus());
			put("Horn Of The Forsaken", new HornOfTheForsaken());
			put("Sundrop Elixer", new SundropElixir());
			put("True Strike", new TrueStrike());
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
