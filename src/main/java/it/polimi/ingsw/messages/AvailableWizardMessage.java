package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.GameBoard;

import java.util.ArrayList;

public class AvailableWizardMessage implements UpdateMessage {
    ArrayList<Integer> remainingWizards;

    public AvailableWizardMessage(ArrayList<Integer> remainingWizards) {
        this.remainingWizards = remainingWizards;
    }

    public ArrayList<Integer> getRemainingWizards(){
        return remainingWizards;
    }

    @Override
    public void update(GameBoard GB) {
        GB.setAvailableWizards(remainingWizards);
    }

}
