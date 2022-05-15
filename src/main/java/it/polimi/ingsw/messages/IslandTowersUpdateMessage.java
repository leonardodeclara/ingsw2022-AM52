package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.Tower;

import java.util.ArrayList;

public class IslandTowersUpdateMessage implements Message{
    int islandIndex;
    ArrayList<Tower> towers;

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
}
