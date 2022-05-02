package it.polimi.ingsw.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Server {
    ServerSocketConnection serverSocket;
    ArrayList<Lobby> lobbies;
    //si potrebbe creare una struttura dati tipo hashmap con chiave a due valori < int NumberPlayers, boolean expertOrNot>

    public Server(){

    }


    public boolean joinLobby(String nickname,int numberPlayers, boolean expertGame){
        try{
            Lobby matchingLobby = getMatchingLobby(numberPlayers,expertGame);
            matchingLobby.addToLobby(nickname);
            return matchingLobby.enoughPlayerToStart();
        }
        catch (NoSuchElementException e){
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

    //alla creazione di una nuova partita viene rimossa da lobbies la lobby riferita a quella partita
    private void removeLobby(int numberPlayers, boolean expertGame){
        Lobby removedLobby=null;
        for (Lobby lobby: lobbies)
            if (lobby.getNumberPlayersRequired()== numberPlayers && lobby.isExpertGame()==expertGame)
                removedLobby=lobby;
        lobbies.remove(removedLobby);
    }
}

/*
finchè i giocatori sono in connection phase hanno l'id del client handler
vengono messi in lobby
quando la lobby parte viene creato il controller, che crea il model e a loro vengono assegnati gli id
a quel punto si avrà in ogni game handler un hashmap nickname->id
in lobby si devono sapere i nickname, il numero di player per partire
 */