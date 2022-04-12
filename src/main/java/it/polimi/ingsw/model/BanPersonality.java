package it.polimi.ingsw.model;

//carta id 4
public class BanPersonality extends Personality{
    private int bans;

    public BanPersonality(int id, int initialCost){
        super(id, initialCost);
        bans=4;
    }

    public void addBan(){
        if(bans<4)
            bans+=1;
    }

}
