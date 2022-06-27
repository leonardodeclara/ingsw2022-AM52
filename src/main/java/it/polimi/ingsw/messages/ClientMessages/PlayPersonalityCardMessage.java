package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;

public class PlayPersonalityCardMessage implements Message {
    int cardID;

    public PlayPersonalityCardMessage(int cardID) {
        this.cardID = cardID;
    }

    public int getCardID() {
        return cardID;
    }
}
