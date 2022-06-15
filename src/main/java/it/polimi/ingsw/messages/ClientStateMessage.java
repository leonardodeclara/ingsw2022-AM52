package it.polimi.ingsw.messages;

import it.polimi.ingsw.client.ClientState;

public class ClientStateMessage implements Message{
    private ClientState newState;

    public ClientStateMessage(ClientState newState){
        this.newState = newState;
    }

    public ClientState getNewState() {
        return newState;
    }
}
