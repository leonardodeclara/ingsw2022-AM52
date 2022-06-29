package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

/**
 * This message is going to be broadcast to all players in game
 * at the end of the game communicating the winner's name
 */

public class EndGameMessage implements UpdateMessage {

    String winnerName;

    /**
     * @param winnerName nickname of the winner of the game
     */

    public EndGameMessage(String winnerName) {

        this.winnerName = winnerName;
    }

    @Override
    public void update(GameBoard GB){
        GB.setWinner(winnerName);}
}
