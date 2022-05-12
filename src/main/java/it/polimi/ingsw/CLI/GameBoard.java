package it.polimi.ingsw.CLI;

import java.util.ArrayList;

public class GameBoard {
    int numberOfPlayers;
    boolean expertGame;
    //isole (con madre natura)
    ArrayList<Integer> availableWizards; //sono 4 e a ogni propagazione diminuiscono
    ArrayList<ClientIsland> islands;
    //board di tutti i giocatori
    //nomi dei giocatori
    //nuvole
    ArrayList<ClientCloud> clients;
    ArrayList<ClientBoard> clientBoards;
    //carte personaggio
    //monete


    void print(){

    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public boolean isExpertGame() {
        return expertGame;
    }

    public void setExpertGame(boolean expertGame) {
        this.expertGame = expertGame;
    }

    public ArrayList<Integer> getAvailableWizards() {
        return availableWizards;
    }

    public void setAvailableWizards(ArrayList<Integer> availableWizards) {
        this.availableWizards = availableWizards;
    }

    public ArrayList<ClientIsland> getIslands() {
        return islands;
    }

    public void setIslands(ArrayList<ClientIsland> islands) {
        this.islands = islands;
    }

    public ArrayList<ClientCloud> getClients() {
        return clients;
    }

    public void setClients(ArrayList<ClientCloud> clients) {
        this.clients = clients;
    }

    public ArrayList<ClientBoard> getClientBoards() {
        return clientBoards;
    }

    public void setClientBoards(ArrayList<ClientBoard> clientBoards) {
        this.clientBoards = clientBoards;
    }
}
