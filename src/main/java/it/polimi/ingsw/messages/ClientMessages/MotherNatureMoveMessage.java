package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;

/**
 * This message is sent from Client to Server to notify Mother Nature moves
 */

public class MotherNatureMoveMessage implements Message {
    int steps;

    /**
     * @param steps number of steps made by Mother Nature
     */
    public MotherNatureMoveMessage(int steps) {
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }
}
