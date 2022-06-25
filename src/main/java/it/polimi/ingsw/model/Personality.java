package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.GameController;

import java.beans.PropertyChangeSupport;

/**
 * This class represents the Personality card.
 * Players can use Personality card in expert game mode.
 * Each player can use 3 Personality card during the game.
 */
public class Personality {
    private final int characterId;
    private boolean hasBeenUsed;
    private int cost;
    protected PropertyChangeSupport listeners;

    /**
     * Constructor creates a Personality card instance
     * @param id: unique identifier for the Personality card
     */
    public Personality(int id){
        characterId=id;
        cost=id%3==0? 3: id%3;
        hasBeenUsed=false;
        listeners= new PropertyChangeSupport(this);
    }

    /**
     * This method update the cost of a Personality card when the card is used
     */
    public void updateCost(){
        if(!hasBeenUsed){
            cost+=1;
            setHasBeenUsed(true);
        }
    }

    /**
     * Method that return the ID of the card
     * @return the unique identifier of the card
     */
    public int getCharacterId() {
        return characterId;
    }

    /**
     * Method that says if a card has been used or not
     * @return true if the card has been used and false if it hasn't been used
     */
    public boolean isHasBeenUsed() {
        return hasBeenUsed;
    }

    /**
     * This method set hasBeenUsed to true or false according to whether the card has been used or not
     * @param hasBeenUsed: truth value regarding the use of the card
     */
    public void setHasBeenUsed(boolean hasBeenUsed) {
        this.hasBeenUsed = hasBeenUsed;
    }

    /**
     * Method that return the cost of the card
     * @return the number that identifies the cost of the card according to the update
     */
    public int getCost() {
        return cost;
    }

    /**
     * Metod setPropertyChangeListener sets the Personality's  state listeners.
     * @param controller: controller instance listening to the game's changes.
     */
    public void setPropertyChangeListener(GameController controller){
        listeners.addPropertyChangeListener("PersonalityUsage",controller);
    }
}