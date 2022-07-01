package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    /**
     * Test addToBoardLobby verifies the correct insertion of student tiles onto the player's lobby.
     */
    @Test
    void addToBoardLobby() {
        Player player = new Player(0, "leo",false);
        assertEquals(0,player.getBoard().getLobby().size());
        player.addToBoardLobby(Color.RED);
        assertEquals(1,player.getBoard().getLobby().size());
        assertEquals(Color.RED,player.getBoard().getLobby().get(0));
    }

    /**
     * Test removeFromBoardLobby verifies the correct removal of student tiles from the player's lobby.
     */
    @Test
    void removeFromBoardLobby() {
        Player player = new Player(0, "leo",false);
        assertFalse(player.removeFromBoardLobby(Color.BLUE));
        player.addToBoardLobby(Color.RED);
        assertEquals(1,player.getBoard().getLobby().size());
        assertTrue(player.removeFromBoardLobby(Color.RED));
        assertEquals(0,player.getBoard().getLobby().size());
    }

    /**
     * Test addToBoardLobby verifies the correct insertion of student tiles onto the player's table.
     */
    @Test
    void addToBoardTable() {
        Player player = new Player(0, "leo",false);
        assertEquals(0, player.getBoard().getTableNumberOfStudents(Color.RED));
        player.addToBoardTable(Color.RED);
        assertEquals(1, player.getBoard().getTableNumberOfStudents(Color.RED));
    }

    /**
     * Test removeFromBoardLobby verifies the correct removal of student tiles from the player's table.
     */
    @Test
    void removeFromBoardTable() {
        Player player = new Player(0, "leo",false);
        player.addToBoardTable(Color.BLUE);
        player.addToBoardTable(Color.BLUE);
        player.removeFromBoardTable(Color.BLUE);
        assertEquals(1, player.getBoard().getTableNumberOfStudents(Color.BLUE));
    }

    /**
     * Test addTeacherToBoard verifies the correct insertion of teacher tiles onto the player's board.
     */
    @Test
    void addTeacherToBoard() {
        Player player = new Player(0, "leo",false);
        player.addToBoardTable(Color.BLUE);
        player.addToBoardTable(Color.BLUE);
        assertEquals(0,player.getBoard().getTeacherTable().size());
        player.addTeacherToBoard(Color.BLUE);
        assertEquals(1,player.getBoard().getTeacherTable().size());
        assertEquals(Color.BLUE,player.getBoard().getTeacherTable().get(0));
    }

    /**
     * Test removeTeacherFromBoard verifies the correct removal of teacher tiles from the player's board.
     */
    @Test
    void removeTeacherFromBoard() {
        Player player = new Player(0, "leo",false);
        player.addToBoardTable(Color.BLUE);
        player.removeTeacherFromBoard(Color.BLUE);
        assertEquals(0,player.getBoard().getTeacherTable().size());
        assertFalse(player.getBoard().getTeacherTable().contains(Color.BLUE));
    }

    /**
     * Test addTowerToBoard verifies the correct insertion of towers onto the player's board.
     */
    @Test
    void addTowerToBoard() {
        Player player = new Player(0, "leo",false);
        player.setTeam(Tower.BLACK);
        assertEquals(0,player.getBoard().getTowers());
        player.addTowersToBoard(2);
        assertEquals(2,player.getBoard().getTowers());
    }

    /**
     * Test removeTowerFromBoard verifies the correct removal of towers from the player's board.
     */
    @Test
    void removeTowerFromBoard() {
        Player player = new Player(0, "leo",false);
        player.setTeam(Tower.BLACK);
        player.addTowersToBoard(3);
        player.removeTowerFromBoard();
        assertEquals(2,player.getBoard().getTowers());
    }
}