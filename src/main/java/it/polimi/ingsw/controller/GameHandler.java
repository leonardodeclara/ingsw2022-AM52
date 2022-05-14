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
import java.util.stream.Collectors;

public class GameHandler implements PropertyChangeListener{
    GameController gameController;
    ServerSocketConnection serverConnection;
    Server server;
    ArrayList<String> players;
    HashMap<String, ClientHandler> nameToHandlerMap;
    boolean expertGame;

    public GameHandler(Server server,HashMap<String, ClientHandler> nameToHandlerMap, boolean expertGame){
        this.server = server;
        this.nameToHandlerMap =nameToHandlerMap;
        this.expertGame=expertGame;
        players = new ArrayList<>();
        players.addAll(nameToHandlerMap.keySet());
    }

    public void handleMessage(Message message,ClientHandler clientHandler){
        if(message instanceof WizardSelectionMessage)
            handleWizardSelectionMessage((WizardSelectionMessage) message, clientHandler);
        else if (message instanceof TowerSelectionMessage)
            handleTowerSelectionMessage((TowerSelectionMessage)message, clientHandler);
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
        gameController= new GameController(expertGame);
        gameController.setUpdateListener(this);
        ArrayList<ClientHandler> clientHandlers = new ArrayList<>(nameToHandlerMap.values());
        Message waitStateMessage = new ClientStateMessage(ClientState.WAIT_TURN);
        Message setUpPhaseStateMessage = new ClientStateMessage(ClientState.SET_UP_WIZARD_PHASE);
        sendAllExcept(clientHandlers,nameToHandlerMap.get(players.get(0)),waitStateMessage); //tutti i giocatori tranne il primo vengono messi in wait
        sendTo(players.get(0), new AvailableWizardMessage(gameController.getAvailableWizards())); //al primo giocatore viene aggiornata la lista di wizard disponibili
        sendTo(players.get(0),setUpPhaseStateMessage);  //viene aggiornato lo stato del primo giocatore
    }


    private void handleWizardSelectionMessage(WizardSelectionMessage message, ClientHandler client){
        int chosenWizard = message.getWizard();
        String clientName = server.getIdToNicknameMap().get(client.getID()); //è orribile, vedere come risolvere
        System.out.println("GameHandler:è arrivato un messaggio di wizardSelection da " + clientName);
        Message response = gameController.updateWizardSelection(clientName, chosenWizard);
        sendTo(clientName, response);
        if (!(response instanceof ErrorMessage)){
            System.out.println(clientName+ " ha scelto il suo wizard, ora gi chiederò che torre vuole");
            //potrei mandargli la lista di wizard residui, ma cosa se ne farebbe?
            sendTo(clientName, new ClientStateMessage(ClientState.SET_UP_TOWER_PHASE));
            sendTo(clientName, new AvailableTowerMessage(gameController.getAvailableTowers()));
        }
    }

    private void handleTowerSelectionMessage(TowerSelectionMessage message, ClientHandler client){
        Tower chosenTower = message.getTower();
        String clientName = server.getIdToNicknameMap().get(client.getID()); //è orribile, vedere come risolvere
        System.out.println("GameHandler:è arrivato un messaggio di towerSelection da " + clientName);
        Message response = gameController.updateTowerSelection(clientName, chosenTower);
        sendTo(clientName, response);
        if (!(response instanceof ErrorMessage)){
            System.out.println(clientName+ " ha scelto la sua torre, ora lo sto mandando in WAIT_TURN");
            //potrei mandargli la lista di torri residue, ma cosa se ne farebbe?
        }
        //qui devo far partire il messaggio per i successivi client in modo che scelgano anche loro wizard e torri con i metodi già scritti
        //sarà un if (tutti i giocatori hanno scelto)
            //faccio partire il gioco (partono metodi di game chiamati da controller ecc)
                //else
                        //manda al giocatore successsivo il messaggio di wizard phase ecc (ultime due/tre righe di start game con l'indice giusto)
        //SOLUZIONE TEMPORANEA PER GESTIONE DUE PLAYER, DEVO TROVARE IL GIUSTO AVANZAMENTO DI PLAYER
        if (clientName.equals(players.get(0))){
            Message setUpPhaseStateMessage = new ClientStateMessage(ClientState.SET_UP_WIZARD_PHASE);
            System.out.println("Siccome " + clientName + " ha finito la sua selezione ora è il turno di " + players.get(1));
            sendTo(players.get(1), new AvailableWizardMessage(gameController.getAvailableWizards())); //al primo giocatore viene aggiornata la lista di wizard disponibili
            sendTo(players.get(1), setUpPhaseStateMessage);  //viene aggiornato lo stato del primo giocatore
        }
        else if (clientName.equals(players.get(1))){
            System.out.println("Adesso faccio partire la partita con la scelta delle carte assistente (credo). comunque la fase dopo");
        }

    }

    private void sendTo(String nickname,Message message){
        ClientHandler clientHandler = nameToHandlerMap.get(nickname);
        System.out.println("Mando a "+nickname+" su client handler " +clientHandler.getID() + " un messaggio di tipo " + (message.getClass().toString()) );
        clientHandler.sendMessage(message);
    }
    //non si può cambiare e tenere in input solo il messaggio ? tanto il metodo ricava la lista di clientHandlers da solo. stessa cosa per il metodo sotto
    private void sendAll(ArrayList<ClientHandler> clientHandlers,Message message){
        for( ClientHandler clientHandler : clientHandlers){
            clientHandler.sendMessage(message);
        }
    }
    private void sendAllExcept(ArrayList<ClientHandler> clientHandlers,ClientHandler except, Message message){
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
        String eventName = evt.getPropertyName();
        if (eventName.equals("UpdateMessage") && evt.getNewValue()!=null){
            Message outwardsMessage = (Message) evt.getNewValue();
            ArrayList<ClientHandler> clientHandlers = new ArrayList<>(nameToHandlerMap.values());
            sendAll(clientHandlers, outwardsMessage);
        }
    }
}
