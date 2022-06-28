package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;
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
