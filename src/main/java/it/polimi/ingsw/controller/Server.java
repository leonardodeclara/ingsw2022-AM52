package it.polimi.ingsw.controller;

import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.messages.*;

import java.util.*;

/**
 * Server class is the main class on the server side: it manages the connection's setup phase up to the game's start;
 * it also handles the registration of clients.
 */
public class Server {
    private ServerSocketConnection serverSocket;
    private HashMap<Integer,String> idToNicknameMap;
    private HashMap<String, ClientHandler> nameToHandlerMap;
    private ArrayList<Lobby> lobbies;
    private HashMap<String, GameHandler> playerToGameMap;
    private ArrayList<GameHandler> gameHandlers;
    private int clientHandlerCounter; //assegna gli id ai clientHandler

    public Server(){
        idToNicknameMap= new HashMap<>();
        nameToHandlerMap= new HashMap<>();
        lobbies = new ArrayList<>();
        playerToGameMap = new HashMap<>();
        gameHandlers = new ArrayList<>();
        clientHandlerCounter=0;
    }


    //questo in teoria è l'unico punto d'accesso tra i thread clienthandlers e il server, quindi lo sincronizziamo per gestirne l'uso concorrente da parte di più thread
    /**
     * It handles the first messages sent by a player in order to register its name and its choice
     * for the number of players and game mode.
     * @param message: message sent by the client.
     * @param sender: ClientHandler instance of the client sending data.
     */
    public synchronized void handleMessage(Message message, ClientHandler sender){
        if (message instanceof LoginRequestMessage)
            handleLogin((LoginRequestMessage) message, sender);
        else if (message instanceof GameParametersMessage)
            handleGameParameters((GameParametersMessage)message,sender );
    }

    /**
     * It handles a client's name choice: if the choice is valid, the server registers it and sends a new message to client,
     * asking for game parameters, otherwise it sends a error message.
     * @param message: LoginRequestMessage instance sent by the client.
     * @param sender: ClientHandler instance of the client sending data.
     */
    public void handleLogin(LoginRequestMessage message, ClientHandler sender){
        String nickname = message.getPlayerNickname();

        //questo controllo deve essere a livello server perché in teoria il nome deve essere univoco a livello server, non a livello partita
        if(isNicknameAvailable(nickname)){
            sender.setID(clientHandlerCounter);
            clientHandlerCounter++;
            registerPlayer(nickname,sender.getID());
            registerClientConnection(nickname, sender);
            sender.sendMessage(new ClientStateMessage(ClientState.INSERT_NEW_GAME_PARAMETERS));
        }else{
            ErrorMessage error = new ErrorMessage(ErrorKind.INVALID_NICKNAME);
            System.out.println("Mando un messaggio di invalid nickname al client " + sender.getID());
            sender.sendMessage(error);
        }
    }

