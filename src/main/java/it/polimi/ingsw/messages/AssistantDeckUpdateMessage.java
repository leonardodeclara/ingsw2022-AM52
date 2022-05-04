package it.polimi.ingsw.messages;

public class AssistantDeckUpdateMessage implements Message{
    int priority;

    public AssistantDeckUpdateMessage(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
