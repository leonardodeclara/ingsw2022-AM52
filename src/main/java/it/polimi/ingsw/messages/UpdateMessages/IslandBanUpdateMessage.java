package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

/**
 * This message is going to be broadcast to all players in game
 * after updates on an island's bans count
 */

public class IslandBanUpdateMessage implements UpdateMessage {
    private int islandId;
    private int banCount;

    /**
     * @param islandId ID of the island on which updates bans count
     * @param banCount number of updated bans
     */
    public IslandBanUpdateMessage(int islandId, int banCount) {
        this.islandId = islandId;
        this.banCount = banCount;
    }

    public int getIslandId() {
        return islandId;
    }

    @Override
    public void update(GameBoard GB) {
        GB.setIslandBans(islandId,banCount);
        GB.print();
    }
}
