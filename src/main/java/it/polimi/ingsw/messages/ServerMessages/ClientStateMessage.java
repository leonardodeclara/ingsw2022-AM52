package it.polimi.ingsw.messages.ServerMessages;

import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.messages.Message;

/**
 * This message is sent from Server informing the Client on its new status
 */

public class ClientStateMessage implements Message {
    private ClientState newState;

    /**
     * @param newState state in which the Client will have to set
     */

    public ClientStateMessage(ClientState newState){
        this.newState = newState;
    }

    public ClientState getNewState() {
        return newState;
    }
}
