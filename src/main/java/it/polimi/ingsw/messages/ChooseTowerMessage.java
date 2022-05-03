package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.Tower;

import java.util.ArrayList;

public class ChooseTowerMessage implements Message {
    ArrayList<Tower> remainingTowers;

    public ChooseTowerMessage(ArrayList<Tower> remainingTowers) {
        this.remainingTowers = remainingTowers;
    }

    public ArrayList<Tower> getRemainingTowers(){
        return remainingTowers;
    }
}
