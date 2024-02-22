package utils;

import java.util.HashMap;
import java.util.Map;

import structures.basic.Unit;
import structures.units.BadOmen;
import structures.units.BloodmoonPriestess;
import structures.units.ShadowWatcher;
import structures.units.Shadowdancer;

public class SubUnitCreator {
	
	final static Map<String, Unit> unitMap = new HashMap<String, Unit>(){{
			put("Bad Omen", new BadOmen());
			put("Bloodmoon Priestess", new BloodmoonPriestess());
			put("Shadowdancer", new Shadowdancer());
			put("Shadow Watcher", new ShadowWatcher());
			
	}};
	
	//aims to identify unit if it has an ability. Will return null if the unit has no ability 
	//currently 
	public static Unit identifyUnitType(Unit unit) {
		if (unitMap.containsKey(unit.getName())) {
			return unitMap.get(unit.getName());
		}
		
		else {
			return null;
		}
	}

}
