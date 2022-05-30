package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.ClientPersonality;
import it.polimi.ingsw.CLI.GameBoard;

public class CoinsUpdateMessage implements UpdateMessage{
    int coins;
    String player;
    boolean hasBeenUsed;

    public CoinsUpdateMessage(int coins, String player, boolean hasBeenUsed){
        this.player = player;
        this.coins = coins;
        this.hasBeenUsed = hasBeenUsed;
    }

    public int getCoins() {
        return coins;
    }

    public String getPlayer() {
        return player;
    }

    public boolean isHasBeenUsed() {return hasBeenUsed;}

    @Override
    public void update(GameBoard GB) {
        GB.updateCoins(coins, player,hasBeenUsed);
        GB.print();
    }
}
