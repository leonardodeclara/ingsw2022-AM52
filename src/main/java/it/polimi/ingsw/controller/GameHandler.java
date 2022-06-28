package it.polimi.ingsw.controller;

import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.ClientMessages.*;
import it.polimi.ingsw.messages.ServerMessages.ClientStateMessage;
import it.polimi.ingsw.messages.ServerMessages.ErrorKind;
import it.polimi.ingsw.messages.ServerMessages.ErrorMessage;
import it.polimi.ingsw.messages.UpdateMessages.AvailableWizardMessage;
import it.polimi.ingsw.messages.UpdateMessages.EndGameMessage;
import it.polimi.ingsw.model.Tower;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * GameHandler's class receives messages from clients through ClientHandler class and propagates them
 * to GameController, in order to change the model's state.
 */
public class GameHandler implements PropertyChangeListener{
    private GameController gameController;
    private ServerSocketConnection serverConnection;
    private Server server;
    private ArrayList<String> players;
    private HashMap<String, ClientHandler> nameToHandlerMap;
    private boolean expertGame;
    private ArrayList<String> playersOrder;
    private Iterator<String> playersOrderIterator;

    /**
     * Constructor GameHandler creates a new GameHandler instance by receiving a reference to the Server,
     * the list of clients that are taking part in the game and their relative ClientHandler reference. It also holds the mode of the new game.
     * @param server: Server instance holding information about the clients and the server state.
     * @param nameToHandlerMap: HashMap containing the associations between a client's nickname and his ClientHandler instance.
     * @param expertGame: game mode for the game managed by GameHandler.
     */
    public GameHandler(Server server,HashMap<String, ClientHandler> nameToHandlerMap, boolean expertGame){
        this.server = server;
        this.nameToHandlerMap =nameToHandlerMap;
        this.expertGame=expertGame;
        players = new ArrayList<>();
        players.addAll(nameToHandlerMap.keySet());
        playersOrder = new ArrayList<>(players);
        playersOrderIterator = playersOrder.iterator();
    }

    /**
     * It manages messages from the client by calling the proper method according the message instance.
     * @param message: instance of the client's message
     * @param clientHandler: ClientHandler instance of the player sending messages.
     */
    public void handleMessage(Message message,ClientHandler clientHandler){ //visitor pure qua
        if(message instanceof WizardSelectionMessage)
            handleWizardSelectionMessage((WizardSelectionMessage) message, clientHandler);
        else if (message instanceof TowerSelectionMessage)
            handleTowerSelectionMessage((TowerSelectionMessage)message, clientHandler);
        else if(message instanceof PlayAssistantCardMessage)
            handlePlayAssistantCardMessage((PlayAssistantCardMessage) message,clientHandler);
        else if (message instanceof MoveStudentsFromLobbyMessage)
            handleMoveStudentMessage((MoveStudentsFromLobbyMessage) message, clientHandler);
        else if (message instanceof MotherNatureMoveMessage)
            handleMoveMotherNatureMessage((MotherNatureMoveMessage)message,clientHandler);
        else if (message instanceof CloudSelectionMessage)
            handleCloudPick((CloudSelectionMessage) message, clientHandler);
        else if (message instanceof CloseTurnMessage)
            handleEndTurn((CloseTurnMessage) message, clientHandler);
        else if (message instanceof PlayPersonalityCardMessage)
            handlePlayPersonalityCard((PlayPersonalityCardMessage) message, clientHandler);
        else if (message instanceof Card1EffectMessage)
            handleCard1Effect((Card1EffectMessage) message, clientHandler);
        else if (message instanceof Card3EffectMessage)
            handleCard3Effect((Card3EffectMessage) message, clientHandler);
        else if (message instanceof Card5EffectMessage)
            handleCard5Effect((Card5EffectMessage) message, clientHandler);
        else if (message instanceof Card7EffectMessage)
            handleCard7Effect((Card7EffectMessage) message, clientHandler);
        else if (message instanceof Card9EffectMessage)
            handleCard9Effect((Card9EffectMessage) message, clientHandler);
        else if (message instanceof Card10EffectMessage)
            handleCard10Effect((Card10EffectMessage) message, clientHandler);
        else if (message instanceof Card11EffectMessage)
            handleCard11Effect((Card11EffectMessage) message, clientHandler);
        else if (message instanceof Card12EffectMessage)
            handleCard12Effect((Card12EffectMessage) message, clientHandler);
    }

