package it.polimi.ingsw.messages;

public class EndGameMessage implements Message{

    String winnerName;

    public EndGameMessage(String winnerName) {

        this.winnerName = winnerName;
    }

    public String getWinnerName() {
        return winnerName;
    }
}
