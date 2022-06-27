package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;

public class Card5EffectMessage implements Message {

    int islandID;

    public Card5EffectMessage(int islandID) {
        this.islandID = islandID;
    }

    public int getIslandID() {
        return islandID;
    }
}
