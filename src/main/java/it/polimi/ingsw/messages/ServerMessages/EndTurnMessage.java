package it.polimi.ingsw.messages.ServerMessages;

import it.polimi.ingsw.messages.Message;

public class EndTurnMessage implements Message {
    String endturnmessage;

    public EndTurnMessage(String endturnmessage) {
        this.endturnmessage = endturnmessage;
    }

    public String getEndturnmessage() {
        return endturnmessage;
    }
}
