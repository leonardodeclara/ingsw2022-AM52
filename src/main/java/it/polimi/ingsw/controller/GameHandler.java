package it.polimi.ingsw.controller;

import com.sun.net.httpserver.Authenticator;
import it.polimi.ingsw.messages.*;

import java.util.ArrayList;

public class GameHandler {
    GameController GC;
    ArrayList<String> playerNames;
    ServerSocketConnection server;

    public GameHandler(){
        playerNames = new ArrayList<String>();

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
            Message confirmation = new SuccessMessage();
            playerSocket.sendTo(confirmation);
        }else{
            ErrorMessage error = new ErrorMessage(ErrorKind.INVALID_NICKNAME);
            playerSocket.sendTo(error);
        }

    }






}
