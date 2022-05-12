package it.polimi.ingsw.messages;

public class ActiveCharacterCardMessage implements Message{
    private int activeCardId;

    public ActiveCharacterCardMessage(int activeCardId){
        this.activeCardId=activeCardId;
    }

    public int getActiveCardId() {
        return activeCardId;
    }
}
