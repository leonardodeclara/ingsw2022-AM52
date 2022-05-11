package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.GameController;

import java.beans.PropertyChangeSupport;
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


    /**
     * Remove each student object from the table
     * @param student: instance of the student that has to be removed
     */
    public void removeFromTable(Color student){
        Integer numOfStudents = studentsTable.get(student);
        numOfStudents--;
        studentsTable.put(student,numOfStudents);
    }

    @SuppressWarnings("GrazieInspection")
    public void SwitchStudents() {
        /*cosa prende in input? Un colore? Un indice?*/

    }

    /**
     * Add each teacher object to the table corresponding to its color
     * @param teacher: instance of the teacher that has to be added
     */
    public void addTeacher(Color teacher){
        teacherTable.add(teacher);
    }

    /**
     * Remove each teacher from the table
     * @param teacher: instance of the teacher that has to be removed
     * @return instance of the teacher that has been removed
     */
    public Color removeTeacher(Color teacher) {
        teacherTable.remove(teacher);
        return teacher;
    }

    /**
     * Add each tower to the board
     */
    public void addTower() {
        //bisogna inserire un controllo che non si inseriscano più torri di quanto permesso
        towers++;
    }

    /**
     * Remove each tower from the board
     */
    public void removeTower(){
        towers--;
    }

    /**
     * Method that returns the list of the students that are in the board's lobby
     * @return ArrayList<Color>: list of the students from the lobby
     */
    public ArrayList<Color> getLobby() {
        return lobby;
    }

    /**
     * Method that returns the instance of the student that are in a specific position in the lobby
     * indicated by an index
     * @param lobbyIndex index that identifies the position of the student in the lobby
     * @return color of the student in lobbyIndex position
     */
    public Color getLobbyStudent(int lobbyIndex){
        return lobby.get(lobbyIndex);
    }

    /**
     * Method that returns the number of the students in the Table divided by color
     * @return HashMap<Color, Integer>: Map that associates every student's color with the number of students
     * of that color
     */
    public HashMap<Color, Integer> getStudentsTable() {
        return studentsTable;
    }

    /**
     * Method that returns the list of teacher in the table
     * @return ArrayList<Color>: ArrayList of teacher
     */
    public ArrayList<Color> getTeacherTable() {
        return teacherTable;
    }

    /**
     * Method that checks if the table is true analyzing each color
     * @param color: color I want to know if its part of table is full
     * @return true if the part of table corresponding to the color is full, false otherwise
     */
    public boolean isTableFull(Color color){
        return studentsTable.get(color) == 10 ? true : false;
    }

    /**
     * Method that returns the number of towers in the board
     * @return number of towers in the board
     */
    public int getTowers() {
        return towers;
    }

    /**
     * Method that returns the number of the student in the table of each color
     * @param tableColor: color I want to know the number of students
     * @return number of students of that color in the Table
     */
    public int getTableNumberOfStudents(Color tableColor){
        return studentsTable.get(tableColor);
    }

    /**
     * Method that set the number of towers in the board
     * @param towers: number of towers
     */
    public void setTowers(int towers) {
        this.towers = towers;
    }

}