    /**
     * It starts a new game by instantiating a new GameController and by setting the listener system.
     * It also communicates to the players the model's initial status.
     */
    public void startGame(){
        System.out.println("GameHandler: ora istanzio GameController");
        updatePlayersOrder(players);
        gameController= new GameController(expertGame, new ArrayList<>(players));
        gameController.setUpdateListener(this); //gameHandler inizia ad ascoltare il controller
        System.out.println("GameHandler: ora faccio ascoltare game da GC");
        Message gameInstantiationMessage = gameController.handleGameInstantiation();
        sendAll(gameInstantiationMessage);
        Message waitStateMessage = new ClientStateMessage(ClientState.WAIT_TURN);
        Message setUpPhaseStateMessage = new ClientStateMessage(ClientState.SET_UP_WIZARD_PHASE);
        sendAllExcept(nameToHandlerMap.get(playersOrder.get(0)),waitStateMessage);
        sendTo(playersOrder.get(0), new AvailableWizardMessage(gameController.getAvailableWizards()));
        sendTo(playersOrder.get(0),setUpPhaseStateMessage);
        System.out.println("GameHandler: finito startGame");
    }

    /**
     * It manages the beginning of the planning phase of a new round: the first player is required to choose a new assistant card which will decide the action's order for the next phase.
     * The other players are sent a message telling them to wait for their turn.
     */
    private void startPlanningPhase(){
        gameController.updateCloudsStudents();
        Message waitStateMessage = new ClientStateMessage(ClientState.WAIT_TURN);
        Message playAssistantCardMessage = new ClientStateMessage(ClientState.PLAY_ASSISTANT_CARD);
        String startingPlayer = playersOrder.get(0);
        sendAllExcept(nameToHandlerMap.get(startingPlayer), waitStateMessage);
        sendTo(startingPlayer, playAssistantCardMessage);
        updatePlayersOrder(playersOrder);
    }

    /**
     * It manages the beginning of a new action phase by sending the highest priority's player a message notifying his status.
     * The other players are sent a message telling them to wait for their turn.
     */
    private void startActionPhase(){
        Message waitStateMessage = new ClientStateMessage(ClientState.WAIT_TURN);
        Message moveFromLobbyMessage = new ClientStateMessage(ClientState.MOVE_FROM_LOBBY);
        ArrayList<String> currentTurnOrder = gameController.getActionPhaseTurnOrder();
        String startingPlayer = currentTurnOrder.get(0);
        gameController.setCurrentPlayer(startingPlayer);
        sendAllExcept(nameToHandlerMap.get(startingPlayer),waitStateMessage);
        sendTo(startingPlayer,moveFromLobbyMessage);
        updatePlayersOrder(currentTurnOrder);
    }

    /**
     * It updates the order by which players are required to take action in the game.
     * @param players: names of the active players.
     */
    private void updatePlayersOrder(ArrayList<String> players){
        playersOrder = new ArrayList<>(players);
        playersOrderIterator = playersOrder.iterator();
        System.out.println("Ordine previsto dei giocatori:"+ playersOrderIterator.toString());
        for (int i = 0; i < players.size(); i++){
            System.out.println(playersOrder.get(i));
        }
        playersOrderIterator.next();
    }

    /**
     * It manages the selection of a wizard card by a player: the choice is communicated to the GameController,
     * whose response is sent back to the client.
     * @param message: WizardSelectionMessage instance carrying the choice sent by the player.
     * @param client: ClientHandler instance handling the player's connection.
     */
    private void handleWizardSelectionMessage(WizardSelectionMessage message, ClientHandler client){
        int chosenWizard = message.getWizard();
        String clientName = getNicknameFromClientID(client.getID());
        System.out.println("GameHandler:è arrivato un messaggio di wizardSelection da " + clientName);
        Message response = gameController.updateWizardSelection(clientName, chosenWizard);
        sendTo(clientName, response);
        if (!(response instanceof ErrorMessage)){
            System.out.println(clientName+ " ha scelto il suo wizard, ora gi chiederò che torre vuole");
            sendTo(clientName, new ClientStateMessage(ClientState.SET_UP_TOWER_PHASE));
        }
    }



