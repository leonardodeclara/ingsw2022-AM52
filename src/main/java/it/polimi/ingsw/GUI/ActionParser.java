package it.polimi.ingsw.GUI;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.messages.ClientState;
import it.polimi.ingsw.model.Tower;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionParser {
    private HashMap<ClientState,ArrayList<Clickable>> stateToClickableList;

    public ActionParser(){
        stateToClickableList = new HashMap<>();
        for(ClientState state : ClientState.values()){
            stateToClickableList.put(state,state.getClickableList());
        }
    }

    public boolean canClick(ClientState state,Clickable clickedElement){
        try{
            return stateToClickableList.get(state).contains(clickedElement);
        }
        catch(NullPointerException e){
            return false;
        }
    }


    public ArrayList<Object> parse(ClientState state,ArrayList<Integer> clickedIDs){
            switch(state){
                case PLAY_ASSISTANT_CARD -> {
                    return parseAssistantCard(clickedIDs);
                }
            }
            return null;
    }

    public ArrayList<Object> parseAssistantCard(ArrayList<Integer> clickedIDs){
        ArrayList<Object> data = new ArrayList<>();
        data.add(clickedIDs.get(0));
        return data;
    }

    public ArrayList<Object> parseNickname(String nickname){
        ArrayList<Object> data = new ArrayList<>();
        data.add(nickname);
        return  data;
    }

    public ArrayList<Object> parseNewGameParameters(int numOfPlayers, boolean expertGame){
        ArrayList<Object> data = new ArrayList<>();
        data.add(numOfPlayers);
        data.add(expertGame);
        return data;
    }

    public ArrayList<Object> parseWizardChoice(int wizard){
        ArrayList<Object> data = new ArrayList<>();
        data.add(wizard);
        return data;
    }

    public ArrayList<Object> parseTowerChoice(Tower tower){
        ArrayList<Object> data = new ArrayList<>();
        data.add(tower);
        return data;
    }


}
