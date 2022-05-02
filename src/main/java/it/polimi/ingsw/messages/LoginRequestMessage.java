package it.polimi.ingsw.messages;

public class LoginRequestMessage extends Message{

    private String Nickname;

    public LoginRequestMessage(String nickname) {
        this.Nickname = nickname;
    }

    public String getNickname() {
        return Nickname;
    }
}

