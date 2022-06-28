package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

public class ActivePersonalityMessage implements UpdateMessage {
    private int activeCardId;

    public ActivePersonalityMessage(int activeCardId){
        this.activeCardId=activeCardId;
    }

    @Override
    public void update(GameBoard GB) {
        GB.setActivePersonality(activeCardId);
        GB.print();
    }
}
