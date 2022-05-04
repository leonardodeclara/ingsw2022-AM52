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
        players.add(nickname);
    }

    public void checkIfShouldStart(){
        shouldStart = players.size() == numberPlayersRequired ? true:false;
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

    public boolean isExpertGame() {
        return expertGame;
    }
}
