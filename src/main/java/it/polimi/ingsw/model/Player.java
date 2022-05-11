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
        listeners = new PropertyChangeSupport(this);
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

    public void addToBoardLobby(Color student){
        board.addToLobby(student);
        listeners.firePropertyChange("Board", null, this);
    }

    public Color removeFromBoardLobby(int index){
        Color removed = board.removeFromLobby(index);
        listeners.firePropertyChange("Board", null, this);
        return removed;
    }

    //in expert game ci sarà moveToTable di game per guadagnare le monete
    public boolean addToBoardTable(Color student){
        boolean gainCoin = board.addToTable(student);
        listeners.firePropertyChange("Board", null, this);
        return gainCoin;
    }

    public void removeFromBoardTable(Color student){
        board.removeFromTable(student);
        listeners.firePropertyChange("Board", null, this);
    }

    public void addTeacherToBoard(Color teacher){
        board.addTeacher(teacher);
        listeners.firePropertyChange("Board", null, this);
    }

    public Color removeTeacherFromBoard(Color teacher){
        Color removed = board.removeTeacher(teacher);
        listeners.firePropertyChange("Board", null, this);
        return removed;
    }

    public void addTowerToBoard(){
        board.addTower();
        listeners.firePropertyChange("Board", null, this);
    }

    public void removeTowerFromBoard(){
        board.removeTower();
        listeners.firePropertyChange("Board", null, this);
    }

    //vanno cambiati tutti i test

    public void setPropertyChangeListener(GameController controller) {
        listeners.addPropertyChangeListener("Deck", controller); //non sono sicuro sia un'informazione visibile a tutti
        listeners.addPropertyChangeListener("Board", controller);

    }
}


