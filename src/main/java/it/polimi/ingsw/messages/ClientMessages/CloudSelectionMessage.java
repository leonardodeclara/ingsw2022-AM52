package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;

/**
 * Thiss message is sent from Client to Server to notify the choice of cloud to be emptied to fill the lobby
 */

//messaggio Client->Server per comunicare la scelta di nuvola da svuotare per riempire la lobby
public class CloudSelectionMessage implements Message {
    private int cloudIndex;

    /**
     * @param cloudIndex index of the cloud to be emptied
     */
    public CloudSelectionMessage(int cloudIndex){
        this.cloudIndex=cloudIndex;
    }

    public int getCloudIndex() {
        return cloudIndex;
    }
}
