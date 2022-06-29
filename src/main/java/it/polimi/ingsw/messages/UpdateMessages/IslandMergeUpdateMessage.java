package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.ClientIsland;
import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

import java.util.ArrayList;

/**
 * This message is going to be broadcast to all players in game
 * after the merge of islands
 */

public class IslandMergeUpdateMessage implements UpdateMessage {
    ArrayList<ClientIsland> updatedClientIslands;

    /**
     * @param updatedClientIslands ArrayList of the merged Islands 
     */
    public IslandMergeUpdateMessage(ArrayList<ClientIsland> updatedClientIslands) {
        this.updatedClientIslands=updatedClientIslands;
    }

    @Override
    public void update(GameBoard GB) {
        GB.setIslands(updatedClientIslands);
        GB.print();
    }
}
