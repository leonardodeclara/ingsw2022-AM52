package it.polimi.ingsw.messages;

public class IslandMergeUpdateMessage implements Message{
    int mergerIsland;
    int mergedIsland;

    public IslandMergeUpdateMessage(int mergerIsland, int mergedIsland) {
        this.mergerIsland = mergerIsland;
        this.mergedIsland = mergedIsland;
    }

    public int getMergerIsland() {
        return mergerIsland;
    }

    public int getMergedIsland() {
        return mergedIsland;
    }
}
