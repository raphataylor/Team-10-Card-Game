package structures.units;

import structures.basic.Unit;

public class BadOmen extends Unit implements DeathwatchAbilityUnit{

	public void deathwatchAbility() {
		this.setHealth(getHealth() + 1); 
		this.setAttack(getAttack()+ 1);
	}

}
