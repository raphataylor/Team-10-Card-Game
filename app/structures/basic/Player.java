package structures.basic;

import java.util.ArrayList;
import java.util.List;

/**
 * A basic representation of of the Player. A player
 * has health and mana.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Player {

	int health;
	int mana;
	
	//Holds the current players Cards in a hand
	List<Card> playerHand = new ArrayList<Card>(6);
	
	public Player() {
		super();
		this.health = 20;
		this.mana = 0;
	}
	public Player(int health, int mana) {
		super();
		this.health = health;
		this.mana = mana;
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	public int getMana() {
		return mana;
	}
	public void setMana(int mana) {
		this.mana = mana;
	}
	
	public Card getPlayerHandCard(int handPosition) {
		return this.playerHand.get(handPosition);
	}
	
	public void setPlayerHandCard(int handPosition, Card card) {
		this.playerHand.add(handPosition, card);
	}
	
	
	
}
