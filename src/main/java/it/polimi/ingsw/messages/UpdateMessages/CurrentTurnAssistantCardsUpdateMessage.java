package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

import java.util.HashMap;

public class CurrentTurnAssistantCardsUpdateMessage implements UpdateMessage {
    private HashMap<String, Integer> currentTurnAssistantCards;

    public CurrentTurnAssistantCardsUpdateMessage(HashMap<String, Integer> currentTurnAssistantCards) {
        this.currentTurnAssistantCards = currentTurnAssistantCards;
    }

    @Override
    public void update(GameBoard GB) {
        GB.setTurnCard(currentTurnAssistantCards);
        GB.print();
    }
}
