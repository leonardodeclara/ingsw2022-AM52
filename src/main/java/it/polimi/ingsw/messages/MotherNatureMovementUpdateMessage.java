package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.GameBoard;

public class MotherNatureMovementUpdateMessage implements UpdateMessage{
    int islandIndex;

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
