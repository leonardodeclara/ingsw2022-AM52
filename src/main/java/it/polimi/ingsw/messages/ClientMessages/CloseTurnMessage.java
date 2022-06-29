package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;

/**
 * this message is sent when a player has finished his turn. It notifies the next player that has to play.
 */

public class CloseTurnMessage implements Message {
    String closeTurnMessage;


    public CloseTurnMessage(String closeTurnMessage) {
        this.closeTurnMessage = closeTurnMessage;
    }

}
