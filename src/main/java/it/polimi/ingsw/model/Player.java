package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.GameController;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

/**
 * This class contains the information of a Player.
 * Each player can be identified by its nickname.
 * Each player has a Board, a deck of Assistant cards and a team.
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
     * Method that assigns a deck of Assistant card to a player
     * @param newDeck: ArrayList of Assistant cards that make up the deck
     */
    public void setDeck(ArrayList<Assistant> newDeck){
        deck.clear();
        deck.addAll(newDeck);
        listeners.firePropertyChange("Deck", null, this);
    }

    /**
     * Method that returns the instance of the player's board
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

    /**
     * Method removeAssistantCard removes a chosen card from the player's deck.
     * @param cardPriority: value of the chosen card's priority.
     */
    public void removeAssistantCard(int cardPriority){
        for (int i =0; i< deck.size();i++)
        if (deck.get(i).getPriority()==cardPriority){
            deck.remove(i);
            return;
        }
    }

    /**
     * Method addToBoardLobby adds the chosen student tile to the player's board's lobby.
     * @param student: chosen student tile.
     */
    public void addToBoardLobby(Color student){
        board.addToLobby(student);
        listeners.firePropertyChange("Board", null, this);
    }

    /**
     * Method removeFromBoardLobby removes the selected student tile from the player's board's lobby.
     * @param student: selected student tile.
     * @return true if the move has been correctly executed, false otherwise.
     */
    public boolean removeFromBoardLobby(Color student){
        if(board.removeFromLobby(student)){
            listeners.firePropertyChange("Board", null, this);
            return true;
        }
        return false;
    }

    /**
     * Method switchStudents switch the selected table student tiles with the the selected lobby students.
     * @param tableStudents: collection of student tiles to be moved from the player's board's table to the the lobby.
     * @param lobbyStudentsIndexes: collection of integers identifying the selected board's lobby students to be moved to the table.
     * @return true if the move has been correctly executed, false otherwise.
     */
    public boolean switchStudents(ArrayList<Color> tableStudents, ArrayList<Integer> lobbyStudentsIndexes){
        if (board.switchStudents(tableStudents,lobbyStudentsIndexes)){
            listeners.firePropertyChange("Board", null, this);
            return true;
        }
        return false;
    }

    /**
     * Method addToBoardTable adds the chosen student tile to the player's board table.
     * @param student: student tile which is being added to the player's board's table.
     * @return true if the student has been correctly moved to the table, false otherwise.
     */
    public boolean addToBoardTable(Color student){
        boolean gainCoin = board.addToTable(student);
        listeners.firePropertyChange("Board", null, this);
        return gainCoin;
    }

    /**
     * Method removeFromBoardTable removes the selected student from the player's board table.
     * @param student: student tile which is being removed the player's board's table.
     */
    public void removeFromBoardTable(Color student){
        board.removeFromTable(student);
        listeners.firePropertyChange("Board", null, this);
    }

    /**
     * Method addTeacherToBoard adds the selected teacher tile to the player's board.
     * @param teacher: teacher tile which is being added the player's board.
     */
    public void addTeacherToBoard(Color teacher){
        board.addTeacher(teacher);
        listeners.firePropertyChange("Board", null, this);
    }

    /**
     * Method removeTeacherFromBoard removes the selected teacher tile.
     * @param teacher: teacher tile which is being removed from the player's board.
     */
    public void removeTeacherFromBoard(Color teacher){
        Color removed = board.removeTeacher(teacher);
        listeners.firePropertyChange("Board", null, this);
    }

    /**
     * Method addTowersToBoard adds the selected amount of towers to the player's board.
     * @param numTowers: amount of towers that are being added to the player's board.
     */
    public void addTowersToBoard(int numTowers){
        for (int i= 0; i<numTowers;i++)
            board.addTower();
        listeners.firePropertyChange("Board", null, this);
    }

    /**
     * Method removeTowerFromBoard removes a single tower from the player's board.
     */
    public void removeTowerFromBoard(){
        board.removeTower();
        listeners.firePropertyChange("Board", null, this);
    }

    /**
     * Method getCardByPriority returns the Assistant card identified by the selected priority number.
     * @param priority: priority number identifying the Assistant card.
     * @return the wanted Assistant card if present, null otherwise.
     */
    public Assistant getCardByPriority(int priority){
        for (Assistant card: deck){
            if (card.getPriority()==priority)
                return card;
        }
        return null;
    }

    /**
     * Method getCoins returns the amount of coins owned by the player.
     * @return the amount of coins owned by the player.
     */
    public int getCoins(){
        return coins;
    }

    /**
     * Method setCoins sets the amount of coins owned by the player.
     * @param coins: amount of coins owned by the player.
     */
    public void setCoins(int coins){
        this.coins=coins;
    }

    /**
     * Method setPropertyChangeListener sets the listener of Player's state.
     * @param controller: controller instance listening to the game's changes.
     */
    public void setPropertyChangeListener(GameController controller) {
        listeners.addPropertyChangeListener("Deck", controller); //non sono sicuro sia un'informazione visibile a tutti
        listeners.addPropertyChangeListener("Board", controller);
    }
}


