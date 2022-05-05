package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.Color;

public class Card11EffectMessage implements Message{
    Color selectedStudent;

    public Card11EffectMessage(Color selectedStudent) {
        this.selectedStudent = selectedStudent;
    }

    public Color getSelectedStudent() {
        return selectedStudent;
    }
}
