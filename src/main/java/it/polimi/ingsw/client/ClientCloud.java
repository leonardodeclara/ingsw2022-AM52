package it.polimi.ingsw.client;

import it.polimi.ingsw.model.Color;

import java.util.ArrayList;

public class ClientCloud {
    private int cloudIndex;
    private ArrayList<Color> students;

    public ClientCloud(int cloudIndex, ArrayList<Color> students) {
        this.cloudIndex = cloudIndex;
        this.students = students;
    }

    public int getCloudIndex() {
        return cloudIndex;
    }

    public void setCloudIndex(int cloudIndex) {
        this.cloudIndex = cloudIndex;
    }

    public ArrayList<Color> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<Color> students) {
        this.students = students;
    }
}
