package it.polimi.ingsw.messages;


import java.util.ArrayList;

public class MoveStudentsFromLobbyMessage implements Message{
    ArrayList<Integer> studentIndex;
    ArrayList<Integer> destinationIndex;

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

