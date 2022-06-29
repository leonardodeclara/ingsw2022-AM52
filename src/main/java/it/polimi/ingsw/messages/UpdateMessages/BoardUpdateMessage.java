package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.ClientBoard;
import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

/**
 * This message is going to be broadcast to all players in game
 * after updates on a player's board content
 */

public class BoardUpdateMessage implements UpdateMessage {
    ClientBoard clientBoard;
    String owner;

    /**
     * @param owner nickname of the player to whom the board was updated
     * @param clientBoard instance of the player's ClientBoard that has been updated
     */

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

