package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

/**
 * This message is going to be broadcast to all players in game
 * after updates of coins allocation
 */

public class CoinsUpdateMessage implements UpdateMessage {
    int coins;
    String player;
    int reserveCoins;

    /**
     * @param coins number of coins added to the player
     * @param player nickname of the player
     * @param reserveCoins number of reserve coins of the player
     */

    public CoinsUpdateMessage(int coins, String player, int reserveCoins){
        this.player = player;
        this.coins = coins;
        this.reserveCoins = reserveCoins;
    }

    public int getCoins() {
        return coins;
    }

    public String getPlayer() {
        return player;
    }

    @Override
    public void update(GameBoard GB) {
        GB.updateCoins(coins, player,reserveCoins);
        GB.print();
    }
}
