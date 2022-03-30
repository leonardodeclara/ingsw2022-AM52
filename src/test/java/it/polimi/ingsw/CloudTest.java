package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CloudTest {

    /**
     * Method fillStudentsTest verifies that fillStudents actually add the given input
     * to the cloud's students attribute
     */
    @Test
    void fillStudentsTest() {
        Cloud cloud = new Cloud(1);
        ArrayList<Student> newStudents = new ArrayList<Student>();
        newStudents.add(new Student(Color.PINK));
        cloud.fillStudents(newStudents);
        assertEquals(newStudents, cloud.getStudents());
        assertEquals(newStudents.size(), cloud.getStudents().size());

    }

    /**
     * Method emptyStudentsTest verifies that fillStudents actually removes the content of
     * the cloud's students attribute.
     */
    @Test
    void emptyStudentsTest() {
        Cloud cloud = new Cloud(1);
        ArrayList<Student> newStudents = new ArrayList<Student>();
        for (int i = 0; i< Color.values().length; i++)
            newStudents.add(new Student(Color.values()[i]));
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