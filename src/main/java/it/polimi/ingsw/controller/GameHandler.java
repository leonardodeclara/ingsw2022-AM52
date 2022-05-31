package it.polimi.ingsw.controller;

import com.sun.net.httpserver.Authenticator;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.Tower;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;



public class GameHandler implements PropertyChangeListener{
    GameController gameController;
    ServerSocketConnection serverConnection;
    Server server;
    ArrayList<String> players;
    HashMap<String, ClientHandler> nameToHandlerMap;
    boolean expertGame;
    ArrayList<String> playersOrder;
    Iterator<String> playersOrderIterator;

    public GameHandler(Server server,HashMap<String, ClientHandler> nameToHandlerMap, boolean expertGame){
        this.server = server;
        this.nameToHandlerMap =nameToHandlerMap;
        this.expertGame=expertGame;
        players = new ArrayList<>();
        players.addAll(nameToHandlerMap.keySet());
        playersOrder = new ArrayList<>(players);
        playersOrderIterator = playersOrder.iterator();
    }

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


    public void setServer(ServerSocketConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    public void startGame(){
        System.out.println("GameHandler: ora istanzio GameController");
        updatePlayersOrder(players);
        gameController= new GameController(expertGame, new ArrayList<>(players));
        gameController.setUpdateListener(this); //gameHandler inizia ad ascoltare il controller
        System.out.println("GameHandler: ora faccio ascoltare game da GC");
        //mando a tutti le isole istanziate
        Message gameInstantiationMessage = gameController.handleGameInstantiation();
        sendAll(gameInstantiationMessage);


        //System.out.println("GameHandler: ho istanziato Controller che ha istanziato game con i listener");
        Message waitStateMessage = new ClientStateMessage(ClientState.WAIT_TURN);
        Message setUpPhaseStateMessage = new ClientStateMessage(ClientState.SET_UP_WIZARD_PHASE);

        sendAllExcept(nameToHandlerMap.get(playersOrder.get(0)),waitStateMessage); //tutti i giocatori tranne il primo vengono messi in wait
        sendTo(playersOrder.get(0), new AvailableWizardMessage(gameController.getAvailableWizards())); //al primo giocatore viene aggiornata la lista di wizard disponibili
        sendTo(playersOrder.get(0),setUpPhaseStateMessage);  //viene aggiornato lo stato del primo giocatore


        //in teoria qui manualmente vanno mandate a tutti i client le informazioni necessarie per far inizializzare le view
        //o qui oppure in  startPlanningPhase() ma dato che viene printata la board lato client in planning phase, si rischia di printare quella vecchia

        System.out.println("GameHandler: finito startGame");
    }

    private void startPlanningPhase(){

        gameController.updateCloudsStudents(); //all'inizio della planning phase vengono riempite le clouds
        Message waitStateMessage = new ClientStateMessage(ClientState.WAIT_TURN);
        Message playAssistantCardMessage = new ClientStateMessage(ClientState.PLAY_ASSISTANT_CARD);

        String startingPlayer = playersOrder.get(0);
        sendAllExcept(nameToHandlerMap.get(startingPlayer), waitStateMessage); //tutti i giocatori tranne il primo vengono messi in wait

        sendTo(startingPlayer, playAssistantCardMessage);  //viene aggiornato lo stato del primo giocatore

        updatePlayersOrder(playersOrder);
        // in teoria togliendo questa chiamata non ci dovrebbero essere problemi con l'ordine
        //perché alla prima planningPhase in assoluto il turno è stato stabilito in startGame
        // e dall seconda si usa l'ordine stabilito dalle carte del round precedente finché non hanno scelto tutti le carte
    }

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


    private void updatePlayersOrder(ArrayList<String> players){
        playersOrder = new ArrayList<String>(players);
        playersOrderIterator = playersOrder.iterator();
        System.out.println("Ordine previsto dei giocatori:"+ playersOrderIterator.toString());
        for (int i = 0; i < players.size(); i++){
            System.out.println(playersOrder.get(i));
        }
        playersOrderIterator.next();
    }

    private void handleWizardSelectionMessage(WizardSelectionMessage message, ClientHandler client){
        int chosenWizard = message.getWizard();
        String clientName = getNicknameFromClientID(client.getID());
        System.out.println("GameHandler:è arrivato un messaggio di wizardSelection da " + clientName);
        Message response = gameController.updateWizardSelection(clientName, chosenWizard);
        sendTo(clientName, response);
        if (!(response instanceof ErrorMessage)){
            System.out.println(clientName+ " ha scelto il suo wizard, ora gi chiederò che torre vuole");
            sendTo(clientName, new ClientStateMessage(ClientState.SET_UP_TOWER_PHASE));
            //sendTo(clientName, new AvailableTowerMessage(gameController.getAvailableTowers()));
            //mandiamo due volte le availableTowers
        }
    }

    private String getNicknameFromClientID(int clientID){
        return server.getIdToNicknameMap().get(clientID);
    }

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

    private void handlePlayAssistantCardMessage(PlayAssistantCardMessage message, ClientHandler client){
        int chosenCard = message.getPriority();
        String clientName = getNicknameFromClientID(client.getID());
        System.out.println("GameHandler:è arrivato un messaggio di playAssistantCard da " + clientName);
        Message response = gameController.updateAssistantCards(clientName, chosenCard); //se il messaggio andava bene il model si è aggiornato dopo questa riga
        sendTo(clientName, response);
        //mandiamo il wait turn due volte se per esempio il secondo giocatore gioca una carta con minor priorità
        //sistemare da qualche parte qui

        if (!(response instanceof ErrorMessage)){
            //Message currentTurnCards = gameController.
            //bisogna mandare in broadcast un messaggio con le carte giocate fino ad ora (CurrentTurnAssistantCard)
            // ->cambiarei da come sono state pensate e farei l'aggiornamento carta per carta,si potrebbe usare AssistantDeckUpdate. boh rivedere
            System.out.println(clientName+ " ha scelto la sua carta, ora lo sto mandando in WAIT_TURN");
            if (playersOrderIterator.hasNext()){ //se il giocatore che ha giocato non è l'ultimo allora avanza di uno l'iterator, altrimenti manda a tutti il messaggio
                String nextPlayer = playersOrderIterator.next();
                Message playAssistantCardMessage = new ClientStateMessage(ClientState.PLAY_ASSISTANT_CARD);
                System.out.println("Siccome " + clientName + " ha finito la sua selezione ora è il turno di " + nextPlayer);
                sendTo(nextPlayer,playAssistantCardMessage);
            }
            else{
                startActionPhase();
            }
        }
    }


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

    private void handleMoveMotherNatureMessage(MotherNatureMoveMessage message, ClientHandler client){
        int steps = message.getSteps();
        String clientName = getNicknameFromClientID(client.getID());
        System.out.println("GameHandler:è arrivato un messaggio di moveMotherNature da " + clientName);
        Message response = gameController.moveMotherNature(clientName, steps); //se il messaggio andava bene il model si è aggiornato dopo questa riga
        //gestire qui il caso di termine partita con la chiamata a closeMatch()
        //dividere i casi: errore, tutto okay avanti con PICK_CLOUD o END_TURN, fine partita
        //nel secondo terzo caso entrambi i messaggi sono
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

    private void handleEndTurn(CloseTurnMessage message, ClientHandler client){
        String clientName = getNicknameFromClientID(client.getID());
        if(expertGame)
            gameController.resetPersonalityCard();
        //qui metto l'avanzamento dell'iterator
        // se l'ultimo giocatore ha giocato passo a EndRound quindi nuova planning phase ecce
        if (playersOrderIterator.hasNext()){ //se il giocatore che ha giocato non è l'ultimo allora avanza di uno l'iterator, altrimenti manda a tutti il messaggio
            Message stateChange = new ClientStateMessage(ClientState.WAIT_TURN);
            sendTo(clientName, stateChange);
            String nextPlayer = playersOrderIterator.next();
            gameController.setCurrentPlayer(nextPlayer); //serve per le carte
            Message moveStudentsFromLobbyMessage = new ClientStateMessage(ClientState.MOVE_FROM_LOBBY);
            System.out.println("Siccome " + clientName + " ha finito il suo turno ora è il turno di " + nextPlayer);
            sendTo(nextPlayer,moveStudentsFromLobbyMessage);
        }
        else{
            handleEndRound();
        }

    }

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
            Message response = new ErrorMessage(ErrorKind.ILLEGAL_MOVE); //il client non dovrebbe mai costringere il client a farlo, ma per solidità facciamo un doppio controllo
            sendTo(clientName,response);
        }
    }

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

