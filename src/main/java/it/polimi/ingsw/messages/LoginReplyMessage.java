package it.polimi.ingsw.messages;

public class LoginReplyMessage implements Message {
    private final String playerNickname;

    public LoginReplyMessage(String nickname){
        playerNickname = nickname;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }
}