    /**
     * It handles a client's choice for number of players and game mode by adding him to a lobby accordingly
     * to his game parameters' selection.
     * @param message: GameParametersMessage instance sent by the client.
     * @param sender: ClientHandler instance of the client sending data.
     */
    public void handleGameParameters(GameParametersMessage message, ClientHandler sender){
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

    /**
     * It adds a client to a game's lobby: if there's already a lobby matching the chosen game parameters
     * it allows the client to join it, otherwise it creates a new one and adds the client to it.
     * @param playerID: identification for the active client's ClientHandler.
     * @param numberPlayers: client's choice for the game's number of players.
     * @param expertGame: client's choice for the game mode.
     * @return the Lobby instance to which the client has been added.
     */
    public Lobby joinLobby(int playerID, int numberPlayers, boolean expertGame){
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

    /**
     * It creates a new match accordingly to the lobby's parameters: a GameHandler is created and the lobby's
     * clients are added to it. The now-empty lobby is also removed from the active lobbies list.
     * @param lobby: Lobby instance to which the clients belong to.
     */
    private void createMatch(Lobby lobby){ //cancella la lobby, crea la lista giocatori, crea gamehandler e manda i messaggi ai giocatori
        ArrayList<String> players = lobby.getPlayers();
        boolean expert = lobby.isExpertGame();
        lobbies.remove(lobby); //cancella la lobby
        System.out.println("Inizializziamo il GH");
        HashMap<String, ClientHandler> playerInGame = selectLobbyPlayers(players);
        for (String player: playerInGame.keySet()){
            System.out.println("Nella nuova partita c'è "+ player +", clientHandler numero " + playerInGame.get(player).getID());
        }
        GameHandler gameHandler = new GameHandler(this,playerInGame,expert);
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

    /**
     * It selects the Nickname-ClientHandler association of the clients that belong to a lobby.
     * @param lobbyPlayers: names of the clients that belong to a lobby.
     * @return a hashmap with the association Nickname-ClientHandler for the lobby's players.
     */
    private HashMap<String,ClientHandler> selectLobbyPlayers(ArrayList<String> lobbyPlayers){
        HashMap<String, ClientHandler> inGamePlayers = new HashMap<>();
        for (String nickname : nameToHandlerMap.keySet()){
            if(lobbyPlayers.contains(nickname))
                inGamePlayers.put(nickname, nameToHandlerMap.get(nickname));
        }
        return inGamePlayers;
    }

    /**
     * It selects the active lobby that matches the parameters given as input.
     * @param numberPlayers: chosen number of players.
     * @param expertGame: chosen game mode.
     * @return a Lobby instance matching the parameters.
     */
    private Lobby getMatchingLobby(int numberPlayers, boolean expertGame){
        return lobbies.stream()
                .filter(x -> x.getNumberPlayersRequired() == numberPlayers && x.isExpertGame() == expertGame)
                .findFirst()
                .get();
    }

    //alla creazione di una nuova partita viene rimossa da lobbies la lobby riferita a quella partita
    //in teoria si può cancellare
    private void removeLobby(int numberPlayers, boolean expertGame){
        Lobby removedLobby=null;
        for (Lobby lobby: lobbies)
            if (lobby.getNumberPlayersRequired()== numberPlayers && lobby.isExpertGame()==expertGame)
                removedLobby=lobby;
        lobbies.remove(removedLobby);
    }

    /**
     * It verifies whether a nickname's choice is valid or not by checking if there's already a client with that nickname.
     * @param nickname: the client's chosen name.
     * @return true if the choice is legal, false otherwise.
     */
    public boolean isNicknameAvailable(String nickname){
        for (Map.Entry<Integer,String> entry: idToNicknameMap.entrySet())
            if (entry.getValue().equalsIgnoreCase(nickname))
                return false;
        return true;
    }

    /**
     * It registers a player by adding the association between the ClientHandler's ID and the client's nickname.
     * @param nickname: name chosen by the client.
     * @param clientHandlerID: ClientHandler's identification.
     */
    public void registerPlayer(String nickname,int clientHandlerID){
        idToNicknameMap.put(clientHandlerID, nickname);
    }

    /**
     * It adds a client's connection by adding the association between the client's nickname and its ClientHandler.
     * @param nickname: name chosen by the client.
     * @param clientConnection: client's ClientHandler.
     */
    public void registerClientConnection(String nickname, ClientHandler clientConnection){
        nameToHandlerMap.put(nickname, clientConnection);
    }

    public ClientHandler getClientHandlerById(int playerId){
        String nickname = idToNicknameMap.get(playerId);
        return nameToHandlerMap.get(nickname);
    }

    public HashMap<Integer, String> getIdToNicknameMap() {
        return idToNicknameMap;
    }

    /**
     * It removes the selected ClientHandler from the server's registries.
     * @param clientHandler: ClientHandler instance that is being removed from the server's maps.
     */
    public synchronized void removeClientConnection(ClientHandler clientHandler){
        String clientName = idToNicknameMap.get(clientHandler.getID());
        idToNicknameMap.remove(clientHandler.getID());
        //bisogna gestire in maniera diversa il caso in cui il client sia ancora in attesa o il caso in cui sia a partita iniziata
        //intanto facciamo così

        //partita esiste
        if (clientHandler.getGameHandler()!=null){
            playerToGameMap.remove(clientName);

        }
        else //è ancora in lobby
        {
            removeClientFromLobby(clientName);
        }
        nameToHandlerMap.remove(clientName);

    }

    /**
     * It removes a player from a waiting lobby by deleting his name from the players' list.
     * @param nickname: name of the player being removed.
     */
    public void removeClientFromLobby(String nickname){
        for (Lobby lobby: lobbies)
            if (lobby.getPlayers().contains(nickname))
                lobby.removePlayer(nickname);
    }

    /**
     * It removes the selected GameHandler instance from the list of gameHandlers.
     * @param gameHandler: GameHandler instance being removed.
     */
    public void removeGameHandler(GameHandler gameHandler){
        gameHandlers.remove(gameHandler);
    }

    /**
     * It sets the server's serverSocket attribute to the ServerSocketConnection instance given as attribute.
     * @param serverSocket: ServerSocketConnection instance handling the server's communication with clients.
     */
    public void setServerSocket(ServerSocketConnection serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static void main(String[] args) {
        Server server = new Server();
        ServerSocketConnection serverSocket = new ServerSocketConnection(1234,server);
        server.setServerSocket(serverSocket);
        serverSocket.run();
    }
}