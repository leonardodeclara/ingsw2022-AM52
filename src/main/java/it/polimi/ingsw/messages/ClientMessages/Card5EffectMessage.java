package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;

/**
 * This message is sent after the player has used personality card 5
 */

public class Card5EffectMessage implements Message {

    int islandID;

    /**
     *
     * @param islandID is the ID of the island on which player wants to place ban cards
     */

    public Card5EffectMessage(int islandID) {
        this.islandID = islandID;
    }

    public int getIslandID() {
        return islandID;
    }
}