    /**
     * It manages the selection of a tower color by a player: the choice is communicated to the GameController,
     * whose response is sent back to the client.
     * @param message: TowerSelectionMessage instance carrying the choice sent by the player.
     * @param client: ClientHandler instance handling the player's connection.
     */
    private void handleTowerSelectionMessage(TowerSelectionMessage message, ClientHandler client){
        Tower chosenTower = message.getTower();
        String clientName = getNicknameFromClientID(client.getID());
        System.out.println("GameHandler:è arrivato un messaggio di towerSelection da " + clientName);
        System.out.println(clientName + "Ha scelto la torre del colore" + chosenTower.toString());
        Message response = gameController.updateTowerSelection(clientName, chosenTower);
        sendTo(clientName, response);
        if (!(response instanceof ErrorMessage)){
            System.out.println(clientName+ " ha scelto la sua torre, ora lo sto mandando in WAIT_TURN");
            if (playersOrderIterator.hasNext()){ //se il giocatore che ha giocato non è l'ultimo allora avanza di uno l'iterator, altrimenti manda a tutti il messaggio
                String nextPlayer = playersOrderIterator.next();
                Message setUpPhaseStateMessage = new ClientStateMessage(ClientState.SET_UP_WIZARD_PHASE);
                System.out.println("Siccome " + clientName + " ha finito la sua selezione ora è il turno di " + nextPlayer);
                sendTo(nextPlayer, new AvailableWizardMessage(gameController.getAvailableWizards())); //al prossimo giocatore viene aggiornata la lista di wizard disponibili
                sendTo(nextPlayer, setUpPhaseStateMessage);  //viene aggiornato lo stato del primo giocatore
            }
            else{
                startPlanningPhase();
                System.out.println("Adesso faccio partire la partita con la scelta delle carte assistente. comunque la fase dopo");
            }
        }
    }

    /**
     * It manages the selection of an assistant card by a player: the choice is communicated to the GameController,
     * whose response is sent back to the client.
     * @param message: PlayAssistantCardMessage instance carrying the choice sent by the player.
     * @param client: ClientHandler instance handling the player's connection.
     */
    private void handlePlayAssistantCardMessage(PlayAssistantCardMessage message, ClientHandler client){
        int chosenCard = message.getPriority();
        String clientName = getNicknameFromClientID(client.getID());
        System.out.println("GameHandler:è arrivato un messaggio di playAssistantCard da " + clientName);
        Message response = gameController.updateAssistantCards(clientName, chosenCard); //se il messaggio andava bene il model si è aggiornato dopo questa riga

        if (!(response instanceof ErrorMessage)){
            System.out.println(clientName+ " ha scelto la sua carta, ora lo mando in WAIT_TURN o in MOVE FROM LOBBY");
            if (playersOrderIterator.hasNext()){ //se il giocatore che ha giocato non è l'ultimo allora avanza di uno l'iterator, altrimenti manda a tutti il messaggio
                sendTo(clientName, response);
                String nextPlayer = playersOrderIterator.next();
                Message playAssistantCardMessage = new ClientStateMessage(ClientState.PLAY_ASSISTANT_CARD);
                System.out.println("Siccome " + clientName + " ha finito la sua selezione ora è il turno di " + nextPlayer);
                sendTo(nextPlayer,playAssistantCardMessage);
            }
            else startActionPhase();
        }
        else sendTo(clientName, response);
    }

