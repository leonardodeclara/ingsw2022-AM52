package it.polimi.ingsw.CLI;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Color;

import java.io.Serializable;
import java.util.ArrayList;

public class ClientPersonality implements Serializable {
    static final long serialVersionUID = 42L;
    private final int cardId;
    private boolean hasBeenUsed;
    private boolean active;
    private int cost;
    private int bans;
    private ArrayList<Color> students;
    private String description;

    public ClientPersonality(Integer cardID, Boolean hasBeenUsed, Integer cost) {
        cardId = cardID;
        this.hasBeenUsed = hasBeenUsed;
        this.cost = cost;
        this.active=false;
        students= new ArrayList<>();
        bans=0;
        description=Constants.personalityDescription(cardID);
    }

    private void printDescription(){
        System.out.print(description);
    }

    public void print(){
        System.out.print("ID: "+cardId + " " +"Costo: "+cost+ " ");
        if(students.size() > 0){
            System.out.print("STUDENTI: ");
            for (Color student : students) {
                System.out.print(Constants.getStudentsColor(student) + "■ ");
                System.out.print(Constants.RESET);
            }
            System.out.println();
        }
        if (bans>0){
            System.out.println("BANS: " + bans);
        }
        System.out.println("DESCRIZIONE: ");
        printDescription();
    }

    public void updateCost(){
        if(!hasBeenUsed){
            cost+=1;
            setHasBeenUsed(true);
        }
    }
    public int getCardID() {
        return cardId;
    }

    public void setHasBeenUsed(Boolean hasBeenUsed) {
        this.hasBeenUsed = hasBeenUsed;
    }

    public void setStudents(ArrayList<Color> students){
        this.students =students;
    }

    public void setBans(int bans) {
        this.bans = bans;
    }

    public ArrayList<Color> getStudents() {
        return students;
    }

    public int getBans() {
        return bans;
    }

    public boolean isHasBeenUsed() {
        return hasBeenUsed;
    }

    public String getDescription(){
        return description;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }
}

