package it.polimi.ingsw.messages;


public class Card11EffectMessage implements Message{
    int selectedStudentIndex;

    public Card11EffectMessage(int selectedStudentIndex) {
        this.selectedStudentIndex = selectedStudentIndex;
    }

    public int getSelectedStudentIndex() {
        return selectedStudentIndex;
    }
}
