package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    //TO DO
    @Test
    void addToBoardLobby() {
        Player player = new Player(0, "leo");
        assertEquals(0,player.getBoard().getLobby().size());
        player.addToBoardLobby(Color.RED);
        assertEquals(1,player.getBoard().getLobby().size());
        assertEquals(Color.RED,player.getBoard().getLobby().get(0));
    }

    @Test
    void removeFromBoardLobby() {
        Player player = new Player(0, "leo");
        assertFalse(player.removeFromBoardLobby(Color.BLUE));
        player.addToBoardLobby(Color.RED);
        assertEquals(1,player.getBoard().getLobby().size());
        assertTrue(player.removeFromBoardLobby(Color.RED));
        assertEquals(0,player.getBoard().getLobby().size());
    }

    @Test
    void addToBoardTable() {
        Player player = new Player(0, "leo");
        assertEquals(0, player.getBoard().getTableNumberOfStudents(Color.RED));
        player.addToBoardTable(Color.RED);
        assertEquals(1, player.getBoard().getTableNumberOfStudents(Color.RED));
    }

    @Test
    void removeFromBoardTable() {
        Player player = new Player(0, "leo");
        player.addToBoardTable(Color.BLUE);
        player.addToBoardTable(Color.BLUE);
        player.removeFromBoardTable(Color.BLUE);
        assertEquals(1, player.getBoard().getTableNumberOfStudents(Color.BLUE));
    }

    @Test
    void addTeacherToBoard() {
        Player player = new Player(0, "leo");
        player.addToBoardTable(Color.BLUE);
        player.addToBoardTable(Color.BLUE);
        assertEquals(0,player.getBoard().getTeacherTable().size());
        player.addTeacherToBoard(Color.BLUE);
        assertEquals(1,player.getBoard().getTeacherTable().size());
        assertEquals(Color.BLUE,player.getBoard().getTeacherTable().get(0));
    }

    @Test
    void removeTeacherFromBoard() {
        Player player = new Player(0, "leo");
        player.addToBoardTable(Color.BLUE);
        player.removeTeacherFromBoard(Color.BLUE);
        assertEquals(0,player.getBoard().getTeacherTable().size());
        assertFalse(player.getBoard().getTeacherTable().contains(Color.BLUE));
    }

    @Test
    void addTowerToBoard() {
        Player player = new Player(0, "leo");
        player.setTeam(Tower.BLACK);
        assertEquals(0,player.getBoard().getTowers());
        player.addTowersToBoard(2);
        assertEquals(2,player.getBoard().getTowers());
    }

    @Test
    void removeTowerFromBoard() {
        Player player = new Player(0, "leo");
        player.setTeam(Tower.BLACK);
        player.addTowersToBoard(3);
        player.removeTowerFromBoard();
        assertEquals(2,player.getBoard().getTowers());
    }
}