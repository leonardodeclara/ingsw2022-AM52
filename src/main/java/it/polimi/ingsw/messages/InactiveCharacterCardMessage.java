package it.polimi.ingsw.messages;

public class InactiveCharacterCardMessage implements Message{
    private int inactiveCardId;

    public InactiveCharacterCardMessage(int inactiveCardId){
        this.inactiveCardId=inactiveCardId;
    }

    public int getInactiveCardId() {
        return inactiveCardId;
    }
}
