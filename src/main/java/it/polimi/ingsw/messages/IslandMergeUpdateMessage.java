package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.ClientIsland;
import java.util.ArrayList;

public class IslandMergeUpdateMessage implements Message{
    ArrayList<ClientIsland> updatedClientIslands;

    public IslandMergeUpdateMessage(ArrayList<ClientIsland> updatedClientIslands) {
        this.updatedClientIslands=updatedClientIslands;
    }

    public ArrayList<ClientIsland> getUpdatedClientIslands() {
        return updatedClientIslands;
    }
}
