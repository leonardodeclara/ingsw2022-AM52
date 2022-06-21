package it.polimi.ingsw.CLI;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ClientBoardTest {

    /**
     * this method verifies the correct setting and removal of students on the Table of a ClientBoard instance
     */
    @Test
    void studentsTableTest() {
        ClientBoard board = new ClientBoard("mari");
        board.setOwner("mari");
        assertEquals("mari", board.getOwner());
        assertEquals(0, board.getStudentsTable().size());
        HashMap<Color, Integer> students = new HashMap<>();
        students.put(Color.GREEN, 2);
        students.put(Color.BLUE, 1);
        board.setStudentsTable(students);
        assertEquals(2, students.get(Color.GREEN));
        assertEquals(1, students.get(Color.BLUE));
        assertEquals(students, board.getStudentsTable());
    }

    /**
     * this method verifies the correct setting and removal of teachers on the table of a ClientBoard instance
     */
    @Test
    void teachersTableTest() {
        ClientBoard board = new ClientBoard("mari");
        assertEquals(0, board.getTeacherTable().size());
        ArrayList<Color> teachers = new ArrayList<>();
        teachers.add(Color.GREEN);
        teachers.add(Color.BLUE);
        board.setTeacherTable(teachers);
        assertEquals(2, board.getTeacherTable().size());
        assertEquals(teachers, board.getTeacherTable());
    }

    /**
     * this method verifies the correct setting and removal of students in the lobby of a ClientBoard instance
     */

    @Test
    void lobbyTest() {
        ClientBoard board = new ClientBoard("mari");
        assertEquals(0, board.getLobby().size());
        ArrayList<Color> students = new ArrayList<>();
        students.add(Color.GREEN);
        students.add(Color.BLUE);
        board.setLobby(students);
        assertEquals(2, board.getLobby().size());
        assertEquals(students, board.getLobby());
    }

    /**
     * this method verifies the correct setting and removal of towers on ClientBoard instance
     */

    @Test
    void towerstest() {
        ClientBoard board = new ClientBoard(0, "mari");
        assertEquals(0, board.getTowers());
        board.setTowers(1);
        assertEquals(1, board.getTowers());
    }


}