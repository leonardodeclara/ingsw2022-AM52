package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

import java.util.ArrayList;

/** This message is going to be broadcast to all players in game
 * after a player has chosen its wizard notifying all remaining players
 * about remaining available wizards
 */

public class AvailableWizardMessage implements UpdateMessage {
    ArrayList<Integer> remainingWizards;

    /**
     * @param remainingWizards ArrayList of available wizards from which players have to choose
     */
    public AvailableWizardMessage(ArrayList<Integer> remainingWizards) {
        this.remainingWizards = remainingWizards;
    }

    @Override
    public void update(GameBoard GB) {
        GB.setAvailableWizards(remainingWizards);
    }

}
