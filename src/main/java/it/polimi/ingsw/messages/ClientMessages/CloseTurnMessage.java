package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;

public class CloseTurnMessage implements Message {
    String closeturnmessage;

    public CloseTurnMessage(String closeturnmessage) {
        this.closeturnmessage = closeturnmessage;
    }

    public String getCloseturnmessage() {
        return closeturnmessage;
    }
}
