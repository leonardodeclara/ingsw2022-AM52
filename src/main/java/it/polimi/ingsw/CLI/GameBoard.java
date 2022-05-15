package it.polimi.ingsw.CLI;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;

import java.util.*;

public class GameBoard {
    int numberOfPlayers;
    boolean expertGame;
    ArrayList<Integer> availableWizards; //sono 4 e a ogni propagazione diminuiscono
    ArrayList<Tower> availableTowers;
    ArrayList<ClientIsland> islands;
    ArrayList<String> PlayersNickname;
    ArrayList<ClientCloud> clouds;
    HashMap<String,ClientBoard> clientBoards; //deve diventare una mappa
    ArrayList<ClientPersonality> personalities;
    int coins;

    public GameBoard(){
        availableWizards = new ArrayList<>();
        availableTowers = new ArrayList<>();
        islands = new ArrayList<>();
        clouds = new ArrayList<>();
        PlayersNickname = new ArrayList<>();
        clientBoards = new HashMap<>();
        personalities = new ArrayList<>();
        availableWizards.add(1);
        availableWizards.add(2);
        availableWizards.add(3);
        availableWizards.add(4);
        coins = 20; //intanto lo settiamo al massimo, ma in teoria all'inizio ogni giocatore ha una moneta
    }

    //usiamo questo metodo solo per inizializzare le cose che non dipendono dai giocatori in sé ma solo dai parametri di gioco,
    // quindi numero di giocatori e modalità
    public void instantiateGameElements(){
        for(int i = 0; i < Constants.MAX_NUM_ISLANDS; i++){
            islands.add(new ClientIsland(i));
        }

        for(int i = 0; i < getNumberOfPlayers(); i++){
            clouds.add(new ClientCloud(i));
        }

        //rivedere se ha senso aggiungere tutte le carte qui, tanto ne vengono estratte casualmente solo tre
        personalities.add(new ClientPersonality(1, false, 1));
        personalities.add(new ClientPersonality(2, false, 2));
        personalities.add(new ClientPersonality(3, false, 3));
        personalities.add(new ClientPersonality(4, false, 1));
        personalities.add(new ClientPersonality(5, false, 2));
        personalities.add(new ClientPersonality(6, false, 3));
        personalities.add(new ClientPersonality(7, false, 1));
        personalities.add(new ClientPersonality(8, false, 2));
        personalities.add(new ClientPersonality(9, false, 3));
        personalities.add(new ClientPersonality(10, false, 1));
        personalities.add(new ClientPersonality(11, false, 2));
        personalities.add(new ClientPersonality(12, false, 3));

    }

    public void print(){
        printClientBoards();
        printClouds();
        printIslands();
        printPersonalityCards();
    }

    private void printClientBoards(){
        for(ClientBoard clientBoard : clientBoards.values()){
            //stampo il nickname
            System.out.println(clientBoard.getOwner().toUpperCase() + "'S SCHOOL");
            //stampo la lobby
            System.out.println("LOBBY:");
            try {
                for (Color color : Color.values()) {
                    int numberOfStudentPerColor = (int) clientBoard.getLobby().stream().filter(c -> c == color).count();
                    for (int i = 0; i < numberOfStudentPerColor; i++) {
                        System.out.print(Constants.getStudentsColor(color) + "■");
                        System.out.println(Constants.RESET);
                    }


                }
                System.out.println("\n");
            } catch (NullPointerException e){
                System.out.println("No students in Lobby");
            }

            //stampo la StudentsTable
            System.out.println("STUDENTS TABLE:");
            try {
                for (Color color : Color.values()) {
                    int numberOfStudentPerColor1 = clientBoard.getStudentsTable().get(color);
                    for (int i = 0; i < Constants.MAX_LOBBY_SIZE; i++)
                        System.out.print(Constants.getStudentsColor(color) + (i < numberOfStudentPerColor1 ? "○ " : "■ "));
                    System.out.println();
                    System.out.println(Constants.RESET);
                }
            } catch (NullPointerException e){
                System.out.println("No students in the Table");
            }


            //stampo la TeachersTable
            System.out.println("TEACHERS TABLE:");
            try {
                for (Color color : Color.values()) {
                    System.out.println(Constants.getStudentsColor(color) + (clientBoard.getTeacherTable().contains(color) ? "■ " : "○ "));
                    System.out.println(Constants.RESET);
                }
                System.out.println();
            } catch(NullPointerException e){
                System.out.println("No teachers in the Table");
            }

            //stampo le torri
            System.out.println("TOWERS:");
            try {
                for (int i = 0; i < clientBoard.getTowers(); i++) {
                    if (clientBoard.getTeam().equals(Tower.BLACK))
                        System.out.print("♢ ");
                    else if (clientBoard.getTeam().equals(Tower.WHITE))
                        System.out.print("♦ ");
                    else if (clientBoard.getTeam().equals(Tower.GREY))
                        System.out.print(Constants.GREY + "♦ ");

                }
                System.out.println(Constants.RESET);
            }catch (NullPointerException e){
                System.out.println("No towers");
            }


        }
    }

