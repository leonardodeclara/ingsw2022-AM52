package it.polimi.ingsw;


import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    /**
     * Method LobbyTest verifies that students are correctly added and removed from the Lobby.
     */

    @Test
    void LobbyTest() {
        Board board = new Board();
        ArrayList<Color> newStudents = new ArrayList<>();
        for(int i=0; i<Color.values().length; i++){
            newStudents.add(Color.values()[i]);
            board.addToLobby(Color.values()[i]);
        }
        assertEquals(newStudents, board.getLobby());

        for(int i=Color.values().length-1; i>0; i--){
            newStudents.remove(i);
            board.removeFromLobby(i);
        }
        assertEquals(newStudents, board.getLobby());


    }


    @Test
    void TestTable() {

    }


    @Test
    void switchStudents() {
    }

    @Test
    void addTeacher() {
    }

    @Test
    void removeTeacher() {
    }

    @Test
    void addTower() {
    }

    @Test
    void removeTower() {
    }

    @Test
    void getLobby() {
    }

    @Test
    void getLobbyStudent() {
    }

    @Test
    void isTableFull() {
    }




}