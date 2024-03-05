package utils;

import java.util.HashMap;
import java.util.Map;

import structures.basic.Card;
import structures.basic.Unit;
import structures.units.BadOmen;
import structures.units.BloodmoonPriestess;
import structures.units.GloomChaser;
import structures.units.IroncliffGuardian;
import structures.units.ShadowWatcher;
import structures.units.Shadowdancer;
import structures.units.SilverguardSquire;
import structures.units.SwampEntangler;
import structures.units.NightsorrowAssasin;
import structures.units.RockPulveriser;

public class SubUnitCreator {
	static int globalUnitID = 0;

	final static Map<String, Class<? extends Unit>> unitMap = new HashMap<String, Class<? extends Unit>>() {
		{
			//Deathwatch units
			put("bad_omen.json", BadOmen.class);
			put("bloodmoon_priestess.json", BloodmoonPriestess.class);
			put("shadowdancer.json", Shadowdancer.class);
			put("shadow_watcher.json", ShadowWatcher.class);
			
			//Opening gambit units
			put("nightsorrow_assassin.json", NightsorrowAssasin.class);
			put("gloom_chaser.json", GloomChaser.class);
			put("silverguard_squire.json", SilverguardSquire.class);
			
			//Provoke units
			put("rock_pulveriser.json", RockPulveriser.class);
			put("swamp_entangler.json", SwampEntangler.class);
			put("ironcliff_guardian.json", IroncliffGuardian.class);
			
		}
	};

	// aims to identify unit if it has an ability. Will return null if the unit has
	// no ability
	// currently
	public static Unit identifyUnitTypeAndSummon(String unitName, String jsonConfig, int x, int y) {
		if (unitMap.containsKey(unitName)) {
			Class<? extends Unit> classType = unitMap.get(unitName);
			globalUnitID++;
			return BasicObjectBuilders.loadUnit(jsonConfig, globalUnitID, classType);
		}
		// If the unit is not a deathwatch type then it will just be constructed as a
		// normal unit
		else {
			globalUnitID++;
			return BasicObjectBuilders.loadUnit(jsonConfig, globalUnitID, Unit.class);
		}
	}

}
