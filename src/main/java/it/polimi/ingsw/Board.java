package it.polimi.ingsw;

import java.util.ArrayList;

/**
 * This class contains the information and the actions that can be performed on a school's Board.
 *
 */

public class Board {

    private ArrayList<Student> greenTable;
    private ArrayList<Student> redTable;
    private ArrayList<Student> yellowTable;
    private ArrayList<Student> pinkTable;
    private ArrayList<Student> blueTable;
    private ArrayList<Teacher> teacherTable;
    private ArrayList<Student> lobby;
    private ArrayList<Tower> towerSlots;

    /**
     * Constructor creates a Board istance.
     *
     */

    public Board() {
        greenTable=new ArrayList<>();
        redTable=new ArrayList<>();
        yellowTable=new ArrayList<>();
        pinkTable=new ArrayList<>();
        blueTable=new ArrayList<>();
        teacherTable=new ArrayList<>();
        lobby=new ArrayList<>();
        towerSlots=new ArrayList<>();

    }

    /**
     * Add a student object to the board's lobby
     * @param student
     */

    public void addToLobby(Student student) {
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
     * @param student
     * @return
     */

    public boolean addToTable(Student student) {
        switch(student.getColor()){
            case PINK:
                pinkTable.add(student);
                if (pinkTable.indexOf(student) == 3 || pinkTable.indexOf(student) == 6 || pinkTable.indexOf(student) == 9)
                    return true;
            case GREEN:
                greenTable.add(student);
                if (greenTable.indexOf(student) == 3 || greenTable.indexOf(student) == 6 || greenTable.indexOf(student) == 9)
                    return true;
            case BLUE:
                blueTable.add(student);
                if (blueTable.indexOf(student) == 3 || blueTable.indexOf(student) == 6 || blueTable.indexOf(student) == 9)
                    return true;
            case YELLOW:
                yellowTable.add(student);
                if (yellowTable.indexOf(student) == 3 || yellowTable.indexOf(student) == 6 || yellowTable.indexOf(student) == 9)
                    return true;
            case RED:
                redTable.add(student);
                if (redTable.indexOf(student) == 3 || redTable.indexOf(student) == 6 || redTable.indexOf(student) == 9)
                    return true;
        }
        return false;

    }

    /**
     * Remove a student from the table corresponding to its color
     * @param color: color of the student
     */

    public void removeFromTable(Color color){

        switch(color){
            case PINK:
                pinkTable.remove(pinkTable.size()-1);
            case GREEN:
                greenTable.remove(greenTable.size()-1);
            case BLUE:
                blueTable.remove(blueTable.size()-1);
            case YELLOW:
                yellowTable.remove(yellowTable.size()-1);
            case RED:
                redTable.remove(redTable.size()-1);
        }

    }

    public void SwitchStudents() {
        /*cosa prende in input? Un colore? Un indice?*/

    }

    public void addTeacher(Teacher teacher){
        teacherTable.add(teacher);
    }

    public Teacher removeTeacher(Color color) {
        Teacher removed=null;
        for (Teacher teacher: teacherTable){
            if(teacher.getColor()==color)
                removed = teacher;
                teacherTable.remove(teacher);
        }
        return removed;
    }

    public void addTower(Tower tower) {
        towerSlots.add(tower);
    }

    public Tower removeTower(){
        Tower removed = towerSlots.remove(towerSlots.size()-1);
        return removed;

    }

    public ArrayList<Student> getLobby() {
        return lobby;
    }
}
