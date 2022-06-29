package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;

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
    private int wizardID;

    /**
     * Constructor creates a Board instance.
     *
     */
    public Board() {
        studentsTable = new HashMap<>();
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
     * Add a student object to the board's lobby.
     * @param student: student instance being added to the board's lobby.
     */
    public void addToLobby(Color student) {
        lobby.add(student);
    }

    /**
     * Remove a student object from the board's lobby
     * @param student: student instance being removed to the board's lobby
     * @return true if the student has been correctly removed, false otherwise.
     */
    public boolean removeFromLobby(Color student){
        if(lobby.contains(student)){
            lobby.remove(student);
            return true;
        }
        return false;
    }

    /**
     * Add each student object to the table corresponding to their color.
     * This method also checks if the students is the third, the sixth or the ninth added to the table for the expert
     * game mode for earn money.
     * @param student instance of the student that has to be added.
     * @return true if the student is the third, the sixth or the ninth added to the table, false otherwise.
     */
    public boolean addToTable(Color student) {
        Integer numOfStudents = studentsTable.get(student);
        numOfStudents++;
        studentsTable.put(student,numOfStudents);
        return (numOfStudents == 3 || numOfStudents == 6 || numOfStudents == 9);
    }

    /**
     * Remove each student object from the table.
     * @param student: instance of the student that has to be removed.
     */
    public void removeFromTable(Color student){
        Integer numOfStudents = studentsTable.get(student);
        numOfStudents--;
        studentsTable.put(student,numOfStudents);
    }

    /**
     * Method switchStudents switches the selected table students with the selected lobby students.
     * @param tableStudents: color of student tiles that are being moved from the table to the lobby.
     * @param lobbyStudentsIndexes: indexes of the lobby student tiles that are being from the lobby to the table.
     * @return true if the input parameters are correct and the switch has been correctly executed, false otherwise.
     */
    public boolean switchStudents(ArrayList<Color> tableStudents, ArrayList<Integer> lobbyStudentsIndexes) {
        if (lobbyStudentsIndexes.size()> Constants.MAX_STUDENTS_FOR_CARD_10_SWITCH
                || tableStudents.size()>Constants.MAX_STUDENTS_FOR_CARD_10_SWITCH
                || tableStudents.size()!=lobbyStudentsIndexes.size()
                || hasDuplicates(lobbyStudentsIndexes))
            return false;

        for (Integer index: lobbyStudentsIndexes)
            if (index<0||index>lobby.size())
                return false;
        int numOfMoves=tableStudents.size();
        HashMap<Color, Integer>  fromLobby = new HashMap<>();
        HashMap<Color, Integer>  fromTable = new HashMap<>();
        for (Color color: Color.values()){
            fromTable.put(color,0);
            fromLobby.put(color,0);
        }
        for (int i = 0; i< numOfMoves;i++){
            Color toBeMovedFromLobby = lobby.get(lobbyStudentsIndexes.get(i));
            fromLobby.put(toBeMovedFromLobby, fromLobby.get(toBeMovedFromLobby)+1);
            Color toBeMovedFromTable = tableStudents.get(i);
            fromTable.put(toBeMovedFromTable, fromTable.get(toBeMovedFromTable)+1);
        }

        for (Color color: Color.values())
            if (fromLobby.get(color)+studentsTable.get(color)>Constants.MAX_TABLE_SIZE
                    || studentsTable.get(color)-fromTable.get(color)<0)
                return false;

        for (Color color: Color.values()){
            for (int i = 0; i<fromLobby.get(color);i++){
                addToTable(color);
                removeFromLobby(color);
            }
            for (int i = 0; i<fromTable.get(color);i++){
                addToLobby(color);
                removeFromTable(color);
            }
        }
        return true;
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
     * @return list of the students from the lobby
     */
    public ArrayList<Color> getLobby() {
        return new ArrayList<>(lobby);
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
     * @return Map that associates every student's color with the number of students
     * of that color
     */
    public HashMap<Color, Integer> getStudentsTable() {
        return new HashMap<>(studentsTable);
    }

    /**
     * Method that returns the list of teacher in the table
     * @return ArrayList of teacher
     */
    public ArrayList<Color> getTeacherTable() {
        return new ArrayList<>(teacherTable);
    }

    /**
     * Method that checks if the table is true analyzing each color
     * @param color: color I want to know if its part of table is full
     * @return true if the part of table corresponding to the color is full, false otherwise
     */
    public boolean isTableFull(Color color, int toBeAdded){
        return studentsTable.get(color) + toBeAdded == Constants.MAX_TABLE_SIZE ? true : false;
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

    public int getWizard(){
        return wizardID;
    }
    public void setWizard(int wizardID){
        this.wizardID = wizardID;
    }
    /**
     * Method hasDuplicates checks if the selected ArrayList has duplicate elements.
     * @param indexes: ArrayList carrying Integer instances that need to be checked for duplicates.
     * @return true if the input ArrayList has duplicated elements, false otherwise.
     */
    public boolean hasDuplicates(ArrayList<Integer> indexes){
        return (indexes.stream().distinct().count()!=indexes.size());
    }

    /**
     * Method isTableEmpty verifies if the board's table has no students.
     * @return true if there are no student tiles in the table,false otherwise.
     */
    public boolean isTableEmpty(){
        for (Color color: Color.values())
            if (studentsTable.get(color)!=0)
                return false;
        return true;
    }

}
