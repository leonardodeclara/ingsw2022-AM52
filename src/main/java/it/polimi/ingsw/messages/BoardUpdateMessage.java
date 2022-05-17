package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.ClientBoard;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;

import java.util.ArrayList;
import java.util.HashMap;
//da cambiare, bisogna creare un messaggio ad hoc per ogni cosa della lobby che cambia.
//altrimenti quando il messaggio di update arriva al client non capisce cosa è cambiato
public class BoardUpdateMessage implements Message {
    ClientBoard clientBoard;
    String owner;

    public BoardUpdateMessage(String owner, ClientBoard clientBoard) {
        this.clientBoard = clientBoard;
        this.owner = owner;

    }

    public ClientBoard getClientBoard() {
        return clientBoard;
    }

    public String getOwner() {
        return owner;
    }
}

