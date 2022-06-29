package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;

/**
 * This message is sent after the player has used Personality card 1
 */

public class Card1EffectMessage implements Message {
    int studentIndex;
    int islandID;

    /**
     * @param studentIndex is the index of the student that player wants to move on an island
     * @param islandID is the index of the island where the player wants to put the choosen student
     */
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