    private void handleCard3Effect(Card3EffectMessage message, ClientHandler client){
        String clientName = getNicknameFromClientID(client.getID());
        Message response = gameController.applyEffect3(message.getIslandID());
        if (response instanceof EndGameMessage){
            sendAll(response);
            closeMatch();
            return;
        }
        else if (response==null) {
            //caso in cui non c'è stato un errore o non è finita la partita
            response= new ClientStateMessage(client.getCurrentClientState());
            System.out.println(clientName + "ha utilizzato l'effetto della carta 3, " +
                    "ora lo mando nello stato precedente alla invocazione della carta");
        }
        sendTo(clientName, response);
    }

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

    private void handleCard9Effect(Card9EffectMessage message, ClientHandler client){
        String clientName = getNicknameFromClientID(client.getID());
        gameController.applyEffect9(message.getBanned());
        System.out.println(clientName + "ha utilizzato l'effetto della carta 9, " +
                    "ora lo mando nello stato precedente alla invocazione della carta");
        sendTo(clientName, new ClientStateMessage(client.getCurrentClientState()));
        //in teoria non ci possono essere errori di invalid input per questo carta perché
        //eventuali input errati (cioè string che non sono i color) vengono gestiti lato client
    }

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

    private void handleEndRound(){
        //in caso di expert game ci saranno un po' di magheggi da fare
        //se non siamo in expert basta chiamare startPlanningPhase() e poi da lì il resto va a oltranza
        //poi non mi ricordo sinceramente
        //ad es bisogna resettare le carte assistente
        Message endRound = gameController.closeCurrentRound();
        if (endRound instanceof EndGameMessage){ //magari cambiare nome a questo metodo e mettere qualcosa tipo isThisLastRound
            sendAll(endRound);
            closeMatch();
        }
        else{
            startPlanningPhase();
        }
    }

