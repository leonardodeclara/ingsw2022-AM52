package it.polimi.ingsw.messages;

public class ClientStateMessage implements Message{
    private ClientState newState;

    public ClientStateMessage(ClientState newState){
        this.newState = newState;
    }

    public ClientState getNewState() {
        return newState;
    }
}
