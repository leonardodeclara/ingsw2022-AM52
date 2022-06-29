package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

/**
 * This message is going to be broadcast to all players in game
 * after the deactivation of a Personality card
 */

public class InactivePersonalityMessage implements UpdateMessage {
    private int inactiveCardId;

    /**
     * @param inactiveCardId ID of the deactivated Personality Card
     */
    public InactivePersonalityMessage(int inactiveCardId){
        this.inactiveCardId=inactiveCardId;
    }

    @Override
    public void update(GameBoard GB) {
        GB.resetActivePersonality(inactiveCardId);
        GB.print();
    }
}
