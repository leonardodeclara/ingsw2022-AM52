package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.GameController;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

/**
 * This class contains the information and the actions that can be performed on a cloud tile.
 * Each cloud tile can be identified by its cloudIndex.
 */
public class Cloud {
    private final int cloudIndex;
    private ArrayList<Color> students;
    private PropertyChangeSupport listeners;
    private boolean filled;

    /**
     * Constructor creates a new cloud instance.
     * @param index: unique identification for the cloud tile.
     */
    public Cloud(int index){
        cloudIndex = index;
        students = new ArrayList<>();
        listeners= new PropertyChangeSupport(this);
        filled=false;
    }

    /**
     * Method which returns the cloud's index.
     * @return the identification number for the cloud tile.
     */
    public int getCloudIndex() {
        return cloudIndex;
    }

    /**
     * This method refills the students' array on top of the cloud.
     * @param newStudents is taken as input and its content is copied into the attribute students.
     */
    public void fillStudents(ArrayList<Color> newStudents){
        students.addAll(newStudents);
    }

    /**
     * This method empties the students' array on top of the cloud.
     * @return the attribute students' content
     */
    public ArrayList<Color> emptyStudents(){
        ArrayList<Color> outStudents = new ArrayList<>(students);
        students.clear();
        listeners.firePropertyChange("PickedCloud", outStudents, cloudIndex);
        return outStudents;
    }
    /**
     * Method getStudents returns the cloud's students attribute current content
     * @return ArrayList<Students>: students attribute content
     */
    public ArrayList<Color> getStudents() {
        return new ArrayList<>(students);
    }

    /**
     * Checks if the cloud has been correctly filled with student tiles at the last refill Clouds phase.
     * @return true if it has been correctly filled, false otherwise.
     */
    public boolean isFilled() {
        return filled;
    }

    /**
     * Sets the flag filled to true or false according to the last refill Clouds phase.
     * @param filled: true if the correct amount of student tiles has been put on it, false otherwise.
     */
    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    /**
     * Method setPropertyChangeListener sets the listener of Cloud's state.
     * @param controller: controller instance listening to the game's changes.
     */
    public void setPropertyChangeListener(GameController controller){
        listeners.addPropertyChangeListener("PickedCloud", controller);
    }
}
