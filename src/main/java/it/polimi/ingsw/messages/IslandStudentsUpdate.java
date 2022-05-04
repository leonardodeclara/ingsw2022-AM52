package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class IslandStudentsUpdate implements Message{
    int islandIndex;
    ArrayList<Color> students;

    public IslandStudentsUpdate(int islandIndex, ArrayList<Color> students) {
        this.islandIndex = islandIndex;
        this.students = students;
    }

    public int getIslandIndex() {
        return islandIndex;
    }

    public ArrayList<Color> getStudents() {
        return students;
    }
}
