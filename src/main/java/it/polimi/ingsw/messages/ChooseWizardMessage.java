package it.polimi.ingsw.messages;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ChooseWizardMessage implements Message{
    ArrayList<Integer> remainingWizards;

    public ChooseWizardMessage(ArrayList<Integer> remainingWizards) {
        this.remainingWizards = remainingWizards;
    }

    public ArrayList<Integer> getRemainingWizards(){
        return remainingWizards;
    }
}
