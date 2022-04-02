package it.polimi.ingsw;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class contains the information and the actions that can be performed on a school's Board.
 *
 */

public class Board {

    private HashMap<Color,ArrayList<Student>> studentsTable;
    private ArrayList<Teacher> teacherTable;
    private ArrayList<Student> lobby;
    private ArrayList<Tower> towerSlots;

    /**
     * Constructor creates a Board istance.
     *
     */

    public Board() {
        studentsTable = new HashMap<Color, ArrayList<Student>>();
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

    //vedo il colore del nuovo studente e lo uso come chiave ricavando l'attuale table di quel colore
    //aggiungo a quella table il nuovo studente e aggiorno la voce corrispondente nella hashmap
    //controllo l'indice a cui è stato messo il nuovo studente e traggo le conclusioni del caso
    public boolean addToTable(Student student) {
        Color newStudentColor = student.getColor();
        ArrayList<Student> students = studentsTable.get(newStudentColor);
        students.add(student);
        studentsTable.put(newStudentColor,students);
        if (students.indexOf(student) == 3 || students.indexOf(student) == 6 || students.indexOf(student) == 9)
            return true;

        return false;

    }

    /**
     * Remove a student from the table corresponding to its color
     * @param color: color of the student
     */

    public void removeFromTable(Color color){
        ArrayList<Student> students = studentsTable.get(color);
        students.remove(students.size() - 1);
        studentsTable.put(color,students);
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
