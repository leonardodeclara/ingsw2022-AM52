package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Color;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class ClientPersonality holds basic information about a Personality state in order to render its content through CLI and GUI interfaces.
 * It carries information about its cost, whether it has been used or not and whether it's active or not.
 * Student tiles are memorised for Lobby personality student tiles, ban counts for Ban Personality.
 */
public class ClientPersonality implements Serializable {
    static final long serialVersionUID = 42L;
    private final int cardId;
    private boolean hasBeenUsed;
    private boolean active;
    private int cost;
    private int bans;
    private ArrayList<Color> students;
    private String description;

    /**
     * Constructor ClientPersonality receives as input an representing an id, an integer representing a cost and a flag
     * showing whether it has also been used and creates a ClientPersonality instance.
     * @param cardID identification number for the Personality card.
     * @param hasBeenUsed true if it has already been used, false otherwise.
     * @param cost card cost.
     */
    public ClientPersonality(Integer cardID, Boolean hasBeenUsed, Integer cost) {
        cardId = cardID;
        this.hasBeenUsed = hasBeenUsed;
        this.cost = cost;
        this.active=false;
        students= new ArrayList<>();
        bans=0;
        description=Constants.personalityDescription(cardID);
    }

    /**
     * Method printDescription prints a description of the card's effect.
     */
    private void printDescription(){
        System.out.print(description);
    }

    /**
     * Method print prints Personality's content on CLI interfaces.
     */
    public void print(){
        System.out.print("ID: "+cardId + " " +"COST: "+cost+ " ");
        if(students.size() > 0){
            System.out.print("STUDENTS: ");
            for (Color student : students) {
                System.out.print(Constants.getStudentsColor(student) + "■ ");
                System.out.print(Constants.RESET);
            }
            System.out.println();
        }
        if (bans>0){
            System.out.println("BANS: " + bans);
        }
        System.out.println("DESCRIPTION: ");
        printDescription();
    }

    /**
     * Method updateCost increments the card's cost according to its usage.
     */
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

