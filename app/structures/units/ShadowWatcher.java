package structures.units;

import structures.basic.Unit;

public class ShadowWatcher extends Unit implements DeathwatchAbilityUnit{
	
	public void deathwatchAbility() {
		this.setHealth(getHealth() + 1); 
		this.setAttack(getAttack()+ 1);
	}

}
