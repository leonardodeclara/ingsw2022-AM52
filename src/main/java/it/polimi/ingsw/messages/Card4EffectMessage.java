package it.polimi.ingsw.messages;

public class Card4EffectMessage implements Message{

    int islandID;

    public Card4EffectMessage(int islandID) {
        this.islandID = islandID;
    }

    public int getIslandID() {
        return islandID;
    }
}
