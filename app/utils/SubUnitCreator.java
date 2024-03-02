package utils;

import java.util.HashMap;
import java.util.Map;

import structures.basic.Card;
import structures.basic.Unit;
import structures.units.BadOmen;
import structures.units.BloodmoonPriestess;
import structures.units.GloomChaser;
import structures.units.IroncliffeGuardian;
import structures.units.ShadowWatcher;
import structures.units.Shadowdancer;
import structures.units.SilverguardSquire;
import structures.units.SwampEntangler;
import structures.units.NightsorrowAssasin;
import structures.units.RockPulveriser;

public class SubUnitCreator {

	final static Map<String, Class<? extends Unit>> unitMap = new HashMap<String, Class<? extends Unit>>() {
		{
			//Deathwatch units
			put("Bad Omen", BadOmen.class);
			put("Bloodmoon Priestess", BloodmoonPriestess.class);
			put("Shadowdancer", Shadowdancer.class);
			put("Shadow Watcher", ShadowWatcher.class);
			
			//Opening gambit units
			put("Nightsorrow Assassin", NightsorrowAssasin.class);
			put("Gloom Chaser", GloomChaser.class);
			put("Silverguard Squire", SilverguardSquire.class);
			
			//Provoke units
			put("Rock Pulveriser", RockPulveriser.class);
			put("Swamp Entangler", SwampEntangler.class);
			put("Ironcliffe Guardian", IroncliffeGuardian.class);
			
		}
	};

	// aims to identify unit if it has an ability. Will return null if the unit has
	// no ability
	// currently
	public static Unit identifyUnitTypeAndSummon(String unitName, String jsonConfig, int x, int y) {
		if (unitMap.containsKey(unitName)) {
			Class<? extends Unit> classType = unitMap.get(unitName);
			return BasicObjectBuilders.loadUnit(jsonConfig, 0, classType);
		}
		// If the unit is not a deathwatch type then it will just be constructed as a
		// normal unit
		else {
			return BasicObjectBuilders.loadUnit(jsonConfig, 0, Unit.class);
		}
	}

}
