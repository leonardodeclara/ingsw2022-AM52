package it.polimi.ingsw.messages;

public class InactivePersonalityMessage implements Message{
    private int inactiveCardId;

    public InactivePersonalityMessage(int inactiveCardId){
        this.inactiveCardId=inactiveCardId;
    }

    public int getInactiveCardId() {
        return inactiveCardId;
    }
}
