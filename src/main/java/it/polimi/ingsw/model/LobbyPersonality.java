package it.polimi.ingsw.model;

import java.util.ArrayList;

public class LobbyPersonality extends Personality{
    private ArrayList<Color> students;
    private int lobbySize;

    public LobbyPersonality(int id){
        super(id);
        students = new ArrayList<>();
        lobbySize = id==7? 6:4;
    }

    public void addStudent(Color color){
        if (students.size()<lobbySize)
            students.add(color);
    }

    public int getLobbySize() {
        return lobbySize;
    }

    public ArrayList<Color> getStudents() {
        return new ArrayList<>(students);
    }

    //all'interno di expertGame ci dovrà essere un metodo prende in input la carta LobbyPersonality
    // giocata e riempie l'arraylist gli studenti pescandoli dal basket

}
