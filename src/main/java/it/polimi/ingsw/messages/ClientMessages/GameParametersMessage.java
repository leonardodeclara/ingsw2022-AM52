package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;

/**
 * This message is sent from Client to Server to notify the number of players
 * and game mode choosen by the player
 */

public class GameParametersMessage implements Message {
    int numberPlayers;
    boolean expertGame;

    /**
     * @param numberPlayers number of players of the game. It can be 2 or 3
     * @param expertGame boolean value indicating if the game is in expert mode
     */
    public GameParametersMessage(int numberPlayers,boolean expertGame){
        this.numberPlayers = numberPlayers;
        this.expertGame = expertGame;
    }

    public int getNumberPlayers() {
        return numberPlayers;
    }

    public boolean isExpertGame() {
        return expertGame;
    }
}
