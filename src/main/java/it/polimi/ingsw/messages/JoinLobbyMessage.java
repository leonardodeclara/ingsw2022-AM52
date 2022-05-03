package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.Player;

public class JoinLobbyMessage implements Message {
    int playersNumber;

    public JoinLobbyMessage(int playersNumber) {
        this.playersNumber = playersNumber;
    }

    public int getPlayersNumber(){ return playersNumber;}
    }



