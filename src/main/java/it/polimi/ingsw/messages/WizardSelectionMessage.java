package it.polimi.ingsw.messages;

public class WizardSelectionMessage implements Message{
    int wizard;

    public WizardSelectionMessage(int wizard) {
        this.wizard = wizard;
    }

    public int getWizard(){
        return wizard;
    }
}
