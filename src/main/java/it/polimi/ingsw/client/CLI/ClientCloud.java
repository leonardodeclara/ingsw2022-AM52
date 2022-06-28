package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Color;

import java.io.Serializable;
import java.util.ArrayList;

public class ClientCloud implements Serializable {
    static final long serialVersionUID = 42L;
    private int cloudIndex;
    private ArrayList<Color> students;

    public ClientCloud(int cloudIndex) {
        this.cloudIndex = cloudIndex;
        this.students = new ArrayList<>();
    }

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
