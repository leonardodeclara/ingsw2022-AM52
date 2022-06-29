package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

/**
 * This message is going to be broadcast to all players in game
 * when there's an update on a Personality card's activation
 */

public class ActivePersonalityMessage implements UpdateMessage {
    private int activeCardId;

    /**
     * @param activeCardId ID of the card that has been activated
     */

    public ActivePersonalityMessage(int activeCardId){
        this.activeCardId=activeCardId;
    }


    @Override
    public void update(GameBoard GB) {
        GB.setActivePersonality(activeCardId);
        GB.print();
    }
}
