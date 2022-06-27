package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;

public class MotherNatureMoveMessage implements Message {
    int steps;

    public MotherNatureMoveMessage(int steps) {
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }
}
