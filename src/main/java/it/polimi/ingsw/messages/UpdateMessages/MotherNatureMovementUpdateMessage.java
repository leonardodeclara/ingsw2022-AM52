package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

/**
 * This message is going to be broadcast to all players in game
 * after updates about Mother Nature movements
 */

public class MotherNatureMovementUpdateMessage implements UpdateMessage {
    int islandIndex;

    /**
     * @param islandIndex ID of the destination island of Mother Nature movement
     */
    public MotherNatureMovementUpdateMessage(int islandIndex) {
        this.islandIndex = islandIndex;
    }

    public int getIslandIndex() {
        return islandIndex;
    }

    @Override
    public void update(GameBoard GB) {
        GB.changeMNPosition(islandIndex);
        GB.print();
    }
}
