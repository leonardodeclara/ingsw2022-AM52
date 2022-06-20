package it.polimi.ingsw.model;



import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    /**
     * Method addToLobbyTest verify that students are correctly added to lobby
     */
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

    /**
     * Method removeFromLobbyTest verifies that students are correctly removed from lobby
     */
    @Test
    void removeFromLobbyTest() {
        Board board = new Board();
        board.addToLobby(Color.BLUE);
        board.addToLobby(Color.BLUE);
        board.removeFromLobby(Color.BLUE);
        assertEquals(1, board.getLobby().size());
    }

    /**
     * Method addToTableTest verifies that students are correctly added to table
     */
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

    /**
     * Method removeFromTableTest verifies that students are correctly removed from table
     */
    @Test
    void removeFromTableTest() {
        Board board = new Board();
        board.addToTable(Color.BLUE);
        board.addToTable(Color.BLUE);
        board.removeFromTable(Color.BLUE);
        assertEquals(1, board.getStudentsTable().get(Color.BLUE));
    }

    @Test
    void firstSwitchStudentsTest() {
        Board board = new Board();
        board.addToLobby(Color.BLUE);
        board.addToLobby(Color.RED);
        board.addToLobby(Color.PINK);
        board.addToTable(Color.GREEN);
        board.addToTable(Color.YELLOW);
        ArrayList<Integer> fromLobby  =new ArrayList<>();
        ArrayList<Color> fromTable = new ArrayList<>();
        for (int i = 0;i<2; i++){
            fromLobby.add(i);
            fromTable.add(Color.GREEN);
        }
        assertFalse(board.switchStudents(fromTable,fromLobby)); //voglio togliere 2 green ma ne ho 1
        for (int i = 0; i<2;i++){
            fromLobby.set(i,i+3);
        }
        assertFalse(board.switchStudents(fromTable,fromLobby)); //indici out of bounds
        fromLobby.clear();
        assertFalse(board.switchStudents(fromTable,fromLobby)); //array degli indici vuoto
        for (int i = 0; i<1;i++){
            fromLobby.add(i,i);
        }
        assertFalse(board.switchStudents(fromTable,fromLobby)); //1 dalla lobby e 2 dalla table
        fromLobby.clear();
        fromTable.clear();

        for (int i = 0; i<2;i++){
            fromTable.add(i==0?Color.GREEN:Color.YELLOW);
            fromLobby.add(i);
        }
        int oldLobbySize = board.getLobby().size();
        assertEquals(2,fromLobby.size());
        assertEquals(2,fromTable.size());
        assertTrue(board.switchStudents(fromTable,fromLobby));
        assertEquals(0,board.getTableNumberOfStudents(Color.GREEN));
        assertEquals(1,board.getTableNumberOfStudents(Color.BLUE));
        assertEquals(1,board.getTableNumberOfStudents(Color.RED));
        assertEquals(oldLobbySize,board.getLobby().size());
    }

    @Test
    void secondSwitchStudentsTest(){
        Board board = new Board();
        board.addToLobby(Color.BLUE);
        board.addToLobby(Color.RED);
        board.addToLobby(Color.PINK);
        board.addToLobby(Color.PINK);
        board.addToTable(Color.GREEN);
        board.addToTable(Color.YELLOW);
        ArrayList<Integer> fromLobby  =new ArrayList<>();
        ArrayList<Color> fromTable = new ArrayList<>();
        for (int i = 0;i<2; i++){
            fromLobby.add(i);
            fromTable.add(Color.GREEN);
        }
        assertFalse(board.switchStudents(fromTable,fromLobby)); //tolgo 2 green ma ne ho 1
        fromTable.set(0,Color.BLUE);
        fromTable.set(1,Color.YELLOW);
        assertFalse(board.switchStudents(fromTable,fromLobby)); //tolgo 1 blue che non ho
        fromTable.clear();
        fromTable.add(Color.GREEN);
        fromTable.add(Color.YELLOW);
        for (int i = 0; i< Constants.MAX_TABLE_SIZE;i++){
            board.addToTable(Color.BLUE);
        }
        fromLobby.clear();
        for (int i = 0; i<2; i++){
            fromLobby.add(i);
        }
        assertFalse(board.switchStudents(fromTable,fromLobby)); //provo a spostare un blue nella table ma è piena
        fromLobby.clear();
        fromTable.clear();
        for (int i = 0; i<2;i++){
            fromTable.add(Color.BLUE);
            fromLobby.add(i+1);
        }
        int oldLobbySize = board.getLobby().size();
        assertTrue(board.switchStudents(fromTable,fromLobby)); //sposto due blue dalla table e un red e un pink dalla lobby
        assertEquals(8,board.getTableNumberOfStudents(Color.BLUE));
        assertEquals(1,board.getTableNumberOfStudents(Color.PINK));
        assertEquals(1,board.getTableNumberOfStudents(Color.RED));
        assertEquals(1,board.getTableNumberOfStudents(Color.YELLOW));
        assertEquals(1,board.getTableNumberOfStudents(Color.GREEN));
        assertEquals(oldLobbySize,board.getLobby().size());

    }


    /**
     * Method addTeacherTest verifies that teachers are correctly added to the board
     */
    @Test
    void addTeacherTest() {
        Board board = new Board();
        board.addTeacher(Color.GREEN);
        assertEquals(1,board.getTeacherTable().size());
        assertTrue(board.getTeacherTable().contains(Color.GREEN));
        board.addTeacher(Color.BLUE);
        assertTrue(board.getTeacherTable().contains(Color.BLUE));
    }

    /**
     * Method removeTeacherTest verifies that teachers are correctly removed from the board
     */
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

    /**
     * Method TowerTest verifies that towers are correctly added and removed from the board
     */
    @Test
    void TowerTest(){
        Board board = new Board();
        board.addTower();
        assertEquals(1, board.getTowers());
        board.addTower();
        assertEquals(2, board.getTowers());
        board.addTower();
        assertEquals(3, board.getTowers());
        board.removeTower();
        assertEquals(2, board.getTowers());
    }

    /**
     * Method getLobbyTest verifies that the getLobby method returns the correct ArrayList of students
     */
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

    /**
     * Method getLobbyStudentTest verifies that the getLobbyStudent method returns the correct instance of
     * the student corresponding to the required index
     */
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

    /**
     * Method getStudentsTableTest verifies that getStudentsTable method returns the correct ArrayList of students
     * that are located in the board's table
     */
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

    /**
     * Method isTableFullTest verifies if isTableFull method returns true when the table is full
     * and false otherwise
     */
    @Test
    void isTableFullTest() {
        Board board = new Board();
        for (int i = 0; i<10; i++)
            board.addToTable(Color.BLUE);
        for (int i = 0; i<9; i++)
            board.addToTable(Color.PINK);
        assertTrue(board.isTableFull(Color.BLUE,0));
        assertFalse(board.isTableFull(Color.PINK,0));
        assertFalse(board.isTableFull(Color.RED,0));
    }

    /**
     * Method getTableNumberOfStudents verifies that method getTableNumberOfStudents return the
     * correct number of students in the board
     */
    @Test
    void getTableNumberOfStudentsTest(){
        Board board = new Board();
         board.addToTable(Color.BLUE);
         board.addToTable(Color.YELLOW);
         assertEquals(1, board.getTableNumberOfStudents(Color.BLUE));
        assertEquals(1, board.getTableNumberOfStudents(Color.YELLOW));
        assertEquals(0, board.getTableNumberOfStudents(Color.RED));
     }

    /**
     * Method setTowersTest verifies that method setTowers returns the correct
     * number of inserted towers
     */
    @Test
    void setTowersTest(){
        Board board = new Board();
        board.setTowers(1);
        assertEquals(1, board.getTowers());
     }

    /**
     * Method changeTowerNumber verifies if the getTowers method continue to return the correct number of towers
     * after changing the number of towers
     *
     */
    @Test
    void changeTowerNumber(){
        Board board = new Board();
        board.addTower();
        assertEquals(1, board.getTowers());
        board.removeTower();
        assertEquals(0, board.getTowers());
    }

    /**
     * This test helps verifying that a student tile is not choosen more than once in a movement.
     */
    @Test
    void hasDuplicatesTest(){
        Board board = new Board();
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 0; i< 3; i++){
            numbers.add(i);
        }
        assertFalse(board.hasDuplicates(numbers));
        numbers.add(0);
        assertTrue(board.hasDuplicates(numbers));
    }


}