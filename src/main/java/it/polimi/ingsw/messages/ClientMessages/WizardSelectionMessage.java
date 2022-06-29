package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;

/**
 * This message is sent from Client to Server to notify
 * which wizard has been choosen by the player
 */

public class WizardSelectionMessage implements Message {
    int wizard;

    /**
     * @param wizard wizard choosen by the player
     */

    public WizardSelectionMessage(int wizard) {
        this.wizard = wizard;
    }

    public int getWizard(){
        return wizard;
    }
}
