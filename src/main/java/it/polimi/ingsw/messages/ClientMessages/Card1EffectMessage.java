package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;

public class Card1EffectMessage implements Message {
    int studentIndex;
    int islandID;

    public Card1EffectMessage(int studentIndex, int islandID) {
        this.studentIndex = studentIndex;
        this.islandID = islandID;
    }

    public int getStudentIndex() {
        return studentIndex;
    }

    public int getIslandID() {
        return islandID;
    }
}
