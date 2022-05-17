package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.Color;

import java.util.ArrayList;
import java.util.HashMap;
//da cambiare, bisogna creare un messaggio ad hoc per ogni cosa della lobby che cambia.
//altrimenti quando il messaggio di update arriva al client non capisce cosa è cambiato
public class BoardUpdateMessage implements Message{
    HashMap<Color, Integer> updatedBoardTable;
    ArrayList<Color> updatedLobbyTable;
    ArrayList<Color> updatedTeacherTable;
    int numTowers;
    String owner;

    public BoardUpdateMessage(HashMap<Color, Integer> updatedBoardTable, ArrayList<Color> updatedLobbyTable, ArrayList<Color> updatedTeacherTable, String owner, int numTowers) {
        this.updatedBoardTable = updatedBoardTable;
        this.updatedLobbyTable = updatedLobbyTable;
        this.updatedTeacherTable = updatedTeacherTable;
        this.owner = owner;
        this.numTowers = numTowers;
    }

    public HashMap<Color, Integer> getUpdatedBoardTable() {
        return updatedBoardTable;
    }

    public ArrayList<Color> getUpdatedLobbyTable() {
        return updatedLobbyTable;
    }

    public ArrayList<Color> getUpdatedTeacherTable() {
        return updatedTeacherTable;
    }

    public String getOwner() {
        return owner;
    }

    public int getNumTowers() {
        return numTowers;
    }
}
