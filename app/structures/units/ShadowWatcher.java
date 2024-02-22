package structures.units;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Unit;

public class ShadowWatcher extends Unit implements DeathwatchAbilityUnit{
	
	public void deathwatchAbility(ActorRef out) {
		this.setHealth(getHealth() + 1); 
		this.setAttack(getAttack()+ 1);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitHealth(out, this, this.getHealth());
		BasicCommands.setUnitAttack(out, this, this.getAttack());
	}

}
