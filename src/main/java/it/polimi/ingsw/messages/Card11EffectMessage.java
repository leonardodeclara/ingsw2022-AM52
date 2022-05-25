package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.Color;

public class Card11EffectMessage implements Message{
    int selectedStudentIndex;

    public Card11EffectMessage(int selectedStudentIndex) {
        this.selectedStudentIndex = selectedStudentIndex;
    }

    public int getSelectedStudentIndex() {
        return selectedStudentIndex;
    }
}
