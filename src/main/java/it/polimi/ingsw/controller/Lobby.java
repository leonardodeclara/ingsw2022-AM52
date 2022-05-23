package it.polimi.ingsw.controller;

import java.util.ArrayList;

public class Lobby {
    private ArrayList<String> players;
    private int numberPlayersRequired;
    private boolean expertGame;
    private boolean shouldStart;

    public Lobby(int numberPlayersRequired, boolean expertGame) {
        this.numberPlayersRequired = numberPlayersRequired;
        this.expertGame = expertGame;
        players = new ArrayList<>();
        this.shouldStart = false;
    }

    public void addToLobby(String nickname){
        System.out.println("Aggiungo "+nickname);
        players.add(nickname);
    }

    public void checkIfShouldStart(){
        shouldStart = players.size() == numberPlayersRequired;
    }

    public int getNumberPlayersRequired() {
        return numberPlayersRequired;
    }

    public boolean getShouldStart(){
        return shouldStart;
    }
    public ArrayList<String> getPlayers() {
        return new ArrayList<>(players);
    }

    public void removePlayer(String playerName){
        players.remove(playerName);
    }

    public boolean isExpertGame() {
        return expertGame;
    }
}
