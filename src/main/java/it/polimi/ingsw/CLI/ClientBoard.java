package it.polimi.ingsw.CLI;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Assistant;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientBoard implements Serializable {
    static final long serialVersionUID = 42L;
    private HashMap<Color,Integer> studentsTable;
    private ArrayList<Color> teacherTable;
    private ArrayList<Color> lobby;
    private HashMap<Integer, Integer> deck;
    private int towers;
    private int coins;
    private String owner;
    private Tower team;
    private int currentCard;
    private GameBoard GB; //TODO: da togliere, gestire in qualche altro modo il fatto di stampare le carte assistente o no

    public ClientBoard(String owner){
        this.owner = owner;
        this.studentsTable = new HashMap<>();
        this.teacherTable = new ArrayList<>();
        this.lobby = new ArrayList<>();
        this.deck = new HashMap<>();
    }

    public ClientBoard(int towers, String owner) {
        this.towers = towers;
        this.owner = owner;
        this.studentsTable = new HashMap<>();
        this.teacherTable = new ArrayList<>();
        this.lobby = new ArrayList<>();
        this.deck = new HashMap<>();
    }

    public void print(){
        //stampo il nickname
        System.out.println("************************************************"+getOwner().toUpperCase() + "'S SCHOOL"+"************************************************");
        //stampo la lobby
        System.out.print("LOBBY: ");
        for(int i=0;i<lobby.size();i++){
            System.out.print(Constants.getStudentsColor(lobby.get(i)) + "■ ");
            System.out.print(Constants.RESET);
        }

        System.out.print("\n");

        //stampo la StudentsTable
        System.out.println("STUDENTS TABLE: ");
        try {
            for(Color color : studentsTable.keySet()){
                for (int i = 0; i < Constants.MAX_LOBBY_SIZE; i++)
                    System.out.print(Constants.getStudentsColor(color) + (i < studentsTable.get(color) ? "■ " : "○ "));
                System.out.print("\n");
                System.out.print(Constants.RESET);
            }

        } catch (NullPointerException e){
            System.out.println("No students in the Table");
        }


        //stampo la TeachersTable
        System.out.print("TEACHERS TABLE: ");
        try {
            for (Color color : Color.values()) {
                System.out.print(Constants.getStudentsColor(color) + (getTeacherTable().contains(color) ? "■ " : "○ "));
                System.out.print(Constants.RESET);
            }
            System.out.print("\n");
        } catch(NullPointerException e){
            System.out.println("No teachers in the Table");
        }

        //stampo le torri
        System.out.print("TOWERS:\n");
        try {
            for (int i = 0; i < getTowers(); i++) {
                if (getTeam().equals(Tower.BLACK))
                    System.out.print("♢ ");
                else if (getTeam().equals(Tower.WHITE))
                    System.out.print("♦ ");
                else if (getTeam().equals(Tower.GREY))
                    System.out.print(Constants.GREY + "♦ ");

            }
            System.out.print("\n");
            System.out.print(Constants.RESET);
        }catch (NullPointerException e){
            System.out.println("No towers");
        }
        System.out.println();

        if (getCurrentCard()!= 0){
            System.out.println("CURRENT ASSISTANT CARD:" + getCurrentCard());
            System.out.println();
        }



        //mancano da stampare: monete (se in expert game)
        if(GB.getNickname().equals(getOwner())){
            for(Map.Entry<Integer,Integer> entry : getDeck().entrySet()){
                System.out.println("CARTA ASSISTENTE "+entry.getKey()+":"+"(priorità: "+entry.getKey()+",numero mosse: "+entry.getValue()+")");
            }
        }

        System.out.print("\n\n");





    }
    public void initializeDeck(){
        for(int numWizard = 0; numWizard < 4; numWizard++){
            int numMoves;
            for(int priority = 1; priority < 11; priority++){
                numMoves = priority%2==0 ? priority/2 : priority/2+1;
                deck.put(priority,numMoves);
            }
        }
    }

    public void setGB(GameBoard GB) {
        this.GB = GB;
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

    public HashMap<Integer, Integer> getDeck() {
        return deck;
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
