package it.polimi.ingsw.client;

import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.controller.ServerSocketConnection;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.Tower;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Client { //gestisce la socket da un lato e dialoga con CLI/GUI dall'altro
    ClientSocket clientSocket;
    InputParser inputParser;
    CLI cli;
    boolean active;

    public Client(CLI cli) {
        this.cli=cli;
        active = true;
    }

    public Message buildMessageFromPlayerInput(ArrayList<Object> data,ClientState currentState){
        switch (currentState){ //in base allo stato costruiamo messaggi differenti
            case CONNECT_STATE:
                return buildConnectMessage(data);
            case INSERT_NEW_GAME_PARAMETERS:
                return buildNewGameParametersMessage(data);
            case SET_UP_WIZARD_PHASE:
                return buildWizardSelectionMessage(data);
            case SET_UP_TOWER_PHASE:
                return buildTowerSelectionMessage(data);
            case PLAY_ASSISTANT_CARD:
                return buildPlayAssistantCardMessage(data);
        }
        return null;
    }

    private Message buildConnectMessage(ArrayList<Object> data){
        return new LoginRequestMessage((String)data.get(0));
    }

    private Message buildNewGameParametersMessage(ArrayList<Object> data){
        cli.prepareView(data);
        return new GameParametersMessage((Integer)data.get(0),(Boolean)data.get(1));
    }

    private Message buildWizardSelectionMessage(ArrayList<Object> data){
        return new WizardSelectionMessage((int) data.get(0));
    }

    private Message buildTowerSelectionMessage(ArrayList<Object> data){
        return new TowerSelectionMessage((Tower) data.get(0));
    }

    private Message buildPlayAssistantCardMessage(ArrayList<Object> data){
        return new PlayAssistantCardMessage((Integer)data.get(0));
    }
}
    /*
    @Override
    public void run(){
        try {
            while(active)
                executeCurrentState();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void instantiateSocket() throws IOException {
        boolean connectionAccepted = false;
        while(!connectionAccepted){
            try{
                String ip = cli.askIP();
                int port = cli.askPort();
                clientSocket = new ClientSocket(ip,port,this);
                connectionAccepted = true;
            }catch(UnknownHostException |SocketException e){
                cli.visualizeCustomMessage("Connessione fallita. Scegli un altro server o riprova più tardi");
                connectionAccepted = false;
            }
        }


    }

    public void handleServerMessage(Message message) throws IOException, ClassNotFoundException, InterruptedException { //gestione dei messaggi asincroni (tipo quelli di aggiornamento model, o quello di inizio partita)
        //System.out.println("Ho ricevuto un messaggio dal server");
        if(message instanceof ClientStateMessage){ //ha ricevuto un nuovo cambio stato (asincrono)
            ClientStateMessage newStateMessage = (ClientStateMessage) message;
            currentState = newStateMessage.getNewState(); //switch del client al prossimo stato
        }
        //if(message instanceof UpdateInterfaceMessage){
          //  userInterface.update();
        //}
    }

    public void connect() throws IOException, ClassNotFoundException, InterruptedException { //client è il ponte tra socket e user interface, quindi è qui che facciamo error handling per input e/o errori lato server
        boolean nicknameAccepted = false;
        do{
            String nickname = cli.askNickname(); //prendi l'input
            Message serverResponse = clientSocket.connect(nickname); //mandalo a clientSocket che lo trasformerà in un messaggio, lo manderà al server e resitutirà una risposta
            if(serverResponse instanceof ClientStateMessage) {
                cli.visualizeCustomMessage("Benvenuto, "+ nickname);
                ClientStateMessage newStateMessage = (ClientStateMessage) serverResponse;
                currentState = newStateMessage.getNewState(); //switch del client al prossimo stato
                //System.out.println("il mio stato attuale è " + currentState.toString());
                //clientSocket.run();
                //faccio partire il thread che gestisce la connessione server-client, non so dove farlo partire
                //se lo eseguo qui poi non termina l'esecuzione del metodo (why?)

                nicknameAccepted = true;
            }
            else if(serverResponse instanceof ErrorMessage) {
                cli.visualizeCustomMessage(nickname + " non è disponibile, scegli un altro nome");
                nicknameAccepted = false;
            }
        } while(!nicknameAccepted);

    }

    public void executeCurrentState() throws IOException, ClassNotFoundException, InterruptedException { //andrebbe aggiunto un listener così quando lo stato cambia viene in automatico chiamato questo
        //System.out.println("Qui eseguo azioni in base allo stato");
        //String[] inputTokens = playerInput.split(" ");
        switch(currentState){
            case CONNECT_STATE: //ogni azione può fallire, o perchè il server dice "bro, no" o perchè a dirlo è il clientSocket
                instantiateSocket();
                connect();
                break;
            case INSERT_NEW_GAME_PARAMETERS: //prima viene richiesto al giocatore l'input mediante CLI/GUI e poi lo si inserisce nel metodo previsto
                insertNewGameParameters();
                break;
            case WAIT_IN_LOBBY:
                waitInLobby();
                break;
            case WAIT_TURN:
                waitTurn();
                break;
            case SET_UP_PHASE:
                chooseAssistantDeck();
                break;
        }

    }

    public void waitInLobby(){
        //final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        //executorService.scheduleAtFixedRate(CLI::waitInLobby, 0, 5, TimeUnit.SECONDS);
        //bisogna capire come printare qualcosa ogni tot finchè non cambia stato
        //fare arrivar qui i giocatori della lobby (che poi diventano anche quelli del gioco)
    }
    public void waitTurn(){
        //bisogna capire come printare qualcosa ogni tot finchè non cambia stato
        //qui dobbiamo sapere chi sta giocando (glielo passa il server)
    }
    public void chooseAssistantDeck(){
        cli.visualizeCustomMessage("Scegli un mazzoooooooo");
    }

    public void insertNewGameParameters() throws IOException, ClassNotFoundException, InterruptedException { //returna boolean in modo da far sapere al chiamante (CLI/GUI) se deve chiedere di nuovo l'input o no
        boolean parametersAccepted = false;
        do{
            ArrayList<Object> parameters = cli.askGameParameters();
            Message serverResponse = clientSocket.sendGameParameters(parameters);
            if(serverResponse instanceof ClientStateMessage){
                ClientStateMessage newStateMessage = (ClientStateMessage) serverResponse;
                currentState = newStateMessage.getNewState(); //switch del client al prossimo stato
                parametersAccepted = true;
            }else if(serverResponse instanceof ErrorMessage) {
                System.out.println("\nI dati inseriti non sono corretti");
                parametersAccepted = false;
            }
        }while(!parametersAccepted);
        executeCurrentState();
    }

    public void send(Message message) throws IOException{
            clientSocket.send(message);
    }


    public ClientState getCurrentState() {
        return currentState;
    }

    public ClientSocket getClientSocket() {
        return clientSocket;
    }

    public void setCurrentState(ClientState currentState) {
        this.currentState = currentState;
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException { //qui parte tutto
        Client client = new Client();
        client.run(); //basta chiamarlo la prima volta, poi si autogestisce
    }
}




//send dei messaggi vanno gestiti separatamente dai metodi che si occupano dell'elaborazione


*/





