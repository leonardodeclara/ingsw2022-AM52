package it.polimi.ingsw.messages;

public class LoginReplyMessage extends Message {

    private String Nickname;

    public LoginReplyMessage(String nickname) {
        this.Nickname = nickname;
    }
}
