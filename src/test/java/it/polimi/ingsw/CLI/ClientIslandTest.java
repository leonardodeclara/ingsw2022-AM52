package it.polimi.ingsw.CLI;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClientIslandTest {

    /**
     * This method verifies the correct setting and removal of bans on a ClientIsland instance.
     */
    @Test
    void bansTest() {
        ClientIsland island = new ClientIsland(1);
        assertEquals(1,island.getIslandIndex());
        assertEquals(0,island.getBans());
        island.setBans(1);
        assertEquals(1,island.getBans());
        island.setBans(0);
        assertEquals(0,island.getBans());
    }

    /**
     * This method verifies the correct setting and removal of towers on a ClientIsland instance
     */

    @Test
    void towersTest() {
        ClientIsland island = new ClientIsland(1);
        ArrayList<Tower> towers = new ArrayList<>();
        towers.add(Tower.WHITE);
        island.setTowers(towers);
        assertEquals(1, island.getTowers().size());
        assertEquals(Tower.WHITE, island.getTowers().get(0));
    }

    /**
     * This method verifies the correct setting and removal of students on a ClientIsland instance
     */
    @Test
    void studentsTest() {
        ClientIsland island = new ClientIsland(1);
        assertEquals(0,island.getStudents().size());
        ArrayList<Color> students = new ArrayList<>();
        students.add(Color.BLUE);
        students.add(Color.GREEN);
        island.setStudents(students);
        assertEquals(2,island.getStudents().size());
        assertEquals(students,island.getStudents());
    }

    /**
     * This method verifies the correct setting of motherNature on a ClientIsland instance
     */
    @Test
    void motherNatureTest() {
        ClientIsland island = new ClientIsland(1);
        island.setMotherNature(true);
        assertTrue(island.isMotherNature());
    }


    /**
     * This method verifies the correct setting of number of merged island of a ClientIsland instance
     */
    @Test
    void setNumMergedIslands() {
        ClientIsland island = new ClientIsland(1);
        assertEquals(0,island.getNumMergedIslands());
        island.setNumMergedIslands(2);
        assertEquals(2, island.getNumMergedIslands());
    }
}