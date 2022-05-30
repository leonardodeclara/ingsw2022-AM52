package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.GameBoard;
import it.polimi.ingsw.model.Tower;

import java.util.ArrayList;

public class AvailableTowerMessage implements UpdateMessage {
    ArrayList<Tower> remainingTowers;

    public AvailableTowerMessage(ArrayList<Tower> remainingTowers) {
        this.remainingTowers = remainingTowers;
    }

    public ArrayList<Tower> getRemainingTowers(){
        return remainingTowers;
    }

    @Override
    public void update(GameBoard GB) {
        GB.setAvailableTowers(remainingTowers);
    }
}
