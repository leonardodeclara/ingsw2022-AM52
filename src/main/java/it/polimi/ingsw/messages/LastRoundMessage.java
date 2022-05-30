package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.GameBoard;

public class LastRoundMessage implements UpdateMessage{
    String lastRoundMessage;

    public LastRoundMessage(String lastRoundMessage) {
        this.lastRoundMessage = lastRoundMessage;
    }

    public String getLastRoundMessage() {
        return lastRoundMessage;
    }

    @Override
    public void update(GameBoard GB) {
        GB.visualizeLastRoundMessage(lastRoundMessage);
    }
}
