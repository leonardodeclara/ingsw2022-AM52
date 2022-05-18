package it.polimi.ingsw.messages;

public class DisconnectMessage implements Message{

    String disconnectMessage;

    public DisconnectMessage(String disconnectmessage) {
        this.disconnectMessage = disconnectMessage;
    }

    public String getDisconnectMessage() {
        return disconnectMessage;
    }
}
