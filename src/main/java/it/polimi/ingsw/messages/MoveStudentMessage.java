package it.polimi.ingsw.messages;

public class MoveStudentMessage implements Message{
    int studentIndex;
    int destinationIndex;

    public MoveStudentMessage(int studentIndex, int destinationIndex) {
        this.studentIndex = studentIndex;
        this.destinationIndex = destinationIndex;
    }

    public int getStudentIndex() {
        return studentIndex;
    }

    public int getDestinationIndex() {
        return destinationIndex;
    }
}

