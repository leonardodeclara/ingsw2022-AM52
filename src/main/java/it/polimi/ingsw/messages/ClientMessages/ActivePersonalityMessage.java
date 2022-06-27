package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

public class ActivePersonalityMessage implements UpdateMessage {
    private int activeCardId;

    public ActivePersonalityMessage(int activeCardId){
        this.activeCardId=activeCardId;
    }

    public int getActiveCardId() {
        return activeCardId;
    }

    @Override
    public void update(GameBoard GB) {
        GB.setActivePersonality(activeCardId);
        GB.print();
    }
}
