package it.polimi.ingsw.messages;

public class LastRoundMessage implements Message{
    String lastroundmessage;

    public LastRoundMessage(String lastroundmessage) {
        this.lastroundmessage = lastroundmessage;
    }

    public String getLastroundmessage() {
        return lastroundmessage;
    }
}
