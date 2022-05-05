package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.Color;

public class Card12EffectMessage implements Message{
    Color selectedStudent;

    public Card12EffectMessage(Color selectedStudent) {
        this.selectedStudent = selectedStudent;
    }

    public Color getSelectedStudent() {
        return selectedStudent;
    }
}
