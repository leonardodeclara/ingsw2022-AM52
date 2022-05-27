package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;
import org.junit.jupiter.api.Test;

import java.io.Console;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;


class ExpertGameTest {

    @Test
    void instantiateGameElementsTest() {
        ExpertGame game = new ExpertGame(3);
        ArrayList<String> players = new ArrayList<>();
        players.add("mari");
        players.add("leo");
        players.add("frizio");
        game.instantiateGameElements(players);
        assertEquals(17,game.getCoins());
    }

    @Test
    void personalityExtractionTest(){
        ExpertGame game = new ExpertGame(3);
        ArrayList<String> players = new ArrayList<>();
        players.add("mari");
        players.add("leo");
        players.add("frizio");
        game.instantiateGameElements(players);
        //game.extractPersonalityCards();
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

    /**
     * TODO: moveStudentFromLobbyForCard2 va modificato. Andranno modificati correttamente anche i test
     * IN TEORIA QUESTO METODO NON ESISTE PIÙ
     */
    /**
    @Test
    void moveStudentFromLobbyForCard2() {
        ExpertGame game = new ExpertGame(2);
        game.addPlayer(new Player(0,"leo",Tower.WHITE),0);
        game.addPlayer(new Player(1,"frizio",Tower.BLACK),1);
        game.instantiateGameElements();
        for (int i = 0; i<7; i++){
            game.getPlayers().get(0).addToBoardLobby(Color.BLUE);
            game.getPlayers().get(1).addToBoardLobby(Color.PINK);
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
     */

    @Test
    void modifiedTeacherMovementTest() {
        ExpertGame game = new ExpertGame(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("mari");
        players.add("leo");
        game.instantiateGameElements(players);
        for (int i = 0; i < 7; i++){
            game.getPlayerByName("leo").addToBoardTable(Color.BLUE);
            game.getPlayerByName("mari").addToBoardTable(Color.BLUE);
        }
        game.getPlayerByName("leo").addToBoardTable(Color.PINK);
        game.getPlayerByName("leo").addToBoardTable(Color.RED);
        game.getPlayerByName("mari").addToBoardTable(Color.RED);
        game.getPlayerByName("mari").addToBoardTable(Color.YELLOW);
        game.getPlayerByName("mari").addToBoardTable(Color.GREEN);

        game.updateTeachersOwnership("mari");
        assertTrue(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.BLUE));
        assertFalse(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.PINK));
        assertTrue(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.GREEN));
        assertTrue(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.YELLOW));
        assertTrue(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.RED));
        game.updateTeachersOwnershipForCard2("leo");
        assertTrue(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.BLUE));
        assertTrue(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.PINK));
        assertFalse(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.GREEN));
        assertFalse(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.YELLOW));
        assertFalse(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.RED));
        assertTrue(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.RED));

        game.getPlayerByName("mari").addToBoardTable(Color.RED);
        game.updateTeachersOwnershipForCard2("mari");
        assertTrue(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.RED));
        assertFalse(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.RED));
        assertTrue(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.BLUE));
        assertTrue(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.PINK));
        assertTrue(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.GREEN));
        assertTrue(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.YELLOW));
    }

    @Test
    void moveMotherNatureForCard4() {
        ExpertGame game = new ExpertGame(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("mari");
        players.add("leo");
        game.instantiateGameElements(players);
        game.giveAssistantDeck("mari",0);
        game.giveAssistantDeck("leo",2);
        game.playAssistantCard("leo",10);
        Island oldMNIsland = game.currentMotherNatureIsland;
        Island newMNIsland = game.islands.get((game.islands.indexOf(oldMNIsland) + 3)%game.islands.size());
        assertTrue(oldMNIsland.isMotherNature());
        assertFalse(newMNIsland.isMotherNature());
        game.moveMotherNatureForCard4("leo",3);
        assertFalse(oldMNIsland.isMotherNature());
        assertTrue(newMNIsland.isMotherNature());
        assertEquals(game.getIslands().get(newMNIsland.getIslandIndex()), game.currentMotherNatureIsland);
    }

    @Test
    void calculateInfluenceForCard6() {
        ExpertGame game = new ExpertGame(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("mari");
        players.add("frizio");
        game.instantiateGameElements(players);
        game.getPlayerByName("mari").setTeam(Tower.GREY);
        game.getPlayerByName("frizio").setTeam(Tower.WHITE);
        int oppositeToMN = 0;
        for (int i = 0; i< 12;i++){
            if(game.getIslands().get(i).isMotherNature())
                oppositeToMN=(i+6)%12;
        }
        HashMap<String,String> result;
        result=game.calculateInfluenceForCard6(game.getIslands().get(oppositeToMN));
        assertNull(result.get("Player Name"));
        assertEquals(Constants.DRAW,result.get("Is Draw"));
        game.getPlayerByName("frizio").addTeacherToBoard(Color.BLUE);
        game.getIslands().get(oppositeToMN).addStudent(Color.BLUE);
        result=game.calculateInfluenceForCard6(game.getIslands().get(oppositeToMN));
        assertEquals("frizio",result.get("Player Name"));
        assertEquals(Constants.NO_DRAW,result.get("Is Draw"));
        game.getIslands().get(oppositeToMN).addTower(game.getPlayerByName("frizio").getTeam());
        result=game.calculateInfluenceForCard6(game.getIslands().get(oppositeToMN));
        assertEquals("frizio",result.get("Player Name"));
        assertEquals(Constants.NO_DRAW,result.get("Is Draw"));
        game.getPlayerByName("mari").addTeacherToBoard(Color.GREEN);
        game.getIslands().get(oppositeToMN).addStudent(Color.GREEN);
        result=game.calculateInfluenceForCard6(game.getIslands().get(oppositeToMN));
        assertEquals("frizio",result.get("Player Name"));
        assertEquals(Constants.DRAW,result.get("Is Draw"));
        game.getIslands().get(oppositeToMN).addStudent(Color.GREEN);
        result=game.calculateInfluenceForCard6(game.getIslands().get(oppositeToMN));
        assertEquals("mari",result.get("Player Name"));
        assertEquals(Constants.NO_DRAW,result.get("Is Draw"));
    }

    @Test
    void card8EffectTest() {
        ExpertGame game = new ExpertGame(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("leo");
        players.add("mari");
        game.instantiateGameElements(players);
        game.getPlayerByName("leo").setTeam(Tower.GREY);
        game.getPlayerByName("mari").setTeam(Tower.BLACK);
        int mnPosition = 0;
        for (int i = 0; i< 12;i++){
            if(game.getIslands().get(i).isMotherNature())
                mnPosition=i;
        }
        game.setCurrentPlayer("mari");
        //testo il calcolo dell'influenza su due isole vuote
        HashMap<String,String> result=game.calculateInfluenceForCard8(game.getIslands().get(mnPosition));
        assertEquals("mari",result.get("Player Name")); //mari 3 (torre + 2 bonus)
        assertEquals(Constants.NO_DRAW,result.get("Is Draw"));
        //riempio le isole e ricalcolo l'influenza
        game.getIslands().get(mnPosition).addStudent(Color.RED);
        game.getIslands().get(mnPosition).addStudent(Color.PINK);
        game.getIslands().get(mnPosition).addStudent(Color.YELLOW);
        game.getPlayerByName("leo").addTeacherToBoard(Color.RED);
        game.getPlayerByName("mari").addTeacherToBoard(Color.PINK);
        result=game.calculateInfluenceForCard8(game.getIslands().get(mnPosition)); //mari 4 - leo 1
        assertEquals("mari",result.get("Player Name"));
        assertEquals(Constants.NO_DRAW,result.get("Is Draw"));
        game.getPlayerByName("leo").addTeacherToBoard(Color.YELLOW);
        result=game.calculateInfluenceForCard8(game.getIslands().get(mnPosition));//mari 4 - leo 2
        assertEquals("mari",result.get("Player Name"));
        assertEquals(Constants.NO_DRAW,result.get("Is Draw"));
        game.getIslands().get(mnPosition).addStudent(Color.YELLOW); //mari 4 - leo 3
        result=game.calculateInfluenceForCard8(game.getIslands().get(mnPosition));
        assertEquals("mari",result.get("Player Name"));
        assertEquals(Constants.NO_DRAW,result.get("Is Draw"));
        game.getIslands().get(mnPosition).addStudent(Color.RED);//mari 4 - leo 4
        result=game.calculateInfluenceForCard8(game.getIslands().get(mnPosition));
        assertEquals("mari",result.get("Player Name"));
        assertEquals(Constants.DRAW, result.get("Is Draw"));
    }

    @Test
    void card8EffectWithTowersTest() {
        ExpertGame game = new ExpertGame(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("mari");
        players.add("frizio");
        game.instantiateGameElements(players);
        game.getPlayerByName("mari").setTeam(Tower.GREY);
        game.getPlayerByName("frizio").setTeam(Tower.WHITE);
        int mnPosition = 0;
        for (int i = 0; i< 12;i++){
            if(game.getIslands().get(i).isMotherNature())
                mnPosition=i;
        }
        game.setCurrentPlayer("frizio");
        game.getIslands().get(mnPosition).setOwner(game.getPlayerByName("frizio"));
        game.getIslands().get(mnPosition).addTower(Tower.WHITE);
        game.getIslands().get(mnPosition).addStudent(Color.PINK);
        game.getIslands().get(mnPosition).addStudent(Color.GREEN);
        game.getPlayerByName("mari").getBoard().addTeacher(Color.PINK);
        game.getPlayerByName("mari").getBoard().addTeacher(Color.GREEN);
        HashMap<String,String> result=game.calculateInfluenceForCard8(game.getIslands().get(mnPosition));
        assertEquals("frizio",result.get("Player Name"));
        assertEquals(Constants.NO_DRAW,result.get("Is Draw"));
        game.getIslands().get(mnPosition).addStudent(Color.GREEN);
        result=game.calculateInfluenceForCard8(game.getIslands().get(mnPosition));
        assertEquals("frizio",result.get("Player Name"));
        assertEquals(Constants.DRAW,result.get("Is Draw"));
        game.getIslands().get(mnPosition).addStudent(Color.GREEN);
        result=game.calculateInfluenceForCard8(game.getIslands().get(mnPosition));
        assertEquals("mari",result.get("Player Name"));
        assertEquals(Constants.NO_DRAW,result.get("Is Draw"));
    }

    /**
     * TODO: nei test commentati va modificata l'istanziazione degli elements, l'aggiunta dei player e nelle carte che giocano con l'influenza anche il confronto dei risultati dell'influenza
     */
    //effetto: scelto un colore, quel colore non viene considerato nel calcolo dell'influenza
    //

    @Test
    void card9EffectTest() {
        ExpertGame game = new ExpertGame(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("mari");
        players.add("frizio");
        game.instantiateGameElements(players);
        game.getPlayerByName("mari").setTeam(Tower.BLACK);
        game.getPlayerByName("frizio").setTeam(Tower.WHITE);
        int mnPosition = 0;
        for (int i = 0; i< 12;i++){
            if(game.getIslands().get(i).isMotherNature())
                mnPosition=i;
        }
        //testo il calcolo dell'influenza su due isole vuote
        game.setBannedColor(Color.GREEN);
        HashMap<String, String> result=game.calculateInfluenceForCard9(game.getIslands().get(mnPosition));
        assertNull(result.get("Player Name"));
        assertEquals( Constants.DRAW, result.get("Is Draw"));
        game.setBannedColor( Color.GREEN);
        result = game.calculateInfluenceForCard9(game.getIslands().get((mnPosition+6)%12));
        assertNull(result.get("Player Name"));
        assertEquals( Constants.DRAW,result.get("Is Draw"));//pareggio + proprietario era null, rimane null
        //riempio le isole e ricalcolo l'influenza
        game.getIslands().get(mnPosition).addStudent(Color.BLUE);
        game.getIslands().get(mnPosition).addStudent(Color.PINK);
        game.getIslands().get(mnPosition).addStudent(Color.YELLOW);
        game.getPlayerByName("frizio").addTeacherToBoard(Color.BLUE);
        game.getPlayerByName("mari").addTeacherToBoard(Color.PINK);
        game.setBannedColor(Color.BLUE);
        result = game.calculateInfluenceForCard9(game.getIslands().get(mnPosition));  //mari 1, frizio 0 ->mari mette una torre (2-0)
        assertEquals("mari",result.get("Player Name"));
        assertEquals( Constants.NO_DRAW, result.get("Is Draw"));
        game.getIslands().get(mnPosition).addStudent(Color.BLUE);
        game.setBannedColor(Color.PINK);
        result = game.calculateInfluenceForCard9(game.getIslands().get(mnPosition)); //mari 1, frizio 2 -> mari toglie una torre e la mette frizio (0-3)
        assertEquals("frizio",result.get("Player Name"));
        assertEquals( Constants.NO_DRAW, result.get("Is Draw"));
        game.getPlayerByName("frizio").addTeacherToBoard(Color.YELLOW);
        game.setBannedColor(Color.YELLOW);
        result = game.calculateInfluenceForCard9(game.getIslands().get(mnPosition));
        assertEquals("frizio",result.get("Player Name"));
        assertEquals( Constants.NO_DRAW, result.get("Is Draw"));
        //dovrei testare anche con le torri
    }

    @Test
    void card9EffectWithTowersTest() {
        ExpertGame game = new ExpertGame(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("leo");
        players.add("mari");
        game.instantiateGameElements(players);
        game.getPlayerByName("leo").setTeam(Tower.WHITE);
        game.getPlayerByName("mari").setTeam(Tower.GREY);

        int mnPosition = 0;
        for (int k = 0; k< 12;k++){
            if(game.getIslands().get(k).isMotherNature())
                mnPosition=k;
        }
        game.getIslands().get(mnPosition).setOwner(game.getPlayerByName("leo"));
        game.getIslands().get(mnPosition).addTower(Tower.WHITE);
        game.setBannedColor(Color.BLUE);
        HashMap<String,String> result=game.calculateInfluenceForCard9(game.getIslands().get(mnPosition));
        assertEquals("leo", result.get("Player Name"));
        assertEquals( Constants.NO_DRAW, result.get("Is Draw"));
        //riempio le isole e ricalcolo l'influenza
        game.getIslands().get(mnPosition).addStudent(Color.BLUE);
        game.getIslands().get(mnPosition).addStudent(Color.PINK);
        game.getIslands().get(mnPosition).addStudent(Color.YELLOW);
        game.getPlayerByName("leo").addTeacherToBoard(Color.BLUE);
        game.getPlayerByName("mari").addTeacherToBoard(Color.PINK);
        game.setBannedColor(Color.BLUE);
        result = game.calculateInfluenceForCard9(game.getIslands().get(mnPosition));
        assertEquals("leo",result.get("Player Name"));
        assertEquals( Constants.DRAW, result.get("Is Draw"));
        game.setBannedColor(Color.PINK);
        result = game.calculateInfluenceForCard9(game.getIslands().get(mnPosition));
        assertEquals("leo",result.get("Player Name"));
        assertEquals(Constants.NO_DRAW,result.get("Is Draw"));
    }


    @Test
    void personalityCardManagementTest(){
        ExpertGame game = new ExpertGame(3);
        ArrayList<String> players = new ArrayList<>();
        players.add("leo");
        players.add("mari");
        players.add("frizio");
        game.instantiateGameElements(players);
        game.setCurrentPlayer("leo");
        game.getPlayerByName("leo").setCoins(20);
        ArrayList<Personality> playableCards= game.getPersonalities();
        int idOfPlayedCard= playableCards.get(0).getCharacterId();
        assertTrue(game.setActivePersonality(idOfPlayedCard));
        assertEquals(idOfPlayedCard, game.getActivePersonality().getCharacterId());
        assertEquals(2, game.getPersonalities().size());
    }

    @Test
    void invalidPersonalitySelectionTest(){
        ExpertGame game = new ExpertGame(3);
        ArrayList<String> players = new ArrayList<>();
        players.add("leo");
        players.add("mari");
        players.add("frizio");

        game.instantiateGameElements(players);

        game.setCurrentPlayer("leo");
        game.getPlayerByName("leo").setCoins(20);
        assertFalse(game.setActivePersonality(1));
        assertTrue(game.setActivePersonality(game.getPersonalities().get(0).getCharacterId()));
        assertFalse(game.setActivePersonality(game.getPersonalities().get(0).getCharacterId()));
    }

    @Test
    void personalityResetTest(){
        ExpertGame game = new ExpertGame(3);
        ArrayList<String> players = new ArrayList<>();
        players.add("leo");
        players.add("mari");
        players.add("frizio");
        game.instantiateGameElements(players);
        ArrayList<Personality> playableCards = game.getPersonalities();
        playableCards.get(0).setHasBeenUsed(true);
        playableCards.get(0).updateCost();
        game.setCurrentPlayer("leo");
        game.getPlayerByName("leo").setCoins(20);
        assertTrue(game.setActivePersonality(game.getPersonalities().get(0).getCharacterId()));
        game.resetActivePersonality();
        assertEquals(3, game.getPersonalities().size());
        for (Personality personality: playableCards){
            assertTrue(game.getPersonalities().contains(personality));
        }
        assertNull(game.getActivePersonality());
    }

    @Test
    void card1EffectsTest(){
        ExpertGame game = new ExpertGame(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("leo");
        players.add("mari");
        game.instantiateGameElements(players);
        game.setCurrentPlayer("leo");
        ArrayList<Integer> selectedPersonalitiesIds= new ArrayList<>();
        for (Personality personality: game.getPersonalities())
            selectedPersonalitiesIds.add(personality.getCharacterId());

        if (!selectedPersonalitiesIds.contains(1)){
            game.getPersonalities().add(new LobbyPersonality(1));
            for (int i = 0; i<4;i++)
                ((LobbyPersonality) game.getPersonalities().get(3)).addStudent(game.getBasket().pickStudent());
                //se non era già tra le carte pescate allora sarà la quarta dell'arraylist
        }
        assertTrue(game.setActivePersonality(1));
        Color selectedStudent = ((LobbyPersonality) game.getActivePersonality()).getStudent(0);
        int oldIslandStudents = game.getIslandById(1).getStudents().size();
        assertTrue(game.executeCard1Effect(0,1));
        assertEquals(4, ((LobbyPersonality) game.getActivePersonality()).getStudents().size());
        assertEquals(oldIslandStudents+1,game.getIslandById(1).getStudents().size());
        assertEquals(selectedStudent, game.getIslandById(1).getStudents().get(oldIslandStudents));
        //si potrebbe controllare che il numero di pedine nel sacchetto sia diminuito di 1
        //testare casi limite
    }

    @Test
    void card5EffectTest(){
        ExpertGame game = new ExpertGame(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("leo");
        players.add("mari");
        game.instantiateGameElements(players);
        game.setCurrentPlayer("leo");
        game.getPlayerByName("leo").setCoins(10);
        ArrayList<Integer> selectedPersonalitiesIds= new ArrayList<>();
        for (Personality personality: game.getPersonalities())
            selectedPersonalitiesIds.add(personality.getCharacterId());

        if (!selectedPersonalitiesIds.contains(5)){
            game.getPersonalities().add(new BanPersonality(5));
        }
        assertTrue(game.setActivePersonality(5));
        assertTrue(game.executeCard5Effect(1));
        assertEquals(1,game.getIslandById(1).getBans());
        assertEquals(3,((BanPersonality)game.getActivePersonality()).getBans());
        //testare casi limite
    }

    @Test
    void card12EffectTest(){
        ExpertGame game = new ExpertGame(3);
        ArrayList<String> players = new ArrayList<>();
        players.add("leo");
        players.add("mari");
        players.add("frizio");
        game.instantiateGameElements(players);
        game.setCurrentPlayer("leo");
        game.getPlayerByName("leo").setCoins(10);
        ArrayList<Integer> selectedPersonalitiesIds= new ArrayList<>();
        for (Personality personality: game.getPersonalities())
            selectedPersonalitiesIds.add(personality.getCharacterId());

        if (!selectedPersonalitiesIds.contains(12)){
            game.getPersonalities().add(new Personality(12));
        }
        assertTrue(game.setActivePersonality(12));
        for (int i = 0; i<3;i++){
            game.getPlayerByName("leo").addToBoardTable(Color.RED);
            game.getPlayerByName("mari").addToBoardTable(Color.RED);
            game.getPlayerByName("frizio").addToBoardTable(Color.RED);
        }
        game.getPlayerByName("leo").addToBoardTable(Color.RED);
        game.getPlayerByName("mari").addToBoardTable(Color.RED);
        assertEquals(4, game.getPlayerByName("leo").getBoard().getTableNumberOfStudents(Color.RED));
        assertEquals(4, game.getPlayerByName("mari").getBoard().getTableNumberOfStudents(Color.RED));
        assertEquals(3, game.getPlayerByName("frizio").getBoard().getTableNumberOfStudents(Color.RED));
        game.executeCard12Effect(Color.RED);
        assertEquals(1, game.getPlayerByName("leo").getBoard().getTableNumberOfStudents(Color.RED));
        assertEquals(1, game.getPlayerByName("mari").getBoard().getTableNumberOfStudents(Color.RED));
        assertEquals(0, game.getPlayerByName("frizio").getBoard().getTableNumberOfStudents(Color.RED));
        //testare casi limite
    }

    @Test
    void card11EffectTest(){
        ExpertGame game = new ExpertGame(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("leo");
        players.add("frizio");
        game.instantiateGameElements(players);
        game.setCurrentPlayer("leo");
        game.getPlayerByName("leo").setCoins(10);
        ArrayList<Integer> selectedPersonalitiesIds= new ArrayList<>();
        for (Personality personality: game.getPersonalities())
            selectedPersonalitiesIds.add(personality.getCharacterId());

        if (!selectedPersonalitiesIds.contains(12)){
            game.getPersonalities().add(new LobbyPersonality(11));
            for (int i = 0; i<4;i++)
                ((LobbyPersonality) game.getPersonalities().get(3)).addStudent(game.getBasket().pickStudent());
                //se non era già tra le carte pescate allora sarà la quarta dell'arraylist
        }
        assertTrue(game.setActivePersonality(11));
        Color selectedStudent = ((LobbyPersonality) game.getActivePersonality()).getStudent(3);
        int oldTableSize = game.getPlayerByName("leo").getBoard().getTableNumberOfStudents(selectedStudent);
        assertFalse(game.executeCard11Effect(5));
        assertTrue(game.executeCard11Effect(3));
        assertEquals(oldTableSize+1, game.getPlayerByName("leo").getBoard().getTableNumberOfStudents(selectedStudent));
        assertEquals(4, ((LobbyPersonality) game.getActivePersonality()).getStudents().size());
        //si potrebbe controllare che il numero di pedine nel sacchetto sia diminuito di 1
        //testare casi limite
    }

    //TODO: mancano da testare: 3,7,10

}