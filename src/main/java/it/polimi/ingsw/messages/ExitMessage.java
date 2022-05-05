package it.polimi.ingsw.messages;

public class ExitMessage implements Message{
    String exitmessage;

    public ExitMessage(String exitmessage) {
        this.exitmessage = exitmessage;
    }

    public String getExitmessage() {
        return exitmessage;
    }
}
