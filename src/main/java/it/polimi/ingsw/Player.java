package it.polimi.ingsw;

import java.util.ArrayList;

/**
 * This class contanis the information of a PLayer.
 * Each player can be identified by its playerId.
 * Each player has a Board (?)
 */

public class Player {
    private int playerId;
    private String nickname;
    private Board board;
    private ArrayList<Assistant> deck;

    /**
     * Constructor creates a Player instance.
     * @param index: unique identification for the player.
     */

    public Player(int index, String nickname) {
        playerId = index;
        this.nickname=nickname;
        board = new Board();
        deck = new ArrayList<>();

    }
}


