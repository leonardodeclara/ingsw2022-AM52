package it.polimi.ingsw.messages;

<<<<<<< HEAD
public class LoginRequestMessage extends Message{

    private String Nickname;

    public LoginRequestMessage(String nickname) {
        this.Nickname = nickname;
    }

    public String getNickname() {
        return Nickname;
    }
}

=======
public class LoginRequestMessage implements Message{
    private final String playerNickname;

    public LoginRequestMessage(String playerInput){
        this.playerNickname = playerInput;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }
}
>>>>>>> origin/master
