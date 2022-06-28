package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

public class IslandBanUpdateMessage implements UpdateMessage {
    private int islandId;
    private int banCount;

    public IslandBanUpdateMessage(int islandId, int banCount) {
        this.islandId = islandId;
        this.banCount = banCount;
    }

    public int getIslandId() {
        return islandId;
    }

    public int getBanCount() {
        return banCount;
    }

    @Override
    public void update(GameBoard GB) {
        GB.setIslandBans(islandId,banCount);
        GB.print();
    }
}