    /**
     * It manages the students' movement from the board's lobby by a player: the choice is communicated to the GameController,
     * whose response is sent back to the client.
     * @param message: MoveStudentsFromLobbyMessage instance carrying the choice sent by the player.
     * @param client: ClientHandler instance handling the player's connection.
     */
    private void handleMoveStudentMessage(MoveStudentsFromLobbyMessage message, ClientHandler client){
        ArrayList<Integer> studentIDs = message.getStudentIndex();
        ArrayList<Integer> destIDs = message.getDestinationIndex();
        String clientName = getNicknameFromClientID(client.getID());
        System.out.println("GameHandler:è arrivato un messaggio di moveStudentFromLobby da " + clientName);
        Message response = gameController.moveStudentsFromLobby(clientName, studentIDs,destIDs); //se il messaggio andava bene il model si è aggiornato dopo questa riga
        sendTo(clientName, response);
        if (!(response instanceof ErrorMessage)){
            System.out.println(clientName + " ha spostato gli studenti, ora lo mando in MOVE_MN");
        }
    }

    /**
     * It manages Mother Nature tile's number of steps chose by a player: the choice is communicated to the GameController,
     * whose response is sent back to the client. If the resulting move brings the game to an end all players are commmunicated the state and the match is closed.
     * @param message: MotherNatureMoveMessage instance carrying the choice sent by the player.
     * @param client: ClientHandler instance handling the player's connection.
     */
    private void handleMoveMotherNatureMessage(MotherNatureMoveMessage message, ClientHandler client){
        int steps = message.getSteps();
        String clientName = getNicknameFromClientID(client.getID());
        System.out.println("GameHandler:è arrivato un messaggio di moveMotherNature da " + clientName);
        Message response = gameController.moveMotherNature(clientName, steps); //se il messaggio andava bene il model si è aggiornato dopo questa riga
        if (response instanceof EndGameMessage){
            sendAll(response);
            closeMatch();
        }
        else {
            sendTo(clientName, response);
            if (!(response instanceof ErrorMessage))
                System.out.println(clientName + "ha spostato MN, ora lo mando in PICK_CLOUD se ci sono abbastanza pedine");
        }
    }

    /**
     * It manages a cloud's selection by a player: the choice is communicated to the GameController,whose response is sent back to the client.
     * @param message: CloudSelectionMessage instance carrying the choice sent by the player.
     * @param client: ClientHandler instance handling the player's connection.
     */
    private void handleCloudPick(CloudSelectionMessage message, ClientHandler client){
        int cloudIndex = message.getCloudIndex();
        String clientName = getNicknameFromClientID(client.getID());
        System.out.println("GameHandler: è arrivato un messaggio di CloudSelection da " + clientName);
        Message response = gameController.refillLobby(clientName, cloudIndex);
        sendTo(clientName, response);
        if (!(response instanceof  ErrorMessage)){
            System.out.println(clientName + "ha spostato le pedine nella lobby, ora lo mando in END_TURN ma dovrebbe esserci la parte di personaggio");
        }
    }

    /**
     * It manages a player's turn end: he is told he needs to wait for the other players to play,
     * while the next player is sent a message notifying him his status.
     * @param message: CloseTurnMessage instance communicating the choice sent by the player.
     * @param client: ClientHandler instance handling the player's connection.
     */
    private void handleEndTurn(CloseTurnMessage message, ClientHandler client){
        String clientName = getNicknameFromClientID(client.getID());
        if(expertGame)
            gameController.resetPersonalityCard();
        if (playersOrderIterator.hasNext()){ //se il giocatore che ha giocato non è l'ultimo allora avanza di uno l'iterator, altrimenti manda a tutti il messaggio
            Message stateChange = new ClientStateMessage(ClientState.WAIT_TURN);
            sendTo(clientName, stateChange);
            String nextPlayer = playersOrderIterator.next();
            gameController.setCurrentPlayer(nextPlayer); //serve per le carte
            Message moveStudentsFromLobbyMessage = new ClientStateMessage(ClientState.MOVE_FROM_LOBBY);
            System.out.println("Siccome " + clientName + " ha finito il suo turno ora è il turno di " + nextPlayer);
            sendTo(nextPlayer,moveStudentsFromLobbyMessage);
        }
        else
            handleEndRound();

    }

