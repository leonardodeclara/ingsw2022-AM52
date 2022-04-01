package it.polimi.ingsw;

import java.util.ArrayList;

/**
 * This class contanis the information of a PLayer.
 * Each player can be identified by its playerId.
 * Each player has a Board (?)
 */

public class Player {
    private int playerId;
    private final String nickname;
    private Board board;
    private ArrayList<Assistant> deck;

    /**
     * Constructor creates a Player instance.
     * @param index: unique identification for the player.
     */
    public Player(int index, String nickname, Tower team) {
        playerId = index;
        this.nickname=nickname;
        board = new Board();
        deck = new ArrayList<>();

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

}


