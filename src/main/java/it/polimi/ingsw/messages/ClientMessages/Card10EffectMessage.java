package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.Color;
import java.util.ArrayList;

/**
 * This message is sent after the player has used personality card 10
 */

public class Card10EffectMessage implements Message {

    ArrayList<Color> studentsFromTable;
    ArrayList<Integer> studentsFromLobby;

    /**
     * @param studentsFromTable students from table that player wants to put in the lobby
     * @param studentsFromLobby students from lobby that player wants to put in the table
     */
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
