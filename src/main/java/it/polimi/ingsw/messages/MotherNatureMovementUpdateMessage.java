package it.polimi.ingsw.messages;

public class MotherNatureMovementUpdateMessage implements Message{
    int islandIndex;

    public MotherNatureMovementUpdateMessage(int islandIndex) {
        this.islandIndex = islandIndex;
    }

    public int getIslandIndex() {
        return islandIndex;
    }
}
