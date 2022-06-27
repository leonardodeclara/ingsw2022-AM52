package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;

public class WizardSelectionMessage implements Message {
    int wizard;

    public WizardSelectionMessage(int wizard) {
        this.wizard = wizard;
    }

    public int getWizard(){
        return wizard;
    }
}
