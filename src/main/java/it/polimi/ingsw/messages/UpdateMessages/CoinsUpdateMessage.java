package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.CLI.ClientPersonality;
import it.polimi.ingsw.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

public class CoinsUpdateMessage implements UpdateMessage {
    int coins;
    String player;
    int reserveCoins;

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

    public int getReserveCoins() {return reserveCoins;}

    @Override
    public void update(GameBoard GB) {
        GB.updateCoins(coins, player,reserveCoins);
        GB.print();
    }
}
