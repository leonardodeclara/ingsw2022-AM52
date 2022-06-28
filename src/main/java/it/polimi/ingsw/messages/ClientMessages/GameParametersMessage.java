package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;

public class GameParametersMessage implements Message {
    int numberPlayers;
    boolean expertGame;

    public GameParametersMessage(int numberPlayers,boolean expertGame){
        this.numberPlayers = numberPlayers;
        this.expertGame = expertGame;
    }

    public int getNumberPlayers() {
        return numberPlayers;
    }

    public boolean isExpertGame() {
        return expertGame;
    }
}
