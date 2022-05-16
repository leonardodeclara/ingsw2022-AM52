package it.polimi.ingsw.controller;

import com.sun.net.httpserver.Authenticator;
import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.Tower;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;


//PROBLEMI
//il server vede 2 giocatori anche se ne ho messi 3

public class GameHandler implements PropertyChangeListener{
    GameController gameController;
    ServerSocketConnection serverConnection;
    Server server;
    ArrayList<String> players;
    HashMap<String, ClientHandler> nameToHandlerMap;
    boolean expertGame;
    ArrayList<String> playersOrder; //ora superfluo ma poi nelle fasi di gioco effettivo è  indispensabile
    Iterator<String> playersOrderIterator; //si fa .next() e si vede a chi tocca dopo

    public GameHandler(Server server,HashMap<String, ClientHandler> nameToHandlerMap, boolean expertGame){
        this.server = server;
        this.nameToHandlerMap =nameToHandlerMap;
        this.expertGame=expertGame;
        players = new ArrayList<>();
        players.addAll(nameToHandlerMap.keySet());
        playersOrder = new ArrayList<>();
        playersOrderIterator = playersOrder.iterator();
    }

    public void handleMessage(Message message,ClientHandler clientHandler){
        if(message instanceof WizardSelectionMessage)
            handleWizardSelectionMessage((WizardSelectionMessage) message, clientHandler);
        else if (message instanceof TowerSelectionMessage)
            handleTowerSelectionMessage((TowerSelectionMessage)message, clientHandler);
        else if(message instanceof PlayAssistantCardMessage)
            handlePlayAssistantCardMessage((PlayAssistantCardMessage) message,clientHandler);
    }


    public void setServer(ServerSocketConnection serverConnection) {
        this.serverConnection = serverConnection;
    }


    //IDEA:
    //se si volesse fare un'interfaccia della lobby lato client con gli altri giocatori all'interno, basterebbe fare in modo che il client richieda
    //i giocatori con cui è in lobby così da poterne scrivere i nomi sulla GUI/CLI
    /*
    si svuota la lobby inserendo tutto in un game controller (che inizializza il model)
    si mappano i nickname a degli id player (in ordine di join nella lobby, usando quindi la lista players di lobby)
    si manda in broadcast a tutti i client il numero di giocatori e il tipo di partita così che possano inizializzare la view
     */
    public void startGame(){
        //System.out.println("GameHandler: ora istanzio GameController");
        gameController= new GameController(expertGame, new ArrayList<>(players));
        gameController.setUpdateListener(this); //gameHandler inizia ad ascoltare il controller
        //System.out.println("GameHandler: ora faccio ascoltare game da GC");
        gameController.getGame().setPropertyChangeListeners(gameController);
        //System.out.println("GameHandler: ora faccio istanziare i gameElements da GC");
        gameController.getGame().instantiateGameElements(); //va inizializzato il model, ma non so se questa chiamata va qui

        //System.out.println("GameHandler: ho istanziato Controller che ha istanziato game con i listener");
        Message waitStateMessage = new ClientStateMessage(ClientState.WAIT_TURN);
        Message setUpPhaseStateMessage = new ClientStateMessage(ClientState.SET_UP_WIZARD_PHASE);

        sendAllExcept(nameToHandlerMap.get(players.get(0)),waitStateMessage); //tutti i giocatori tranne il primo vengono messi in wait
        sendTo(players.get(0), new AvailableWizardMessage(gameController.getAvailableWizards())); //al primo giocatore viene aggiornata la lista di wizard disponibili
        sendTo(players.get(0),setUpPhaseStateMessage);  //viene aggiornato lo stato del primo giocatore

        //in teoria qui manualmente vanno mandate a tutti i client le informazioni necessarie per far inizializzare le view
        //o qui oppure in  startPlanningPhase() ma dato che viene printata la board lato client in planning phase, si rischia di printare quella vecchia
        updatePlayersOrder(players);
    }

