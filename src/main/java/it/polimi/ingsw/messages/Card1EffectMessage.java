package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.Color;

public class Card1EffectMessage implements Message{
    Integer studentIndex;
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
