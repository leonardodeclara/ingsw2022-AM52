package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

/**
 * This message is going to be broadcast to all players in game
 * notifying that current round is the last game round
 */

public class LastRoundMessage implements UpdateMessage {
    String lastRoundMessage;

    /**
     * @param lastRoundMessage string that communicates last game round
     */
    public LastRoundMessage(String lastRoundMessage) {
        this.lastRoundMessage = lastRoundMessage;
    }


    @Override
    public void update(GameBoard GB) {
        GB.visualizeLastRoundMessage(lastRoundMessage);
    }
}
