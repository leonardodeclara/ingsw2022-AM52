package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.Tower;

import java.util.ArrayList;

public class AvailableTowerMessage implements Message {
    ArrayList<Tower> remainingTowers;

    public AvailableTowerMessage(ArrayList<Tower> remainingTowers) {
        this.remainingTowers = remainingTowers;
    }

    public ArrayList<Tower> getRemainingTowers(){
        return remainingTowers;
    }
}
