package it.polimi.ingsw.messages;

public class PlayChardCardMessage implements Message{
    int cardID;

    public PlayChardCardMessage(int cardID) {
        this.cardID = cardID;
    }

    public int getCardID() {
        return cardID;
    }
}
