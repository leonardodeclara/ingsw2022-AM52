package it.polimi.ingsw.messages;

public class GameParametersMessage implements Message {
    int numberPlayers;
    boolean expertGame;

    public GameParametersMessage(int numberPlayers,boolean expertGame){
        this.numberPlayers = numberPlayers;
        this.expertGame = expertGame;
    }

    public GameParametersMessage(){

    }

    public int getNumberPlayers() {
        return numberPlayers;
    }

    public boolean isExpertGame() {
        return expertGame;
    }
}
