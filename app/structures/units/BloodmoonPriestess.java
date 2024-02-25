package structures.units;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Unit;

public class BloodmoonPriestess extends Unit implements DeathwatchAbilityUnit {

	public void deathwatchAbility(ActorRef out) {
		// summon wraithling logic will come with spell i hope
		this.setHealth(getHealth() + 1);
		this.setAttack(getAttack() + 1);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		BasicCommands.setUnitHealth(out, this, this.getHealth());
		BasicCommands.setUnitAttack(out, this, this.getAttack());
	}

}
