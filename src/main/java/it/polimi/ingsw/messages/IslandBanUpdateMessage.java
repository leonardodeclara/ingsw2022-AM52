package it.polimi.ingsw.messages;

public class IslandBanUpdateMessage implements Message{
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
}
