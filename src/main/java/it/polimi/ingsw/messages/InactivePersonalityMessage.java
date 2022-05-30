package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.GameBoard;

public class InactivePersonalityMessage implements UpdateMessage{
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
