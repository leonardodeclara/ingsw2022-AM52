package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

public class LastRoundMessage implements UpdateMessage {
    String lastRoundMessage;

    public LastRoundMessage(String lastRoundMessage) {
        this.lastRoundMessage = lastRoundMessage;
    }


    @Override
    public void update(GameBoard GB) {
        GB.visualizeLastRoundMessage(lastRoundMessage);
    }
}
