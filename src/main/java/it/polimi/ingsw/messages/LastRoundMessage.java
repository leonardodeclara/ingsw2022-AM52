package it.polimi.ingsw.messages;

public class LastRoundMessage implements Message{
    String lastRoundMessage;

    public LastRoundMessage(String lastRoundMessage) {
        this.lastRoundMessage = lastRoundMessage;
    }

    public String getLastRoundMessage() {
        return lastRoundMessage;
    }
}
