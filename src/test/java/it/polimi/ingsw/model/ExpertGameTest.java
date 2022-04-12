package it.polimi.ingsw.model;

import it.polimi.ingsw.model.ExpertGame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExpertGameTest {

    @Test
    void instantiateGameElementsTest() {
        ExpertGame game = new ExpertGame();
        game.instantiateGameElements();
        assertEquals(20,game.getCoins());
        assertEquals(4, game.getBans());
    }

    @Test
    void personalityExtractionTest(){
        ExpertGame game = new ExpertGame();
        game.instantiateGameElements();
        game.extractPersonalityCards();
        assertEquals(3, game.getPersonalities().size());
        for (int i = 0; i< 2; i++){
            for(int j = i+1; j<3; j++){
                assertNotEquals(game.getPersonalities().get(i).getCharacterId(), game.getPersonalities().get(j).getCharacterId());
                assertTrue(game.getPersonalities().get(i).getCharacterId()<13);
                assertTrue(game.getPersonalities().get(i).getCharacterId()>0);
                assertTrue(game.getPersonalities().get(j).getCharacterId()<13);
                assertTrue(game.getPersonalities().get(j).getCharacterId()>0);
            }
        }
    }

    @Test
    void moveStudentFromLobbyForCard2() {
        ExpertGame game = new ExpertGame();
        game.addPlayer(new Player(0,"leo",Tower.WHITE));
        game.addPlayer(new Player(1,"frizio",Tower.BLACK));
        game.instantiateGameElements();
        for (int i = 0; i<7; i++){
            game.getPlayers().get(0).getBoard().addToLobby(Color.BLUE);
            game.getPlayers().get(1).getBoard().addToLobby(Color.PINK);
        }
        int tableSizeP0 = game.getPlayers().get(0).getBoard().getStudentsTable().get(Color.BLUE);
        int tableSizeP1 = game.getPlayers().get(1).getBoard().getStudentsTable().get(Color.PINK);
        int lobbySizeP0 = game.getPlayers().get(0).getBoard().getLobby().size();
        int lobbySizeP1 = game.getPlayers().get(1).getBoard().getLobby().size();
        game.moveStudentFromLobbyForCard2(0,1,-1);
        game.moveStudentFromLobbyForCard2(1,1,-1);
        assertEquals(tableSizeP0+1, game.getPlayers().get(0).getBoard().getStudentsTable().get(Color.BLUE));
        assertEquals(tableSizeP1+1, game.getPlayers().get(1).getBoard().getStudentsTable().get(Color.PINK));
        assertEquals(lobbySizeP0-1, game.getPlayers().get(0).getBoard().getLobby().size());
        assertEquals(lobbySizeP1-1, game.getPlayers().get(1).getBoard().getLobby().size());
    }

    @Test
    void modifiedTeacherMovementTest() {
        ExpertGame game = new ExpertGame();
        game.addPlayer(new Player(0, "leo", Tower.BLACK));
        game.addPlayer(new Player(1, "mari", Tower.WHITE));
        game.instantiateGameElements();
        for (int i = 0; i < 7; i++){
            game.getPlayerByName("leo").getBoard().addToTable(Color.BLUE);
            game.getPlayerByName("mari").getBoard().addToTable(Color.BLUE);
        }
        game.getPlayerByName("leo").getBoard().addToTable(Color.PINK);
        game.getPlayerByName("leo").getBoard().addToTable(Color.RED);
        game.getPlayerByName("mari").getBoard().addToTable(Color.RED);
        game.getPlayerByName("mari").getBoard().addToTable(Color.YELLOW);
        game.getPlayerByName("mari").getBoard().addToTable(Color.GREEN);

        game.updateTeachersOwnership(game.getPlayerByName("mari"));
        assertTrue(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.BLUE));
        assertFalse(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.PINK));
        assertTrue(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.GREEN));
        assertTrue(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.YELLOW));
        assertTrue(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.RED));
        game.updateTeachersOwnershipForCard2(game.getPlayerByName("leo"));
        assertTrue(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.BLUE));
        assertTrue(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.PINK));
        assertFalse(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.GREEN));
        assertFalse(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.YELLOW));
        assertFalse(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.RED));
        assertTrue(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.RED));

        game.getPlayerByName("mari").getBoard().addToTable(Color.RED);
        game.updateTeachersOwnershipForCard2(game.getPlayerByName("mari"));
        assertTrue(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.RED));
        assertFalse(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.RED));
        assertTrue(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.BLUE));
        assertTrue(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.PINK));
        assertTrue(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.GREEN));
        assertTrue(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.YELLOW));

    }

    @Test
    void moveMotherNatureForCard4() {
    }

    @Test
    void calculateInfluenceForCard6() {
    }

    @Test
    void calculateInfluenceForCard8() {
    }

    //va sistemato il calcolo dell'influenza
    //ci sono problemi se l'isola non ha pedine e quindi non ha proprietari
    @Test
    void calculateInfluenceForCard9() {

        /*
        ExpertGame game = new ExpertGame();
        game.addPlayer(new Player(0,"mari",Tower.BLACK));
        game.addPlayer(new Player(1,"frizio",Tower.WHITE));
        game.instantiateGameElements();
        game.getPlayerByName("frizio").getBoard().addTeacher(Color.BLUE);
        game.getPlayerByName("frizio").getBoard().addTeacher(Color.PINK);
        game.getPlayerByName("frizio").getBoard().addTeacher(Color.RED);
        game.getPlayerByName("frizio").getBoard().addTeacher(Color.YELLOW);
        game.getPlayerByName("mari").getBoard().addTeacher(Color.GREEN);
        assertNotEquals(0,game.calculateInfluenceForCard9(game.getIslands().get(0), Color.GREEN).get("ID Player"));
*/

    }
}