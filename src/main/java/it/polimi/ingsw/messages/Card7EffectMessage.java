package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.Color;

import java.util.ArrayList;

public class Card7EffectMessage implements Message{

    ArrayList<Color> studentsFromCard;
    ArrayList<Integer> studentsFromLobbyPosition;

    public Card7EffectMessage(ArrayList<Color> studentsFromCard, ArrayList<Integer> studentsFromLobbyPosition) {
        this.studentsFromCard = studentsFromCard;
        this.studentsFromLobbyPosition = studentsFromLobbyPosition;
    }

    public ArrayList<Color> getStudentsFromCard() {
        return studentsFromCard;
    }

    public ArrayList<Integer> getStudentsFromLobbyPosition() {
        return studentsFromLobbyPosition;
    }
}
