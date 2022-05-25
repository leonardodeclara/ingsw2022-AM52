package it.polimi.ingsw.messages;

public class Card5EffectMessage implements Message{

    int islandID;

    public Card5EffectMessage(int islandID) {
        this.islandID = islandID;
    }

    public int getIslandID() {
        return islandID;
    }
}
