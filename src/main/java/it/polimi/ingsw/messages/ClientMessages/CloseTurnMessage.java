package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;

public class CloseTurnMessage implements Message {
    String closeTurnMessage;

    public CloseTurnMessage(String closeTurnMessage) {
        this.closeTurnMessage = closeTurnMessage;
    }

}
