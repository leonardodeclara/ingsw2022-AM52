package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyBasketException;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;



@SuppressWarnings("ALL")



class GameTest {

    /**
     * This method aims at verifying the correct insertion of players to the game.
     */
    @Test
    void addPlayerTest(){
        Game game = new Game(3);
        game.addPlayer(new Player(0,"leo",Tower.BLACK),0);
        game.addPlayer(new Player(1,"frizio",Tower.WHITE),1);
        game.addPlayer(new Player(2,"mari",Tower.GREY),2);
        assertEquals(3, game.getPlayers().size());
        assertThrows(Exception.class, ()->game.addPlayer(new Player(3,"leo",Tower.GREY),2));
    }

    /**
     * teacherInstantiationTest method verifies that five different teachers are correctly instantiated and added
     * to game's teachers attribute.
     */
    @Test
    void teacherInstantiationTest() {
        Game game = new Game(2);
        game.instantiateGameElements();
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
        game.instantiateGameElements();
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
        game.instantiateGameElements();
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
        game.addPlayer(new Player(0,"leo",Tower.BLACK),0);
        game.addPlayer(new Player(1,"frizio",Tower.WHITE),1);
        game.addPlayer(new Player(2,"mari",Tower.GREY),2);
        game.instantiateGameElements();
        assertEquals(3, game.getClouds().size());
    }

    @Test
    void towerInstantiationTest(){}

    /**
     * Method deckInstantiationTest verifies the correct instantiation of the Assistant card deck
     */
    @Test
    void deckInstantiationTest() {
        Game game = new Game(2);
        game.instantiateGameElements();
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
        //for (int i = 0; i< 12; i++){
        //    game.getIslands().add(new Island(i));
        //}
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
        game.addPlayer(new Player(0,"leo",Tower.BLACK),0);
        game.addPlayer(new Player(1,"frizio",Tower.WHITE),1);
        game.instantiateGameElements();
        game.initiatePlayerLobby(0);
        assertEquals(7, game.getPlayers().get(0).getBoard().getLobby().size());
        assertEquals(113, game.getBasket().getSize());
    }

    /**
     * This method verifies that a player receives the proper deck of assistant cards,
     * which must also be removed from the assistantDecks attribute of game.
     */
    @Test
    void giveAssistantDeck() {
        Game game = new Game(2);
        game.instantiateGameElements();
        game.addPlayer(new Player(0,"leo",Tower.BLACK),1);
        game.addPlayer(new Player(1,"mari",Tower.WHITE),2);
        assertEquals(10, game.getPlayers().get(0).getDeck().size());
        for (int i = 0; i<9; i++){
            for (int j = i+1; j < 10; j++){
                assertTrue(game.getPlayers().get(0).getDeck().get(i)!=game.getPlayers().get(0).getDeck().get(j));
            }
        }
        for (int i = 0; i<10; i++){
            assertEquals(1,game.getPlayers().get(0).getDeck().get(i).getWizard());
            assertEquals(i+1,game.getPlayers().get(0).getDeck().get(i).getPriority());
        }
    }

    /**
     * refillCloudsTest method is responsible for verifying that clouds are rightly
     * filled with students according to the number of players.
     */
    @Test
    void refillTwoCloudsTest(){
        Game game = new Game(2);
        game.instantiateGameElements();
        game.addPlayer(new Player(0,"leo",Tower.BLACK),1);
        game.addPlayer(new Player(1,"mari",Tower.WHITE),2);
        game.refillClouds();
        for (int i = 0; i< 2; i++){
            assertEquals(3,game.getClouds().get(i).getStudents().size());
        }
    }


    /**
     * playAssistantCard method is responsible for verifying that card are removed from player's deck and
     * added to the game current turn deck
     */

