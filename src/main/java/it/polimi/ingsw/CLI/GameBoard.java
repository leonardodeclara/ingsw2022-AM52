package it.polimi.ingsw.CLI;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.exceptions.InvalidMoveException;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;

import javax.swing.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.*;

public class GameBoard {
    private int numberOfPlayers;
    private boolean expertGame;
    private String nickname;
    private ArrayList<Integer> availableWizards; //sono 4 e a ogni propagazione diminuiscono
    private ArrayList<Tower> availableTowers;
    private ArrayList<ClientIsland> islands;
    private ArrayList<String> PlayersNickname;
    private ArrayList<ClientCloud> clouds;
    private HashMap<String,ClientBoard> clientBoards; //deve diventare una mappa
    private ArrayList<ClientPersonality> personalities;
    private ClientPersonality activePersonality;
    private PrintStream outputStream;
    int coins;

//TODO sostituire con ascii art intestazioni "board","islands","clouds"

    public GameBoard(PrintStream outputStream){
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
        this.outputStream = outputStream;
        coins = 20; //intanto lo settiamo al massimo, ma in teoria all'inizio ogni giocatore ha una moneta
    }

    //usiamo questo metodo solo per inizializzare le cose che non dipendono dai giocatori in sé ma solo dai parametri di gioco,
    // quindi numero di giocatori e modalità
    public void instantiateGameElements(ArrayList<ClientIsland> newIslands, HashMap<String,ClientBoard> boards,ArrayList<ClientPersonality> personalities){
        islands.addAll(newIslands);
        for (String player: boards.keySet()){
            boards.get(player).setGB(this);
            clientBoards.put(player, boards.get(player));
        }

        for(int i = 0; i < numberOfPlayers; i++){
            clouds.add(new ClientCloud(i));
        }

        if(personalities!=null)
            this.personalities = new ArrayList<>(personalities);
    }

    public void print(){
        outputStream.print("\033[H\033[2J");
        outputStream.flush();
        printClientBoards();
        printClouds();
        printIslands();
        if (isExpertGame())
            printPersonalityCards(); //sistemare, deve stampare solo le carte estratte, non tutte
    }

    private void printClientBoards(){ //sarebbe meglio se ogni componente avesse un metodo print e qui venisse chiamato solo quello
        outputStream.println("*****************************************BOARDS DEI GIOCATORI*****************************************************");
        for(ClientBoard clientBoard : clientBoards.values()){
            clientBoard.print();
        }
        outputStream.println("\n");
    }

    private void printIslands() {
        outputStream.println("*****************************************ISOLE*****************************************");
        for (ClientIsland island : islands) {
            island.print();
        }
        outputStream.println("\n");
    }



    private void printClouds(){
        outputStream.println("*****************************************NUVOLE*****************************************");
        for (ClientCloud cloud : clouds){
            cloud.print();
        }
        outputStream.println("\n");
    }

   private void printPersonalityCards(){
        outputStream.println("AVAILABLE PERSONALITY CARDS:");
        for(ClientPersonality personality : personalities){
            outputStream.println("( ID: "+personality.getCardID() + " " +"Costo: "+personality.getCost()+" )");
        }
       outputStream.println();
        if(activePersonality!=null)
            outputStream.println("ACTIVE PERSONALITY CARD:"+activePersonality.getCardID());

    }

    public void setClientTeam(String playerNickname, Tower tower){
        clientBoards.get(playerNickname).setTeam(tower);
    }


    public void addClientBoard(String playerName){
        int towerNumber = numberOfPlayers == 2? 8 : 6;
        ClientBoard newBoard = new ClientBoard(towerNumber, playerName);
        if (isExpertGame())
            newBoard.setCoins(1);
        newBoard.initializeDeck();
        clientBoards.put(playerName,newBoard);
    }

    //Imposto la currentTurn card per ogni giocatore e tolgo quella carta dal mazzo del tizio
    //faccio con l'if perché in teoria l'associazione string-int di tutti i giocatori viene mandata ogni volta che un tizio gioca una carta
    //quindi in una partita a 2 il primo che l'ha giocata riceve due volte questo messaggio contente il suo nome e la carta giocata
    public void setTurnCard(HashMap<String,Integer> playersCards){
        if (playersCards.isEmpty())
            for (String player: clientBoards.keySet())
                clientBoards.get(player).setCurrentCard(0);
        else
            for (Map.Entry<String,Integer> playerToCard: playersCards.entrySet()){
                clientBoards.get(playerToCard.getKey()).setCurrentCard(playerToCard.getValue());
                if (clientBoards.get(playerToCard.getKey()).getDeck().containsKey(playerToCard.getValue()))
                    clientBoards.get(playerToCard.getKey()).getDeck().remove(playerToCard.getValue());
                }
    }

    public void setPlayerDeck(String player, HashMap<Integer, Integer> cards){
        clientBoards.get(player).setDeck(cards);
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
    public void changeMNPosition(int islandIndex){
        ClientIsland oldMNIsland = islands.stream().filter(ClientIsland::isMotherNature).findFirst().orElse(null);
        if (oldMNIsland !=null){
            oldMNIsland.setMotherNature(false);
        }
        getIslandByIndex(islandIndex).setMotherNature(true);
    }

    public void setUpdatedClientBoard(String player, ClientBoard clientBoard){
        ClientBoard board = clientBoards.get(player);
        board.setStudentsTable(clientBoard.getStudentsTable());
        board.setLobby(clientBoard.getLobby());
        board.setTeacherTable(clientBoard.getTeacherTable());
        board.setTowers(clientBoard.getTowers());
        board.setTeam(clientBoard.getTeam());
        //eventualmente si aggiungono gli altri set
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

    public boolean isPersonalityCardBeenPlayed(){
        return activePersonality != null;
    }
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setActivePersonality(int activePersonality) {
        Optional<ClientPersonality> activePers = personalities.stream()
                                                .filter(clientPersonality -> clientPersonality.getCardID() == activePersonality)
                                                .findFirst();
        if(activePers.isPresent())
            this.activePersonality = activePers.get();
        else
            new Throwable().printStackTrace(); //non dovrebbe mai accadere quindi mettiamo eccezione così nel caso in runtime salta fuori un bug
    }

    public void resetActivePersonality(int inactivePersonality){
        outputStream.println("La carta personaggio "+inactivePersonality+ " non è più attiva!");
        this.activePersonality = null;
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


