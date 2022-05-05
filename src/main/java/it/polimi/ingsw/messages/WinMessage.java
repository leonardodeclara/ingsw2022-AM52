package it.polimi.ingsw.messages;

public class WinMessage implements Message{

    String winmessage;

    public WinMessage(String winmessage) {
        this.winmessage = winmessage;
    }

    public String getWinmessage() {
        return winmessage;
    }
}
