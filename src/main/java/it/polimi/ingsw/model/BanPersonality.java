package it.polimi.ingsw.model;

//carta id 4
public class BanPersonality extends Personality{
    private int bans;

    public BanPersonality(int id){
        super(id);
        bans=4;
    }

    public void addBan(){
        if(bans<4)
            bans+=1;
    }

    public void removeBan(){
        if(bans>0)
            bans-=1;
    }

    public int getBans() {
        return bans;
    }


}
