package it.polimi.ingsw.messages;

<<<<<<< HEAD
public class LoginReplyMessage extends Message {

    private String Nickname;

    public LoginReplyMessage(String nickname) {
        this.Nickname = nickname;
=======
public class LoginReplyMessage implements Message {
    private final String playerNickname;

    public LoginReplyMessage(String nickname){
        playerNickname = nickname;
    }

    public String getPlayerNickname() {
        return playerNickname;
>>>>>>> origin/master
    }
}
