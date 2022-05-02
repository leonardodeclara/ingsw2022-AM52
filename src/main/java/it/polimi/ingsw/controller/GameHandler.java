package it.polimi.ingsw.controller;

import com.sun.net.httpserver.Authenticator;
import it.polimi.ingsw.messages.*;

import java.util.ArrayList;
import java.util.HashMap;

public class GameHandler {
    GameController gameController;
    HashMap<Integer,String> clientToNickname;
    ServerSocketConnection server;
    Server serverFather;

    public GameHandler(Server serverFather){
        clientToNickname = new HashMap<>();
        this.serverFather = serverFather;
    }

    public void handleMessage(Message message,int playerID){
        if(message instanceof NicknameMessage)
            handleNicknameMessage((NicknameMessage) message, playerID);
        if(message instanceof GameParametersMessage)
            handleGameParametersMessage((GameParametersMessage) message, playerID);
    }

    public void setServer(ServerSocketConnection server) {
        this.server = server;
    }

    public void handleNicknameMessage(NicknameMessage message, int playerID){ //playerID = id clienthandler
        String playerProposedNickname = ((NicknameMessage) message).getPlayerNickname();
        ClientHandler playerSocket = server.getClientHandlers().get(playerID);

        //questo controllo deve essere a livello server perché in teoria il nome deve essere univoco a livello server, non a livello partita
        if(!clientToNickname.containsKey(playerProposedNickname)){
            clientToNickname.put(playerID,playerProposedNickname);
            Message newState = new ClientStateMessage(ClientState.INSERT_NEW_GAME_PARAMETERS);
            playerSocket.sendTo(newState);
        }else{
            ErrorMessage error = new ErrorMessage(ErrorKind.INVALID_NICKNAME);
            playerSocket.sendTo(error);
        }
    }

    public void handleGameParametersMessage(GameParametersMessage message, int playerID){
        String playerNickname = clientToNickname.get(playerID);
        boolean expertGame = message.isExpertGame();
        int numberOfPlayers = message.getNumberPlayers();
        //si potrebbe aggiungere in ServerSocketConnection getClientHandlerById
        ClientHandler playerSocket = server.getClientHandlers().get(playerID);
        //manca il controllo dell'input e l'eventuale invio di INVALID_INPUT error message
        if(serverFather.joinLobby(playerNickname,numberOfPlayers,expertGame)){ //c'è una lobby e il gioco sta per partire
            startGame();
        } else { //lobby appena creata/lobby già esistente ma non abbastanza players
            Message newState = new ClientStateMessage(ClientState.WAIT_IN_LOBBY);
            playerSocket.sendTo(newState);
        }

        //IDEA:
        //se si volesse fare un'interfaccia della lobby lato client con gli altri giocatori all'interno, basterebbe fare in modo che il client richieda
        //i giocatori con cui è in lobby così da poterne scrivere i nomi sulla GUI/CLI
    }



    /*
    si svuota la lobby inserendo tutto in un game controller (che inizializza il model)
    si mappano i nickname a degli id player (in ordine di join nella lobby, usando quindi la lista players di lobby)
    si manda in broadcast a tutti wait_turn e al primo della lista setup_phase
    quando poi giunge il messaggio dal giocatore usando handleMessages si passa in un altro metodo che fa la setup phase così questo
    non viene riempito di robe che non gli competono
     */
    public void startGame(){

    }


}
