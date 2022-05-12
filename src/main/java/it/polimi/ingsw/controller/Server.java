package it.polimi.ingsw.controller;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.Game;

import java.io.IOException;
import java.lang.reflect.Array;
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
    ArrayList<GameHandler> gameHandlers;
    //per le lobby si potrebbe creare una struttura dati tipo hashmap con chiave a due valori < int NumberPlayers, boolean expertOrNot>

    public Server(){
        idToNicknameMap= new HashMap<>();
        nameToHandlerMap= new HashMap<>();
        lobbies = new ArrayList<>();
        playerToGameMap = new HashMap<>();
        gameHandlers = new ArrayList<>();
    }


    //questo in teoria è l'unico punto d'accesso tra i thread clienthandlers e il server, quindi lo sincronizziamo per gestirne l'uso concorrente da parte di più thread
    public synchronized void handleMessage(Message message, ClientHandler sender){
        if (message instanceof LoginRequestMessage)
            handleLogin((LoginRequestMessage) message, sender);
        else if (message instanceof GameParametersMessage)
            handleGameParameters((GameParametersMessage)message,sender );
    }
    public void handleLogin(LoginRequestMessage message, ClientHandler sender){
        String nickname = ((LoginRequestMessage) message).getPlayerNickname();

        //questo controllo deve essere a livello server perché in teoria il nome deve essere univoco a livello server, non a livello partita
        if(isNicknameAvailable(nickname)){
            registerPlayer(nickname,sender.getID());
            registerClientConnection(nickname, sender);
            sender.sendMessage(new ClientStateMessage(ClientState.INSERT_NEW_GAME_PARAMETERS));
        }else{
            ErrorMessage error = new ErrorMessage(ErrorKind.INVALID_NICKNAME);
            sender.sendMessage(error);
        }
    }

    public void handleGameParameters(GameParametersMessage message,ClientHandler sender){
        boolean expertGame = message.isExpertGame();
        int numberOfPlayers = message.getNumberPlayers();
        Lobby matchingLobby = joinLobby(sender.getID(),numberOfPlayers,expertGame);
        if(matchingLobby.getShouldStart()){ //c'è una lobby e il gioco sta per partire
            System.out.println("Si parte!");
            createMatch(matchingLobby);
        } else { //lobby appena creata/lobby già esistente ma non abbastanza players
            System.out.println("Mando lo stato di attesa della lobby");
            sender.sendMessage(new ClientStateMessage(ClientState.WAIT_IN_LOBBY));
        }
    }

    public Lobby joinLobby(int playerID,int numberPlayers, boolean expertGame){ //clientHandler id, parametri della lobby
        try{
            Lobby matchingLobby = getMatchingLobby(numberPlayers,expertGame);
            matchingLobby.addToLobby(idToNicknameMap.get(playerID));
            matchingLobby.checkIfShouldStart();
            System.out.println("Esiste già una lobby di questo tipo e ha "+(matchingLobby.getPlayers().size()- 1)+" giocatori in attesa");
            return matchingLobby;
        }
        catch (NoSuchElementException e){
            Lobby newLobby = new Lobby(numberPlayers,expertGame);
            newLobby.addToLobby(idToNicknameMap.get(playerID));
            lobbies.add(newLobby);
            System.out.println("Non esiste una lobby con questi parametri quindi la creiamo");
            return newLobby;
        }
    }

    private void createMatch(Lobby lobby){ //cancella la lobby, crea la lista giocatori, crea gamehandler e manda i messaggi ai giocatori
        ArrayList<String> players = lobby.getPlayers();
        boolean expert = lobby.isExpertGame();
        lobbies.remove(lobby); //cancella la lobby
        System.out.println("Inizializziamo il GH");
        GameHandler gameHandler = new GameHandler(this,removeUnusedPlayers(nameToHandlerMap,players),expert);
        gameHandlers.add(gameHandler);
        for (String player : players) {
            playerToGameMap.put(player,gameHandler);
        }
        ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
        for (String nickname : players){ //fetching degli id dei giocatori della partita creata
            clientHandlers.add(nameToHandlerMap.get(nickname));
        }
        for(ClientHandler ch : clientHandlers) //aggiungi al reference del gamehandler creato a ogni clienthandler
            ch.setGameHandler(gameHandler);

        gameHandler.startGame();
    }

    private HashMap<String,ClientHandler> removeUnusedPlayers(HashMap<String,ClientHandler> hashMap, ArrayList<String> list){
        for (String nickname : hashMap.keySet()){
            if(!list.contains(nickname))
                hashMap.remove(nickname);
        }
        return hashMap;
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
    public void registerPlayer(String nickname,int clientHandlerID){
        idToNicknameMap.put(clientHandlerID, nickname);
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