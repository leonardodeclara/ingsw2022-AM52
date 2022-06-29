package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;

/**
 * This message is sent from Client to Server to notify
 * which Personality card the player has choosen
 */

public class PlayPersonalityCardMessage implements Message {
    int cardID;

    /**
     * @param cardID ID of personality card choosen by the player
     */

    public PlayPersonalityCardMessage(int cardID) {
        this.cardID = cardID;
    }

    public int getCardID() {
        return cardID;
    }
}
