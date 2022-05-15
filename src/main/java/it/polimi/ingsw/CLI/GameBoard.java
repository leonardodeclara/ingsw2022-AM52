package it.polimi.ingsw.CLI;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Personality;
import it.polimi.ingsw.model.Tower;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class GameBoard {
    int numberOfPlayers;
    boolean expertGame;
    ArrayList<Integer> availableWizards; //sono 4 e a ogni propagazione diminuiscono
    ArrayList<Tower> availableTowers;
    ArrayList<ClientIsland> islands;
    ArrayList<String> PlayersNickname;
    ArrayList<ClientCloud> clouds;
    ArrayList<ClientBoard> clientBoards;
    ArrayList<ClientPersonality> personalities;
    int coins;

    public GameBoard(){
        availableWizards = new ArrayList<>();
        availableTowers = new ArrayList<>();
        islands = new ArrayList<>();
        clouds = new ArrayList<>();
        PlayersNickname = new ArrayList<>();
        clientBoards = new ArrayList<>();
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
            islands.add(new ClientIsland(i, null, null, false, 0, null));
        }

        for(int i = 0; i < getNumberOfPlayers(); i++){
            clouds.add(new ClientCloud(i, null));
        }

        //messa a parte
        /*
        for(int i = 0; i < getNumberOfPlayers(); i++){
            clientBoards.add(new ClientBoard(null, null, null, 0, PlayersNickname.get(i)));
        }*/

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
    }

    private void printClientBoards(){
        for(ClientBoard clientBoard : clientBoards){
            //stampo il nickname
            System.out.println(clientBoard.getOwner().toUpperCase() + "'S SCHOOL");
            //stampo la lobby
            System.out.println("LOBBY:");
            for(Color color : Color.values()) {
                int numberOfStudentPerColor = (int) clientBoard.getLobby().stream().filter(c -> c == color).count();
                for (int i = 0; i < numberOfStudentPerColor; i++) {
                    System.out.print(Constants.getStudentsColor(color) + "■");
                }

            }
            System.out.println("\n");

            //stampo la StudentsTable
            System.out.println("STUDENTS TABLE:");
            for(Color color : Color.values()){
                int numberOfStudentPerColor1 = clientBoard.getStudentsTable().get(color);
                for (int i = 0; i < Constants.MAX_LOBBY_SIZE; i++)
                    System.out.print(Constants.getStudentsColor(color) + (i < numberOfStudentPerColor1 ? "○ " : "■ "));
                System.out.println();
            }

            //stampo la TeachersTable
            System.out.println("TEACHERS TABLE:");
            for(Color color : Color.values()){
                System.out.println(Constants.getStudentsColor(color) + (clientBoard.getTeacherTable().contains(color) ? "■ " : "○ "));
            }
            System.out.println();

            //stampo le torri
            /*System.out.println("TOWERS:");
                for(int i = 0; i<clientBoard.getTowers(); i++){
                if(tower.equals(Tower.GREY))
                    System.out.print(Constants.GREY + "♦ ");
                else if(tower.equals(Tower.BLACK))
                    System.out.print("♢ ");
                else if(tower.equals(Tower.WHITE))
                    System.out.print("♦ ");}

                System.out.println();*/


        }
    }

    //Mari occhio che se non ci sono studenti ti lancia una nullPointerException
    //gestiscilo con un try-catch
    private void printIslands() {
        for (ClientIsland island : islands) {
            System.out.println("ISOLA" + island.getIslandIndex() + ":");
            System.out.print("STUDENTS ON THE ISLAND: ");
            for (Color color : Color.values()) {
                int numberOfStudentsPerColor = (int) island.getStudents().stream().filter(c -> c == color).count();
                for (int i = 0; i < numberOfStudentsPerColor; i++) {
                    System.out.print(Constants.getStudentsColor(color) + "■ ");
                }
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
            System.out.println();

            System.out.print("NUMBER OF MERGED ISLANDS: " + island.getNumMergedIslands());

            if (island.isMotherNature())
                System.out.println("MOTHER NATURE IS HERE!");
            else System.out.println();


        }

    }



    private void printClouds() {
        for (ClientCloud cloud : clouds) {
            System.out.println("NUVOLA: " + cloud.getCloudIndex());
            try {
                for(Color color : Color.values()) {
                    int numberOfStudentPerColor = (int) cloud.getStudents().stream().filter(c -> c == color).count();
                    for (int i = 0; i < numberOfStudentPerColor; i++) {
                        System.out.print(Constants.getStudentsColor(color) + "■");
                    }

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


        public void addClientBoard(String playerName){
            int towerNumber = numberOfPlayers == 2 ? 8 : 6;
            clientBoards.add(new ClientBoard(towerNumber, playerName));
            if (isExpertGame())
                for (ClientBoard board : clientBoards)
                    board.setCoins(1);
        }

        public int getNumberOfPlayers() {
            return numberOfPlayers;
        }

        public boolean isExpertGame () {
            return expertGame;
        }

        public ArrayList<Integer> getAvailableWizards () {
            return availableWizards;
        }

        public ArrayList<Tower> getAvailableTowers () {
            return availableTowers;
        }

        public ArrayList<ClientIsland> getIslands () {
            return islands;
        }

        public ArrayList<String> getPlayersNickname () {
            return PlayersNickname;
        }

        public ArrayList<ClientCloud> getClouds () {
            return clouds;
        }

        public ArrayList<ClientBoard> getClientBoards () {
            return clientBoards;
        }

        public ArrayList<ClientPersonality> getPersonalities () {
            return personalities;
        }

        public int getCoins () {
            return coins;
        }

        public void setNumberOfPlayers(int numberOfPlayers){
            this.numberOfPlayers = numberOfPlayers;
        }

        public void setExpertGame(boolean expertGame){
            this.expertGame = expertGame;
        }

        public void setAvailableWizards (ArrayList < Integer > availableWizards) {
            this.availableWizards = availableWizards;
        }

        public void setAvailableTowers (ArrayList < Tower > availableTowers) {
            this.availableTowers = availableTowers;
        }

        public void setIslands (ArrayList < ClientIsland > islands) {
            this.islands = islands;
        }

        public void setPlayersNickname (ArrayList < String > playersNickname) {
            PlayersNickname = playersNickname;
        }

        public void setClouds (ArrayList < ClientCloud > clouds) {
            this.clouds = clouds;
        }

        public void setClientBoards (ArrayList < ClientBoard > clientBoards) {
            this.clientBoards = clientBoards;
        }

        public void setPersonalities (ArrayList < ClientPersonality > personalities) {
            this.personalities = personalities;
        }

        public void setCoins ( int coins){
            this.coins = coins;
        }
    }


