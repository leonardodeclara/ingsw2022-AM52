package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class CloudTest {

    /**
     * Method fillStudentsTest verifies that fillStudents actually add the given input
     * to the cloud's students attribute
     */
    @Test
    void fillStudentsTest() {
        Cloud cloud = new Cloud(1);
        ArrayList<Color> newStudents = new ArrayList<>();
        newStudents.add(Color.PINK);
        newStudents.add(Color.PINK);
        cloud.fillStudents(newStudents);
        assertEquals(newStudents, cloud.getStudents());
        assertEquals(newStudents.size(), cloud.getStudents().size());
    }

    /**
     * Method emptyStudentsTest verifies that emptyStudents actually removes the content of
     * the cloud's students attribute.
     */
    @Test
    void emptyStudentsTest() {
        Cloud cloud = new Cloud(1);
        ArrayList<Color> newStudents = new ArrayList<>();
        Collections.addAll(newStudents,Color.values());
        cloud.fillStudents(newStudents);
        assertEquals(newStudents, cloud.emptyStudents());
        assertEquals(0, cloud.getStudents().size());
    }

    /**
     * Method getCloudIndexTest() verifies that the right index has been assigned to the cloud.
     */
    @Test
    void getCloudIndexTest() {
        Cloud cloud = new Cloud(1);
        assertEquals(1, cloud.getCloudIndex());
    }

}