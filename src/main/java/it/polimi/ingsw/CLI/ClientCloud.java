package it.polimi.ingsw.CLI;

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