    //Mari occhio che se non ci sono studenti ti lancia una nullPointerException
    //gestiscilo con un try-catch
    private void printIslands() {
        for (ClientIsland island : islands) {
            System.out.println("ISOLA" + island.getIslandIndex() + ":");
            System.out.print("STUDENTS ON THE ISLAND: ");
            for (Color color : Color.values()) {
                try{
                    int numberOfStudentsPerColor = (int) island.getStudents().stream().filter(c -> c == color).count();
                    for (int i = 0; i < numberOfStudentsPerColor; i++) {
                        System.out.print(Constants.getStudentsColor(color) + "■ ");
                    }
                }
                catch (NullPointerException e){
                    System.out.println("No studenti del colore " + color.toString());
                }

                System.out.println(Constants.RESET);
            }
            System.out.println();
            System.out.print("TOWERS ON THE ISLAND: ");
            for (int i = 0; i < island.getTowers().size(); i++) {
                Tower towerOnIsland = island.getTowers().get(i);
                if (towerOnIsland.equals(Tower.WHITE))
                    System.out.print("♦ ");
                else if (towerOnIsland.equals(Tower.BLACK))
                    System.out.print("♢ ");
                else if (towerOnIsland.equals(Tower.GREY))
                    System.out.print(Constants.GREY + "♦ ");
            }
            System.out.println(Constants.RESET);
            System.out.println();

            System.out.println("NUMBER OF MERGED ISLANDS:" + island.getNumMergedIslands());

            if (island.isMotherNature())
                System.out.println("MOTHER NATURE IS HERE!");
            else System.out.println();


        }

    }



    private void printClouds(){
        for (ClientCloud cloud : clouds){
            System.out.println("NUVOLA: " + cloud.getCloudIndex());
            try {
                for(Color color : Color.values()) {
                    int numberOfStudentPerColor = (int) cloud.getStudents().stream().filter(c -> c == color).count();
                    for (int i = 0; i < numberOfStudentPerColor; i++) {
                        System.out.print(Constants.getStudentsColor(color) + "■");
                    }
                    System.out.println(Constants.RESET);
                }

            } catch (NullPointerException e) {
                System.out.println("no studenti");
            }
        }
    }
   private void printPersonalityCards(){
        System.out.println("AVAILABLE PERSONALITY CARDS:");
        for(ClientPersonality personality : personalities){
            if(personality.getHasBeenUsed().equals(false))
                System.out.print(personality);

        }
    }

    public void setClientTeam(String playerNickname, Tower tower){
        clientBoards.get(playerNickname).setTeam(tower);
    }


    public void addClientBoard(String playerName){
        int towerNumber = numberOfPlayers == 2? 8 : 6;
        ClientBoard newBoard = new ClientBoard(towerNumber, playerName);
        if (isExpertGame())
            newBoard.setCoins(1);
        clientBoards.put(playerName,newBoard);
    }

    public void setTurnCard(HashMap<String,Integer> playersCards){
        for (Map.Entry<String,Integer> card: playersCards.entrySet()){
            clientBoards.get(card.getKey()).setCurrentCard(card.getValue());
        }
    }

    public void setPlayerDeck(String player, HashMap<Integer, Integer> cards){
        //aggiorno il deck di quel player
    }

    public void setIslandStudents(int islandIndex, ArrayList<Color> students){
        getIslandByIndex(islandIndex).setStudents(students);
    }

    public void setIslandTowers(int islandIndex, ArrayList<Tower> towers){
        getIslandByIndex(islandIndex).setTowers(towers);
    }

    public void emptyCloud(int cloudIndex){
        getCloudByIndex(cloudIndex).getStudents().clear();

    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public boolean isExpertGame() {
        return expertGame;
    }

    public ArrayList<Integer> getAvailableWizards() {
        return availableWizards;
    }

    public ArrayList<Tower> getAvailableTowers() {
        return availableTowers;
    }

    public ClientIsland getIslandByIndex(int index){
        for (ClientIsland island : islands){
            if (island.getIslandIndex()==index)
                return island;
        }
        return null;
    }

    public ClientCloud getCloudByIndex(int index){
        for (ClientCloud cloud: clouds){
            if (cloud.getCloudIndex()==index)
                return cloud;
        }
        return null;
    }

    public ArrayList<ClientIsland> getIslands() {
        return islands;
    }

    public ArrayList<String> getPlayersNickname() {
        return PlayersNickname;
    }

    public ArrayList<ClientCloud> getClouds() {
        return clouds;
    }

    public HashMap<String,ClientBoard> getClientBoards() {
        return clientBoards;
    }

    public ArrayList<ClientPersonality> getPersonalities() {
        return personalities;
    }

    public int getCoins() {
        return coins;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public void setExpertGame(boolean expertGame) {
        this.expertGame = expertGame;
    }

    public void setAvailableWizards(ArrayList<Integer> availableWizards) {
        this.availableWizards = availableWizards;
    }

    public void setAvailableTowers(ArrayList<Tower> availableTowers) {
        this.availableTowers = availableTowers;
    }

    public void setIslands(ArrayList<ClientIsland> islands) {
        this.islands = islands;
    }

    public void setPlayersNickname(ArrayList<String> playersNickname) {
        PlayersNickname = playersNickname;
    }

    public void setClouds(ArrayList<ClientCloud> clouds) {
        this.clouds = clouds;
    }

    public void setClientBoards(HashMap<String,ClientBoard> clientBoards) {
        this.clientBoards = clientBoards;
    }

    public void setPersonalities(ArrayList<ClientPersonality> personalities) {
        this.personalities = personalities;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }
}


