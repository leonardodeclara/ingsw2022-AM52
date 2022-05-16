package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.ClientCloud;
import it.polimi.ingsw.CLI.ClientIsland;

import java.util.ArrayList;

public class GameInstantiationMessage implements Message{
    ArrayList<ClientIsland> islands;

    public GameInstantiationMessage(ArrayList<ClientIsland> islands){
        this.islands=islands;
    }

    public ArrayList<ClientIsland> getIslands() {
        return islands;
    }
}
