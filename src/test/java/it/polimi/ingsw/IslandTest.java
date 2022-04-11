package it.polimi.ingsw;

import org.junit.jupiter.api.Test;
import org.junit.platform.engine.support.descriptor.FileSystemSource;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class IslandTest {

    /**
     * Method getIslandIndexTest verifies that constructor and getIslandIndexTest correctly manage
     * the island's id.
     */
    @Test
    void IslandIndexTest() {
        Island island = new Island(10);
        assertEquals(10, island.getIslandIndex());
    }

    /**
     * Method MotherNatureTest verifies that the boolean flag motherNature is updated correctly
     */
    @Test
    void MotherNatureTest() {
        Island island = new Island(1);
        island.setMotherNature(true);
        assertTrue(island.isMotherNature());

    }

    /**
     * Method TowerTest verifies that towers are correctly added and removed from the island.
     */
    @Test
    void TowerTest() {
        Island island = new Island(1);
        island.addTower(Tower.BLACK);
        assertEquals(Tower.BLACK, island.getTowers().get(0));
        assertEquals(1, island.getTowers().size());
        ArrayList<Tower> oldTowers= island.removeTower();
        assertEquals(0, island.getTowers().size());

    }

    /**
     * Method studentTest checks that the insertion of students to the island's students attribute is accurate.
     */
    @Test
    void studentTest() {
        Island island = new Island(1);
        ArrayList<Color> newStudents = new ArrayList<>();
        for (int i = 0; i< Color.values().length; i++){
            newStudents.add(Color.values()[i]);
            island.addStudent(Color.values()[i]);
        }
        assertEquals(newStudents, island.getStudents());
    }

    /**
     * Method mergingTest checks that two island are appropriately unified. As a matter of fact
     * the merger island must contain its previous students attribute combined with the merged one,
     * its previous towers combined with the merged ones, its index must not change and the boolean
     * flag motherNature must return true if one of the two island's flag was true before.
     */
    @Test
    void mergingTest() {
        Island merger = new Island(7);
        Island merged = new Island( 10);
        merger.addStudent(Color.PINK);
        merger.addStudent(Color.RED);
        merger.addStudent(Color.BLUE);
        merger.addTower(Tower.BLACK);
        merged.addTower(Tower.BLACK);
        merged.setMotherNature(true);
        merger.merge(merged);
        assertEquals(7, merger.getIslandIndex());
        assertEquals(3, merger.getStudents().size());
        assertEquals(2, merger.getTowers().size());
        assertEquals(2, merger.getNumMergedIslands());
        assertTrue(merger.isMotherNature());

    }


}