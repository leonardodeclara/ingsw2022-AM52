package it.polimi.ingsw.messages;

public class DisconnectMessage implements Message{

    String disconnectmessage;

    public DisconnectMessage(String disconnectmessage) {
        this.disconnectmessage = disconnectmessage;
    }

    public String getDisconnectmessage() {
        return disconnectmessage;
    }
}
