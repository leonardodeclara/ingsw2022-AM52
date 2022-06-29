package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.ClientBoard;
import it.polimi.ingsw.client.CLI.ClientIsland;
import it.polimi.ingsw.client.CLI.ClientPersonality;
import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This message is going to be broadcast to all players in game
 * and carries the information about newly instantiated game elements
 */

public class GameInstantiationMessage implements UpdateMessage {
    private final ArrayList<ClientIsland> islands;
    private HashMap<String,ClientBoard> boards;
    private ArrayList<ClientPersonality> personalities;

    /**
     * @param islands Islands newly instantiated
     * @param boards Board newly instantiated
     */
    public GameInstantiationMessage(ArrayList<ClientIsland> islands, HashMap<String,ClientBoard> boards){
        this.islands=new ArrayList<>(islands);
        this.boards= new HashMap<>(boards);
    }

    /**
     * @param islands Islands newly instantiated
     * @param boards Boards newly instantiated
     * @param personalities Personality cards newly instantiated in case of Expert Game mode
     */
    public GameInstantiationMessage(ArrayList<ClientIsland> islands, HashMap<String,ClientBoard> boards,ArrayList<ClientPersonality> personalities){
        this.islands=new ArrayList<>(islands);
        this.boards= new HashMap<>(boards);
        this.personalities = new ArrayList<>(personalities);
    }

    public ArrayList<ClientIsland> getIslands() {
        return islands;
    }

    @Override
    public void update(GameBoard GB) {
        GB.instantiateGameElements(islands, boards,personalities);
        GB.print();
    }
}
