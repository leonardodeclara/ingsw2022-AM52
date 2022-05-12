package it.polimi.ingsw.messages;

import java.util.HashMap;

public class CurrentTurnAssistantCardsUpdateMessage implements Message{
    private HashMap<String, Integer> currentTurnAssistantCards;

    public CurrentTurnAssistantCardsUpdateMessage(HashMap<String, Integer> currentTurnAssistantCards) {
        this.currentTurnAssistantCards = currentTurnAssistantCards;
    }

    public HashMap<String, Integer> getCurrentTurnAssistantCards() {
        return currentTurnAssistantCards;
    }
}
