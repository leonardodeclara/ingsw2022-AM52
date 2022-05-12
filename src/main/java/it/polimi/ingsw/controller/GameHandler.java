package it.polimi.ingsw.controller;

import com.sun.net.httpserver.Authenticator;
import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.messages.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class GameHandler implements PropertyChangeListener {
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
        //if(message instanceof SetUpMessage)
        //    handleSetupMessage((SetUpMessage) message, clientHandler);
       // if(message instanceof GameParametersMessage)
        //    handleGameParametersMessage((GameParametersMessage) message, playerID);
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
    si manda in broadcast a tutti wait_turn e al primo della lista setup_phase
    quando poi giunge il messaggio dal giocatore usando handleMessages si passa in un altro metodo che fa la setup phase così questo
    non viene riempito di robe che non gli competono
     */
    public void startGame(){
        gameController= new GameController(expertGame);
        //settaggio dei listener in gameController da aggiungere (devo dargli this in input)
        ClientStateMessage waitStateMessage = new ClientStateMessage(ClientState.WAIT_TURN);
        ClientStateMessage setUpPhaseStateMessage = new ClientStateMessage(ClientState.SET_UP_PHASE);
        ArrayList<ClientHandler> clientHandlers = new ArrayList<>(nameToHandlerMap.values());
        sendAllExcept((ArrayList<ClientHandler>) clientHandlers,nameToHandlerMap.get(players.get(0)),waitStateMessage);
        sendTo(players.get(0),setUpPhaseStateMessage);

    }

    /*
    private void handleSetupMessage(SetUpMessage message, ClientHandler clientID){
        gameController.updateTeamAndWizard();
    }
*/
    private void sendAllExcept(ArrayList<ClientHandler> clientHandlers,ClientHandler except, Message message){
        for( ClientHandler clientHandler : clientHandlers){
            if(!clientHandler.equals(except))
                clientHandler.sendMessage(message);
        }
    }

    private void sendTo(String nickname,Message message){
        ClientHandler clientHandler = nameToHandlerMap.get(nickname);
        System.out.println("Mando a "+nickname+" su client handler "+clientHandler.getID());
        clientHandler.sendMessage(message);
    }


    public GameController getGameController() {
        return gameController;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //spacchetta l'evento, prende il messaggio e lo invia
    }
}
