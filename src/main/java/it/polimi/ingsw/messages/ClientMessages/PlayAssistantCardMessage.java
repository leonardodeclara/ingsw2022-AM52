package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;

/**
 * This message is sent from Client to Server to notify
 * which assistant card the player has choosen
 */

public class PlayAssistantCardMessage implements Message {
    int priority;

    /**
     * @param priority priority of the Assistant card choosen
     */
    public PlayAssistantCardMessage(int priority) {
        this.priority = priority;
    }

    public int getPriority(){
        return priority;
    }
}
