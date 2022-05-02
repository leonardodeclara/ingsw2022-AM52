package it.polimi.ingsw.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Server {
    ServerSocketConnection serverSocket;
    HashMap<Integer,String> idToNicknameMap;
    HashMap<String, ClientHandler> nameToHandlerMap;
    ArrayList<Lobby> lobbies;
    HashMap<String, GameHandler> games;
    int clientIdCounter;
    //per le lobby si potrebbe creare una struttura dati tipo hashmap con chiave a due valori < int NumberPlayers, boolean expertOrNot>

    public Server(){
        clientIdCounter=0;

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

    //controllo se il nickname inserito è disponibile oppure no
    public boolean isNicknameAvailable(String nickname){
        for (Map.Entry<Integer,String> entry: idToNicknameMap.entrySet())
            if (entry.getValue().equals(nickname))
                return false;
        return true;
    }

    //aggiungo un giocatore alla mappa che associa l'id al nome
    public void registerPlayer(String nickname){
        idToNicknameMap.put(clientIdCounter, nickname);
        clientIdCounter++;
    }

    public void registerClientConnection(String nickname, ClientHandler clientConnection){
        nameToHandlerMap.put(nickname, clientConnection);
    }

    public ClientHandler getClientHandlerById(int playerId){
        String nickname = idToNicknameMap.get(playerId);
        return nameToHandlerMap.get(nickname);
    }

    public boolean checkExistingLobby(int playersNumber,boolean expertGame){
        for (Lobby lobby: lobbies){
            if (lobby.isExpertGame()==expertGame && lobby.getNumberPlayersRequired()==playersNumber)
                return true;
        }
        return false;
    }

}

/*
finchè i giocatori sono in connection phase hanno l'id del client handler
vengono messi in lobby
quando la lobby parte viene creato il controller, che crea il model e a loro vengono assegnati gli id
a quel punto si avrà in ogni game handler un hashmap nickname->id
in lobby si devono sapere i nickname, il numero di player per partire
 */