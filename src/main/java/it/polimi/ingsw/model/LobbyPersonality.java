package it.polimi.ingsw.model;

import java.util.ArrayList;

/**
 * This is a subclass of the class Personality
 * This class represents the Personality card with CardID 1, 7 and 11
 * At the beginning of the game this card has a number of student tiles on it according to the card
 */

public class LobbyPersonality extends Personality{
    private ArrayList<Color> students;
    private final int lobbySize;

    /**
     * Constructor creates a LobbyPersonality card instance
     * @param id: unique identifier for the card
     */
    public LobbyPersonality(int id){
        super(id);
        students = new ArrayList<>();
        lobbySize = id==7? 6:4;
    }

    /**
     * Add a student tile on the card
     * @param color: color of the students that player want to add on the card
     */
    public void addStudent(Color color){
        if (students.size()<lobbySize){
            students.add(color);
            listeners.firePropertyChange("PersonalityUsage", null,this);
        }
    }

    /**
     * Method that returns the size of the lobbySize array that represents the list of students on the card
     * @return 6 or 4 according to the cardID
     */
    public int getLobbySize() {
        return lobbySize;
    }

    /**
     * Method that returns the list of students placed on the card
     * @return ArrayList<>: list containing the students placed on the card
     */
    public ArrayList<Color> getStudents() {
        return new ArrayList<>(students);
    }

    public Color getStudent(int studentIndex){
        if (studentIndex>=0 && studentIndex<students.size())
            return students.get(studentIndex);
        else
            return null;
    }

    public void removeStudent(Color student){
        if (students.contains(student)){
            students.remove(student);
            listeners.firePropertyChange("PersonalityUsage", null,this);
        }
    }

    //all'interno di expertGame ci dovrà essere un metodo prende in input la carta LobbyPersonality
    // giocata e riempie l'arraylist gli studenti pescandoli dal basket

}
