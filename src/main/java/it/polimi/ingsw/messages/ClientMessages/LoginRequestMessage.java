package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;

public class LoginRequestMessage implements Message {
    private final String playerNickname;

    public LoginRequestMessage(String playerInput){
        this.playerNickname = playerInput;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }
}
