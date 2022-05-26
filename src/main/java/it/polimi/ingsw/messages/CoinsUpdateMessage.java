package it.polimi.ingsw.messages;

public class CoinsUpdateMessage implements Message{
    int coins;
    String player;

    public CoinsUpdateMessage(int coins,String player){
        this.player = player;
        this.coins = coins;
    }

    public int getCoins() {
        return coins;
    }

    public String getPlayer() {
        return player;
    }
}
