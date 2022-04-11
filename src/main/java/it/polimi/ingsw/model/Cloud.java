package it.polimi.ingsw.model;

import java.util.ArrayList;

/**
 * This class contains the information and the actions that can be performed on a cloud tile.
 * Each cloud tile can be identified by its cloudIndex.
 */
public class Cloud {
    private final int cloudIndex;
    private ArrayList<Color> students;

    /**
     * Constructor creates a new cloud instance.
     * @param index: unique identification for the cloud tile.
     */
    public Cloud(int index){
        cloudIndex = index;
        students = new ArrayList<>();
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
        return outStudents;
    }
    /**
     * Method getStudents returns the cloud's students attribute current content
     * @return ArrayList<Students>: students attribute content
     */
    public ArrayList<Color> getStudents() {
        return students;
    }
}
