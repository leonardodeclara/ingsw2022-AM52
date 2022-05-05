package it.polimi.ingsw.messages;

public class EndGameMessage implements Message{
    String endgamemessage;

    public EndGameMessage(String endgamemessage) {
        this.endgamemessage = endgamemessage;
    }

    public String getEndgamemessage() {
        return endgamemessage;
    }
}
