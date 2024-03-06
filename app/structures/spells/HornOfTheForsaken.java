package structures.spells;

import akka.actor.ActorRef;
import structures.Game;
import structures.GameState;
import structures.basic.*;
import java.util.List;
import java.util.Random;
import commands.BasicCommands;

/*
 * Spell Name - Horn of the Forsaken
 * Player Deck - Human Player (Player 1)
 * Cost = 1
 * Effects:
 *      Artifact 3: When cast, the artifact is equipped to the player’s avatar with 3 robustness.
 *                  Whenever the player’s avatar takes damage from any source, decrease this artifact’s
 *                  robustness by 1 (regardless of the amount of damage taken).
 *                  When this artifact’s robustness reaches 0, the artifact is destroyed and its effects no longer trigger.
 *                  
 *      On Hit (whenever this unit deals damage to an enemy unit): Summon a Wraithling on a randomly
 *      selected unoccupied adjacent tile. If there are no unoccupied tiles, then this ability has no effect.
 */

public class HornOfTheForsaken extends Card implements SpellAbility {
    private int robustness = 3; // Initial robustness of the artifact

    public HornOfTheForsaken() {
        super();
        // Initialize the card with proper values
        this.setManacost(1);
        this.setCardname("Horn of the Forsaken");
    }

    public void spellAbility(ActorRef out, GameState gameState, Tile tile) {
        Unit playerAvatar = gameState.currentPlayer.getAvatar();
        playerAvatar.setArtifact(this); // Equipping the artifact to the player's avatar

        BasicCommands.addPlayer1Notification(out, "Horn of the Forsaken equipped", 2); // Display notification
        gameState.currentPlayer.setMana(gameState.currentPlayer.getMana() - this.getManacost()); // Deducting mana cost
        BasicCommands.setPlayer1Mana(out, gameState.currentPlayer);
    }

    public void onPlayerAvatarDamaged(ActorRef out, GameState gameState) {
        robustness -= 1;
        BasicCommands.addPlayer1Notification(out, "Horn of the Forsaken robustness decreased", 2);
        if (robustness <= 0) {
            BasicCommands.addPlayer1Notification(out, "Horn of the Forsaken destroyed", 2);
            GameState.currentPlayer.getAvatar().removeArtifact(); // Remove the artifact when robustness is 0
        }
    }

    public void onUnitHitsEnemy(ActorRef out, GameState gameState, Tile tile) {
        List<Tile> adjacentTiles = Game.getBoard().getAdjacentTiles(tile);
        Random rand = new Random();
        boolean wraithlingSummoned = false;

        for (Tile adjacentTile : adjacentTiles) {
            if (!adjacentTile.hasUnit()) {
                Unit wraithling = WraithlingSwarm.loadUnit(StaticConfFiles.u_wraithling, Unit.getUniqueUnitId(), Unit.class); 
                wraithling.setPositionByTile(adjacentTile);
                adjacentTile.setUnit(wraithling);
                BasicCommands.drawUnit(out, wraithling, adjacentTile);
                wraithlingSummoned = true;
                break; // Summon only one wraithling
            }
        }

        if (!wraithlingSummoned) {
            BasicCommands.addPlayer1Notification(out, "No space for Wraithling", 2);
        }
    }
}
