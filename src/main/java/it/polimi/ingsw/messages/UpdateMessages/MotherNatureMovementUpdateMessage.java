package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

public class MotherNatureMovementUpdateMessage implements UpdateMessage {
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
