package it.polimi.ingsw.model;



import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    @Test
    void addToLobbyTest() {
        Board board = new Board();
        assertEquals(0, board.getLobby().size());
        board.addToLobby(Color.RED);
        assertEquals(1,board.getLobby().size());
        board.addToLobby(Color.RED);
        assertEquals(2,board.getLobby().size());
        board.addToLobby(Color.PINK);
        assertEquals(3,board.getLobby().size());
        int redCount=0, pinkCount=0;
        for(Color tile: board.getLobby()){
            if(tile.equals(Color.RED))
                redCount+=1;
            else if(tile.equals(Color.PINK))
                pinkCount+=1;
        }
        assertEquals(2, redCount);
        assertEquals(1,pinkCount);
    }

    @Test
    void removeFromLobbyTest() {
        Board board = new Board();
        board.addToLobby(Color.BLUE);
        board.addToLobby(Color.BLUE);
        board.removeFromLobby(1);
        assertEquals(1, board.getLobby().size());
    }

    @Test
    void addToTableTest() {
        Board board = new Board();
        for(Color color: Color.values()){
            assertEquals(0, board.getStudentsTable().get(color));
        }
        board.addToTable(Color.PINK);
        assertEquals(1,board.getStudentsTable().get(Color.PINK));
        assertFalse(board.addToTable(Color.PINK));
        assertTrue(board.addToTable(Color.PINK));
    }

    @Test
    void removeFromTableTest() {
        Board board = new Board();
        board.addToTable(Color.BLUE);
        board.addToTable(Color.BLUE);
        board.removeFromTable(Color.BLUE);
        assertEquals(1, board.getStudentsTable().get(Color.BLUE));
    }

    @Test
    void switchStudentsTest() {
    }

    @Test
    void addTeacherTest() {
        Board board = new Board();
        board.addTeacher(Color.GREEN);
        assertEquals(1,board.getTeacherTable().size());
        assertTrue(board.getTeacherTable().contains(Color.GREEN));
        board.addTeacher(Color.BLUE);
        assertTrue(board.getTeacherTable().contains(Color.BLUE));
    }

    @Test
    void removeTeacherTest() {
        Board board = new Board();
        board.addTeacher(Color.GREEN);
        board.addTeacher(Color.BLUE);
        board.addTeacher(Color.PINK);
        board.addTeacher(Color.RED);
        assertEquals(Color.BLUE,board.removeTeacher(Color.BLUE));
        for (Color teacher: board.getTeacherTable()){
            assertNotEquals(Color.BLUE, teacher);
        }
    }

    @Test
    void TowerTest(){
        Board board = new Board();
        board.addTower(Tower.BLACK);
        assertEquals(1, board.getTowers());
        board.addTower(Tower.WHITE);
        assertEquals(2, board.getTowers());
        board.addTower(Tower.GREY);
        assertEquals(3, board.getTowers());
        board.removeTower();
        assertEquals(2, board.getTowers());
    }

    @Test
    void getLobbyTest() {
        Board board = new Board();
        board.addToLobby(Color.PINK);
        board.addToLobby(Color.RED);
        ArrayList<Color> colors = new ArrayList<>();
        colors.add(Color.PINK);
        colors.add(Color.RED);
        assertEquals(colors, board.getLobby());
    }

    @Test
    void getLobbyStudentTest() {
        Board board = new Board();
        board.addToLobby(Color.YELLOW);
        board.addToLobby(Color.YELLOW);
        board.addToLobby(Color.YELLOW);
        board.addToLobby(Color.BLUE);
        assertEquals(Color.YELLOW, board.getLobbyStudent(0));
        assertEquals(Color.BLUE, board.getLobbyStudent(3));
    }

    @Test
    void getStudentsTableTest() {
        Board board = new Board();
        board.addToTable(Color.BLUE);
        HashMap<Color, Integer> colors = new HashMap<>();
        colors.put(Color.GREEN,0);
        colors.put(Color.RED,0);
        colors.put(Color.YELLOW,0);
        colors.put(Color.PINK,0);
        colors.put(Color.BLUE,1);
        assertEquals(colors, board.getStudentsTable());
    }

    @Test
    void isTableFullTest() {
        Board board = new Board();
        for (int i = 0; i<10; i++)
            board.addToTable(Color.BLUE);
        for (int i = 0; i<9; i++)
            board.addToTable(Color.PINK);
        assertTrue(board.isTableFull(Color.BLUE));
        assertFalse(board.isTableFull(Color.PINK));
        assertFalse(board.isTableFull(Color.RED));
    }

     @Test
    void getTableNumberOfStudentsTest(){
        Board board = new Board();
         board.addToTable(Color.BLUE);
         HashMap<Color, Integer> colors = new HashMap<>();
         colors.put(Color.GREEN,0);
         colors.put(Color.RED,0);
         colors.put(Color.YELLOW,0);
         colors.put(Color.PINK,0);
         colors.put(Color.BLUE,1);
         assertEquals(1, board.getTableNumberOfStudents(Color.BLUE));
     }

     @Test
    void setTowersTest(){
        Board board = new Board();
        board.setTowers(1);
        assertEquals(1, board.getTowers());

     }


}