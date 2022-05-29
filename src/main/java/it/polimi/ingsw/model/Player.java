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
    private int coins;
    /**
     * Constructor creates a Player instance.
     * @param index: unique identification for the player.
     */
    public Player(int index, String nickname, boolean isExpert) {
        playerId = index;
        this.nickname=nickname;
        board = new Board();
        deck = new ArrayList<>();
        listeners = new PropertyChangeSupport(this);
        coins = isExpert ? 1 : 0;
    }

    /**
     * Method that assign a deck of Assistant card to a player
     * @param newDeck: ArrayList of Assistant cards that make up the deck
     */
    public void setDeck(ArrayList<Assistant> newDeck){
        deck.clear();
        deck.addAll(newDeck);
        listeners.firePropertyChange("Deck", null, this);
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
        listeners.firePropertyChange("Board", null, this);
    }

    /*
    TODO: testare le modifiche
     */
    //tolto il fire perché l'aggiornamento lato client viene gestito attraverso CurrentTurnAssistantCards in automatico
    //rimozione avviene attraverso la priority della carta
    public void removeAssistantCard(int cardPriority){
        for (int i =0; i< deck.size();i++)
        if (deck.get(i).getPriority()==cardPriority){
            deck.remove(i);
            return;
        }
        //listeners.firePropertyChange("Deck", null, this);
    }

    public void addToBoardLobby(Color student){
        board.addToLobby(student);
        listeners.firePropertyChange("Board", null, this);
    }

    public boolean removeFromBoardLobby(Color student){
        if(board.removeFromLobby(student)){
            listeners.firePropertyChange("Board", null, this);
            return true;
        }
        return false;
    }

    public boolean switchStudents(ArrayList<Color> tableStudents, ArrayList<Integer> lobbyStudentsIndexes){
        if (board.switchStudents(tableStudents,lobbyStudentsIndexes)){
            listeners.firePropertyChange("Board", null, this);
            return true;
        }
        return false;
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

    public void addTowersToBoard(int numTowers){
        for (int i= 0; i<numTowers;i++)
            board.addTower();
        listeners.firePropertyChange("Board", null, this);
    }

    public void removeTowerFromBoard(){
        board.removeTower();
        listeners.firePropertyChange("Board", null, this);
    }

    /*
    TODO: testarla
     */
    public Assistant getCardByPriority(int priority){
        for (Assistant card: deck){
            if (card.getPriority()==priority)
                return card;
        }
        return null;
    }

    public int getCoins(){
        return coins;
    }

    public void setCoins(int coins){
        this.coins=coins;
    }

    //vanno cambiati tutti i test

    public void setPropertyChangeListener(GameController controller) {
        listeners.addPropertyChangeListener("Deck", controller); //non sono sicuro sia un'informazione visibile a tutti
        listeners.addPropertyChangeListener("Board", controller);
    }
}