    public synchronized void closeMatch(){
        System.out.println("Qui muore il gameHandler");
        sendAll(new ClientStateMessage(ClientState.END_GAME));
        for (ClientHandler clientHandler: nameToHandlerMap.values()){
            clientHandler.closeConnection();
        }
        server.removeGameHandler(this);
        //poi lato server bisogna cancellare la partita e tutto il resto
    }

    private void sendTo(String nickname,Message message){
        ClientHandler clientHandler = nameToHandlerMap.get(nickname);
        System.out.println("Mando a "+nickname+" su client handler " +clientHandler.getID() + " un messaggio di tipo " + (message.getClass().toString()) );
        clientHandler.sendMessage(message);
    }

    private void sendAll(Message message){
        System.out.println("Mando in broadcast messaggio di " + (message.getClass().toString()));
        ArrayList<ClientHandler> clientHandlers = new ArrayList<>(nameToHandlerMap.values());
        for( ClientHandler clientHandler : clientHandlers){
            clientHandler.sendMessage(message);
        }
    }
    private void sendAllExcept(ClientHandler except, Message message){
        ArrayList<ClientHandler> clientHandlers = new ArrayList<>(nameToHandlerMap.values());
        for( ClientHandler clientHandler : clientHandlers){
            if(!clientHandler.equals(except))
                clientHandler.sendMessage(message);
        }
    }

    public GameController getGameController() {
        return gameController;
    }

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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("GH: ho ricevuto un messaggio di update, ora lo mando in broadcast");
        String eventName = evt.getPropertyName();
        if (eventName.equals("UpdateMessage") && evt.getNewValue()!=null){
            Message outwardsMessage = (Message) evt.getNewValue();
            sendAll(outwardsMessage);
        }
        System.out.println("GH: mandato il messaggio in broadcast");
    }
}
