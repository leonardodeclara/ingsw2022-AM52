package it.polimi.ingsw.messages;

public class PlayAssistantCardMessage implements Message{
    int priority;

    public PlayAssistantCardMessage(int priority) {
        this.priority = priority;
    }

    public int getPriority(){
        return priority;
    }
}
