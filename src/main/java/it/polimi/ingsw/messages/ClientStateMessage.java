package it.polimi.ingsw.messages;

public class ClientStateMessage extends Message{

    private ClientState newState;

    public ClientStateMessage(ClientState newState){
        this.newState = newState;
    }

}
