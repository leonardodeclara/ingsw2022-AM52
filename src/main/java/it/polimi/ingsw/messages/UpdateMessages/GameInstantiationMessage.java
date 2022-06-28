package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.ClientBoard;
import it.polimi.ingsw.client.CLI.ClientIsland;
import it.polimi.ingsw.client.CLI.ClientPersonality;
import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

import java.util.ArrayList;
import java.util.HashMap;

public class GameInstantiationMessage implements UpdateMessage {
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
