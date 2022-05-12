package it.polimi.ingsw.messages;

public class EndGameMessage implements Message{
    String message;
    String winnerName;

    public EndGameMessage(String message, String winnerName) {
        this.message = message;
        this.winnerName = winnerName;
    }

    public String getMessage() {
        return message;
    }

    public String getWinnerName() {
        return winnerName;
    }
}
