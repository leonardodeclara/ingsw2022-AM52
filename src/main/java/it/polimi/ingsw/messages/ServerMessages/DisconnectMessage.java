package it.polimi.ingsw.messages.ServerMessages;

import it.polimi.ingsw.messages.Message;

/**
 * This message is sent to the Server in case of disconnection
 */

public class DisconnectMessage implements Message {

    String disconnectMessage;

    /**
     * @param disconnectmessage string that identifies a disconnection
     */

    public DisconnectMessage(String disconnectmessage) {
        this.disconnectMessage = disconnectMessage;
    }

    public String getDisconnectMessage() {
        return disconnectMessage;
    }
}
