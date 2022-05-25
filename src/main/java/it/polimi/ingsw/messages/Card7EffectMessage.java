package it.polimi.ingsw.messages;

import java.util.ArrayList;

public class Card7EffectMessage implements Message{
    ArrayList<Integer> studentsFromCard;
    ArrayList<Integer> studentsFromLobby;

    public Card7EffectMessage(ArrayList<Integer> studentsFromCard, ArrayList<Integer> studentsFromLobbyPosition) {
        this.studentsFromCard = studentsFromCard;
        this.studentsFromLobby = studentsFromLobbyPosition;
    }

    public ArrayList<Integer> getStudentsFromCard() {
        return studentsFromCard;
    }

    public ArrayList<Integer> getStudentsFromLobby() {
        return studentsFromLobby;
    }
}
