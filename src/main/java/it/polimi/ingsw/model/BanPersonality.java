package it.polimi.ingsw.model;

//carta id 4

/**
 * This is a subclass of the class Personality
 * This class represents the Personality card with CardID 4
 * At the beginning of the game this card has 4 bans tiles on it
 */
public class BanPersonality extends Personality{
    private int bans;

    /**
     * Constructor creates a BanPersonality card instance
     * @param id: unique identifier for the Personality card
     */

    public BanPersonality(int id){
        super(id);
        bans=4;
    }

    /**
     * This method add bans on the card
     */
    public void addBan(){
        if(bans<4)
            bans+=1;
    }

    /**
     * This method remove bans from the card
     */
    public void removeBan(){
        if(bans>0)
            bans-=1;
    }

    /**
     * This method returns the number of bans that are on the card
     * @return number of bans on the card
     */
    public int getBans() {
        return bans;
    }


}
