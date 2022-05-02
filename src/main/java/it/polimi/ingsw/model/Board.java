package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class contains the information and the actions that can be performed on a school's Board.
 *
 */

public class Board {

    private HashMap<Color,Integer> studentsTable;
    private ArrayList<Color> teacherTable;
    private ArrayList<Color> lobby;
    private int towers;

    /**
     * Constructor creates a Board instance.
     *
     */

    public Board() {
        studentsTable = new HashMap<Color, Integer>();
        studentsTable.put(Color.GREEN,0);
        studentsTable.put(Color.RED,0);
        studentsTable.put(Color.YELLOW,0);
        studentsTable.put(Color.PINK,0);
        studentsTable.put(Color.BLUE,0);
        teacherTable=new ArrayList<>();
        lobby=new ArrayList<>();
        towers = 0;
    }

    /**
     * Add a student object to the board's lobby
     * @param student
     */

    public void addToLobby(Color student) {
        lobby.add(student);
    }

    /**
     * Remove a student object from the board's lobby
     * @param index
     */

    public void removeFromLobby(int index){
        lobby.remove(index);
    }

    /**
     * Add each student object to the table corresponding to their color
     * This method also check if the students is the third, the sixth or the ninth added to the table for the expert
     * game mode for earn money
     * @param student instance of the student that has to be added
     * @return true if the student is the third, the sixth or the ninth added to the table, false otherwise
     */

    //vedo il colore del nuovo studente e lo uso come chiave ricavando l'attuale table di quel colore
    //aggiungo a quella table il nuovo studente e aggiorno la voce corrispondente nella hashmap
    //controllo l'indice a cui è stato messo il nuovo studente e traggo le conclusioni del caso
    public boolean addToTable(Color student) {
        Integer numOfStudents = studentsTable.get(student);
        numOfStudents++;
        studentsTable.put(student,numOfStudents);
        if (numOfStudents == 3 || numOfStudents == 6 || numOfStudents == 9)
            return true;
        return false;
    }


    public void removeFromTable(Color student){
        Integer numOfStudents = studentsTable.get(student);
        numOfStudents--;
        studentsTable.put(student,numOfStudents);
    }

    @SuppressWarnings("GrazieInspection")
    public void SwitchStudents() {
        /*cosa prende in input? Un colore? Un indice?*/

    }

    public void addTeacher(Color teacher){
        teacherTable.add(teacher);
    }

    public Color removeTeacher(Color teacher) {
        teacherTable.remove(teacher);
        return teacher;
    }

    public void addTower() {
        //bisogna inserire un controllo che non si inseriscano più torri di quanto permesso
        towers++;
    }

    public void removeTower(){
        towers--;
    }

    public ArrayList<Color> getLobby() {
        return lobby;
    }

    public Color getLobbyStudent(int lobbyIndex){
        return lobby.get(lobbyIndex);
    }

    public HashMap<Color, Integer> getStudentsTable() {
        return studentsTable;
    }

    public ArrayList<Color> getTeacherTable() {
        return teacherTable;
    }

    public boolean isTableFull(Color color){
        return studentsTable.get(color) == 10 ? true : false;
    }

    public int getTowers() {
        return towers;
    }

    public int getTableNumberOfStudents(Color tableColor){
        return studentsTable.get(tableColor);
    }

    public void setTowers(int towers) {
        this.towers = towers;
    }
}
