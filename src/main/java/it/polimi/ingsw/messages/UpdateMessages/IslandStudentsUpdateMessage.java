package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;
import it.polimi.ingsw.model.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class IslandStudentsUpdateMessage implements UpdateMessage {
    int islandIndex;
    ArrayList<Color> students;

    public IslandStudentsUpdateMessage(int islandIndex, ArrayList<Color> students) {
        this.islandIndex = islandIndex;
        this.students = students;
    }

    public int getIslandIndex() {
        return islandIndex;
    }

    public ArrayList<Color> getStudents() {
        return students;
    }

    @Override
    public void update(GameBoard GB) {
        GB.setIslandStudents(islandIndex, students);
        GB.print();
    }
}
