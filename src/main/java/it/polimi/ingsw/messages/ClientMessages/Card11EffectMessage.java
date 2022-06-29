package it.polimi.ingsw.messages.ClientMessages;


import it.polimi.ingsw.messages.Message;

/**
 * This message is sent after the player has used personality card 11
 */

public class Card11EffectMessage implements Message {
    int selectedStudentIndex;

    /**
     * @param selectedStudentIndex is the index of the student that player wants to move to table
     */
    public Card11EffectMessage(int selectedStudentIndex) {
        this.selectedStudentIndex = selectedStudentIndex;
    }

    public int getSelectedStudentIndex() {
        return selectedStudentIndex;
    }
}
