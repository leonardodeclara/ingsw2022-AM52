package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;
import it.polimi.ingsw.model.Tower;

import java.util.ArrayList;

/**
 * This message is going to be broadcast to all players in game
 * after updates of an island's towers number
 */

public class IslandTowersUpdateMessage implements UpdateMessage {
    int islandIndex;
    ArrayList<Tower> towers;

    /**
     * @param islandIndex ID of the island on which the number of towers change
     * @param towers ArrayList of new towers added
     */
    public IslandTowersUpdateMessage(int islandIndex, ArrayList<Tower> towers) {
        this.islandIndex = islandIndex;
        this.towers = towers;
    }

    public int getIslandIndex() {
        return islandIndex;
    }

    public ArrayList<Tower> getTowers() {
        return new ArrayList<>(towers);
    }

    @Override
    public void update(GameBoard GB) {
        GB.setIslandTowers(islandIndex, new ArrayList<>(towers));
        GB.print();
    }
}
