package it.polimi.ingsw.messages.ClientMessages;


import it.polimi.ingsw.messages.Message;

import java.util.ArrayList;

/**
 * This message is sent from Client to Server to notify the movement of students from lobby
 */

public class MoveStudentsFromLobbyMessage implements Message {
    ArrayList<Integer> studentIndex;
    ArrayList<Integer> destinationIndex;

    /**
     * @param studentIndex index of student that player has moved
     * @param destinationIndex index of destination
     */
    public MoveStudentsFromLobbyMessage(ArrayList<Integer> studentIndex, ArrayList<Integer> destinationIndex) {
        this.studentIndex = studentIndex;
        this.destinationIndex = destinationIndex;
    }

    public ArrayList<Integer> getStudentIndex() {
        return studentIndex;
    }

    public ArrayList<Integer> getDestinationIndex() {
        return destinationIndex;
    }
}

