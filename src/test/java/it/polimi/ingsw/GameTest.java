package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    /**
     * This method aims at verifying the correct insertion of players to the game.
     */
    @Test
    void addPlayerTest(){
        Game game = new Game();
        game.addPlayer(new Player(0,"gigi",Tower.BLACK));
        game.addPlayer(new Player(1,"mario",Tower.WHITE));
        game.addPlayer(new Player(2,"luca",Tower.GREY));
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
    @Test
    void cloudInstantiationTest(){
        Game game = new Game();
        game.addPlayer(new Player(0,"gigi",Tower.BLACK));
        game.addPlayer(new Player(1,"mario",Tower.WHITE));
        game.addPlayer(new Player(2,"luca",Tower.GREY));
        game.instantiateGameElements();
        assertEquals(3, game.getClouds().size());

    }

    /**
     * This method aims at checking the correct insertion of students to a player's lobby.
     */
    @Test
    void initiatePlayerLobby(){
        Game game = new Game();
        game.addPlayer(new Player(0,"gigi",Tower.BLACK));
        game.addPlayer(new Player(1,"mario",Tower.WHITE));
        game.instantiateGameElements();
        game.initiatePlayerLobby(0);
        assertEquals(7, game.getPlayers().get(0).getBoard().getLobby().size());

    }


    @Test
    void giveAssistantDeck() {


    }
}