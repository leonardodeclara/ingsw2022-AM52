package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.ClientIsland;
import it.polimi.ingsw.CLI.GameBoard;

import java.util.ArrayList;

public class IslandMergeUpdateMessage implements UpdateMessage {
    ArrayList<ClientIsland> updatedClientIslands;

    public IslandMergeUpdateMessage(ArrayList<ClientIsland> updatedClientIslands) {
        this.updatedClientIslands=updatedClientIslands;
    }

    public ArrayList<ClientIsland> getUpdatedClientIslands() {
        return updatedClientIslands;
    }

    @Override
    public void update(GameBoard GB) {
        GB.setIslands(updatedClientIslands);
        GB.print();
    }
}
