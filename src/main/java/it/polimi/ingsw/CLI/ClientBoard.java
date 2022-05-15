package it.polimi.ingsw.CLI;

import it.polimi.ingsw.model.Color;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientBoard {
    private HashMap<Color,Integer> studentsTable;
    private ArrayList<Color> teacherTable;
    private ArrayList<Color> lobby;
    private int towers;
    String owner;

    public ClientBoard(HashMap<Color, Integer> studentsTable, ArrayList<Color> teacherTable, ArrayList<Color> lobby, int towers, String owner) {
        this.studentsTable = studentsTable;
        this.teacherTable = teacherTable;
        this.lobby = lobby;
        this.towers = towers;
        this.owner = owner;
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
}