    /**
     * It manages player's personality card's selection: the choice is communicated to the GameController, whose response is sent back to the client.
     * @param message: PlayPersonalityCardMessage instance carrying the personality choice.
     * @param client: ClientHandler instance handling the player's connection.
     */
    private void handlePlayPersonalityCard(PlayPersonalityCardMessage message, ClientHandler client){
        String clientName = getNicknameFromClientID(client.getID());
        if(expertGame){
            int cardID = message.getCardID();
            System.out.println("GameHandler: è arrivato un messaggio di PlayPersonality da " + clientName);
            Message response = gameController.playPersonalityCard(clientName, cardID,client.getCurrentClientState());
            sendTo(clientName, response);
            if (!(response instanceof  ErrorMessage)){
                System.out.println(clientName + "ha giocato una carta personaggio, ora lo mando in uno stato che dipende dalla carta giocata");
            }
        }
        else
        {
            Message response = new ErrorMessage(ErrorKind.ILLEGAL_MOVE);
            sendTo(clientName,response);
        }
    }

    /**
     * This method is responsible for the sequence of events related to the effect of Personality 1:
     * the player's choice is communicated to the GameController, whose response is sent back to the client.
     * @param message Card1EffectMessage instance carrying the student and island choice made by the player.
     * @param client ClientHandler instance handling the player's connection.
     */
    private void handleCard1Effect(Card1EffectMessage message, ClientHandler client){
        String clientName = getNicknameFromClientID(client.getID());
        Message response = gameController.applyEffect1(message.getStudentIndex(),message.getIslandID());
        if (!(response instanceof  ErrorMessage)){
            response= new ClientStateMessage(client.getCurrentClientState());
            System.out.println(clientName + "ha utilizzato l'effetto della carta 1, " +
                    "ora lo mando nello stato precedente alla invocazione della carta");
        }
        sendTo(clientName, response);
    }

    /**
     * This method is responsible for the sequence of events related to the effect of Personality 3:
     * the player's choice is communicated to the GameController, whose response is sent back to the client.
     * @param message: Card3EffectMessage instance carrying the island choice made by the player.
     * @param client: ClientHandler instance handling the player's connection.
     */
    private void handleCard3Effect(Card3EffectMessage message, ClientHandler client){
        String clientName = getNicknameFromClientID(client.getID());
        Message response = gameController.applyEffect3(message.getIslandID());
        if (response instanceof EndGameMessage){
            sendAll(response);
            closeMatch();
            return;
        }
        else if (response==null) {
            response= new ClientStateMessage(client.getCurrentClientState());
            System.out.println(clientName + "ha utilizzato l'effetto della carta 3, " +
                    "ora lo mando nello stato precedente alla invocazione della carta");
        }
        sendTo(clientName, response);
    }

    /**
     * This method is responsible for the sequence of events related to the effect of Personality 5:
     * the player's choice is communicated to the GameController, whose response is sent back to the client.
     * @param message: Card5EffectMessage instance carrying the island choice made by the player.
     * @param client: ClientHandler instance handling the player's connection.
     */
    private void handleCard5Effect(Card5EffectMessage message, ClientHandler client){
        String clientName = getNicknameFromClientID(client.getID());
        Message response = gameController.applyEffect5(message.getIslandID());
        if (!(response instanceof ErrorMessage)){
            response= new ClientStateMessage(client.getCurrentClientState());
            System.out.println(clientName + "ha utilizzato l'effetto della carta 5, " +
                    "ora lo mando nello stato precedente alla invocazione della carta");
        }
        sendTo(clientName, response);
    }

    /**
     * This method is responsible for the sequence of events related to the effect of Personality 7:
     * the player's choice is communicated to the GameController, whose response is sent back to the client.
     * @param message: Card7EffectMessage instance carrying the students choice made by the player.
     * @param client: ClientHandler instance handling the player's connection.
     */
    private void handleCard7Effect(Card7EffectMessage message, ClientHandler client){
        String clientName = getNicknameFromClientID(client.getID());
        Message response = gameController.applyEffect7(message.getStudentsFromCard(), message.getStudentsFromLobby());
        if (!(response instanceof  ErrorMessage)){
            response= new ClientStateMessage(client.getCurrentClientState());
            System.out.println(clientName + "ha utilizzato l'effetto della carta 7, " +
                    "ora lo mando nello stato precedente alla invocazione della carta");
        }
        sendTo(clientName, response);
    }

