package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;

public class PlayAssistantCardMessage implements Message {
    int priority;

    public PlayAssistantCardMessage(int priority) {
        this.priority = priority;
    }

    public int getPriority(){
        return priority;
    }
}
