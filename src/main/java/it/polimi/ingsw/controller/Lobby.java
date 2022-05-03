package it.polimi.ingsw.controller;

import java.util.ArrayList;

public class Lobby {
    private ArrayList<String> players;
    private int numberPlayersRequired;
    private boolean expertGame;

    public Lobby(int numberPlayersRequired, boolean expertGame) {
        this.numberPlayersRequired = numberPlayersRequired;
        this.expertGame = expertGame;
        players = new ArrayList<>();
    }

    public void addToLobby(String nickname){
        players.add(nickname);
    }

    public boolean enoughPlayerToStart(){
        return players.size() == numberPlayersRequired ? true:false;
    }

    public int getNumberPlayersRequired() {
        return numberPlayersRequired;
    }

    public ArrayList<String> getPlayers() {
        return new ArrayList<>(players);
    }

    public boolean isExpertGame() {
        return expertGame;
    }
}
