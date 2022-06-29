package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.ClientBoard;
import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

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

