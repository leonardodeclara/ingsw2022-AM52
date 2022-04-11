package it.polimi.ingsw.model;

import java.util.ArrayList;

/**
 * This class shapes the behaviour of the game's component represented by the island tile.
 * It can contain students, towers and Mother Nature, whose presence is reported by a boolean flag.
 * It may consist of merged islands.
 * It is uniquely identified by its islandIndex.
 */
public class Island {
    private final int islandIndex;
    private ArrayList<Tower> towers;
    private ArrayList<Color> students;
    private boolean motherNature;
    private int numMergedIslands;
    private Player owner;

    /**
     * Constructor creates an island instance.
     * @param index: unique identification for the island.
     */
    public Island(int index){
        islandIndex=index;
        towers = new ArrayList<>();
        students = new ArrayList<>();
        motherNature=false;
        numMergedIslands=1;
    }

    /**
     * This method returns a number which identify the island.
     * @return the island's index.
     */
    public int getIslandIndex() {
        return islandIndex;
    }

    /**
     * Method getTowers extracts the list of students placed on the island.
     * @return list containing the towers placed by players during the match.
     */
    public ArrayList<Color> getStudents() {
        return students;
    }

    /**
     * Method getTowers extracts the list of towers placed on the island.
     * @return ArrayList<Tower>: list containing the towers placed by eligible players.
     */
    public ArrayList<Tower> getTowers() {
        return towers;
    }

    /**
     * This method indicates the archipelago's dimension.
     * If the return value is 1 the island has not been merged yet.
     * @return the number of single islands that are part of the macro-island.
     */
    public int getNumMergedIslands() {
        return numMergedIslands;
    }

    /**
     * This method checks Mother Nature's presence.
     * @return true if Mother Nature is currently on the island tile, false otherwise.
     */
    public boolean isMotherNature() {
        return motherNature;
    }

    /**
     * This method set motherNature to true or false according to whether she's on the island or not.
     * @param motherNature: truth value regarding the mother nature's presence.
     */
    public void setMotherNature(boolean motherNature) {
        this.motherNature = motherNature;
    }

    /**
     * Add a tower object to the island's towers list.
     * @param tower: tower instance that needs to be added to the island's towers attribute.
     */
    public void addTower(Tower tower){
        towers.add(tower);
    }

    /**
     * Add a student tile to the island's students list.
     * @param student: studente tile that needs to be added to the island's students attribute.
     */
    public void addStudent(Color student){
        students.add(student);
    }

    /**
     * This method removes the island's towers, emptying the relative arraylist.
     * @return ArrayList<Tower>: containing the former island's towers.
     */
    public ArrayList<Tower> removeTower(){
        ArrayList<Tower> removedTowers = new ArrayList<>(towers);
        towers.clear();
        return removedTowers;
    }

    /**
     * This method is responsible for the merging of two eligible islands,
     * adding the "merged" island's content to the "merging" one.
     * @param merged:  island whose towers and students get added to the calling island.
     */
    public void merge(Island merged){
        students.addAll(merged.getStudents());
        towers.addAll(merged.getTowers());
        numMergedIslands+= 1;
        motherNature = motherNature || merged.isMotherNature();
    }

    public ArrayList<Color> getStudentsOfColor(Color c) {
        ArrayList<Color> studentsOfColor = new ArrayList<>();
        for(Color s : students)
            if(s.equals(c))
                studentsOfColor.add(s);
        return studentsOfColor;
    }

    public Player getOwner(){
        return owner;
    }

    public void setOwner(Player o){
        this.owner = o;
    }

    public Tower getOwnerTeam(){
        return towers.get(0);
    }
}
