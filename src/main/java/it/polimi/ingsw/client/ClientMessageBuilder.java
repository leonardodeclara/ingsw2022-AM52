package it.polimi.ingsw.client;

import it.polimi.ingsw.client.GUI.UI;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.ClientMessages.*;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.model.Color;

import java.util.ArrayList;

/**
 * Class ClientMessageBuilder receives generic arraylist of data and a clientState
 * to build state-specific messages which will be sent to the server
 */
public class ClientMessageBuilder {
    private ClientSocket clientSocket;
    private InputParser inputParser;
    private UI cli;
    private boolean active;

    public ClientMessageBuilder(UI cli) {
        this.cli=cli;
        active = true;
    }

    /**
     * Method buildMessageFromPlayerInput calls a different message builder method accordingly to the client state
     * @param data contains the informations to wrap into the message
     * @param currentState represents the client current state
     * @return message to send to the server
     */
    public Message buildMessageFromPlayerInput(ArrayList<Object> data,ClientState currentState){
        switch (currentState){
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

    /**
     * Method buildConnectMessage builds a LoginRequestMessage which contains player's nickname
     * @param data contains the informations to wrap into the message
     * @return message to send to the server
     */
    private Message buildConnectMessage(ArrayList<Object> data){
        return new LoginRequestMessage((String)data.get(0));
    }

    /**
     * Method buildNewGameParameters builds a GameParametersMessage which contains expert/base flag and number of players
     * @param data contains the informations to wrap into the message
     * @return message to send to the server
     */
    private Message buildNewGameParametersMessage(ArrayList<Object> data){
        cli.prepareView(data);
        return new GameParametersMessage((Integer)data.get(0),(Boolean)data.get(1));
    }

    /**
     * Method buildWizardSelectionMessage builds a WizardSelectionMessage which contains chosen wizard id
     * @param data contains the informations to wrap into the message
     * @return message to send to the server
     */
    private Message buildWizardSelectionMessage(ArrayList<Object> data){
        return new WizardSelectionMessage((int) data.get(0));
    }

    /**
     * Method buildTowerSelectionMessage builds a TowerSelectionMessage which contains chosen Tower instance
     * @param data contains the informations to wrap into the message
     * @return message to send to the server
     */
    private Message buildTowerSelectionMessage(ArrayList<Object> data){
        return new TowerSelectionMessage((Tower) data.get(0));
    }

    /**
     * Method buildPlayAssistantCardMessage builds a PlayAssistantCardMessage which contains chosen assistant card ID
     * @param data contains the informations to wrap into the message
     * @return message to send to the server
     */
    private Message buildPlayAssistantCardMessage(ArrayList<Object> data){
        return new PlayAssistantCardMessage((Integer)data.get(0));
    }

    /**
     * Method buildMoveFromLobbyMessage builds a MoveStudentsFromLobbyMessage which contains 3 lobby students ID
     * and 3 destinations ID (where ID = islandID or -1 for the table)
     * @param data contains the informations to wrap into the message
     * @return message to send to the server
     */
    private Message buildMoveFromLobbyMessage(ArrayList<Object> data){
        ((ArrayList<Integer>) data.get(0)).stream().limit(3);
        return new MoveStudentsFromLobbyMessage((ArrayList<Integer>) data.get(0),(ArrayList<Integer>) data.get(1));
    }

    /**
     * Method buildMoveMotherNature builds a MotherNatureMessage which contains destination island ID for Mother Nature
     * @param data contains the informations to wrap into the message
     * @return message to send to the server
     */
    private Message buildMoveMotherNature(ArrayList<Object> data){
        return new MotherNatureMoveMessage((Integer) data.get(0));
    }

    /**
     * Method buildCloudSelectionMessage builds a CloudSelectionMessage which contains chosen cloud ID
     * @param data contains the informations to wrap into the message
     * @return message to send to the server
     */
    private Message buildCloudSelectionMessage(ArrayList<Object> data){
        return new CloudSelectionMessage((Integer) data.get(0));
    }

    /**
     * Method buildPlayPersonalityMessage builds a PlayPersonalityCardMessage which contains chosen personality card ID
     * @param data contains the informations to wrap into the message
     * @return message to send to the server
     */
    private Message buildPlayPersonalityCardMessage(ArrayList<Object> data){
        return new PlayPersonalityCardMessage((Integer) data.get(1));
    }

    /**
     * Method buildCloseTurnMessage builds a CloseTurnMessage which contains the end turn keyword
     * @param data contains the informations to wrap into the message
     * @return message to send to the server
     */
    private Message buildCloseTurnMessage(ArrayList<Object> data){
        return new CloseTurnMessage((String) data.get(0));
    }

    /**
     * Method buildCard1EffectMessage builds a Card1EffectMessage which contains chosen students and island ID
     * @param data contains the informations to wrap into the message
     * @return message to send to the server
     */
    private Message buildCard1EffectMessage(ArrayList<Object> data){
        return new Card1EffectMessage((Integer) data.get(0), (Integer) data.get(1));
    }

    /**
     * Method buildCard3EffectMessage builds a Card3EffectMessage which contains chosen island ID
     * @param data contains the informations to wrap into the message
     * @return message to send to the server
     */
    private Message buildCard3EffectMessage(ArrayList<Object> data){
        return new Card3EffectMessage((Integer) data.get(0));
    }

    /**
     * Method buildCard5EffectMessage builds a Card5EffectMessage which contains chosen island ID
     * @param data contains the informations to wrap into the message
     * @return message to send to the server
     */
    private Message buildCard5EffectMessage(ArrayList<Object> data){
        return new Card5EffectMessage((Integer) data.get(0));
    }

    /**
     * Method buildCard7EffectMessage builds a Card7EffectMessage which contains chosen students ID from board lobby and card lobby
     * @param data contains the informations to wrap into the message
     * @return message to send to the server
     */
    private Message buildCard7EffectMessage(ArrayList<Object> data){
        return new Card7EffectMessage((ArrayList<Integer>) data.get(0),(ArrayList<Integer>)data.get(1));
    }

    /**
     * Method buildCard9EffectMessage builds a Card9EffectMessage which contains the banned color
     * @param data contains the informations to wrap into the message
     * @return message to send to the server
     */
    private Message buildCard9EffectMessage(ArrayList<Object> data){
        return new Card9EffectMessage((Color) data.get(0));
    }

    /**
     * Method buildCard10EffectMessage builds a Card10EffectMessage which contains chosen students ID from lobby and table
     * @param data contains the informations to wrap into the message
     * @return message to send to the server
     */
    private Message buildCard10EffectMessage(ArrayList<Object> data){
        return new Card10EffectMessage((ArrayList<Color>) data.get(0),(ArrayList<Integer>)data.get(1));
    }

    /**
     * Method buildCard11EffectMessage builds a Card11EffectMessage which contains chosen student ID
     * @param data contains the informations to wrap into the message
     * @return message to send to the server
     */
    private Message buildCard11EffectMessage(ArrayList<Object> data){
        return new Card11EffectMessage((Integer)data.get(0));
    }

    /**
     * Method buildCard12EffectMessage builds a Card12EffectMessage which contains banned color
     * @param data contains the informations to wrap into the message
     * @return message to send to the server
     */
    private Message buildCard12EffectMessage(ArrayList<Object> data){
        return new Card12EffectMessage((Color)data.get(0));
    }
}





