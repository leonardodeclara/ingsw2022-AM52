package it.polimi.ingsw.client;

import it.polimi.ingsw.GUI.UI;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.Tower;

import java.util.ArrayList;

public class Client { //gestisce la socket da un lato e dialoga con CLI/GUI dall'altro
    ClientSocket clientSocket;
    InputParser inputParser;
    UI cli;
    boolean active;

    public Client(UI cli) {
        this.cli=cli;
        active = true;
    }

    public Message buildMessageFromPlayerInput(ArrayList<Object> data,ClientState currentState){
        switch (currentState){ //in base allo stato costruiamo messaggi differenti
            case CONNECT_STATE:
                return buildConnectMessage(data);
            case INSERT_NEW_GAME_PARAMETERS:
                return buildNewGameParametersMessage(data);
            case SET_UP_WIZARD_PHASE:
                return buildWizardSelectionMessage(data);
            case SET_UP_TOWER_PHASE:
                return buildTowerSelectionMessage(data);
            case PLAY_ASSISTANT_CARD:
                return buildPlayAssistantCardMessage(data);
            case MOVE_FROM_LOBBY:
                if(data.get(0) instanceof Boolean)
                        return buildPlayPersonalityCardMessage(data);

                return buildMoveFromLobbyMessage(data);
            case MOVE_MOTHER_NATURE:
                if(data.get(0) instanceof Boolean)
                        return buildPlayPersonalityCardMessage(data);

                return buildMoveMotherNature(data);
            case PICK_CLOUD:
                if(data.get(0) instanceof Boolean)
                        return buildPlayPersonalityCardMessage(data);

                return buildCloudSelectionMessage(data);
            case END_TURN:
                if(data.size() >= 2)
                    if((Boolean) data.get(1) == true)
                        return buildPlayPersonalityCardMessage(data);

                return buildCloseTurnMessage(data);
        }
        return null;
    }

    private Message buildConnectMessage(ArrayList<Object> data){
        return new LoginRequestMessage((String)data.get(0));
    }

    private Message buildNewGameParametersMessage(ArrayList<Object> data){
        cli.prepareView(data);
        return new GameParametersMessage((Integer)data.get(0),(Boolean)data.get(1));
    }

    private Message buildWizardSelectionMessage(ArrayList<Object> data){
        return new WizardSelectionMessage((int) data.get(0));
    }

    private Message buildTowerSelectionMessage(ArrayList<Object> data){
        return new TowerSelectionMessage((Tower) data.get(0));
    }

    private Message buildPlayAssistantCardMessage(ArrayList<Object> data){
        return new PlayAssistantCardMessage((Integer)data.get(0));
    }

    private Message buildMoveFromLobbyMessage(ArrayList<Object> data){
        return new MoveStudentsFromLobbyMessage((ArrayList<Integer>) data.get(0),(ArrayList<Integer>) data.get(1));
    }

    private Message buildMoveMotherNature(ArrayList<Object> data){
        return new MotherNatureMoveMessage((Integer) data.get(0));
    }

    private Message buildCloudSelectionMessage(ArrayList<Object> data){
        return new CloudSelectionMessage((Integer) data.get(0));
    }

    private Message buildPlayPersonalityCardMessage(ArrayList<Object> data){
        return new PlayPersonalityCardMessage((Integer) data.get(0));
    }

    //in questo
    private Message buildCloseTurnMessage(ArrayList<Object> data){
        return new CloseTurnMessage((String) data.get(0));
    }
}





