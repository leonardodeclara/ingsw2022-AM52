package it.polimi.ingsw.messages;

public class NicknameMessage extends Message{
    private final String playerNickname;

    public NicknameMessage(String playerInput){
        this.playerNickname = playerInput;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }
}
