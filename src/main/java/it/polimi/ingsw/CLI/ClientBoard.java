package it.polimi.ingsw.CLI;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientBoard {
    private HashMap<Color,Integer> studentsTable;
    private ArrayList<Color> teacherTable;
    private ArrayList<Color> lobby;
    private HashMap<Integer, Integer> deck;
    private int towers;
    private int coins;
    private String owner;
    private Tower team;
    private int currentCard;


    public ClientBoard(int towers, String owner) {
        this.studentsTable = new HashMap<>();
        this.teacherTable = new ArrayList<>();
        this.lobby = new ArrayList<>();
        this.deck = new HashMap<>();
        this.towers = towers;
        this.owner = owner;
    }

    public void setCurrentCard(int currentCard) {
        this.currentCard = currentCard;
    }

    public int getCurrentCard() {
        return currentCard;
    }

    public HashMap<Color, Integer> getStudentsTable() {
        return studentsTable;
    }

    public void setStudentsTable(HashMap<Color, Integer> studentsTable) {
        this.studentsTable = studentsTable;
    }

    public ArrayList<Color> getTeacherTable() {
        return teacherTable;
    }

    public void setTeacherTable(ArrayList<Color> teacherTable) {
        this.teacherTable = teacherTable;
    }

    public ArrayList<Color> getLobby() {
        return lobby;
    }

    public void setLobby(ArrayList<Color> lobby) {
        this.lobby = lobby;
    }

    public void setCoins(int coins){
        this.coins=coins;
    }

    public int getTowers() {
        return towers;
    }

    public void setTowers(int towers) {
        this.towers = towers;
    }

    public String getOwner() {
        return owner;
    }


    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getCoins() {
        return coins;
    }


    public Tower getTeam() {
        return team;
    }

    public void setTeam(Tower team) {
        this.team = team;
    }

    public void setDeck(HashMap<Integer, Integer> deck) {
        this.deck = deck;
    }
}
