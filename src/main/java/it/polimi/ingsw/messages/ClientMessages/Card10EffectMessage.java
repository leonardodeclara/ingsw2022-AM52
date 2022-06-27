package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.Color;
import java.util.ArrayList;

public class Card10EffectMessage implements Message {

    ArrayList<Color> studentsFromTable;
    ArrayList<Integer> studentsFromLobby;

    public Card10EffectMessage(ArrayList<Color> studentsFromTable, ArrayList<Integer> studentsFromLobby) {
        this.studentsFromTable = studentsFromTable;
        this.studentsFromLobby = studentsFromLobby;
    }

    public ArrayList<Color> getStudentsFromTable() {
        return studentsFromTable;
    }

    public ArrayList<Integer> getStudentsFromLobby() {
        return studentsFromLobby;
    }
}
