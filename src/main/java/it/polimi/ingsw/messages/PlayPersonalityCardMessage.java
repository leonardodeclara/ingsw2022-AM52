package it.polimi.ingsw.messages;

public class PlayPersonalityCardMessage implements Message{
    int cardID;

    public PlayPersonalityCardMessage(int cardID) {
        this.cardID = cardID;
    }

    public int getCardID() {
        return cardID;
    }
}
