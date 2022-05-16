package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.ClientIsland;

import java.util.ArrayList;

public class GameInstantiationMessage implements Message{
    private final ArrayList<ClientIsland> islands;

    public GameInstantiationMessage(ArrayList<ClientIsland> islands){
        this.islands=new ArrayList<>(islands);
    }

    public ArrayList<ClientIsland> getIslands() {
        return islands;
    }
}