    /**
     * This method is responsible for the sequence of events related to the effect of Personality 9:
     * the player's choice is communicated to the GameController, whose response is sent back to the client.
     * @param message: Card9EffectMessage instance carrying the color choice made by the player.
     * @param client: ClientHandler instance handling the player's connection.
     */
    private void handleCard9Effect(Card9EffectMessage message, ClientHandler client){
        String clientName = getNicknameFromClientID(client.getID());
        gameController.applyEffect9(message.getBanned());
        System.out.println(clientName + "ha utilizzato l'effetto della carta 9, " +
                    "ora lo mando nello stato precedente alla invocazione della carta");
        sendTo(clientName, new ClientStateMessage(client.getCurrentClientState()));
    }

    /**
     * This method is responsible for the sequence of events related to the effect of Personality 10:
     * the player's choice is communicated to the GameController, whose response is sent back to the client.
     * @param message: Card10EffectMessage instance carrying the students choice made by the player.
     * @param client: ClientHandler instance handling the player's connection.
     */
    private void handleCard10Effect(Card10EffectMessage message, ClientHandler client){
        String clientName = getNicknameFromClientID(client.getID());
        Message response = gameController.applyEffect10(clientName,message.getStudentsFromTable(), message.getStudentsFromLobby());
        if (!(response instanceof  ErrorMessage)){
            response= new ClientStateMessage(client.getCurrentClientState());
            System.out.println(clientName + "ha utilizzato l'effetto della carta 10, " +
                    "ora lo mando nello stato precedente alla invocazione della carta");
        }
        sendTo(clientName, response);
    }

    /**
     * It handles the sequence of events related to the effect of Personality 11:
     * the player's choice is communicated to the GameController, whose response is sent back to the client.
     * @param message: Card11EffectMessage instance carrying the student choice made by the player.
     * @param client: ClientHandler instance handling the player's connection.
     */
    private void handleCard11Effect(Card11EffectMessage message, ClientHandler client){
        String clientName = getNicknameFromClientID(client.getID());
        Message response = gameController.applyEffect11(clientName, message.getSelectedStudentIndex());

        if (!(response instanceof  ErrorMessage)){
            response= new ClientStateMessage(client.getCurrentClientState());
            System.out.println(clientName + "ha utilizzato l'effetto della carta 11, " +
                    "ora lo mando nello stato precedente alla invocazione della carta");
        }
        sendTo(clientName, response);
    }

    /**
     * It handles the sequence of events related to the effect of Personality 12:
     * the player's choice is communicated to the GameController, whose response is sent back to the client.
     * @param message: Card12EffectMessage instance carrying the student choice made by the player.
     * @param client: ClientHandler instance handling the player's connection.
     */
    private void handleCard12Effect(Card12EffectMessage message, ClientHandler client){
        String clientName = getNicknameFromClientID(client.getID());
        Message response = gameController.applyEffect12(message.getChosenColor());

        if (!(response instanceof  ErrorMessage)){
            response= new ClientStateMessage(client.getCurrentClientState());
            System.out.println(clientName + "ha utilizzato l'effetto della carta 12, " +
                    "ora lo mando nello stato precedente alla invocazione della carta");
        }
        sendTo(clientName, response);
    }

    /**
     * It manages the end of current round: based on the game conditions, if it was the last one it communicates
     * the game result to all players and proceeds to close the match. Otherwise, a new planning phase takes place.
     */
    private void handleEndRound(){
        Message endRound = gameController.closeCurrentRound();
        if (endRound instanceof EndGameMessage){ //magari cambiare nome a questo metodo e mettere qualcosa tipo isThisLastRound
            sendAll(endRound);
            closeMatch();
        }
        else{
            startPlanningPhase();
        }
    }

