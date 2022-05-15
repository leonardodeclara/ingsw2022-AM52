package it.polimi.ingsw.CLI;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tower;

import java.util.ArrayList;

public class ClientIsland {
    private int islandIndex;
    private ArrayList<Tower> towers;
    private ArrayList<Color> students;
    private boolean motherNature;
    private int numMergedIslands;
    private String owner;

    public ClientIsland(int islandIndex ) {
        this.islandIndex = islandIndex;
        this.towers = new ArrayList<>();
        this.students = new ArrayList<>();
        this.motherNature = false;
        this.numMergedIslands = 0;
        this.owner = null;
    }

    public int getIslandIndex() {
        return islandIndex;
    }

    public void setIslandIndex(int islandIndex) {
        this.islandIndex = islandIndex;
    }

    public ArrayList<Tower> getTowers() {
        return towers;
    }

    public void setTowers(ArrayList<Tower> towers) {
        this.towers = towers;
    }

    public ArrayList<Color> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<Color> students) {
        this.students = students;
    }

    public boolean isMotherNature() {
        return motherNature;
    }

    public void setMotherNature(boolean motherNature) {
        this.motherNature = motherNature;
    }

    public int getNumMergedIslands() {
        return numMergedIslands;
    }

    public void setNumMergedIslands(int numMergedIslands) {
        this.numMergedIslands = numMergedIslands;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
