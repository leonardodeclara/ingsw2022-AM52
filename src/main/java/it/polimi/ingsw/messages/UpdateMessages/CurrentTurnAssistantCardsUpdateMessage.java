package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

import java.util.HashMap;

/**
 * This message is going to be broadcast to all players in game
 * after updates on selected assistant card
 */

public class CurrentTurnAssistantCardsUpdateMessage implements UpdateMessage {
    private HashMap<String, Integer> currentTurnAssistantCards;

    /**
     * @param currentTurnAssistantCards Assistant Cards that are used in the current turn
     */
    public CurrentTurnAssistantCardsUpdateMessage(HashMap<String, Integer> currentTurnAssistantCards) {
        this.currentTurnAssistantCards = currentTurnAssistantCards;
    }

    @Override
    public void update(GameBoard GB) {
        GB.setTurnCard(currentTurnAssistantCards);
        GB.print();
    }
}