    /**
     * It closes a match by communicating to all clients the game has ended. Then it proceeds to close socket
     * connections to all clients and to remove the GameHandler's instance from the server registry.
     */
    public synchronized void closeMatch(){
        System.out.println("Qui muore il gameHandler");
        sendAll(new ClientStateMessage(ClientState.END_GAME));
        //rivedere questo genere di chiusura perché per come è gestita ora si chiude la connessione ai client non disconnessi due volte
        //la prima qui con clientHandler.closeConnection
        //la seconda dentro il run di clientHandler perché scatta una SocketExcpetion
        //provare a tenere una delle due cose (magari si fa uscire dal while del run e si chiude separatamente
        //oppure se si catcha la SocketException in run non si chiama closeConnection
        //provare entrambe le versioni
        for (ClientHandler clientHandler: nameToHandlerMap.values()){
            clientHandler.closeConnection();
            clientHandler.setGameHandler(null);
        }
        server.removeGameHandler(this);
    }

    /**
     * It sends a serialized message to a single client through socket connection.
     * @param nickname: name of the player whom the message is being sent.
     * @param message: Message instance being sent to the player.
     */
    private void sendTo(String nickname,Message message){
        ClientHandler clientHandler = nameToHandlerMap.get(nickname);
        System.out.println("Mando a "+nickname+" su client handler " +clientHandler.getID() + " un messaggio di tipo " + (message.getClass().toString()) );
        clientHandler.sendMessage(message);
    }

    /**
     * It sends a serialized message to all clients involved in the game.
     * @param message: Message instance being sent to the players.
     */
    private void sendAll(Message message){
        System.out.println("Mando in broadcast messaggio di " + (message.getClass().toString()));
        ArrayList<ClientHandler> clientHandlers = new ArrayList<>(nameToHandlerMap.values());
        for( ClientHandler clientHandler : clientHandlers){
            clientHandler.sendMessage(message);
        }
    }

    /**
     * It sends a serialized message to all clients except the one linked to the ClientHandler's except instance.
     * @param except: ClientHandler's instance of the player who is being excluded from the message.
     * @param message: Message instance being sent to all players bar one.
     */
    private void sendAllExcept(ClientHandler except, Message message){
        ArrayList<ClientHandler> clientHandlers = new ArrayList<>(nameToHandlerMap.values());
        for( ClientHandler clientHandler : clientHandlers){
            if(!clientHandler.equals(except))
                clientHandler.sendMessage(message);
        }
    }

    /**
     * It sets the class serverConnection attribute referencing the object responsible for the socket Connection.
     * @param serverConnection: ServerSocketConnection's instance handling the socket.
     */
    public void setServer(ServerSocketConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    /**
     * It removes a ClientHandler's instance from the clientHandler's registry.
     * @param clientHandler: ClientHandler istance that is being removed from the registry.
     */
    public synchronized void removeClientHandler(ClientHandler clientHandler){
        System.out.println("GameHandler: ora rimuovo dalla mia lista di CH " + clientHandler.getID());
        for (Map.Entry<String,ClientHandler> client: nameToHandlerMap.entrySet()){
            System.out.println("Entry di nameToHandlerMap: " + client.getKey());
            if (client.getValue().equals(clientHandler)){
                nameToHandlerMap.remove(client.getKey());
                System.out.println("GameHandler: ho rimosso dalla mia lista di CH " + clientHandler.getID());
                return;
            }
        }
    }

    /**
     * It extracts a player's name by its clientHandler's identification number.
     * @param clientID: ClientHandler's identification number.
     * @return the name of the player linked to the ClientHandler's clientId.
     */
    private String getNicknameFromClientID(int clientID){
        return server.getIdToNicknameMap().get(clientID);
    }

    /**
     * Method propertyChange receives an update notification from the GameController and sends all client a serialized
     * message containing the information about the update.
     * @param evt event of type PropertyChangeEvent
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("GH: ho ricevuto un messaggio di update, ora lo mando in broadcast");
        String eventName = evt.getPropertyName();
        if (eventName.equals("UpdateMessage") && evt.getNewValue()!=null){
            Message outwardsMessage = (Message) evt.getNewValue();
            sendAll(outwardsMessage);
            System.out.println("GH: mandato il messaggio in broadcast");
        }
    }
}
