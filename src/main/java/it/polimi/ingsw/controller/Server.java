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
    HashMap<String, GameHandler> playerToGameMap;
    int clientIdCounter;
    //per le lobby si potrebbe creare una struttura dati tipo hashmap con chiave a due valori < int NumberPlayers, boolean expertOrNot>

    public Server(){
        idToNicknameMap= new HashMap<>();
        nameToHandlerMap= new HashMap<>();
        lobbies = new ArrayList<>();
        playerToGameMap = new HashMap<>();
        clientIdCounter=0;
    }

    //questo metodo deve essere sincronizzato (?)
    public boolean joinLobby(int playerID,int numberPlayers, boolean expertGame){
        try{
            Lobby matchingLobby = getMatchingLobby(numberPlayers,expertGame);
            matchingLobby.addToLobby(idToNicknameMap.get(playerID));
            if (matchingLobby.enoughPlayerToStart()) {
                GameHandler newGameHandler = new GameHandler(this, numberPlayers, expertGame);
                for (String nickname : matchingLobby.getPlayers()){
                    nameToHandlerMap.get(nickname).setGameHandler(newGameHandler);
                    playerToGameMap.put(nickname, newGameHandler);
                }
                lobbies.remove(matchingLobby);

                return true;
            }
            else
                return false;
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


    public static void main(String[] args) {
        Server server = new Server();
        //GameHandler gameHandler = new GameHandler();
        ServerSocketConnection serverSocket = new ServerSocketConnection(1234,server);
        //gameHandler.setServer(serverSocket);
        serverSocket.run();
    }

}

/*
finchè i giocatori sono in connection phase hanno l'id del client handler
vengono messi in lobby
quando la lobby parte viene creato il controller, che crea il model e a loro vengono assegnati gli id
a quel punto si avrà in ogni game handler un hashmap nickname->id
in lobby si devono sapere i nickname, il numero di player per partire
 */