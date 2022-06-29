package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;

/**
 * This message is sent from Client to Server to notify a login request
 * by a player
 */

public class LoginRequestMessage implements Message {
    private final String playerNickname;

    /**
     * @param playerInput nickname of the player
     */
    public LoginRequestMessage(String playerInput){
        this.playerNickname = playerInput;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }
}