    private void startPlanningPhase(){

        Message waitStateMessage = new ClientStateMessage(ClientState.WAIT_TURN);
        Message setUpPhaseStateMessage = new ClientStateMessage(ClientState.PLAY_ASSISTANT_CARD);

        //qui usiamo ancora players.get(0), non è previsto un ordine specifico
        sendAllExcept(nameToHandlerMap.get(players.get(0)),waitStateMessage); //tutti i giocatori tranne il primo vengono messi in wait
        sendTo(players.get(0), new AvailableWizardMessage(gameController.getAvailableWizards())); //al primo giocatore viene aggiornata la lista di wizard disponibili
        //perché mandi i wizard residui? dovresti mandare le carte disponibili
        //ah no okay hai fatto copia incolla da startGame, comunque va cambiata
        sendTo(players.get(0),setUpPhaseStateMessage);  //viene aggiornato lo stato del primo giocatore

        updatePlayersOrder(players);
    }

    private void startActionPhase(){


    }

    private void updatePlayersOrder(ArrayList<String> players){
        playersOrder = new ArrayList<String>(players);
        playersOrderIterator = playersOrder.iterator();
        System.out.println("Ordine previsto dei giocatori:"+ playersOrderIterator.toString());
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
            sendTo(clientName, new AvailableTowerMessage(gameController.getAvailableTowers()));
        }
    }

    private String getNicknameFromClientID(int clientID){
        return server.getIdToNicknameMap().get(clientID);
    }
    private void handleTowerSelectionMessage(TowerSelectionMessage message, ClientHandler client){
        Tower chosenTower = message.getTower();
        String clientName = getNicknameFromClientID(client.getID());
        System.out.println("GameHandler:è arrivato un messaggio di towerSelection da " + clientName);
        Message response = gameController.updateTowerSelection(clientName, chosenTower);
        sendTo(clientName, response);
        if (!(response instanceof ErrorMessage)){
            System.out.println(clientName+ " ha scelto la sua torre, ora lo sto mandando in WAIT_TURN");
        }

        if (playersOrderIterator.hasNext()){ //se il giocatore che ha giocato non è l'ultimo allora avanza di uno l'iterator, altrimenti manda a tutti il messaggio
            String nextPlayer = playersOrderIterator.next();
            Message setUpPhaseStateMessage = new ClientStateMessage(ClientState.SET_UP_WIZARD_PHASE);
            System.out.println("Siccome " + clientName + " ha finito la sua selezione ora è il turno di " + nextPlayer);
            sendTo(nextPlayer, new AvailableWizardMessage(gameController.getAvailableWizards())); //al prossimo giocatore viene aggiornata la lista di wizard disponibili
            sendTo(nextPlayer, setUpPhaseStateMessage);  //viene aggiornato lo stato del primo giocatore
        }
        else{
            //mando in broadcast la scelta di torri di ogni player, in modo che tutti possano visualizzarle
            //in teoria qui per la prima volta i client visualizzano la view
            Message gameStart = gameController.buildPlayerTowerAssociation();
            sendAll(gameStart);
            startPlanningPhase();
            System.out.println("Adesso faccio partire la partita con la scelta delle carte assistente (credo). comunque la fase dopo");
        }

    }

    private void handlePlayAssistantCardMessage(PlayAssistantCardMessage message, ClientHandler client){
        int chosenCard = message.getPriority();
        String clientName = getNicknameFromClientID(client.getID());
        System.out.println("GameHandler:è arrivato un messaggio di playAssistantCard da " + clientName);
        Message response = gameController.updateAssistantCards(clientName, chosenCard); //se il messaggio andava bene il model si è aggiornato dopo questa riga
        sendTo(clientName, response);
        if (!(response instanceof ErrorMessage)){
            System.out.println(clientName+ " ha scelto la sua carta, ora lo sto mandando in WAIT_TURN");
        }

        if (playersOrderIterator.hasNext()){ //se il giocatore che ha giocato non è l'ultimo allora avanza di uno l'iterator, altrimenti manda a tutti il messaggio
            String nextPlayer = playersOrderIterator.next();
            Message playAssistantCardMessage = new ClientStateMessage(ClientState.PLAY_ASSISTANT_CARD);
            System.out.println("Siccome " + clientName + " ha finito la sua selezione ora è il turno di " + nextPlayer);

            //in teoria la view si dovrebbe aggiornare da sola a questo punto, senza dover mandare messaggi espliciti come per wizard e towers
        }
        else{
            startActionPhase();
        }

    }



    private void sendTo(String nickname,Message message){
        ClientHandler clientHandler = nameToHandlerMap.get(nickname);
        System.out.println("Mando a "+nickname+" su client handler " +clientHandler.getID() + " un messaggio di tipo " + (message.getClass().toString()) );
        clientHandler.sendMessage(message);
    }

    private void sendAll(Message message){
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
