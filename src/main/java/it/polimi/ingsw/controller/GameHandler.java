package it.polimi.ingsw.controller;

import com.sun.net.httpserver.Authenticator;
import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.messages.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class GameHandler {
    GameController gameController;
    ServerSocketConnection serverConnection;
    Server server;
    ArrayList<String> players;
    HashMap<String, ClientHandler> nameToHandlerMap;
    boolean expertGame;

    public GameHandler(Server server,HashMap<String, ClientHandler> nameToHandlerMap, boolean expertGame){
        this.server = server;
        this.nameToHandlerMap =nameToHandlerMap;
        this.expertGame=expertGame;
        players.addAll(nameToHandlerMap.keySet());
    }

    public void handleMessage(Message message,ClientHandler clientHandler){
        //if(message instanceof LoginRequestMessage)
        //    handleLoginRequestMessage((LoginRequestMessage) message, playerID);
        //if(message instanceof GameParametersMessage)
        //    handleGameParametersMessage((GameParametersMessage) message, playerID);
    }

    public void setServer(ServerSocketConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    /*
    public void handleGameParametersMessage(GameParametersMessage message, int playerID){

        String playerNickname = server.idToNicknameMap.get(playerID);
        boolean expertGame = message.isExpertGame();
        int numberOfPlayers = message.getNumberPlayers();
        ClientHandler playerSocket = server.getClientHandlerById(playerID);
        //manca il controllo dell'input e l'eventuale invio di INVALID_INPUT error message
        if(server.joinLobby(playerNickname,numberOfPlayers,expertGame)){ //c'è una lobby e il gioco sta per partire
            startGame();
        } else { //lobby appena creata/lobby già esistente ma non abbastanza players
            playerSocket.sendMessage(new ClientStateMessage(ClientState.WAIT_IN_LOBBY));
        }

        //IDEA:
        //se si volesse fare un'interfaccia della lobby lato client con gli altri giocatori all'interno, basterebbe fare in modo che il client richieda
        //i giocatori con cui è in lobby così da poterne scrivere i nomi sulla GUI/CLI
    }

    */


    /*
    si svuota la lobby inserendo tutto in un game controller (che inizializza il model)
    si mappano i nickname a degli id player (in ordine di join nella lobby, usando quindi la lista players di lobby)
    si manda in broadcast a tutti wait_turn e al primo della lista setup_phase
    quando poi giunge il messaggio dal giocatore usando handleMessages si passa in un altro metodo che fa la setup phase così questo
    non viene riempito di robe che non gli competono
     */
    public void startGame(){
        gameController= new GameController(expertGame);
        ClientStateMessage waitStateMessage = new ClientStateMessage(ClientState.WAIT_TURN);
        ClientStateMessage setUpPhaseStateMessage = new ClientStateMessage(ClientState.SET_UP_PHASE);
        sendAllExcept((ArrayList<ClientHandler>) nameToHandlerMap.values(),nameToHandlerMap.get(players.get(0)),waitStateMessage);
        sendTo(players.get(0),setUpPhaseStateMessage);

    }

    private void sendAllExcept(ArrayList<ClientHandler> clientHandlers,ClientHandler except, Message message){
        for( ClientHandler clientHandler : clientHandlers){
            if(!clientHandler.equals(except))
                clientHandler.sendMessage(message);
        }
    }

    private void sendTo(String nickname,Message message){
        ClientHandler clientHandler = nameToHandlerMap.get(nickname);
        clientHandler.sendMessage(message);
    }


    public GameController getGameController() {
        return gameController;
    }
}
