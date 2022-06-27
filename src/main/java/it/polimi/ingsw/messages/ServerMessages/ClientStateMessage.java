package it.polimi.ingsw.messages.ServerMessages;

import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.messages.Message;

public class ClientStateMessage implements Message {
    private ClientState newState;

    public ClientStateMessage(ClientState newState){
        this.newState = newState;
    }

    public ClientState getNewState() {
        return newState;
    }
}
