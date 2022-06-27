package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;
import it.polimi.ingsw.model.Tower;

import java.util.ArrayList;

public class IslandTowersUpdateMessage implements UpdateMessage {
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

    @Override
    public void update(GameBoard GB) {
        GB.setIslandTowers(islandIndex, new ArrayList<>(towers));
        GB.print();
    }
}
