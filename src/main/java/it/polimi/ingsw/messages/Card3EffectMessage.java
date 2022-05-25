package it.polimi.ingsw.messages;

public class Card3EffectMessage implements Message{

    int islandID;

    public Card3EffectMessage(int islandID) {
        this.islandID = islandID;
    }

    public int getIslandID() {
        return islandID;
    }
}
