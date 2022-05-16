package it.polimi.ingsw.messages;

public class ActivePersonalityMessage implements Message{
    private int activeCardId;

    public ActivePersonalityMessage(int activeCardId){
        this.activeCardId=activeCardId;
    }

    public int getActiveCardId() {
        return activeCardId;
    }
}
