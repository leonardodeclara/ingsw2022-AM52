package it.polimi.ingsw.CLI;

public class ClientPersonality {
    private Integer CardID;
    private Boolean hasBeenUsed;
    private Integer cost;

    public ClientPersonality(Integer cardID, Boolean hasBeenUsed, Integer cost) {
        CardID = cardID;
        this.hasBeenUsed = hasBeenUsed;
        this.cost = cost;
    }

    public Integer getCardID() {
        return CardID;
    }

    public Boolean getHasBeenUsed() {
        return hasBeenUsed;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCardID(Integer cardID) {
        CardID = cardID;
    }

    public void setHasBeenUsed(Boolean hasBeenUsed) {
        this.hasBeenUsed = hasBeenUsed;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }
}
