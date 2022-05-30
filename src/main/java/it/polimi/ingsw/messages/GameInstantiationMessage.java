package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.ClientBoard;
import it.polimi.ingsw.CLI.ClientIsland;
import it.polimi.ingsw.CLI.ClientPersonality;
import it.polimi.ingsw.CLI.GameBoard;

import java.util.ArrayList;
import java.util.HashMap;

public class GameInstantiationMessage implements UpdateMessage{
    private final ArrayList<ClientIsland> islands;
    private HashMap<String,ClientBoard> boards;
    private ArrayList<ClientPersonality> personalities;

    public GameInstantiationMessage(ArrayList<ClientIsland> islands, HashMap<String,ClientBoard> boards){
        this.islands=new ArrayList<>(islands);
        this.boards= new HashMap<>(boards);
    }

    public GameInstantiationMessage(ArrayList<ClientIsland> islands, HashMap<String,ClientBoard> boards,ArrayList<ClientPersonality> personalities){
        this.islands=new ArrayList<>(islands);
        this.boards= new HashMap<>(boards);
        this.personalities = new ArrayList<>(personalities);
    }
    public ArrayList<ClientIsland> getIslands() {
        return islands;
    }

    public HashMap<String,ClientBoard> getBoards() {
        return boards;
    }

    public ArrayList<ClientPersonality> getPersonalities() {return personalities;}

    @Override
    public void update(GameBoard GB) {
        GB.instantiateGameElements(islands, boards,personalities);
        GB.print();
    }
}
