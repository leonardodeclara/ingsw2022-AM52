package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class ClientIsland holds basic information about an Island state in order to render its content through CLI and GUI interfaces.
 * It carries information about towers, student tiles and bans placed on it. It also allows verifying whether Mother Nature is placed on it or not.
 */
public class ClientIsland implements Serializable {
    static final long serialVersionUID = 42L;
    private int islandIndex;
    private ArrayList<Tower> towers;
    private ArrayList<Color> students;
    private boolean motherNature;
    private int numMergedIslands;
    private int bans;
    private String owner;

    /**
     * Constructor islandIndex creates a ClientIsland instance whose identification number is islandIndex.
     * @param islandIndex identification number for the island.
     */
    public ClientIsland(int islandIndex ) {
        this.islandIndex = islandIndex;
        this.towers = new ArrayList<>();
        this.students = new ArrayList<>();
        this.motherNature = false;
        this.numMergedIslands = 0;
        this.bans = 0;
        this.owner = null;
    }

    /**
     * Method print prints island's content on CLI interfaces.
     */
    public void print(){
        System.out.println("ISLAND " + getIslandIndex() + ":");
        System.out.print("STUDENTS ON THE ISLAND: ");
        for (Color color : students) {
            try{
                System.out.print(Constants.getStudentsColor(color) + "■ ");
            }
            catch (NullPointerException e){
                System.out.println("No studenti del colore " + color.toString());
                e.printStackTrace();
            }

            System.out.print(Constants.RESET);
        }
        System.out.println();
        System.out.print("TOWERS ON THE ISLAND: ");
        for (int i = 0; i < getTowers().size(); i++) {
            Tower towerOnIsland = getTowers().get(i);
            if (towerOnIsland.equals(Tower.WHITE))
                System.out.print(Constants.WHITE + Constants.FILLED_RECTANGLE + " ");
            else if (towerOnIsland.equals(Tower.BLACK))
                System.out.print(Constants.BLUE + Constants.FILLED_RECTANGLE+ " ");
            else if (towerOnIsland.equals(Tower.GREY))
                System.out.print(Constants.CYAN +Constants.FILLED_RECTANGLE+" ");
        }
        System.out.print(Constants.RESET);
        System.out.println();

        System.out.print("NUMBER OF MERGED ISLANDS: " + getNumMergedIslands() + "\n");

        if (isMotherNature())
            System.out.println("MOTHER NATURE IS HERE!");

        if (bans>0)
            System.out.println("THIS ISLAND HAS " + bans + " BAN" + (bans>1? "S":""));


    }
    public int getIslandIndex() {
        return islandIndex;
    }

    public void setBans(int bans){
        this.bans=bans;
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

    public int getBans() {
        return bans;
    }

    public void setNumMergedIslands(int numMergedIslands) {
        this.numMergedIslands = numMergedIslands;
    }

}
