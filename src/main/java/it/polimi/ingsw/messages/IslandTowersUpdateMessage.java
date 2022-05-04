package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.Tower;

public class IslandTowersUpdateMessage implements Message{
    int islandIndex;
    Tower tower;

    public IslandTowersUpdateMessage(int islandIndex, Tower tower) {
        this.islandIndex = islandIndex;
        this.tower = tower;
    }

    public int getIslandIndex() {
        return islandIndex;
    }

    public Tower getTower() {
        return tower;
    }
}
