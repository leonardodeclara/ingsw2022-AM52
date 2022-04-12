package it.polimi.ingsw.model;

public class Personality {
    private int characterId;
    private boolean hasBeenUsed;
    private int cost;

    public Personality(int id, int initialCost){
        characterId=id;
        cost=initialCost;
    }

    public void updateCost(){
        if(!hasBeenUsed){
            cost+=1;
            setHasBeenUsed(true);
        }
    }

    public int getCharacterId() {
        return characterId;
    }

    public void setCharacterId(int characterId) {
        this.characterId = characterId;
    }

    public boolean isHasBeenUsed() {
        return hasBeenUsed;
    }

    public void setHasBeenUsed(boolean hasBeenUsed) {
        this.hasBeenUsed = hasBeenUsed;
    }

    public int getCost() {
        return cost;
    }

}


//carte 0, 6, 10: lobby personality
//carta 4 ban personality
//le altre personality classiche