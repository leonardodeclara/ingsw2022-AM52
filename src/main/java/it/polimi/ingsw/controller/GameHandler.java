package it.polimi.ingsw.controller;

import com.sun.net.httpserver.Authenticator;
import it.polimi.ingsw.messages.*;

import java.util.ArrayList;
import java.util.HashMap;

public class GameHandler {
    GameController gameController;
    ServerSocketConnection serverConnection;
    Server server;
    int numOfPlayers;
    boolean expertGame;

    public GameHandler(Server server, int numOfPlayers, boolean expertGame){
        this.server = server;
        this.numOfPlayers= numOfPlayers;
        this.expertGame=expertGame;
    }

    public void handleMessage(Message message,int playerID){
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

    }

    public GameController getGameController() {
        return gameController;
    }
}
