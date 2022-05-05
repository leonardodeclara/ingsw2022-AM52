package it.polimi.ingsw.messages;

public class LoseMessage implements Message{
    String winnerNickname;

    public LoseMessage(String winnerNickname) {
        this.winnerNickname = winnerNickname;
    }

    public String getWinnerNickname() {
        return winnerNickname;
    }
}
