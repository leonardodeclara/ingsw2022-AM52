package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;

/**
 * this message is sent after the player has used personality card 3
 */

public class Card3EffectMessage implements Message {

    int islandID;

    /**
     *
     * @param islandID is the ID of the island on which player wants to calculate influence
     */
    public Card3EffectMessage(int islandID) {
        this.islandID = islandID;
    }

    public int getIslandID() {
        return islandID;
    }
}
