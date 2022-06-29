package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.ClientIsland;
import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

import java.util.ArrayList;

public class IslandMergeUpdateMessage implements UpdateMessage {
    ArrayList<ClientIsland> updatedClientIslands;

    public IslandMergeUpdateMessage(ArrayList<ClientIsland> updatedClientIslands) {
        this.updatedClientIslands=updatedClientIslands;
    }

    @Override
    public void update(GameBoard GB) {
        GB.setIslands(updatedClientIslands);
        GB.print();
    }
}