    /**
     * TODO: sistemare il test in modo che controlli che la carta giocata è effettivamente quella
     */
    @Test
    void playAssistantCard(){
        Game game = new Game(2);
        game.instantiateGameElements();
        game.addPlayer(new Player(0,"leo",Tower.BLACK),1);
        game.addPlayer(new Player(1,"mari",Tower.WHITE),2);
        Assistant a0 = new Assistant(2,4,1);
        Assistant a1 = new Assistant(4,8,2);
        ArrayList<Assistant> d1 = new ArrayList<>();
        ArrayList<Assistant> d2 = new ArrayList<>();
        d1.add(a0);
        d2.add(a1);
        //game.getPlayerByName("leo").setDeck(d1);
        //game.getPlayerByName("mari").setDeck(d2);
        game.playAssistantCard("leo",4); //giochiamo a2
        game.playAssistantCard("mari",8); //giochiamo a1
        //ci si assicura che a1,a2 siano stati rimossi dai rispettivi deck
        assertEquals(false,game.getPlayerByName("leo").getDeck().contains(a0));
        assertEquals(false,game.getPlayerByName("mari").getDeck().contains(a1));
        //Ci si assicura che a1,a2 ora siano nelle celle dell'hashmap corrispondenti agli id dei giocatori che le hanno giocate
        assertEquals(a0.getPriority(),game.getCurrentTurnAssistantCards().get("leo").getPriority());
        assertEquals(a1.getPriority(),game.getCurrentTurnAssistantCards().get("mari").getPriority());
    }


    /**
     * Method isMoveMNLegal is responsible for verifying that Mother Nature moves are legal according to
     * the numMoves written on the Assistant card played
     */
    @Test
    void isMoveMNLegal(){
        Game game = new Game(2);
        game.instantiateGameElements();
        game.addPlayer(new Player(0,"leo",Tower.BLACK),1);
        game.addPlayer(new Player(1,"mari",Tower.WHITE),2);
        //Assistant a0 = new Assistant(3,4,1);
        //Assistant a1 = new Assistant(5,8,2);
        //Assistant a2 = new Assistant(7,15,1);
        //Assistant a3 = new Assistant(8,12,2);
        //ArrayList<Assistant> d1 = new ArrayList<>();
        //ArrayList<Assistant> d2 = new ArrayList<>();
        //d1.add(a0);
        //d1.add(a2);
        //d2.add(a1);
        //d2.add(a3);
        //game.getPlayers().get(0).setDeck(d1);
        //game.getPlayers().get(1).setDeck(d2);
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
        Game game = new Game(2);
        game.instantiateGameElements();
        game.addPlayer(new Player(0,"leo",Tower.BLACK),1);
        game.addPlayer(new Player(1,"mari",Tower.WHITE),2);
        //game.initiatePlayerLobby(0);
        //game.initiatePlayerLobby(1);
        for(int i = 0;i<7;i++)
            game.getPlayerByName("leo").addToBoardLobby(Color.RED);
        for(int i = 0;i<7;i++)
            game.getPlayerByName("mari").addToBoardLobby(Color.YELLOW);

        for(int i = 0;i<10;i++) //riempie completamente la table rosa di "leo"
            game.getPlayers().get(0).addToBoardTable(Color.RED);
        for(int i = 0;i<10;i++) //riempie completamente la table blu di "mari"
            game.getPlayers().get(1).addToBoardTable(Color.BLUE);

        assertTrue(game.isMoveStudentFromLobbyLegal(game.getPlayers().get(0), 0, 3));
        assertFalse(game.isMoveStudentFromLobbyLegal(game.getPlayers().get(0), 0, -1));
        assertTrue(game.isMoveStudentFromLobbyLegal(game.getPlayers().get(0), 6, 1));
        assertFalse(game.isMoveStudentFromLobbyLegal(game.getPlayers().get(1), 7, 6));
        assertTrue(game.isMoveStudentFromLobbyLegal(game.getPlayers().get(1), 6, 6));
        assertFalse(game.isMoveStudentFromLobbyLegal(game.getPlayers().get(1), 0, 23));
        assertFalse(game.isMoveStudentFromLobbyLegal(game.getPlayers().get(1),-1,10));
    }


