package it.polimi.ingsw.CLI;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {

    /**
     * This method verifies the correct instantiation of the elements on a GameBoard instance
     */
    @Test
    void instantiationTest(){
        GameBoard game = new GameBoard();
        game.setNumberOfPlayers(2);
        game.setExpertGame(true);
        ArrayList<ClientIsland> islands = new ArrayList<>();
        for(int i= 0; i<12; i++){
            islands.add(new ClientIsland(i));
        }
        HashMap<String, ClientBoard> boards = new HashMap<>();
        ClientBoard mari = new ClientBoard("mari");
        ClientBoard leo = new ClientBoard("leo");
        boards.put("mari", mari);
        boards.put("leo", leo);
        ArrayList<ClientPersonality> personalities = new ArrayList<>();
        game.instantiateGameElements(islands, boards, personalities);
        assertEquals(12, game.getIslands().size());
        assertEquals(boards, game.getClientBoards());
        assertEquals(2, game.getClouds().size());
        assertEquals(18, game.getCoins());
    }

    /**
     * this method verifies the correct management of the cards played at each turn
     */
    @Test
    void setTurnCardTest() {
        GameBoard game = new GameBoard();
        game.setNumberOfPlayers(2);
        game.setExpertGame(true);
        ArrayList<ClientIsland> islands = new ArrayList<>();
        for(int i= 0; i<12; i++){
            islands.add(new ClientIsland(i));
        }
        HashMap<String, ClientBoard> boards = new HashMap<>();
        ClientBoard mari = new ClientBoard("mari");
        ClientBoard leo = new ClientBoard("leo");
        boards.put("mari", mari);
        boards.put("leo", leo);
        ArrayList<ClientPersonality> personalities = new ArrayList<>();
        game.instantiateGameElements(islands, boards, personalities);
        mari.initializeDeck();
        leo.initializeDeck();
        assertEquals(0, game.getTurnCards().get("mari"));
        assertEquals(0, game.getTurnCards().get("leo"));
        HashMap<String, Integer> playersCards = new HashMap<>();
        playersCards.put("mari", 8);
        playersCards.put("leo", 5);
        game.setTurnCard(playersCards);
        assertEquals(8, game.getTurnCards().get("mari"));
        assertEquals(5, game.getTurnCards().get("leo"));

    }

    /**
     * this method verifies the correct setting and removal of students on an island of the GameBoard instance
     */
    @Test
    void setIslandsStudentsTest(){
        GameBoard game = new GameBoard();
        game.setNumberOfPlayers(2);
        game.setExpertGame(true);
        ArrayList<ClientIsland> islands = new ArrayList<>();
        for(int i= 0; i<12; i++){
            islands.add(new ClientIsland(i));
        }
        HashMap<String, ClientBoard> boards = new HashMap<>();
        ClientBoard mari = new ClientBoard("mari");
        ClientBoard leo = new ClientBoard("leo");
        boards.put("mari", mari);
        boards.put("leo", leo);
        ArrayList<ClientPersonality> personalities = new ArrayList<>();
        game.instantiateGameElements(islands, boards, personalities);
        ArrayList<Color> students = new ArrayList<>();
        students.add(Color.BLUE);
        students.add(Color.GREEN);
        students.add(Color.RED);
        game.setIslandStudents(3, students);
        assertEquals(game.getIslandByIndex(3).getStudents(), students);


    }

    /**
     * this method verifies the correct setting and removal of towers on an island of the GameBoard instance
     */
    @Test
    void setIslandTowersTest(){
        GameBoard game = new GameBoard();
        game.setNumberOfPlayers(2);
        game.setExpertGame(true);
        ArrayList<ClientIsland> islands = new ArrayList<>();
        for(int i= 0; i<12; i++){
            islands.add(new ClientIsland(i));
        }
        game.setIslands(islands);
        HashMap<String, ClientBoard> boards = new HashMap<>();
        ClientBoard mari = new ClientBoard("mari");
        ClientBoard leo = new ClientBoard("leo");
        boards.put("mari", mari);
        boards.put("leo", leo);
        ArrayList<ClientPersonality> personalities = new ArrayList<>();
        game.instantiateGameElements(islands, boards, personalities);
        ArrayList<Tower> towers = new ArrayList<>();
        towers.add(Tower.WHITE);
        towers.add(Tower.WHITE);
        game.setIslandTowers(2, towers);
        assertEquals(game.getIslandByIndex(2).getTowers(), towers);

    }

    /**
     * this method verifies the correct behaviour of changeMNPosition method on GameBoard instance
     */
    @Test
    void changeMNpositionTest() {
        GameBoard game = new GameBoard();
        game.setNumberOfPlayers(2);
        game.setExpertGame(true);
        ArrayList<ClientIsland> islands = new ArrayList<>();
        for(int i= 0; i<12; i++){
            islands.add(new ClientIsland(i));
        }
        HashMap<String, ClientBoard> boards = new HashMap<>();
        ClientBoard mari = new ClientBoard("mari");
        ClientBoard leo = new ClientBoard("leo");
        boards.put("mari", mari);
        boards.put("leo", leo);
        ArrayList<ClientPersonality> personalities = new ArrayList<>();
        game.instantiateGameElements(islands, boards, personalities);
        game.getIslandByIndex(2).setMotherNature(true);
        game.changeMNPosition(4);
        assertEquals(true, game.getIslandByIndex(4).isMotherNature());
        assertEquals(2, game.getMotherNatureDistance(6));
        assertEquals(10, game.getMotherNatureDistance(2));
    }

    /**
     * this method verifies the correct updating of a GameBoard instance
     */
    @Test
    void setUpdatedClientBoardTest(){
        GameBoard game = new GameBoard();
        game.setNumberOfPlayers(2);
        game.setExpertGame(true);
        game.setNickname("mari");
        ArrayList<ClientIsland> islands = new ArrayList<>();
        for(int i= 0; i<12; i++){
            islands.add(new ClientIsland(i));
        }
        HashMap<String, ClientBoard> boards = new HashMap<>();
        ClientBoard mari = new ClientBoard("mari");
        ClientBoard leo = new ClientBoard("leo");
        boards.put("mari", mari);
        boards.put("leo", leo);
        ArrayList<ClientPersonality> personalities = new ArrayList<>();
        game.instantiateGameElements(islands, boards, personalities);
        game.setUpdatedClientBoard("mari", mari);
        assertEquals(mari, game.getOwningPlayerClientBoard());
    }

    /**
     * this method verifies the correct updating of coins on a GameBoard instance
     */
    @Test
    void updateCoins() {
        GameBoard game = new GameBoard();
        game.setNumberOfPlayers(2);
        game.setExpertGame(true);
        game.setNickname("mari");
        ArrayList<ClientIsland> islands = new ArrayList<>();
        for(int i= 0; i<12; i++){
            islands.add(new ClientIsland(i));
        }
        HashMap<String, ClientBoard> boards = new HashMap<>();
        ClientBoard mari = new ClientBoard("mari");
        ClientBoard leo = new ClientBoard("leo");
        boards.put("mari", mari);
        boards.put("leo", leo);
        ArrayList<ClientPersonality> personalities = new ArrayList<>();
        game.instantiateGameElements(islands, boards, personalities);
        game.updateCoins(5, "mari", 0);
        game.setCoins(5);
        assertEquals(5, mari.getCoins());
    }

    /**
     * this method verifies the correct setting and removal of bans on a GameBoard instance
     */
    @Test
    void setIslandBansTest(){
        GameBoard game = new GameBoard();
        game.setNumberOfPlayers(2);
        game.setExpertGame(true);
        game.setNickname("mari");
        ArrayList<ClientIsland> islands = new ArrayList<>();
        for(int i= 0; i<12; i++){
            islands.add(new ClientIsland(i));
        }
        HashMap<String, ClientBoard> boards = new HashMap<>();
        ClientBoard mari = new ClientBoard("mari");
        ClientBoard leo = new ClientBoard("leo");
        boards.put("mari", mari);
        boards.put("leo", leo);
        ArrayList<ClientPersonality> personalities = new ArrayList<>();
        game.instantiateGameElements(islands, boards, personalities);
        game.setIslandBans(3, 5);
        assertEquals(5, game.getIslandByIndex(3).getBans());
    }

    /**
     * this method verifies if method getNumberOfPlayers returns the correct number of players of the game
     */
    @Test
    void getNumberOfPlayersTest() {
        GameBoard game = new GameBoard();
        game.setNumberOfPlayers(2);
        game.setExpertGame(true);
        game.setNickname("mari");
        ArrayList<ClientIsland> islands = new ArrayList<>();
        for(int i= 0; i<12; i++){
            islands.add(new ClientIsland(i));
        }
        HashMap<String, ClientBoard> boards = new HashMap<>();
        ClientBoard mari = new ClientBoard("mari");
        ClientBoard leo = new ClientBoard("leo");
        boards.put("mari", mari);
        boards.put("leo", leo);
        ArrayList<ClientPersonality> personalities = new ArrayList<>();
        game.instantiateGameElements(islands, boards, personalities);
        assertEquals(2, game.getNumberOfPlayers());
    }

    /**
     * this method verifies if isExpertGame method returns the correct boolean value
     */
    @Test
    void isExpertGameTest(){
        GameBoard game = new GameBoard();
        game.setNumberOfPlayers(2);
        game.setExpertGame(true);
        game.setNickname("mari");
        ArrayList<ClientIsland> islands = new ArrayList<>();
        for(int i= 0; i<12; i++){
            islands.add(new ClientIsland(i));
        }
        HashMap<String, ClientBoard> boards = new HashMap<>();
        ClientBoard mari = new ClientBoard("mari");
        ClientBoard leo = new ClientBoard("leo");
        boards.put("mari", mari);
        boards.put("leo", leo);
        ArrayList<ClientPersonality> personalities = new ArrayList<>();
        game.instantiateGameElements(islands, boards, personalities);
        assertEquals(true, game.isExpertGame());
    }

    /**
     * this method verifies if method getAvailableWizards returns the correct available wizards
     */
    @Test
    void getAvailableWizardsTest(){
        GameBoard game = new GameBoard();
        game.setNumberOfPlayers(2);
        game.setExpertGame(true);
        game.setNickname("mari");
        ArrayList<ClientIsland> islands = new ArrayList<>();
        for(int i= 0; i<12; i++){
            islands.add(new ClientIsland(i));
        }
        HashMap<String, ClientBoard> boards = new HashMap<>();
        ClientBoard mari = new ClientBoard("mari");
        ClientBoard leo = new ClientBoard("leo");
        boards.put("mari", mari);
        boards.put("leo", leo);
        ArrayList<ClientPersonality> personalities = new ArrayList<>();
        game.instantiateGameElements(islands, boards, personalities);
        ArrayList<Integer> availableWizards = new ArrayList<>();
        game.setAvailableWizards(availableWizards);
        assertEquals(availableWizards, game.getAvailableWizards());
    }

    /**
     * this method verifies if getAvailableTowers method returns the correct available team
     */
    @Test
    void getAvailableTowersTest(){
        GameBoard game = new GameBoard();
        game.setNumberOfPlayers(2);
        game.setExpertGame(true);
        game.setNickname("mari");
        ArrayList<ClientIsland> islands = new ArrayList<>();
        for(int i= 0; i<12; i++){
            islands.add(new ClientIsland(i));
        }
        HashMap<String, ClientBoard> boards = new HashMap<>();
        ClientBoard mari = new ClientBoard("mari");
        ClientBoard leo = new ClientBoard("leo");
        boards.put("mari", mari);
        boards.put("leo", leo);
        ArrayList<ClientPersonality> personalities = new ArrayList<>();
        game.instantiateGameElements(islands, boards, personalities);
        mari.setTeam(Tower.WHITE);
        leo.setTeam(Tower.BLACK);
        ArrayList<Tower> availableTowers = new ArrayList<>();
        availableTowers.add(Tower.GREY);
        game.setAvailableTowers(availableTowers);
        assertEquals(availableTowers, game.getAvailableTowers());
        ;
    }

    /**
     * this method verifies the correct getting and removal of students on a cloud
     */
    @Test
    void cloudsTest() {
        GameBoard game = new GameBoard();
        game.setNumberOfPlayers(2);
        game.setExpertGame(true);
        game.setNickname("mari");
        ArrayList<ClientIsland> islands = new ArrayList<>();
        for(int i= 0; i<12; i++){
            islands.add(new ClientIsland(i));
        }
        HashMap<String, ClientBoard> boards = new HashMap<>();
        ClientBoard mari = new ClientBoard("mari");
        ClientBoard leo = new ClientBoard("leo");
        boards.put("mari", mari);
        boards.put("leo", leo);
        ArrayList<ClientPersonality> personalities = new ArrayList<>();
        game.instantiateGameElements(islands, boards, personalities);
        ClientCloud cloud = new ClientCloud(1);
        ArrayList<ClientCloud> clouds = new ArrayList<>();
        clouds.add(cloud);
        game.setClouds(clouds);
        ArrayList<Color> studentsOnCloud = new ArrayList<>();
        studentsOnCloud.add(Color.GREEN);
        studentsOnCloud.add(Color.BLUE);
        cloud.setStudents(studentsOnCloud);
        assertEquals(2, game.getCloudByIndex(1).getStudents().size());
        game.emptyCloud(1);
        assertEquals(0, game.getCloudByIndex(1).getStudents().size());
    }

    /**
     * this method verifies if method getPlayersNickname returns the correct string of nicknames
     */
    @Test
    void getPlayersNicknameTest(){
        GameBoard game = new GameBoard();
        game.setNumberOfPlayers(2);
        game.setExpertGame(true);
        game.setNickname("mari");
        ArrayList<ClientIsland> islands = new ArrayList<>();
        for(int i= 0; i<12; i++){
            islands.add(new ClientIsland(i));
        }
        HashMap<String, ClientBoard> boards = new HashMap<>();
        ClientBoard mari = new ClientBoard("mari");
        ClientBoard leo = new ClientBoard("leo");
        boards.put("mari", mari);
        boards.put("leo", leo);
        ArrayList<ClientPersonality> personalities = new ArrayList<>();
        game.instantiateGameElements(islands, boards, personalities);
        List<String> playersNickname = new ArrayList<>();
        playersNickname.add("leo");
        playersNickname.add("mari");
        assertEquals(playersNickname, game.getPlayersNicknames());

            }


    /**
     * this method verifies the correct setting and removal of students or bans on a personality card.
     * this method verifies the correct management of active personalities
     */
    @Test
    void personalityTest(){
        GameBoard game = new GameBoard();
        game.setNumberOfPlayers(2);
        game.setExpertGame(true);
        game.setNickname("mari");
        ArrayList<ClientIsland> islands = new ArrayList<>();
        for(int i= 0; i<12; i++){
            islands.add(new ClientIsland(i));
        }
        HashMap<String, ClientBoard> boards = new HashMap<>();
        ClientBoard mari = new ClientBoard("mari");
        ClientBoard leo = new ClientBoard("leo");
        boards.put("mari", mari);
        boards.put("leo", leo);
        ArrayList<ClientPersonality> personalities = new ArrayList<>();
        personalities.add(new ClientPersonality(1, true, 3));
        personalities.add(new ClientPersonality(2, false, 4));
        personalities.add(new ClientPersonality(3, true, 3));
        personalities.add(new ClientPersonality(4, false, 5));
        personalities.add(new ClientPersonality(5, false, 4));
        game.instantiateGameElements(islands, boards, personalities);
        assertEquals(personalities, game.getPersonalities());
        assertEquals(true, game.getPersonalityById(1).isHasBeenUsed());
        game.setActivePersonality(1);
        assertEquals(personalities.get(0), game.getActivePersonality());
        game.resetActivePersonality(1);
        assertEquals(null, game.getActivePersonality());
        ArrayList<Color> studentsOnTheCard = new ArrayList<>();
        studentsOnTheCard.add(Color.GREEN);
        studentsOnTheCard.add(Color.BLUE);
        game.updatePersonality(1, studentsOnTheCard, 3);
        assertEquals(studentsOnTheCard, game.getPersonalityById(1).getStudents());
    }


            }



