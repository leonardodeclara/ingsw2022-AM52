package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.GameBoard;

public class CoinsUpdateMessage implements UpdateMessage{
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

    @Override
    public void update(GameBoard GB) {
        GB.updateCoins(coins, player);
        GB.print();
    }
}
