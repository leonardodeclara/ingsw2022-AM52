package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.GameBoard;

import java.util.HashMap;

public class CurrentTurnAssistantCardsUpdateMessage implements UpdateMessage{
    private HashMap<String, Integer> currentTurnAssistantCards;

    public CurrentTurnAssistantCardsUpdateMessage(HashMap<String, Integer> currentTurnAssistantCards) {
        this.currentTurnAssistantCards = currentTurnAssistantCards;
    }

    public HashMap<String, Integer> getCurrentTurnAssistantCards() {
        return currentTurnAssistantCards;
    }

    @Override
    public void update(GameBoard GB) {
        GB.setTurnCard(currentTurnAssistantCards);
        GB.print();
    }
}
