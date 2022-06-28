package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

public class EndGameMessage implements UpdateMessage {

    String winnerName;

    public EndGameMessage(String winnerName) {

        this.winnerName = winnerName;
    }

    public String getWinnerName() {
        return winnerName;
    }

    @Override
    public void update(GameBoard GB){
        GB.setWinner(winnerName);}
}
