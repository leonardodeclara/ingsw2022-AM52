package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.ClientBoard;
import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

//da cambiare, bisogna creare un messaggio ad hoc per ogni cosa della lobby che cambia.
//altrimenti quando il messaggio di update arriva al client non capisce cosa è cambiato
public class BoardUpdateMessage implements UpdateMessage {
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

    @Override
    public void update(GameBoard GB) {
        GB.setUpdatedClientBoard(owner, clientBoard);
        GB.print();
    }
}

