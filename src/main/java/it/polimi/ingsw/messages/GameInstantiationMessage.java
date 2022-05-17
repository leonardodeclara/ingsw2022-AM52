package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.ClientBoard;
import it.polimi.ingsw.CLI.ClientIsland;

import java.util.ArrayList;
import java.util.HashMap;

public class GameInstantiationMessage implements Message{
    private final ArrayList<ClientIsland> islands;
    private HashMap<String,ClientBoard> boards;

    public GameInstantiationMessage(ArrayList<ClientIsland> islands, HashMap<String,ClientBoard> boards){
        this.islands=new ArrayList<>(islands);
        this.boards= new HashMap<>(boards);
    }

    public ArrayList<ClientIsland> getIslands() {
        return islands;
    }

    public HashMap<String,ClientBoard> getBoards() {
        return boards;
    }
}
