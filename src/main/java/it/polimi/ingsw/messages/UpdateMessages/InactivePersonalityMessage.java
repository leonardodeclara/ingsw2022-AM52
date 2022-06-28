package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

public class InactivePersonalityMessage implements UpdateMessage {
    private int inactiveCardId;

    public InactivePersonalityMessage(int inactiveCardId){
        this.inactiveCardId=inactiveCardId;
    }

    public int getInactiveCardId() {
        return inactiveCardId;
    }

    @Override
    public void update(GameBoard GB) {
        GB.resetActivePersonality(inactiveCardId);
        GB.print();
    }
}
