package it.polimi.ingsw.messages;

public class EndTurnMessage implements Message{
    String endturnmessage;

    public EndTurnMessage(String endturnmessage) {
        this.endturnmessage = endturnmessage;
    }

    public String getEndturnmessage() {
        return endturnmessage;
    }
}
