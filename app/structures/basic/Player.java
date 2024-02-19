package structures.basic;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

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

	// Holds the current players Cards in a hand
	//CHANGE HAND TO ARRAY AND TEST LATER
	List<Card> playerHand = new ArrayList<Card>(6);

	List<Card> playerDeck = new ArrayList<Card>(20);

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

	public void removeCardFromHand(int handPosition) {
		//test bandaid
		playerHand.add(new Card());
		playerHand.remove(handPosition);
	}

	// function for drawing cards
	public void drawCard(ActorRef out, int cardsToDraw) {
		if (cardsToDraw <= 0) {
			removeCardFromDeck(0);
			return;
		}
		for (int i = 0; i < cardsToDraw; i++) {
			Card card = playerDeck.get(0);
			BasicCommands.drawCard(out, card, playerHand.size(), 0);
			setPlayerHandCard(playerHand.size(), card);
			removeCardFromDeck(0);
		}

	}

	public void drawInitialHand(ActorRef out) {
		if (!this.playerDeck.isEmpty()) {
			this.drawCard(out, 3);
		}
	}

	public void drawCardAtTurnEnd(ActorRef out) {
		if (!this.playerDeck.isEmpty()) {
			drawCard(out, this.playerHand.size() >= 6 ? 0 : 1);
		}
	}

	public void removeCardFromDeck(int handPosition) {
		this.playerDeck.remove(handPosition);
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
		// handPosition returning -1 has been crashing the game
		System.out.println(handPosition);
		return this.playerHand.get(handPosition);
	}

	public void setPlayerHandCard(int handPosition, Card card) {
		this.playerHand.add(card);
	}

	public List<Card> getPlayerDeck() {
		return this.playerDeck;
	}

	public void setPlayerDeck(List<Card> list) {
		this.playerDeck = list;
	}
}
