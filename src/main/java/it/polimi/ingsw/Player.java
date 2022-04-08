package it.polimi.ingsw;

import java.util.ArrayList;

/**
 * This class contains the information of a PLayer.
 * Each player can be identified by its playerId.
 * Each player has a Board (?)
 */

public class Player {
    private int playerId;
    private final String nickname;
    private Board board;
    private ArrayList<Assistant> deck;
    private Tower team;

    /**
     * Constructor creates a Player instance.
     * @param index: unique identification for the player.
     */
    public Player(int index, String nickname, Tower team) {
        playerId = index;
        this.nickname=nickname;
        board = new Board();
        deck = new ArrayList<>();
        this.team = team;
    }

    public void setDeck(ArrayList<Assistant> deck){
        this.deck.addAll(deck);
    }

    public Board getBoard() {
        return board;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getNickname() {
        return nickname;
    }

    public ArrayList<Assistant> getDeck() {
        return deck;
    }

    public Tower getTeam() {
        return team;
    }

    public void setTeam(Tower team) {
        this.team = team;
    }

}


