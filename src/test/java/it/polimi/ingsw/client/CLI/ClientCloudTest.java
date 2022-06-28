package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.model.Color;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ClientCloudTest {

    /**
     * This method verifies the correct setting and removal on a ClientIsland instance
     */

    @Test
    void studentsTest() {
        ClientCloud cloud = new ClientCloud(1);
        assertEquals(1, cloud.getCloudIndex());
        assertEquals(0, cloud.getStudents().size());
        ArrayList<Color> students = new ArrayList<>();
        students.add(Color.BLUE);
        students.add(Color.GREEN);
        cloud.setStudents(students);
        assertEquals(2, cloud.getStudents().size());
        assertEquals(students, cloud.getStudents());
    }
}