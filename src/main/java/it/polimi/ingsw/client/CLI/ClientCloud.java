package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Color;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class ClientCloud holds basic information about a Cloud state in order to render its content through CLI and GUI interfaces.
 * It carries information about student tiles placed on it.
 */
public class ClientCloud implements Serializable {
    static final long serialVersionUID = 42L;
    private int cloudIndex;
    private ArrayList<Color> students;

    /**
     * Constructor ClientCloud creates a ClientCloud instance whose identification number is cloudIndex.
     * @param cloudIndex identification for the cloud.
     */
    public ClientCloud(int cloudIndex) {
        this.cloudIndex = cloudIndex;
        this.students = new ArrayList<>();
    }

    /**
     * Method print prints the cloud's content on CLI interfaces.
     */
    public void print(){
        System.out.println("NUVOLA " + getCloudIndex() + ":");
        try {
            for(Color color : Color.values()) {
                int numberOfStudentPerColor = (int) getStudents().stream().filter(c -> c == color).count();
                for (int i = 0; i < numberOfStudentPerColor; i++) {
                    System.out.print(Constants.getStudentsColor(color) + "■ ");
                }
                System.out.print(Constants.RESET);
            }
            System.out.println();

        } catch (NullPointerException e) {
            System.out.println("no studenti");
        }
    }
    public int getCloudIndex() {
        return cloudIndex;
    }

    public ArrayList<Color> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<Color> students) {
        this.students = students;
    }
}
