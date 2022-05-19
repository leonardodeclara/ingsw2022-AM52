package it.polimi.ingsw.client;

import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.controller.ServerSocketConnection;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.Tower;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Client { //gestisce la socket da un lato e dialoga con CLI/GUI dall'altro
    ClientSocket clientSocket;
    InputParser inputParser;
    CLI cli;
    boolean active;

    public Client(CLI cli) {
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
                return buildMoveFromLobbyMessage(data);
            case MOVE_MOTHER_NATURE:
                return buildMoveMotherNature(data);
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
}





