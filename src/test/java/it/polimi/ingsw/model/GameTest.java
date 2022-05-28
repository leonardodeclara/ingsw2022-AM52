package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.exceptions.EmptyBasketException;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;



@SuppressWarnings("ALL")



class GameTest {

    Game gameSetup(int playersNumber){
        Game game = new Game(playersNumber);
        ArrayList<String> players = new ArrayList<>();
        players.add("leo");
        players.add("mari");
        if (playersNumber==3)
            players.add("frizio");
        game.instantiateGameElements(players);
        return game;
    }
    /**
     * This method aims at verifying the correct insertion of players to the game.
     */
    @Test
    void addPlayerTest(){
        Game game = new Game(3);
        ArrayList<String> players = new ArrayList<>();
        players.add("mari");
        players.add("leo");
        players.add("frizio");
        game.addPlayers(players);
        assertEquals(3, game.getPlayers().size());
        assertThrows(Exception.class, ()->game.addPlayers(players));
    }

    /**
     * teacherInstantiationTest method verifies that five different teachers are correctly instantiated and added
     * to game's teachers attribute.
     */
    @Test
    void teacherInstantiationTest() {
        Game game = new Game(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("mari");
        players.add("leo");
        players.add("frizio");
        game.instantiateGameElements(players);
        assertEquals(5, game.getTeachers().size());
        for(int i = 0; i< game.getTeachers().size()-1; i++){
            for(int j=i+1;j<game.getTeachers().size();j++){
                assertTrue(game.getTeachers().get(i)!=game.getTeachers().get(j));
            }
        }
        for (int i = 0; i< Color.values().length; i++)
            assertNull(game.getTeachersOwners().get(Color.values()[i]));
    }

    /**
     * islandInstantiationTest examines the correct indexing of the newly created island tiles.
     */
    @Test
    void islandInstantiationTest(){
        Game game = new Game(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("mari");
        players.add("leo");
        game.instantiateGameElements(players);
        assertEquals(12, game.getIslands().size());
        for(int i = 0; i< game.getIslands().size()-1; i++){
            for(int j=i+1;j<game.getIslands().size();j++){
                assertTrue(game.getIslands().get(i).getIslandIndex()!=game.getIslands().get(j).getIslandIndex());
            }
        }
    }

    /**
     * This method is responsible for checking the instantiation of a single mother nature
     * at the start of the game. It also checks the absence of students in mother nature's tile.
     */
    @Test
    void motherNatureInstantiationTest(){
        Game game = new Game(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("mari");
        players.add("leo");
        game.instantiateGameElements(players);
        int countMotherNature=0;
        int indexMN=0;
        for (int i = 0; i< 12; i++ ){
            if(game.getIslands().get(i).isMotherNature()){
                countMotherNature+=1;
                indexMN=i;
            }
        }
        assertEquals(1,countMotherNature);
        assertEquals(0,game.getIslands().get(indexMN).getStudents().size());
        assertEquals(0,game.getIslands().get((indexMN+6)%12).getStudents().size());
    }

    /**
     * Method cloudInstantiationTest verifies the correct instantiation of the newly created cloud tiles
     */
    @Test
    void cloudInstantiationTest(){
        Game game = new Game(3);
        ArrayList<String> players = new ArrayList<>();
        players.add("mari");
        players.add("leo");
        players.add("frizio");
        game.instantiateGameElements(players);
        assertEquals(3, game.getClouds().size());
    }

    /**
     * Method deckInstantiationTest verifies the correct instantiation of the Assistant card deck
     */
    @Test
    void deckInstantiationTest() {
        Game game = new Game(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("mari");
        players.add("frizio");
        game.instantiateGameElements(players);
        assertEquals(40, game.getAssistantDecks().size());
        int[] deckSize={0,0,0,0};
        for (int i =0; i<40; i++){
            deckSize[game.getAssistantDecks().get(i).getWizard()]+=1;
        }
        int[] expectedDeckSize={10,10,10,10};
        for(int k=0; k<4;k++)
            assertEquals(expectedDeckSize[k],deckSize[k]);
    }

    /**
     * This method verifies that the island opposite to mother nature's one
     * is the one and only which receives no student at the beginning of the game.
     */
    @Test
    void fillIslandTest(){
        Game game = new Game(2);
        Basket basket = new Basket(new int[]{2, 2, 2, 2, 2});
        game.setBasket(basket);
        game.getIslands().get(11).setMotherNature(true);
        game.setCurrentMotherNatureIsland(game.getIslands().get(11));
        game.fillIslands();

        int noStudentIsland = 0;
        int countNoStudentIslands=0;
        for (int i = 0; i< game.getIslands().size()-1; i++ ){
            if(game.getIslands().get(i).getStudents().size()==0 && !game.getIslands().get(i).isMotherNature()){
                noStudentIsland=i;
                countNoStudentIslands+=1;
            }
        }
        assertEquals(5, noStudentIsland);
        assertEquals(1,countNoStudentIslands);
    }

    /**
     * This method aims at checking the correct insertion of students to a player's lobby.
     * It also checks that the basket is missing te correct amount of students tile.
     */
    @Test
    void initiatePlayerLobby(){
        Game game = new Game(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("mari");
        players.add("frizio");
        game.instantiateGameElements(players);
        assertEquals(7, game.getPlayerByName("mari").getBoard().getLobby().size());
        assertEquals(7, game.getPlayerByName("frizio").getBoard().getLobby().size());
        //assertEquals(106, game.getBasket().getSize());
        /**
         * TODO: riattivare assert quando è terminato il testing delle condizioni di fine partita
         */
    }

    /**
     * This method verifies that a player receives the proper deck of assistant cards,
     * which must also be removed from the assistantDecks attribute of game.
     */
    @Test
    void giveAssistantDeck() {
        Game game = gameSetup(2);
        game.giveAssistantDeck("leo", 1);
        assertEquals(10, game.getPlayerByName("leo").getDeck().size());
        for (int i = 0; i<9; i++){
            for (int j = i+1; j < 10; j++){
                assertTrue(!game.getPlayerByName("leo").getDeck().get(i).equals(game.getPlayerByName("Leo").getDeck().get(j)));
            }
        }
        for (int i = 0; i<10; i++){
            assertEquals(1,game.getPlayerByName("leo").getDeck().get(i).getWizard());
            assertEquals(i+1,game.getPlayerByName("leo").getDeck().get(i).getPriority());
        }
    }

    /**
     * refillCloudsTest method is responsible for verifying that clouds are rightly
     * filled with students according to the number of players.
     */
    @Test
    void refillTwoCloudsTest(){
        Game game = gameSetup(2);
        game.refillClouds();
        for (int i = 0; i< 2; i++){
            assertEquals(3,game.getClouds().get(i).getStudents().size());
        }
    }

    /**
     * playAssistantCard method is responsible for verifying that card are removed from player's deck and
     * added to the game current turn deck
     */
    @Test
    void playAssistantCard(){
        Game game = gameSetup(2);
        game.giveAssistantDeck("leo", 1);
        game.giveAssistantDeck("mari", 2);

        Assistant a0 = new Assistant(2,4,1);
        Assistant a1 = new Assistant(4,8,2);

        game.playAssistantCard("leo",4); //giochiamo a2
        game.playAssistantCard("mari",8); //giochiamo a1
        //ci si assicura che a1,a2 siano stati rimossi dai rispettivi deck
        assertEquals(9,game.getPlayerByName("leo").getDeck().size() );
        assertEquals(9,game.getPlayerByName("mari").getDeck().size() );
        assertEquals(false,game.getPlayerByName("leo").getDeck().contains(a0));
        assertEquals(false,game.getPlayerByName("mari").getDeck().contains(a1));
        //Ci si assicura che a1,a2 ora siano nelle celle dell'hashmap corrispondenti agli id dei giocatori che le hanno giocate
        assertEquals(a0.getPriority(),game.getCurrentTurnAssistantCards().get("leo").getPriority());
        assertEquals(a1.getPriority(),game.getCurrentTurnAssistantCards().get("mari").getPriority());
    }

    @Test
    void lastAssistantCardTest(){
        Game game = new Game(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("leo");
        players.add("mari");
        game.instantiateGameElements(players);
        game.getPlayerByName("leo").setTeam(Tower.WHITE);
        game.getPlayerByName("mari").setTeam(Tower.BLACK);
        ArrayList<Assistant> deck = new ArrayList<>();
        deck.add(new Assistant(1,1,1));
        game.getPlayerByName("leo").setDeck(deck);
        game.playAssistantCard("leo", 1);
        assertTrue(game.isLastRound());
        assertEquals(0, game.getPlayerByName("leo").getDeck().size());
    }

    /**
     * Method isMoveMNLegal is responsible for verifying that Mother Nature moves are legal according to
     * the numMoves written on the Assistant card played
     */
    @Test
    void isMoveMNLegal(){
        Game game = gameSetup(2);
        game.giveAssistantDeck("leo", 0);
        game.giveAssistantDeck("mari", 1);
        game.playAssistantCard("leo",4); //giochiamo a0
        game.playAssistantCard("mari",8); //giochaimo a1
        assertEquals(false,game.isMoveMNLegal("leo",25));
        assertEquals(true,game.isMoveMNLegal("leo",2));
        assertEquals(false,game.isMoveMNLegal("mari",6));
        assertEquals(true,game.isMoveMNLegal("mari",4));
    }

    /**
     * Method isMoveStudentFromLobbyLegalTest verifies that method isMoveStudentFromLobby returns the
     * correct boolean values according to the availability of places in the board's table
     */
    @Test
    void isMoveStudentFromLobbyLegalTest(){
        Game game = gameSetup(2);

        for(int i = 0;i<7;i++)
            game.getPlayerByName("leo").addToBoardLobby(Color.RED);
        for(int i = 0;i<7;i++)
            game.getPlayerByName("mari").addToBoardLobby(Color.YELLOW);

        for(int i = 0;i<10;i++) //riempie completamente la table rossa di "leo"
            game.getPlayerByName("leo").addToBoardTable(Color.RED);
        for(int i = 0;i<10;i++) //riempie completamente la table blu di "mari"
            game.getPlayerByName("mari").addToBoardTable(Color.BLUE);

        assertTrue(game.isMoveStudentFromLobbyLegal(game.getPlayerByName("leo"), 7, 3,null));
        assertFalse(game.isMoveStudentFromLobbyLegal(game.getPlayerByName("leo"), 7, -1,null));
        assertTrue(game.isMoveStudentFromLobbyLegal(game.getPlayerByName("leo"), 6, 1,null));
        assertTrue(game.isMoveStudentFromLobbyLegal(game.getPlayerByName("mari"), 7, 6,null));
        assertTrue(game.isMoveStudentFromLobbyLegal(game.getPlayerByName("mari"), 6, 6,null));
        assertFalse(game.isMoveStudentFromLobbyLegal(game.getPlayerByName("mari"), 0, 23,null));
        assertFalse(game.isMoveStudentFromLobbyLegal(game.getPlayerByName("mari"),-1,10,null));
    }

    /**
     * Method isMoveStudentsToLobbyLegal verifies if method isMoveStudentsToLobbyLegal returns the correct
     * boolean value according to the correct cloudID
     */
    @Test
    void isMoveStudentsToLobbyLegal(){
        Game game = gameSetup(2);
        game.refillClouds();
        assertFalse(game.isMoveStudentsToLobbyLegal(game.getPlayers().get(0), -25));
        assertTrue(game.isMoveStudentsToLobbyLegal(game.getPlayers().get(0), 1));
        assertFalse(game.isMoveStudentsToLobbyLegal(game.getPlayers().get(1), 2));
        assertTrue(game.isMoveStudentsToLobbyLegal(game.getPlayers().get(0), 1));
    }

    /**
     * This method verifies that method moveStudentFromLobby works corectly when I want to move a student to an island
     * according to the current Mother Nature position
     *
     */
    @Test
    void moveStudentFromLobbyToIslandTest(){
        Game game = gameSetup(2);

        //subtest per le isole
        Color s1 = game.getPlayerByName("leo").getBoard().getLobbyStudent(0);
        Color s2 = game.getPlayerByName("mari").getBoard().getLobbyStudent(0);
        int mnPosition = 0;
        for (Island island : game.getIslands()){
            if (island.isMotherNature())
                mnPosition= island.getIslandIndex();
        }

        ArrayList<Integer> studentsToMove = new ArrayList<>();
        studentsToMove.add(0);
        ArrayList<Integer> destinations = new ArrayList<>();
        destinations.add((mnPosition+1)%12);
        game.moveStudentsFromLobby("leo", studentsToMove,destinations);
        destinations.clear();
        destinations.add((mnPosition+2)%12);
        game.moveStudentsFromLobby("mari",studentsToMove, destinations);
        assertEquals(2, game.getIslands().get((mnPosition+1)%12).getStudents().size());
        assertEquals(2, game.getIslands().get((mnPosition+2)%12).getStudents().size());
        assertEquals(s1, game.getIslands().get((mnPosition+1)%12).getStudents().get(1));
        assertEquals(s2, game.getIslands().get((mnPosition+2)%12).getStudents().get(1));
    }


    /**
     * This method verifies that moveStudentFromLobby wors correctly when I want to move a student to the table
     * according to the availability of the places in the Table
     */
    @Test
    void moveStudentFromLobbyToTableTest(){
        Game game = gameSetup(2);

        Color s0 = game.getPlayers().get(0).getBoard().getLobbyStudent(1);
        int tableSizeP0 = game.getPlayers().get(0).getBoard().getStudentsTable().get(s0);
        Color s1 = game.getPlayers().get(1).getBoard().getLobbyStudent(1);
        int tableSizeP1 = game.getPlayers().get(1).getBoard().getStudentsTable().get(s1);
        int lobbySizeP0 = game.getPlayers().get(0).getBoard().getLobby().size();
        int lobbySizeP1 = game.getPlayers().get(1).getBoard().getLobby().size();
        ArrayList<Integer> studentsToMoveIndexes = new ArrayList<>();
        studentsToMoveIndexes.add(1);
        ArrayList<Integer> destinations = new ArrayList<>();
        destinations.add(-1);
        game.moveStudentsFromLobby("leo",studentsToMoveIndexes,destinations);
        game.moveStudentsFromLobby("mari",studentsToMoveIndexes,destinations);
        assertEquals(tableSizeP0+1, game.getPlayers().get(0).getBoard().getStudentsTable().get(s0));
        assertEquals(tableSizeP1+1, game.getPlayers().get(1).getBoard().getStudentsTable().get(s1));
        assertEquals(lobbySizeP0-1, game.getPlayers().get(0).getBoard().getLobby().size());
        assertEquals(lobbySizeP1-1, game.getPlayers().get(1).getBoard().getLobby().size());
    }

    @Test
    void studentsToFullTableTest(){
        Game game = new Game(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("leo");
        players.add("mari");
        game.instantiateGameElements(players);
        for (int i = 0; i<5;i++){
            game.getPlayerByName("leo").addToBoardTable(Color.RED);
        }
        ArrayList<Integer> destinations= new ArrayList<>();
        ArrayList<Integer> studentsIndexes= new ArrayList<>();
        for (int i = 0;i<3;i++){
            studentsIndexes.add(i+7); //seleziono i tre studenti extra aggiunti alla lobby
            destinations.add(Constants.ISLAND_ID_NOT_RECEIVED);
            game.getPlayerByName("leo").addToBoardLobby(Color.RED);
        }
        assertEquals(5, game.getPlayerByName("leo").getBoard().getTableNumberOfStudents(Color.RED));
        assertTrue(game.moveStudentsFromLobby("leo", studentsIndexes,destinations));
        assertEquals(8, game.getPlayerByName("leo").getBoard().getTableNumberOfStudents(Color.RED));
        for (int i = 0;i<3;i++){
            game.getPlayerByName("leo").addToBoardLobby(Color.RED);
        }
        assertFalse(game.moveStudentsFromLobby("leo", studentsIndexes,destinations));
        assertEquals(8, game.getPlayerByName("leo").getBoard().getTableNumberOfStudents(Color.RED));
    }


    /**
     * This method verifies the correct working of moveMotherNature method checking the old and the new position
     * of Mother Nature before and after the move
     */
    @Test
    void moveMNTest(){
        Game game = gameSetup(2);
        game.giveAssistantDeck("leo", 0);
        game.giveAssistantDeck("mari", 1);
        game.playAssistantCard("mari",9);
        Island oldMNIsland = game.currentMotherNatureIsland;
        Island newMNIsland = game.islands.get((game.islands.indexOf(oldMNIsland) + 5)%game.islands.size());
        assertEquals(true,oldMNIsland.isMotherNature());
        assertEquals(false,newMNIsland.isMotherNature());
        game.moveMotherNature("mari",5);
        assertEquals(false,oldMNIsland.isMotherNature());
        assertEquals(true,newMNIsland.isMotherNature());
        assertEquals(game.getIslands().get(newMNIsland.getIslandIndex()), game.currentMotherNatureIsland);

    }

    /**
     * This method verifies the size of the lobby before and after the addition of a student
     */
    @Test
    void moveStudentsToLobby() {
        Game game = gameSetup(2);
        int l1size = game.getPlayers().get(0).getBoard().getLobby().size();
        int l2size = game.getPlayers().get(1).getBoard().getLobby().size();
        game.refillClouds();
        game.moveStudentsToLobby("leo", 0);
        game.moveStudentsToLobby("mari", 1);
        //test per vedere se le nuvole sono vuote
        assertTrue(game.getClouds().get(0).getStudents().size() == 0);
        assertTrue(game.getClouds().get(1).getStudents().size() == 0);
        //test per vedere se sono stati aggiunti 3 studenti alla lobby
        assertEquals(l1size + 3, game.getPlayers().get(0).getBoard().getLobby().size());
        assertEquals(l2size + 3, game.getPlayers().get(1).getBoard().getLobby().size());
    }

    /**
     * This method verifies if the Cloud with the ID returned by the getEmptyClouds method is empty
     */
    @Test
    void getEmptyClouds(){
        Game game = gameSetup(2);
        ArrayList<Integer> Id1 = new ArrayList<>();
        Id1.add(0);
        Id1.add(1);
        assertEquals(Id1,game.GetEmptyCloudsID());
        game.refillClouds();
        ArrayList<Integer> Id2 = new ArrayList<>();
        assertEquals(Id2,game.GetEmptyCloudsID());
    }

    /**
     * This method verifies the correct calculation of the influence of each  player
     */
    @Test
    void studentsInfluenceTest(){
        Game game = gameSetup(3);
        game.getPlayerByName("leo").setTeam(Tower.WHITE);
        game.getPlayerByName("mari").setTeam(Tower.BLACK);
        game.getPlayerByName("frizio").setTeam(Tower.GREY);
        int mnIndex = 0;
        for (int j = 0; j< 12;j++){
            if(game.getIslands().get(j).isMotherNature())
                mnIndex=j;
        }
        game.getIslands().get(mnIndex).addStudent(Color.BLUE);
        game.getIslands().get(mnIndex).addStudent(Color.PINK);
        game.getPlayerByName("leo").addTeacherToBoard(Color.BLUE);
        game.getPlayerByName("frizio").addTeacherToBoard(Color.RED);
        HashMap<String,Integer> influences = new HashMap<>();
        influences.put("leo",1);
        influences.put("frizio",0);
        influences.put("mari",0);
        HashMap<String,Integer> result = new HashMap<>();
        result = game.calculateStudentsInfluences(game.getIslands().get(mnIndex), game.getPlayers());
        assertEquals(influences,result);
        assertEquals(3,result.size());
        game.getPlayerByName("mari").addTeacherToBoard(Color.PINK);
        game.getIslands().get(mnIndex).addStudent(Color.BLUE);
        influences.put("leo",2);
        influences.put("mari",1);
        result = game.calculateStudentsInfluences(game.getIslands().get(mnIndex), game.getPlayers());
        assertEquals(influences,result);
        assertEquals(3,result.size());
        game.getIslands().get(mnIndex).addStudent(Color.PINK);
        influences.put("mari",2);
        result = game.calculateStudentsInfluences(game.getIslands().get(mnIndex), game.getPlayers());
        assertEquals(influences,result);
        assertEquals(3,result.size());
    }

    /**
     * This method verifies if the calculateIslandOwner method returns the correct playerID of the player
     * that is the owner of the island
     */
    @Test
    void calculateIslandOwnerTest(){
        Game game = new Game(3);
        ArrayList<String> players = new ArrayList<>();
        players.add("mari");
        players.add("frizio");
        players.add("leo");
        game.instantiateGameElements(players);
        game.getPlayerByName("leo").setTeam(Tower.GREY);
        game.getPlayerByName("mari").setTeam(Tower.WHITE);
        game.getPlayerByName("frizio").setTeam(Tower.BLACK);
        int mnIndex = 0;
        for (int j = 0; j< 12;j++){
            if(game.getIslands().get(j).isMotherNature())
                mnIndex=j;
        }
        Island testedIsland = game.getIslands().get(mnIndex);
        HashMap<String,Integer> influences = new HashMap<>();
        influences.put("mari", 1);
        influences.put("frizio",0);
        influences.put("leo",2);
        HashMap<String,String> result  = new HashMap<>();
        result = game.calculateIslandOwner(testedIsland,influences);
        assertEquals("leo", result.get("Player Name"));
        assertEquals(Constants.NO_DRAW, result.get("Is Draw"));
        assertEquals(game.getPlayerByName("leo"), testedIsland.getOwner());
        influences.put("frizio",2);
        result = game.calculateIslandOwner(testedIsland,influences);
        assertEquals("leo", result.get("Player Name"));
        assertEquals(Constants.DRAW, result.get("Is Draw"));
        assertEquals(game.getPlayerByName("leo"), testedIsland.getOwner());
        influences.put("frizio",3);
        result = game.calculateIslandOwner(testedIsland,influences);
        assertEquals("frizio", result.get("Player Name"));
        assertEquals(Constants.NO_DRAW, result.get("Is Draw"));
        assertEquals(game.getPlayerByName("frizio"), testedIsland.getOwner());
    }

    //test senza torri e con pareggi

    /**
     * This method verifies the correct calculation of the influence excluding the towers
     */
    @Test
    void CalculateInfluenceWithTowersTest(){
        Game game = new Game(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("leo");
        players.add("mari");
        game.instantiateGameElements(players);
        game.getPlayerByName("leo").setTeam(Tower.WHITE);
        game.getPlayerByName("mari").setTeam(Tower.BLACK);
        int mnIndex = 0;
        for (int j = 0; j< 12;j++){
            if(game.getIslands().get(j).isMotherNature())
                mnIndex=j;
        }
        assertNull(game.calculateInfluence(game.getIslands().get(mnIndex)).get("Player Name"));
        assertEquals(Constants.DRAW, game.calculateInfluence(game.getIslands().get(mnIndex)).get("Is Draw"));
        game.getPlayerByName("leo").addTeacherToBoard(Color.BLUE);
        game.getPlayerByName("leo").addTeacherToBoard(Color.PINK);
        game.getPlayerByName("leo").addTeacherToBoard(Color.RED);
        game.getPlayerByName("mari").addTeacherToBoard(Color.YELLOW);
        game.getPlayerByName("mari").addTeacherToBoard(Color.GREEN);
        game.getIslands().get(0).addStudent(Color.BLUE);
        game.getIslands().get(0).addStudent(Color.BLUE);
        assertEquals("leo",game.calculateInfluence(game.getIslands().get(0)).get("Player Name"));
        game.getIslands().get(0).addStudent(Color.YELLOW);
        game.getIslands().get(0).addStudent(Color.YELLOW);
        game.getIslands().get(0).addStudent(Color.YELLOW);
        game.getIslands().get(0).addStudent(Color.YELLOW);
        game.getIslands().get(0).addStudent(Color.YELLOW);
        assertEquals("mari",game.calculateInfluence(game.getIslands().get(0)).get("Player Name"));
    }

    //test con torri e senza pareggi

    /**
     * Method that verifies the correct calculation of influence excluding the draw cases
     */
    @Test
    void totalCalculateInfluenceTest(){
        Game game = new Game(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("leo");
        players.add("mari");
        game.instantiateGameElements(players);
        game.getPlayerByName("leo").setTeam(Tower.WHITE);
        game.getPlayerByName("mari").setTeam(Tower.BLACK);
        game.getPlayerByName("leo").addTeacherToBoard(Color.BLUE);
        game.getPlayerByName("leo").addTeacherToBoard(Color.PINK);
        game.getPlayerByName("leo").addTeacherToBoard(Color.RED);
        game.getPlayerByName("mari").addTeacherToBoard(Color.YELLOW);
        game.getPlayerByName("mari").addTeacherToBoard(Color.GREEN);
        game.getIslands().get(0).setOwner(game.getPlayerByName("leo"));
        game.getIslands().get(0).addTower(Tower.WHITE);
        game.getIslands().get(0).addStudent(Color.BLUE);
        assertEquals("leo",game.calculateInfluence(game.getIslands().get(0)).get("Player Name"));
        game.getIslands().get(0).removeTower();
        game.getIslands().get(0).setOwner(game.getPlayerByName("mari"));
        game.getIslands().get(0).addTower(Tower.BLACK);
        game.getIslands().get(0).addStudent(Color.GREEN);
        game.getIslands().get(0).addStudent(Color.GREEN);
        assertEquals("mari",game.calculateInfluence(game.getIslands().get(0)).get("Player Name"));
    }

    /**
     * This method verifies the correct working of checkGameOver method according to the island number
     */
    @Test
    void checkGameOverForIslandNumber(){
        Game game = new Game(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("leo");
        players.add("mari");
        game.instantiateGameElements(players);
        game.getPlayerByName("leo").setTeam(Tower.BLACK);
        game.getPlayerByName("mari").setTeam(Tower.WHITE);
        assertEquals(12,game.getIslands().size());
        game.getIslands().clear();
        assertTrue(game.checkGameOver());
        assertNull(game.getWinner());
        while(game.getPlayers().get(1).getBoard().getTowers() > 5)
            game.getPlayers().get(1).removeTowerFromBoard();
        assertEquals(5,game.getPlayers().get(1).getBoard().getTowers());
        assertTrue(game.checkGameOver());
        assertEquals(game.getPlayers().get(1), game.getWinner());
    }

    /**
     * This method verifies the correct working of checkGameOver method according to the number of towers
     */
    @Test
    void checkGameOverForTowers() {
        Game game = new Game(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("leo");
        players.add("mari");
        game.instantiateGameElements(players);
        game.getPlayerByName("leo").setTeam(Tower.BLACK);
        game.getPlayerByName("mari").setTeam(Tower.WHITE);
        assertFalse(game.checkGameOver());
        for (int i = 0; i < 2; i++)
            game.getPlayerByName("leo").removeTowerFromBoard();
        assertFalse(game.checkGameOver());
        assertEquals(6, game.getPlayerByName("leo").getBoard().getTowers());
        assertEquals(8, game.getPlayerByName("mari").getBoard().getTowers());
        while (game.getPlayerByName("mari").getBoard().getTowers() > 0)
            game.getPlayerByName("mari").removeTowerFromBoard();
        assertTrue(game.checkGameOver());
    }

    /**
     * This method verifies the correct working of the method that moves the teachers
     * according to their color
     */
    @Test
    void teacherMovementTest(){
        Game game = new Game(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("leo");
        players.add("mari");
        game.instantiateGameElements(players);
        game.getPlayerByName("leo").setTeam(Tower.BLACK);
        game.getPlayerByName("mari").setTeam(Tower.WHITE);
        for (int i = 0; i< 7;i++)
            game.getPlayerByName("leo").addToBoardTable(Color.BLUE);
        game.getPlayerByName("leo").addToBoardTable(Color.PINK);
        game.getPlayerByName("leo").addToBoardTable(Color.YELLOW);
        game.getPlayerByName("leo").addToBoardTable(Color.GREEN);
        game.getPlayerByName("mari").addToBoardTable(Color.RED);

        game.updateTeachersOwnership("mari");
        assertFalse(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.BLUE));
        assertFalse(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.PINK));
        assertFalse(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.GREEN));
        assertFalse(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.YELLOW));
        assertTrue(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.RED));
        game.updateTeachersOwnership("leo");
        assertTrue(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.BLUE));
        assertTrue(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.PINK));
        assertTrue(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.GREEN));
        assertTrue(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.YELLOW));
        assertTrue(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.RED));

        game.getPlayerByName("leo").addToBoardTable(Color.RED);
        game.getPlayerByName("leo").addToBoardTable(Color.RED);
        game.updateTeachersOwnership("leo");
        assertTrue(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.RED));
        assertFalse(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.RED));
        assertTrue(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.BLUE));
        assertTrue(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.PINK));
        assertTrue(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.GREEN));
        assertTrue(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.YELLOW));
    }

    /**
     * This method
     */
    @Test
    void lastRoundTest(){
        Game game = new Game(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("frizio");
        players.add("mari");
        game.instantiateGameElements(players);
        game.getPlayerByName("frizio").setTeam(Tower.GREY);
        game.getPlayerByName("mari").setTeam(Tower.WHITE);
        game.setBasket(new Basket(new int[]{0,0,0,0,0}));
        game.refillClouds();
        assertTrue(game.isLastRound());
    }

    @Test
    void nullPlayerNameTest(){
        Game game = new Game(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("frizio");
        players.add("mari");
        game.instantiateGameElements(players);
        game.getPlayerByName("frizio").setTeam(Tower.GREY);
        game.getPlayerByName("mari").setTeam(Tower.WHITE);
        assertNull(game.getPlayerByName("leo"));
    }

    @Test
    void islandOwnerTest(){
        Game game = new Game(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("leo");
        players.add("frizio");
        game.instantiateGameElements(players);
        game.getPlayerByName("frizio").setTeam(Tower.WHITE);
        game.getPlayerByName("leo").setTeam(Tower.BLACK);
        game.getIslands().get(0).setOwner(game.getPlayerByName("leo"));
        assertEquals(null, game.getTowersOwnerName(game.getIslands().get(0), game.getPlayers()));
        game.getIslands().get(0).addTower(Tower.BLACK);
        assertEquals("leo", game.getTowersOwnerName(game.getIslands().get(0), game.getPlayers()));
    }

    /**
     * TODO: sistemare metodo in modo che controlli che è manchi dal mazzo solo quella carta giocata (contains in teoria non va bene)
     */
    @Test
    void getPlayableAssistantCardTest(){
        Game game = new Game(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("mari");
        players.add("frizio");
        game.instantiateGameElements(players);
        game.giveAssistantDeck("mari", 0);
        assertEquals(10,game.getPlayableAssistantCards("mari").size());
        game.playAssistantCard("mari",1);
        assertEquals(1, game.getCurrentTurnAssistantCards().get("mari").getPriority());
        assertFalse(game.getPlayableAssistantCards("mari").contains(new Assistant(1,1,0)));
        assertEquals(9,game.getPlayableAssistantCards("mari").size());
    }


    @Test
    void mergeTwoIslandsTest(){
        Game game = new Game(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("leoviatano");
        players.add("frizio");
        game.instantiateGameElements(players);
        game.getPlayerByName("leoviatano").setTeam(Tower.BLACK);
        game.getPlayerByName("frizio").setTeam(Tower.WHITE);
        Island island1 = game.islands.get(0);
        Island leftIsland = game.islands.get(11);
        Island rightIsland = game.islands.get(1);
        Island testIsland = game.islands.get(10);
        island1.addStudent(Color.RED);
        island1.addStudent(Color.RED);
        island1.addStudent(Color.RED);
        island1.addStudent(Color.PINK);
        leftIsland.addStudent(Color.BLUE);
        leftIsland.addStudent(Color.BLUE);
        leftIsland.addStudent(Color.BLUE);
        int[] oldNumbersOfStudent = new int[5];
        oldNumbersOfStudent[0] = island1.getStudentsOfColor(Color.RED).size();
        oldNumbersOfStudent[1] = island1.getStudentsOfColor(Color.PINK).size();
        oldNumbersOfStudent[2] = island1.getStudentsOfColor(Color.BLUE).size();
        oldNumbersOfStudent[3] = island1.getStudentsOfColor(Color.YELLOW).size();
        oldNumbersOfStudent[4] = island1.getStudentsOfColor(Color.GREEN).size();
        island1.setOwner(game.getPlayerByName("leoviatano"));
        leftIsland.setOwner(game.getPlayerByName("leoviatano"));
        rightIsland.setOwner(game.getPlayerByName("frizio"));
        game.mergeIslands(island1);
        assertEquals(11,game.islands.size());
        assertEquals(game.getPlayerByName("leoviatano"),island1.getOwner());
        assertEquals(testIsland,game.islands.get(10));
        assertEquals(oldNumbersOfStudent[0] + leftIsland.getStudentsOfColor(Color.RED).size(),island1.getStudentsOfColor(Color.RED).size());
        assertEquals(oldNumbersOfStudent[1] + leftIsland.getStudentsOfColor(Color.PINK).size(),island1.getStudentsOfColor(Color.PINK).size());
        assertEquals(oldNumbersOfStudent[2] + leftIsland.getStudentsOfColor(Color.BLUE).size(),island1.getStudentsOfColor(Color.BLUE).size()); //expected 3, actual 4
        //aggiungere caso in cui mergia 3 isole invece di 2
    }

    @Test
    void moveAndInfluenceTest(){
        Game game = new Game(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("leoviatano");
        players.add("frizio");
        game.instantiateGameElements(players);
        game.getPlayerByName("leoviatano").setTeam(Tower.BLACK);
        game.getPlayerByName("frizio").setTeam(Tower.WHITE);
        game.giveAssistantDeck("leoviatano", 0);
        game.giveAssistantDeck("frizio", 1);
        game.playAssistantCard("leoviatano",8);
        game.playAssistantCard("frizio",4);
        game.getPlayerByName("leoviatano").addTeacherToBoard(Color.RED);
        int mnIndex = 0;
        for (int j = 0; j< 12;j++){
            if(game.getIslands().get(j).isMotherNature())
                mnIndex=j;
        }
        Island initialMNIsland = game.getIslands().get(mnIndex);
        game.getIslands().get((mnIndex+1)%game.getIslands().size()).setOwner(game.getPlayerByName("leoviatano"));
        game.getIslands().get((mnIndex+1)%game.getIslands().size()).addTower(Tower.BLACK);
        game.getIslands().get((mnIndex+2)%game.getIslands().size()).addStudent(Color.RED);
        game.getIslands().get((mnIndex+2)%game.getIslands().size()).addStudent(Color.RED);
        game.moveMotherNature("leoviatano", 2);
        assertEquals((mnIndex+2)%game.getIslands().size(), game.getCurrentMotherNatureIsland().getIslandIndex());
        assertEquals((game.getIslands().indexOf(initialMNIsland)+2)%game.getIslands().size(), game.getIslands().indexOf(game.getCurrentMotherNatureIsland()));
        Island dest = game.getIslands().get((mnIndex+2)%game.getIslands().size());
        game.calculateInfluence(dest);
        assertEquals("leoviatano", dest.getOwner().getNickname());
        assertEquals(11,game.getIslands().size());
        //Struttura del test: trovo madre natura, nella isola dopo ci piazzo una torre di leo
        //poi muovo madre natura di due posizioni  e chiamo calculateInfluence in modo da fare merge
        //poi faccio spostare a frizio madre natura e controllo che sia finita al posto giusto
    }

    //test da sistemare perché ogni tanto fallisce
    @Test
    void influenceAndMergeTest(){
        Game game = new Game(2);
        ArrayList<String> players = new ArrayList<>();
        players.add("leoviatano");
        players.add("frizio");
        game.instantiateGameElements(players);
        game.getPlayerByName("leoviatano").setTeam(Tower.BLACK);
        game.getPlayerByName("frizio").setTeam(Tower.WHITE);
        game.getPlayerByName("leoviatano").addTeacherToBoard(Color.RED);
        game.giveAssistantDeck("leoviatano", 0);
        game.giveAssistantDeck("frizio", 1);
        game.playAssistantCard("leoviatano",8);
        game.playAssistantCard("frizio",4);
        int mnIndex = 0;
        for (int j = 0; j< 12;j++){
            if(game.getIslands().get(j).isMotherNature())
                mnIndex=j;
        }
        Island initialMN = game.getIslands().get(mnIndex);
        game.getIslands().get((mnIndex+2)%game.getIslands().size()).addStudent(Color.RED);
        game.getIslands().get((mnIndex+2)%game.getIslands().size()).addStudent(Color.RED);
        game.getIslands().get((mnIndex+1)%game.getIslands().size()).setOwner(game.getPlayerByName("leoviatano"));
        game.getIslands().get((mnIndex+1)%game.getIslands().size()).addTower(Tower.BLACK);
        Island dest = game.getIslands().get((mnIndex+2)%game.getIslands().size());
        game.calculateInfluence(dest); //qui c'è un merge
        assertEquals(11, game.getIslands().size());
        game.moveMotherNature("leoviatano", 2);
        assertEquals((mnIndex+2)%game.getIslands().size(),game.getIslands().indexOf(game.getCurrentMotherNatureIsland())); //le isole sono scalate di 1 rispetto all'inizio
        //riga sopra ogni tanto da errore rivedere
        game.moveMotherNature("frizio", 1);
        assertEquals((mnIndex+3)%game.getIslands().size(),game.getIslands().indexOf(game.getCurrentMotherNatureIsland()));
    }

    /**
     * TODO: controllare il funzionamento
     */
    @Test
    void isCardPlayableTest(){
        Game game = new Game(3);
        ArrayList<String> players = new ArrayList<>();
        players.add("mari");
        players.add("frizio");
        players.add("leoviatano");
        game.instantiateGameElements(players);
        Player leoviatano =game.getPlayerByName("leoviatano");
        Player mari = game.getPlayerByName("mari");
        Player frizio = game.getPlayerByName("frizio");

        leoviatano.setTeam(Tower.GREY);
        frizio.setTeam(Tower.WHITE);
        mari.setTeam(Tower.BLACK);
        game.giveAssistantDeck("leoviatano", 0);
        game.giveAssistantDeck("frizio", 1);
        game.giveAssistantDeck("mari", 2);

        ArrayList<Assistant> deck0 = game.getPlayerByName("mari").getDeck();
        ArrayList<Assistant> deck1 = game.getPlayerByName("frizio").getDeck();
        ArrayList<Assistant> deck2 = game.getPlayerByName("leoviatano").getDeck();
        assertEquals(true,game.isCardPlayable(mari.getCardByPriority(1),mari.getDeck()));
        assertEquals(true,game.isCardPlayable(frizio.getCardByPriority(1),frizio.getDeck()));
        game.playAssistantCard("mari",1); //cardPriority = 1 <==> getCardByPriority(1)
        assertEquals(false,game.isCardPlayable(frizio.getCardByPriority(1),frizio.getDeck()));
        assertEquals(true,game.isCardPlayable(frizio.getCardByPriority(2),frizio.getDeck()));
        assertEquals(false,game.isCardPlayable(leoviatano.getCardByPriority(1),leoviatano.getDeck()));
        assertEquals(true,game.isCardPlayable(leoviatano.getCardByPriority(2),leoviatano.getDeck()));
        game.playAssistantCard("frizio",2);
        assertEquals(false,game.isCardPlayable(new Assistant(1,2,2),mari.getDeck()));
        assertEquals(true,game.isCardPlayable(new Assistant(2,3,2),mari.getDeck()));
        assertEquals(false,game.isCardPlayable(new Assistant(1,2,0),leoviatano.getDeck()));
        assertEquals(true,game.isCardPlayable(new Assistant(2,3,0),leoviatano.getDeck()));
        Assistant deck2FirstElement = leoviatano.getCardByPriority(1);
        Assistant deck2SecondElement = leoviatano.getCardByPriority(2);
        deck2.clear();
        deck2.add(deck2FirstElement);
        deck2.add(deck2SecondElement);
        //a leoviatano rimangono solo carte già giocate da altri, quindi può giocarne una qualunque
        assertEquals(true,game.isCardPlayable(deck2.get(0),deck2));
        assertEquals(true,game.isCardPlayable(deck2.get(1),deck2));
        //a mari viene lasciata in mano solo una carta e la deve giocare per forza non avendone altre
        Assistant deck0FirstElement = deck0.get(0);
        deck0.clear();
        deck0.add(deck0FirstElement);
        assertEquals(true,game.isCardPlayable(deck0.get(0),deck0));
    }

    @Test
    void cloudEmptinessTest(){
        Game game = new Game(3);
        ArrayList<String> players = new ArrayList<>();
        players.add("mari");
        players.add("frizio");
        players.add("leoviatano");
        game.instantiateGameElements(players);
        game.refillClouds();
        assertTrue(game.areCloudsFull());
        assertEquals(9,game.getPlayerByName("mari").getBoard().getLobby().size());
        game.moveStudentsToLobby("mari", 0);
        assertFalse(game.areCloudsFull());
        assertEquals(13,game.getPlayerByName("mari").getBoard().getLobby().size());
    }

    @Test
    void resetAssistantCardTest(){
        Game game = new Game(3);
        ArrayList<String> players = new ArrayList<>();
        players.add("mari");
        players.add("frizio");
        players.add("leo");
        game.instantiateGameElements(players);
        game.giveAssistantDeck("mari", 0);
        game.giveAssistantDeck("leo", 1);
        game.giveAssistantDeck("frizio", 2);
        game.playAssistantCard("mari",1);
        game.playAssistantCard("leo",2);
        game.playAssistantCard("frizio",3);
        assertEquals(3,game.getCurrentTurnAssistantCards().size());
        game.resetCurrentTurnAssistantCards();
        assertEquals(0,game.getCurrentTurnAssistantCards().size());
    }

    @Test
    void actionPhaseOrderTest(){
        Game game = new Game(3);
        ArrayList<String> players = new ArrayList<>();
        players.add("mari");
        players.add("frizio");
        players.add("leo");
        game.instantiateGameElements(players);
        game.giveAssistantDeck("mari", 0);
        game.giveAssistantDeck("leo", 1);
        game.giveAssistantDeck("frizio", 2);
        game.playAssistantCard("mari",1);
        game.playAssistantCard("leo",2);
        game.playAssistantCard("frizio",3);
        ArrayList<String> order = new ArrayList<>();
        order.add(0,"frizio");
        order.add(1,"leo");
        order.add(2, "mari");
        assertEquals(order,game.getActionPhasePlayerOrder());
    }
    /*
    TODO: aggiungere test che verifichi il corretto merge con una isola a dx
     */

}