package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static it.polimi.ingsw.Color.BLUE;
import static it.polimi.ingsw.Color.PINK;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    /**
     * This method aims at verifying the correct insertion of players to the game.
     */
    @Test
    void addPlayerTest(){
        Game game = new Game();
        game.addPlayer(new Player(0,"leo",Tower.BLACK));
        game.addPlayer(new Player(1,"frizio",Tower.WHITE));
        game.addPlayer(new Player(2,"mari",Tower.GREY));
        assertEquals(3, game.getPlayers().size());
        assertThrows(Exception.class, ()->game.addPlayer(new Player(3,"leo",Tower.GREY)));
    }

    /**
     * teacherInstantiationTest method verifies that five different teachers are correctly instantiated and added
     * to game's teachers attribute.
     */
    @Test
    void teacherInstantiationTest() {
        Game game = new Game();
        game.instantiateGameElements();
        assertEquals(5, game.getTeachers().size());
        for(int i = 0; i< game.getTeachers().size()-1; i++){
                for(int j=i+1;j<game.getTeachers().size();j++){
                    assertTrue(game.getTeachers().get(i).getColor()!=game.getTeachers().get(j).getColor());
                }
        }
    }

    /**
     * islandInstantiationTest examines the correct indexing of the newly created island tiles.
     */
    @Test
    void islandInstantiationTest(){
        Game game = new Game();
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
        Game game = new Game();
        game.instantiateGameElements();
        int countMotherNature=0;
        int indexMN=0;
        for (int i = 0; i< game.getIslands().size()-1; i++ ){
            if(game.getIslands().get(i).isMotherNature()){
                countMotherNature+=1;
                indexMN=i;
            }
        }
        assertEquals(1,countMotherNature);
        assertEquals(0,game.getIslands().get(indexMN).getStudents().size());

    }

    @Test
    void cloudInstantiationTest(){
        Game game = new Game();
        game.addPlayer(new Player(0,"leo",Tower.BLACK));
        game.addPlayer(new Player(1,"frizio",Tower.WHITE));
        game.addPlayer(new Player(2,"mari",Tower.GREY));
        game.instantiateGameElements();
        assertEquals(3, game.getClouds().size());
    }

    @Test
    void deckInstantiationTest() {
        Game game = new Game();
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
        Game game = new Game();
        Basket basket = new Basket(new int[]{2, 2, 2, 2, 2});
        game.setBasket(basket);
        for (int i = 0; i< 12; i++){
            game.getIslands().add(new Island(i));
        }
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
        Game game = new Game();
        game.addPlayer(new Player(0,"leo",Tower.BLACK));
        game.addPlayer(new Player(1,"frizio",Tower.WHITE));
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
        Game game = new Game();
        game.addPlayer(new Player(0,"leo",Tower.BLACK));
        game.addPlayer(new Player(1,"mari",Tower.WHITE));
        game.instantiateGameElements();
        game.giveAssistantDeck(0, 1);
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
        Game game = new Game();
        game.addPlayer(new Player(0,"leo",Tower.BLACK));
        game.addPlayer(new Player(1,"mari",Tower.WHITE));
        game.instantiateGameElements();
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
        Game game = new Game();
        game.addPlayer(new Player(0,"leo",Tower.BLACK));
        game.addPlayer(new Player(1,"mari",Tower.WHITE));
        game.instantiateGameElements();
        Assistant a0 = new Assistant(3,4,1);
        Assistant a1 = new Assistant(5,8,2);
        Assistant a2 = new Assistant(7,15,1);
        Assistant a3 = new Assistant(8,12,2);
        ArrayList<Assistant> d1 = new ArrayList<>();
        ArrayList<Assistant> d2 = new ArrayList<>();
        d1.add(a0);
        d1.add(a2);
        d2.add(a1);
        d2.add(a3);
        game.getPlayers().get(0).setDeck(d1);
        game.getPlayers().get(1).setDeck(d2);
        game.playAssistantCard(0,1); //giochiamo a2
        game.playAssistantCard(1,0); //giochiamo a1
        //ci si assicura che a1,a2 siano stati rimossi dai rispettivi deck
        assertEquals(false,game.getPlayers().get(0).getDeck().contains(a2));
        assertEquals(false,game.getPlayers().get(1).getDeck().contains(a1));
        //Ci si assicura che a1,a2 ora siano nelle celle dell'hashmap corrispondenti agli id dei giocatori che le hanno giocate
        assertEquals(a1,game.getCurrentTurnAssistantCards().get(0));
        assertEquals(a2,game.getCurrentTurnAssistantCards().get(1));
    }



    @Test
    void isMoveMNLegal(){
        Game game = new Game();
        game.addPlayer(new Player(0,"leo",Tower.BLACK));
        game.addPlayer(new Player(1,"mari",Tower.WHITE));
        game.instantiateGameElements();
        Assistant a0 = new Assistant(3,4,1);
        Assistant a1 = new Assistant(5,8,2);
        Assistant a2 = new Assistant(7,15,1);
        Assistant a3 = new Assistant(8,12,2);
        ArrayList<Assistant> d1 = new ArrayList<>();
        ArrayList<Assistant> d2 = new ArrayList<>();
        d1.add(a0);
        d1.add(a2);
        d2.add(a1);
        d2.add(a3);
        game.getPlayers().get(0).setDeck(d1);
        game.getPlayers().get(1).setDeck(d2);
        game.playAssistantCard(0,0); //giochiamo a0
        game.playAssistantCard(1,0); //giochaimo a1
        assertEquals(false,game.isMoveMNLegal(0,25));
        assertEquals(true,game.isMoveMNLegal(0,3));
        assertEquals(false,game.isMoveMNLegal(1,6));
        assertEquals(true,game.isMoveMNLegal(1,0));
    }

    @Test
    void isMoveStudentFromLobbyLegal(){
        Game game = new Game();
        game.addPlayer(new Player(0,"leo",Tower.BLACK));
        game.addPlayer(new Player(1,"mari",Tower.WHITE));
        game.instantiateGameElements();
        game.initiatePlayerLobby(0);
        game.initiatePlayerLobby(1);

        for(int i = 0;i<10;i++) //riempie completamente la table rosa di "leo"
            game.getPlayers().get(0).getBoard().addToTable(PINK);
        for(int i = 0;i<10;i++) //riempie completamente la table blu di "mari"
            game.getPlayers().get(1).getBoard().addToTable(BLUE);

        assertEquals(true, game.isMoveStudentFromLobbyLegal(game.getPlayers().get(0),0,3));
        assertEquals(true, game.isMoveStudentFromLobbyLegal(game.getPlayers().get(0),0,-1));
        assertEquals(false, game.isMoveStudentFromLobbyLegal(game.getPlayers().get(1),0,23));
    }


    @Test
    void isMoveStudentsToLobbyLegal(){
        Game game = new Game();
        game.addPlayer(new Player(0,"leo",Tower.BLACK));
        game.addPlayer(new Player(1,"mari",Tower.WHITE));
        game.instantiateGameElements();
        game.initiatePlayerLobby(0);
        game.initiatePlayerLobby(1);
        game.refillClouds();
        assertEquals(false,game.isMoveStudentsToLobbyLegal(game.getPlayers().get(0),-25));
        assertEquals(true,game.isMoveStudentsToLobbyLegal(game.getPlayers().get(0),1));
        assertEquals(true,game.isMoveStudentsToLobbyLegal(game.getPlayers().get(1),2));
        assertEquals(false,game.isMoveStudentsToLobbyLegal(game.getPlayers().get(0),1));
    }

    @Test
    void moveStudentFromLobby(){
        Game game = new Game();
        game.addPlayer(new Player(0,"leo",Tower.BLACK));
        game.addPlayer(new Player(1,"mari",Tower.WHITE));
        game.instantiateGameElements();
        game.initiatePlayerLobby(0);
        game.initiatePlayerLobby(1);

        //subtest per le isole
        Color s1 = game.getPlayers().get(0).getBoard().getLobbyStudent(0);
        Color s2 = game.getPlayers().get(1).getBoard().getLobbyStudent(0);
        game.moveStudentFromLobby(0,0,3);
        game.moveStudentFromLobby(1,0,4);
        assertEquals(true,game.getIslands().get(3).getStudents().contains(s1));
        assertEquals(true, game.getIslands().get(4).getStudents().contains(s2));
        assertEquals(false,game.getPlayers().get(0).getBoard().getLobby().contains(s1));
        assertEquals(false, game.getPlayers().get(1).getBoard().getLobby().contains(s2));

        //subtest per le table
        Color s3 = game.getPlayers().get(0).getBoard().getLobbyStudent(1);
        Color s4 = game.getPlayers().get(1).getBoard().getLobbyStudent(1);
        Integer old1 = game.getPlayers().get(0).getBoard().getStudentsTable().get(s3);
        Integer old2 = game.getPlayers().get(1).getBoard().getStudentsTable().get(s4);
        game.moveStudentFromLobby(0,1,-1);
        game.moveStudentFromLobby(1,1,-1);
        assertEquals(old1 + 1,game.getPlayers().get(0).getBoard().getStudentsTable().get(s3));
        assertEquals(old2 + 2, game.getPlayers().get(1).getBoard().getStudentsTable().get(s4));
        assertEquals(false,game.getPlayers().get(0).getBoard().getLobby().contains(s1));
        assertEquals(false, game.getPlayers().get(1).getBoard().getLobby().contains(s2));
    }


    @Test
    void moveStudentsToLobby(){
        Game game = new Game();
        game.addPlayer(new Player(0,"leo",Tower.BLACK));
        game.addPlayer(new Player(1,"mari",Tower.WHITE));
        game.instantiateGameElements();
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

    @Test
    void getEmptyClouds(){
        Game game = new Game();
        game.addPlayer(new Player(0,"leo",Tower.BLACK));
        game.addPlayer(new Player(1,"mari",Tower.WHITE));
        game.instantiateGameElements();
        ArrayList<Integer> Id1 = new ArrayList<>();
        Id1.add(0);
        Id1.add(1);
        assertEquals(Id1,game.GetEmptyCloudsID());
        game.refillClouds();
        ArrayList<Integer> Id2 = new ArrayList<>();
        assertEquals(Id2,game.GetEmptyCloudsID());

    }

}