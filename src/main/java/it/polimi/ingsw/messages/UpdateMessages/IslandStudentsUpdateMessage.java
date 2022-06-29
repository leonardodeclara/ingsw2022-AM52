package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;
import it.polimi.ingsw.model.Color;

import java.util.ArrayList;

/**
 * This message is going to be broadcast to all players in game
 * after updates of an island's students number
 */
public class IslandStudentsUpdateMessage implements UpdateMessage {
    int islandIndex;
    ArrayList<Color> students;

    /**
     * @param islandIndex ID of the island on which the number of students changes
     * @param students ArrayList of new added Students
     */
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
