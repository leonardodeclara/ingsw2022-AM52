package it.polimi.ingsw.client;

import it.polimi.ingsw.GUI.UI;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.model.Color;

import java.util.ArrayList;

public class Client { //gestisce la socket da un lato e dialoga con CLI/GUI dall'altro
    private ClientSocket clientSocket;
    private InputParser inputParser;
    private UI cli;
    private boolean active;

    public Client(UI cli) {
        this.cli=cli;
        active = true;
    }

    public Message buildMessageFromPlayerInput(ArrayList<Object> data,ClientState currentState){
        switch (currentState){ //in base allo stato costruiamo messaggi differenti
            case CONNECT_STATE:
                if(data.size() > 0)
                    return buildConnectMessage(data);
            case INSERT_NEW_GAME_PARAMETERS:
                if(data.size() > 1)
                    return buildNewGameParametersMessage(data);
            case SET_UP_WIZARD_PHASE:
                if(data.size() > 0)
                    return buildWizardSelectionMessage(data);
            case SET_UP_TOWER_PHASE:
                if(data.size() > 0)
                    return buildTowerSelectionMessage(data);
            case PLAY_ASSISTANT_CARD:
                if(data.size() > 0)
                    return buildPlayAssistantCardMessage(data);
            case MOVE_FROM_LOBBY:
                if(data.size() >= 2)
                    if(data.get(0) instanceof Boolean)
                        return buildPlayPersonalityCardMessage(data);

                if(data.size() > 0)
                    return buildMoveFromLobbyMessage(data);
            case MOVE_MOTHER_NATURE:
                if(data.size() >= 2)
                    if(data.get(0) instanceof Boolean)
                        return buildPlayPersonalityCardMessage(data);

                if(data.size() > 0)
                    return buildMoveMotherNature(data);
            case PICK_CLOUD:
                if(data.size() >= 2)
                    if(data.get(0) instanceof Boolean)
                        return buildPlayPersonalityCardMessage(data);

                if(data.size() > 0)
                return buildCloudSelectionMessage(data);
            case END_TURN:
                if(data.size() >= 2)
                    if((Boolean) data.get(0) == true)
                        return buildPlayPersonalityCardMessage(data);

                return buildCloseTurnMessage(data);
            case CHOOSE_STUDENT_FOR_CARD_1:
                return buildCard1EffectMessage(data);
            case CHOOSE_ISLAND_FOR_CARD_3:
                return buildCard3EffectMessage(data);
            case CHOOSE_ISLAND_FOR_CARD_5:
                return buildCard5EffectMessage(data);
            case SWAP_STUDENTS_FOR_CARD_7:
                return buildCard7EffectMessage(data);
            case CHOOSE_COLOR_FOR_CARD_9:
                return buildCard9EffectMessage(data);
            case CHOOSE_STUDENTS_FOR_CARD_10:
                return buildCard10EffectMessage(data);
            case CHOOSE_STUDENT_FOR_CARD_11:
                return buildCard11EffectMessage(data);
            case CHOOSE_COLOR_FOR_CARD_12:
                return buildCard12EffectMessage(data);
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
        ((ArrayList<Integer>) data.get(0)).stream().limit(3);
        return new MoveStudentsFromLobbyMessage((ArrayList<Integer>) data.get(0),(ArrayList<Integer>) data.get(1));
    }

    private Message buildMoveMotherNature(ArrayList<Object> data){
        return new MotherNatureMoveMessage((Integer) data.get(0));
    }

    private Message buildCloudSelectionMessage(ArrayList<Object> data){
        return new CloudSelectionMessage((Integer) data.get(0));
    }

    private Message buildPlayPersonalityCardMessage(ArrayList<Object> data){
        return new PlayPersonalityCardMessage((Integer) data.get(1));
    }

    //in questo
    private Message buildCloseTurnMessage(ArrayList<Object> data){
        return new CloseTurnMessage((String) data.get(0));
    }

    private Message buildCard1EffectMessage(ArrayList<Object> data){
        return new Card1EffectMessage((Integer) data.get(0), (Integer) data.get(1));
    }

    private Message buildCard3EffectMessage(ArrayList<Object> data){
        return new Card3EffectMessage((Integer) data.get(0));
    }

    private Message buildCard5EffectMessage(ArrayList<Object> data){
        return new Card5EffectMessage((Integer) data.get(0));
    }

    private Message buildCard7EffectMessage(ArrayList<Object> data){
        return new Card7EffectMessage((ArrayList<Integer>) data.get(0),(ArrayList<Integer>)data.get(1));
    }

    private Message buildCard9EffectMessage(ArrayList<Object> data){
        return new Card9EffectMessage((Color) data.get(0));
    }

    private Message buildCard10EffectMessage(ArrayList<Object> data){
        return new Card10EffectMessage((ArrayList<Color>) data.get(0),(ArrayList<Integer>)data.get(1));
    }

    private Message buildCard11EffectMessage(ArrayList<Object> data){
        return new Card11EffectMessage((Integer)data.get(0));
    }

    private Message buildCard12EffectMessage(ArrayList<Object> data){
        return new Card12EffectMessage((Color)data.get(0));
    }
}





