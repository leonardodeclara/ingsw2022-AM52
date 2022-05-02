package it.polimi.ingsw.messages;

public class LoginRequestMessage implements Message{
    private final String playerNickname;

    public LoginRequestMessage(String playerInput){
        this.playerNickname = playerInput;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }
}
