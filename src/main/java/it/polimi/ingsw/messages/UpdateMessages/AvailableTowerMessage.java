package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;
import it.polimi.ingsw.model.Tower;

import java.util.ArrayList;

/** This message is going to be broadcast to all players in game
 * after a player has chosen its tower notifying all players about
 * the remaining available towers
 */

public class AvailableTowerMessage implements UpdateMessage {
    ArrayList<Tower> remainingTowers;

    /**
     * @param remainingTowers ArrayList of towers that are available
     */
    public AvailableTowerMessage(ArrayList<Tower> remainingTowers) {
        this.remainingTowers = remainingTowers;
    }



    @Override
    public void update(GameBoard GB) {
        GB.setAvailableTowers(remainingTowers);
    }
}
