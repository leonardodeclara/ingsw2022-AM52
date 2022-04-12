package it.polimi.ingsw.model;

import it.polimi.ingsw.model.ExpertGame;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

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
        ExpertGame game = new ExpertGame();
        game.addPlayer(new Player(0,"leo",Tower.GREY));
        game.addPlayer(new Player(1,"mari",Tower.BLACK));
        game.instantiateGameElements();
        int mnPosition = 0;
        for (int i = 0; i< 12;i++){
            if(game.getIslands().get(i).isMotherNature())
                mnPosition=i;
        }
        game.setCurrentPlayer(game.getPlayerByName("mari"));
        //testo il calcolo dell'influenza su due isole vuote
        HashMap<String,Number> result=game.calculateInfluenceForCard8(game.getIslands().get(mnPosition));
        assertEquals(1,result.get("ID Player"));
        assertEquals((short) 0, result.get("Is Draw"));
        //riempio le isole e ricalcolo l'influenza
        game.getIslands().get(mnPosition).addStudent(Color.RED);
        game.getIslands().get(mnPosition).addStudent(Color.PINK);
        game.getIslands().get(mnPosition).addStudent(Color.YELLOW);
        game.getPlayerByName("leo").getBoard().addTeacher(Color.RED);
        game.getPlayerByName("mari").getBoard().addTeacher(Color.PINK);
        result=game.calculateInfluenceForCard8(game.getIslands().get(mnPosition));
        assertEquals(1,result.get("ID Player"));
        assertEquals((short) 0, result.get("Is Draw"));
        game.getPlayerByName("leo").getBoard().addTeacher(Color.YELLOW);
        result=game.calculateInfluenceForCard8(game.getIslands().get(mnPosition));
        assertEquals(1,result.get("ID Player"));
        assertEquals((short) 0, result.get("Is Draw"));
        game.getIslands().get(mnPosition).addStudent(Color.YELLOW);
        result=game.calculateInfluenceForCard8(game.getIslands().get(mnPosition));
        assertEquals(1,result.get("ID Player"));
        assertEquals((short) 1, result.get("Is Draw"));
        game.getIslands().get(mnPosition).addStudent(Color.RED);
        result=game.calculateInfluenceForCard8(game.getIslands().get(mnPosition));
        assertEquals(0,result.get("ID Player"));
        assertEquals((short) 0, result.get("Is Draw"));
        //dovrei testare anche con le torri
    }


    //effetto: scelto un colore, quel colore non viene considerato nel calcolo dell'influenza
    //
    @Test
    void card9EffectTest() {
        ExpertGame game = new ExpertGame();
        game.addPlayer(new Player(0,"mari",Tower.BLACK));
        game.addPlayer(new Player(1,"frizio",Tower.WHITE));
        game.instantiateGameElements();
        int mnPosition = 0;
        for (int i = 0; i< 12;i++){
            if(game.getIslands().get(i).isMotherNature())
                mnPosition=i;
        }
        //testo il calcolo dell'influenza su due isole vuote
        HashMap<String,Number> result=game.calculateInfluenceForCard9(game.getIslands().get(mnPosition), Color.GREEN);
        assertNull(result.get("ID Player"));
        assertEquals((short) 1, result.get("Is Draw"));
        result = game.calculateInfluenceForCard9(game.getIslands().get((mnPosition+6)%12), Color.GREEN);
        assertNull(result.get("ID Player"));
        assertEquals((short) 1,result.get("Is Draw"));//pareggio + proprietario era null, rimane null
        //riempio le isole e ricalcolo l'influenza
        game.getIslands().get(mnPosition).addStudent(Color.BLUE);
        game.getIslands().get(mnPosition).addStudent(Color.PINK);
        game.getIslands().get(mnPosition).addStudent(Color.YELLOW);
        game.getPlayerByName("frizio").getBoard().addTeacher(Color.BLUE);
        game.getPlayerByName("mari").getBoard().addTeacher(Color.PINK);
        result = game.calculateInfluenceForCard9(game.getIslands().get(mnPosition), Color.BLUE);
        assertEquals(0,result.get("ID Player"));
        assertEquals((short) 0, result.get("Is Draw"));
        result = game.calculateInfluenceForCard9(game.getIslands().get(mnPosition), Color.PINK);
        assertEquals(1,result.get("ID Player"));
        assertEquals((short) 0, result.get("Is Draw"));
        game.getPlayerByName("frizio").getBoard().addTeacher(Color.YELLOW);
        result = game.calculateInfluenceForCard9(game.getIslands().get(mnPosition), Color.YELLOW);
        assertEquals(1,result.get("ID Player"));
        assertEquals((short) 1, result.get("Is Draw")); //pareggio, resta proprietario il precedente proprietario
        //dovrei testare anche con le torri
    }

}