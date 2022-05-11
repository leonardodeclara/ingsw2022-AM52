package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.GameController;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

/**
 * This class contains the information of a PLayer.
 * Each player can be identified by its playerId.
 * Each player has a Board
 */

public class Player {
    private int playerId;
    private final String nickname;
    private Board board;
    private ArrayList<Assistant> deck;
    private Tower team;
    private PropertyChangeSupport listeners;

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

    /**
     * Method that assign a deck of Assistant card to a player
     * @param newDeck: ArrayList of Assistant cards that make up the deck
     */
    public void setDeck(ArrayList<Assistant> newDeck){
        deck.clear();
        deck.addAll(newDeck);
    }

    /**
     * Method that return the instance of the player's board
     * @return instance of the board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Method that returns the ID given to the player
     * @return integer that represents the playerID
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * Method that returns the nickname of the player
     * @return string that represents the nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Method that returns the deck of Assistant card of the player
     * @return ArrayList of Assistant cards that make up the deck
     */
    public ArrayList<Assistant> getDeck() {
        return new ArrayList<>(deck);
    }

    /**
     * Method that returns the color of the tower that represent the team of the player
     * @return instance of the Tower representing the team
     */
    public Tower getTeam() {
        return team;
    }


    public void setTeam(Tower team) {
        this.team = team;
    }

    public void setPropertyChangeListener(GameController controller) {
        listeners.addPropertyChangeListener("Deck", controller);
        board.setPropertyChangeListener(controller);

    }
}


