package it.polimi.ingsw.model;

import java.util.ArrayList;

public class LobbyPersonality extends Personality{
    private ArrayList<Color> students;

    public LobbyPersonality(int id, int initialCost){
        super(id, initialCost);
        students = new ArrayList<>();
    }

    public void addStudent(Color color){
        students.add(color);
    }

    //all'interno di expertGame ci dovrà essere un metodo prende in input la carta LobbyPersonality
    // giocata e riempie l'arraylist gli studenti pescandoli dal basket

}
