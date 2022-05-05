package it.polimi.ingsw.messages;

public class Card2EffectMessage implements Message{

    int islandID;

    public Card2EffectMessage(int islandID) {
        this.islandID = islandID;
    }

    public int getIslandID() {
        return islandID;
    }
}
