package it.polimi.ingsw.controller;

import com.sun.net.httpserver.Authenticator;
import it.polimi.ingsw.messages.*;

import java.util.ArrayList;

public class GameHandler {
    GameController GC;
    ArrayList<String> playerNames;
    ServerSocketConnection server;
    Server serverFather;

    public GameHandler(Server serverFather){
        playerNames = new ArrayList<String>();
        this.serverFather = serverFather;
    }

    public void handleMessage(Message message,int playerID){
        if(message instanceof NicknameMessage)
            handleNicknameMessage((NicknameMessage) message, playerID);
    }

    public void setServer(ServerSocketConnection server) {
        this.server = server;
    }

    public void handleNicknameMessage(NicknameMessage message, int playerID){
        String playerProposedNickname = ((NicknameMessage) message).getPlayerNickname();
        ClientHandler playerSocket = server.getClienthandlers().get(playerID);

        if(!playerNames.contains(playerProposedNickname)){
            playerNames.add(playerProposedNickname);

            //if() //se esiste una lobby inserisci il giocatore là e manda stato wait_lobby
            //else // altrimenti manda stato insert_new_game_parameters e fagliene creare una
            //Message newState = new ClientStateMessage(ClientState.INSERT_NEW_GAME_PARAMETERS);
            //playerSocket.sendTo(newState);
        }else{
            ErrorMessage error = new ErrorMessage(ErrorKind.INVALID_NICKNAME);
            playerSocket.sendTo(error);
        }

    }







}
