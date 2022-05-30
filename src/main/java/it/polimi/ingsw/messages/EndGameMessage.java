package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.GameBoard;

public class EndGameMessage implements UpdateMessage{

    String winnerName;

    public EndGameMessage(String winnerName) {

        this.winnerName = winnerName;
    }

    public String getWinnerName() {
        return winnerName;
    }

    @Override
    public void update(GameBoard GB) {
        GB.visualizeEndGameMessage(winnerName);
    }
}