    /**
     * Method isMoveStudentsToLobbyLegal verifies if method isMoveStudentsToLobbyLegal returns the correct
     * boolean value according to the correct cloudID
     */
    @Test
    void isMoveStudentsToLobbyLegal(){
        Game game = new Game(2);
        game.instantiateGameElements();
        game.addPlayer(new Player(0,"leo",Tower.BLACK),1);
        game.addPlayer(new Player(1,"mari",Tower.WHITE),2);
        game.initiatePlayerLobby(0);
        game.initiatePlayerLobby(1);
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
        Game game = new Game(2);
        game.instantiateGameElements();
        game.addPlayer(new Player(0,"leo",Tower.BLACK),1);
        game.addPlayer(new Player(1,"mari",Tower.WHITE),2);
        game.initiatePlayerLobby(0);
        game.initiatePlayerLobby(1);

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
        Game game = new Game(2);
        game.instantiateGameElements();
        game.addPlayer(new Player(0,"leo",Tower.BLACK),1);
        game.addPlayer(new Player(1,"mari",Tower.WHITE),2);
        game.initiatePlayerLobby(0);
        game.initiatePlayerLobby(1);

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

    /**
     * This method verifies the correct working of moveMotherNature method checking the old and the new position
     * of Mother Nature before and after the move
     */
    @Test
    void moveMNTest(){
        Game game = new Game(2);
        game.instantiateGameElements();
        game.addPlayer(new Player(0,"leo",Tower.BLACK),0);
        game.addPlayer(new Player(1,"mari",Tower.WHITE),2);
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
    void moveStudentsToLobby(){
        Game game = new Game(2);
        game.instantiateGameElements();
        game.addPlayer(new Player(0,"leo",Tower.BLACK),0);
        game.addPlayer(new Player(1,"mari",Tower.WHITE),1);
        game.initiatePlayerLobby(0);
        game.initiatePlayerLobby(1);
        int l1size = game.getPlayers().get(0).getBoard().getLobby().size();
        int l2size = game.getPlayers().get(1).getBoard().getLobby().size();
        game.refillClouds();
        game.moveStudentsToLobby(0,0);
        game.moveStudentsToLobby(1,1);
        //test per vedere se le nuvole sono vuote
        assertEquals(true,game.getClouds().get(0).getStudents().size() == 0);
        assertEquals(true,game.getClouds().get(1).getStudents().size() == 0);
        //test per vedere se sono stati aggiunti 3 studenti alla lobby
        assertEquals(l1size+3,game.getPlayers().get(0).getBoard().getLobby().size());
        assertEquals(l2size+3,game.getPlayers().get(1).getBoard().getLobby().size());

    }

    /**
     * This method verifies if the Cloud with the ID returned by the getEmptyClouds method is empty
     */
    @Test
    void getEmptyClouds(){
        Game game = new Game(2);
        game.instantiateGameElements();
        game.addPlayer(new Player(0,"leo",Tower.BLACK),0);
        game.addPlayer(new Player(1,"mari",Tower.WHITE),1);
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
        Game game = new Game(3);
        game.instantiateGameElements();
        game.addPlayer(new Player(0,"leo",Tower.WHITE),0);
        game.addPlayer(new Player(1,"mari",Tower.BLACK),1);
        game.addPlayer(new Player(2,"frizio",Tower.GREY),2);
        int mnIndex = 0;
        for (int j = 0; j< 12;j++){
            if(game.getIslands().get(j).isMotherNature())
                mnIndex=j;
        }
        game.getIslands().get(mnIndex).addStudent(Color.BLUE);
        game.getIslands().get(mnIndex).addStudent(Color.PINK);
        game.getPlayerByName("leo").addTeacherToBoard(Color.BLUE);
        game.getPlayerByName("frizio").addTeacherToBoard(Color.RED);
        ArrayList<Integer> influences = new ArrayList<>();
        influences.add(1);
        influences.add(0);
        influences.add(0);
        ArrayList<Integer> result = game.calculateStudentsInfluences(game.getIslands().get(mnIndex), game.getPlayers());
        assertEquals(influences,result);
        assertEquals(3,result.size());
        game.getPlayerByName("mari").addTeacherToBoard(Color.PINK);
        game.getIslands().get(mnIndex).addStudent(Color.BLUE);
        influences.set(0,2);
        influences.set(1,1);
        result = game.calculateStudentsInfluences(game.getIslands().get(mnIndex), game.getPlayers());
        assertEquals(influences,result);
        assertEquals(3,result.size());
        game.getIslands().get(mnIndex).addStudent(Color.PINK);
        influences.set(1,2);
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
        game.instantiateGameElements();
        game.addPlayer(new Player(0,"mari",Tower.WHITE),0);
        game.addPlayer(new Player(1,"frizio",Tower.BLACK),1);
        game.addPlayer(new Player(2,"leo",Tower.GREY),2);
        int mnIndex = 0;
        for (int j = 0; j< 12;j++){
            if(game.getIslands().get(j).isMotherNature())
                mnIndex=j;
        }
        Island testedIsland = game.getIslands().get(mnIndex);
        ArrayList<Integer> influences = new ArrayList<>();
        influences.add(1);
        influences.add(0);
        influences.add(2);
        HashMap<String,Integer> result = game.calculateIslandOwner(testedIsland,influences);
        assertEquals(2, result.get("ID Player"));
        assertEquals(0, result.get("Is Draw"));
        assertEquals(game.getPlayerByName("leo"), testedIsland.getOwner());
        influences.set(1,2);
        result = game.calculateIslandOwner(testedIsland,influences);
        assertEquals(2, result.get("ID Player"));
        assertEquals(1, result.get("Is Draw"));
        assertEquals(game.getPlayerByName("leo"), testedIsland.getOwner());
        influences.set(1,3);
        result = game.calculateIslandOwner(testedIsland,influences);
        assertEquals(1, result.get("ID Player"));
        assertEquals(0, result.get("Is Draw"));
        assertEquals(game.getPlayerByName("frizio"), testedIsland.getOwner());
    }

    //test senza torri e con pareggi

    /**
     * This method verifies the correct calculation of the influence excluding the towers
     */
    @Test
    void CalculateInfluenceWithTowersTest(){
        Game game = new Game(2);
        game.instantiateGameElements();
        game.addPlayer(new Player(0,"leo",Tower.WHITE),0);
        game.addPlayer(new Player(1,"mari",Tower.BLACK),1);
        int mnIndex = 0;
        for (int j = 0; j< 12;j++){
            if(game.getIslands().get(j).isMotherNature())
                mnIndex=j;
        }
        assertNull(game.calculateInfluence(game.getIslands().get(mnIndex)).get("ID Player"));
        assertEquals(1, game.calculateInfluence(game.getIslands().get(mnIndex)).get("Is Draw"));
        game.getPlayerByName("leo").addTeacherToBoard(Color.BLUE);
        game.getPlayerByName("leo").addTeacherToBoard(Color.PINK);
        game.getPlayerByName("leo").addTeacherToBoard(Color.RED);
        game.getPlayerByName("mari").addTeacherToBoard(Color.YELLOW);
        game.getPlayerByName("mari").addTeacherToBoard(Color.GREEN);
        game.getIslands().get(0).addStudent(Color.BLUE);
        game.getIslands().get(0).addStudent(Color.BLUE);
        assertEquals(0,game.calculateInfluence(game.getIslands().get(0)).get("ID Player"));
        game.getIslands().get(0).addStudent(Color.YELLOW);
        game.getIslands().get(0).addStudent(Color.YELLOW);
        game.getIslands().get(0).addStudent(Color.YELLOW);
        game.getIslands().get(0).addStudent(Color.YELLOW);
        assertEquals(1,game.calculateInfluence(game.getIslands().get(0)).get("ID Player"));
    }

    //test con torri e senza pareggi

    /**
     * Method that verifies the correct calculation of influence excluding the draw cases
     */
    @Test
    void totalCalculateInfluenceTest(){
        Game game = new Game(2);
        game.instantiateGameElements();
        game.addPlayer(new Player(0,"leo",Tower.WHITE),0);
        game.addPlayer(new Player(1,"mari",Tower.BLACK),1);
        game.getPlayerByName("leo").addTeacherToBoard(Color.BLUE);
        game.getPlayerByName("leo").addTeacherToBoard(Color.PINK);
        game.getPlayerByName("leo").addTeacherToBoard(Color.RED);
        game.getPlayerByName("mari").addTeacherToBoard(Color.YELLOW);
        game.getPlayerByName("mari").addTeacherToBoard(Color.GREEN);
        game.getIslands().get(0).setOwner(game.getPlayerByName("leo"));
        game.getIslands().get(0).addTower(Tower.WHITE);
        game.getIslands().get(0).addStudent(Color.BLUE);
        assertEquals(0,game.calculateInfluence(game.getIslands().get(0)).get("ID Player"));
        game.getIslands().get(0).removeTower();
        game.getIslands().get(0).setOwner(game.getPlayerByName("mari"));
        game.getIslands().get(0).addTower(Tower.BLACK);
        game.getIslands().get(0).addStudent(Color.GREEN);
        game.getIslands().get(0).addStudent(Color.GREEN);
        assertEquals(1,game.calculateInfluence(game.getIslands().get(0)).get("ID Player"));
    }

    /**
     * This method verifies the correct working of checkGameOver method according to the island number
     */
    @Test
    void checkGameOverForIslandNumber(){
        Game game = new Game(2);
        game.instantiateGameElements();
        game.addPlayer(new Player(0,"leo",Tower.BLACK),0);
        game.addPlayer(new Player(1,"mari",Tower.WHITE),1);
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
        game.instantiateGameElements();
        game.addPlayer(new Player(0, "leo", Tower.BLACK),0);
        game.addPlayer(new Player(1, "mari", Tower.WHITE),1);
        assertFalse(game.checkGameOver());
        for (int i = 0; i < 2; i++)
            game.getPlayers().get(0).removeTowerFromBoard();
        assertFalse(game.checkGameOver());
        assertEquals(6, game.getPlayers().get(0).getBoard().getTowers());
        assertEquals(8, game.getPlayers().get(1).getBoard().getTowers());
        while (game.getPlayers().get(1).getBoard().getTowers() > 0)
            game.getPlayers().get(1).removeTowerFromBoard();
        assertTrue(game.checkGameOver());
    }


    /**
     * This method verifies the correct working of the method that moves the teachers
     * according to their color
     */
    @Test
    void teacherMovementTest(){
        Game game = new Game(2);
        game.instantiateGameElements();
        game.addPlayer(new Player(0, "leo", Tower.BLACK),0);
        game.addPlayer(new Player(1, "mari", Tower.WHITE),1);
        for (int i = 0; i< 7;i++)
            game.getPlayerByName("leo").addToBoardTable(Color.BLUE);
        game.getPlayerByName("leo").addToBoardTable(Color.PINK);
        game.getPlayerByName("leo").addToBoardTable(Color.YELLOW);
        game.getPlayerByName("leo").addToBoardTable(Color.GREEN);
        game.getPlayerByName("mari").addToBoardTable(Color.RED);

        game.updateTeachersOwnership(game.getPlayerByName("mari"));
        assertFalse(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.BLUE));
        assertFalse(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.PINK));
        assertFalse(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.GREEN));
        assertFalse(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.YELLOW));
        assertTrue(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.RED));
        game.updateTeachersOwnership(game.getPlayerByName("leo"));
        assertTrue(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.BLUE));
        assertTrue(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.PINK));
        assertTrue(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.GREEN));
        assertTrue(game.getPlayerByName("leo").getBoard().getTeacherTable().contains(Color.YELLOW));
        assertTrue(game.getPlayerByName("mari").getBoard().getTeacherTable().contains(Color.RED));

        game.getPlayerByName("leo").addToBoardTable(Color.RED);
        game.getPlayerByName("leo").addToBoardTable(Color.RED);
        game.updateTeachersOwnership(game.getPlayerByName("leo"));
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
        game.instantiateGameElements();
        game.addPlayer(new Player(0, "mari", Tower.GREY),0);
        game.addPlayer(new Player(1, "frizio", Tower.WHITE),1);
        game.setBasket(new Basket(new int[]{0,0,0,0,0}));
        game.refillClouds();
        assertTrue(game.isLastRound());
    }

    @Test
    void nullPlayerNameTest(){
        Game game = new Game(2);
        game.instantiateGameElements();
        game.addPlayer(new Player(0, "mari", Tower.GREY),0);
        game.addPlayer(new Player(1, "frizio", Tower.WHITE),1);
        assertNull(game.getPlayerByName("leo"));
    }

    @Test
    void islandOwnerTest(){
        Game game = new Game(2);
        game.instantiateGameElements();
        Player leo = new Player(0, "leo", Tower.BLACK);
        Player frizio = new Player(1, "frizio", Tower.WHITE);
        game.addPlayer(leo,0);
        game.addPlayer(frizio,1);
        game.getIslands().get(0).setOwner(leo);
        assertEquals(-1, game.getTowersOwnerIndex(game.getIslands().get(0), game.getPlayers()));
        game.getIslands().get(0).addTower(Tower.BLACK);
        assertEquals(0, game.getTowersOwnerIndex(game.getIslands().get(0), game.getPlayers()));
    }

    /**
     * TODO: sistemare metodo in modo che controlli che è manchi dal mazzo solo quella carta giocata (contains in teoria non va bene)
     */
    @Test
    void getPlayableAssistantCardTest(){
        Game game = new Game(2);
        game.instantiateGameElements();
        Player mari = new Player(0, "mari", Tower.BLACK);
        Player frizio = new Player(1, "frizio", Tower.WHITE);
        game.addPlayer(mari,0);
        game.addPlayer(frizio,1);
        assertEquals(10,game.getPlayableAssistantCards("mari").size());
        game.playAssistantCard("mari",1);
        Assistant playedCard= new Assistant(1,1,0);
        //assertFalse(game.getPlayableAssistantCards("mari").contains(playedCard)); //bisogna controllare che manchi solo quella carta giocata
        assertEquals(9,game.getPlayableAssistantCards("mari").size());
    }

    @Test
    void mergeTwoIslandsTest(){
        Game game = new Game(2);
        game.instantiateGameElements();
        Player mari = new Player(0, "leoviatano", Tower.BLACK);
        Player frizio = new Player(1, "frizio", Tower.WHITE);
        game.addPlayer(mari,0);
        game.addPlayer(frizio,1);
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
        island1.setOwner(mari);
        leftIsland.setOwner(mari);
        rightIsland.setOwner(frizio);
        game.mergeIslands(island1);
        assertEquals(11,game.islands.size());
        assertEquals(mari,island1.getOwner());
        assertEquals(testIsland,game.islands.get(10));
        assertEquals(oldNumbersOfStudent[0] + leftIsland.getStudentsOfColor(Color.RED).size(),island1.getStudentsOfColor(Color.RED).size());
        assertEquals(oldNumbersOfStudent[1] + leftIsland.getStudentsOfColor(Color.PINK).size(),island1.getStudentsOfColor(Color.PINK).size());
        assertEquals(oldNumbersOfStudent[2] + leftIsland.getStudentsOfColor(Color.BLUE).size(),island1.getStudentsOfColor(Color.BLUE).size()); //expected 3, actual 4
        //aggiungere caso in cui mergia 3 isole invece di 2
    }

    /**
     * TODO: controllare il funzionamento
     */
    @Test
    void isCardPlayableTest(){
        Game game = new Game(3);
        game.instantiateGameElements();
        Player mari = new Player(0, "mari", Tower.BLACK);
        Player frizio = new Player(1, "frizio", Tower.WHITE);
        Player leoviatano = new Player(2, "leoviatano", Tower.GREY);
        game.addPlayer(mari,0);
        game.addPlayer(frizio,1);
        game.addPlayer(leoviatano,2);
        ArrayList<Assistant> deck0 = mari.getDeck();
        ArrayList<Assistant> deck1 = frizio.getDeck();
        ArrayList<Assistant> deck2 = leoviatano.getDeck();
        assertEquals(true,game.isCardPlayable(deck0.get(0),deck0));
        assertEquals(true,game.isCardPlayable(deck1.get(0),deck1));
        game.playAssistantCard("mari",1); //cardid = 1 <==> deck0.get(0)
        assertEquals(false,game.isCardPlayable(deck1.get(0),deck1));
        assertEquals(true,game.isCardPlayable(deck1.get(1),deck1));
        assertEquals(false,game.isCardPlayable(deck2.get(0),deck2));
        assertEquals(true,game.isCardPlayable(deck2.get(1),deck2));
        game.playAssistantCard("frizio",2);
        assertEquals(false,game.isCardPlayable(deck0.get(1),deck0));
        assertEquals(true,game.isCardPlayable(deck0.get(2),deck0));
        assertEquals(false,game.isCardPlayable(deck2.get(1),deck2));
        assertEquals(true,game.isCardPlayable(deck2.get(2),deck2));
        Assistant deck2FirstElement = deck2.get(0);
        Assistant deck2SecondElement = deck2.get(1);
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

    //mancano test su moveMN e getPlayableAssistantCard
}

