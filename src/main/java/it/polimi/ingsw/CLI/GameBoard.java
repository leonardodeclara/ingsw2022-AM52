package it.polimi.ingsw.CLI;

import it.polimi.ingsw.model.Tower;

import java.util.ArrayList;

public class GameBoard {
    int numberOfPlayers;
    boolean expertGame;
    //isole (con madre natura)
    ArrayList<Integer> availableWizards; //sono 4 e a ogni propagazione diminuiscono
    ArrayList<Tower> availableTowers;
    ArrayList<ClientIsland> islands;
    //board di tutti i giocatori
    //nomi dei giocatori
    //nuvole
    ArrayList<ClientCloud> clouds;
    ArrayList<ClientBoard> clientBoards;
    ArrayList<ClientAssistant> deck;
    //monete

    public GameBoard(){
        availableWizards = new ArrayList<>();
        availableTowers = new ArrayList<>();
        islands = new ArrayList<>();
        clouds = new ArrayList<>();
        clientBoards = new ArrayList<>();
        deck = new ArrayList<>();
    }

    void print(){

    }

    public ArrayList<ClientCloud> getClouds() {
        return clouds;
    }

    public void setClouds(ArrayList<ClientCloud> clouds) {
        this.clouds = clouds;
    }

    public ArrayList<ClientAssistant> getDeck() {
        return deck;
    }

    public void setDeck(ArrayList<ClientAssistant> deck) {
        this.deck = deck;
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

    public ArrayList<Tower> getAvailableTowers() {
        return availableTowers;
    }

    public void setAvailableWizards(ArrayList<Integer> availableWizards) {
        this.availableWizards = availableWizards;
    }

    public void setAvailableTowers(ArrayList<Tower> availableTowers) {
        this.availableTowers = availableTowers;
    }

    public ArrayList<ClientIsland> getIslands() {
        return islands;
    }

    public void setIslands(ArrayList<ClientIsland> islands) {
        this.islands = islands;
    }

    public ArrayList<ClientCloud> getClients() {
        return clouds;
    }

    public void setClients(ArrayList<ClientCloud> clouds) {
        this.clouds = clouds;
    }

    public ArrayList<ClientBoard> getClientBoards() {
        return clientBoards;
    }

    public void setClientBoards(ArrayList<ClientBoard> clientBoards) {
        this.clientBoards = clientBoards;
    }
}
