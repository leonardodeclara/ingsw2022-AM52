package it.polimi.ingsw.controller;

import java.io.IOException;
import java.util.ArrayList;

public class Server {
    ServerSocketConnection serverSocket;
    ArrayList<Lobby> lobbies;

    public Server(){

    }


    public boolean joinLobby(String nickname,int numberPlayers, boolean expertGame){
        Lobby matchingLobby = getMatchingLobby(numberPlayers,expertGame);
        if(matchingLobby!=null){
            matchingLobby.addToLobby(nickname);
            if(matchingLobby.enoughPlayerToStart())
                return true;
            else
                return false;
        }
        else{
            Lobby newLobby = new Lobby(numberPlayers,expertGame);
            lobbies.add(newLobby);
            return false;
        }


    }

    private Lobby getMatchingLobby(int numberPlayers, boolean expertGame){
        return lobbies.stream()
                .filter(x -> x.getNumberPlayersRequired() == numberPlayers && x.isExpertGame() == expertGame)
                .findFirst()
                .get();
    }
}

/*
finchè i giocatori sono in connection phase hanno l'id del client handler
vengono messi in lobby
quando la lobby parte viene creato il controller, che crea il model e a loro vengono assegnati gli id
a quel punto si avrà in ogni game handler un hashmap nickname->id
in lobby si devono sapere i nickname, il numero di player per partire
 */