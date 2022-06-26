package it.polimi.ingsw.controller;

import java.util.ArrayList;

/**
 * This class manages the phase between the connection of a client and the beginning of a new game.
 * According to the game parameters selected a newly connected client is being added to a Lobby instance, where he will wait for a new game to start.
 */
public class Lobby {
    private ArrayList<String> players;
    private int numberPlayersRequired;
    private boolean expertGame;
    private boolean shouldStart;

    /**
     * Constructor Lobby creates a new Lobby instance and sets the game parameters accordingly to the choice of the client.
     * @param numberPlayersRequired: number of players that will take part in the game.
     * @param expertGame: game mode, it can be expert or base.
     */
    public Lobby(int numberPlayersRequired, boolean expertGame) {
        this.numberPlayersRequired = numberPlayersRequired;
        this.expertGame = expertGame;
        players = new ArrayList<>();
        this.shouldStart = false;
    }

    /**
     * Method addToLobby adds a new player name to the list of players waiting in the lobby.
     * @param nickname: name of the player added to the lobby.
     */
    public void addToLobby(String nickname){
        players.add(nickname);
    }

    /**
     * Method checkIfShouldStart sets the shouldStart attribute based on  the number of players in the lobby.
     */
    public void checkIfShouldStart(){
        shouldStart = players.size() == numberPlayersRequired;
    }

    /**
     * Method getNumberPlayersRequired returns the maximum number of players that will be added to the lobby.
     * @return the maximum number of players that will be added to the lobby.
     */
    public int getNumberPlayersRequired() {
        return numberPlayersRequired;
    }

    /**
     * Method getShouldStart returns the boolean attribute that determines if there are enough players in order to start a new game.
     * @return true if the maximum number of players has been reached, false otherwise.
     */
    public boolean getShouldStart(){
        return shouldStart;
    }

    /**
     * Method getPlayers returns a list containing of nicknames of the players admitted to the lobby.
     * @return the list of nicknames of the players waiting.
     */
    public ArrayList<String> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * Method removePlayer removes the player with nickname playerName from the lobby.
     * @param playerName: name of the player being removed from the lobby.
     */
    public void removePlayer(String playerName){
        players.remove(playerName);
    }

    /**
     * Method isExpertGame returns the value of expertGame attribute stating the expected game mode.
     * @return the game mode setted for this lobby.
     */
    public boolean isExpertGame() {
        return expertGame;
    }
}
