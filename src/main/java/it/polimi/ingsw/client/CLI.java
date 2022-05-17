package it.polimi.ingsw.client;

import it.polimi.ingsw.CLI.GameBoard;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Tower;

import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class CLI implements Runnable{
    private final Scanner inputStream;
    private final PrintStream outputStream;
    private boolean active;
    private Client client;
    private ClientSocket clientSocket;
    private ClientState currentState;
    private InputParser inputParser;
    private ArrayList<Object> playerInput;
    private volatile Message receivedMessage;
    private final ScheduledExecutorService executorService;
    private GameBoard GB;

    /*
    TODO:
    i metodi che visualizzano i messaggi a schermo van sistemati. Si potrebbe creare un unico metodo e un hashmap (key:state,value:message)
     */
    public CLI() throws IOException {
        inputStream = new Scanner(System.in);
        outputStream = new PrintStream(System.out);
        client = new Client(this);
        receivedMessage = null;
        executorService = Executors.newSingleThreadScheduledExecutor();
        GB = new GameBoard(outputStream);
        inputParser = new InputParser();
        inputStream.useDelimiter("\n");
    }

    public void setClientSocket(ClientSocket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void setNextState(ClientState newState){
        currentState = newState;
        visualizeContextMessage();
    }
    public void run(){
        try {
            instantiateSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }

        active = true;
        setNextState(ClientState.CONNECT_STATE); //stato iniziale



        while(active) { //bisogna trovare il modo di impedire al giocatore di spammare invio
            if (inputStream.hasNext()) {
                playerInput = inputParser.parse(inputStream.nextLine(), currentState);
                if (playerInput.size() > 0) {
                    Message messageToSend = client.buildMessageFromPlayerInput(playerInput, currentState);
                    try {
                        clientSocket.send(messageToSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    visualizeInputErrorMessage();
                    visualizeContextMessage();
                }
            }
        }
    }

    public void handleMessageFromServer(Message receivedMessage){
        //System.out.println("Ho ricevuto dal server un messaggio di " + (receivedMessage.getClass().toString()));
        if(receivedMessage instanceof ClientStateMessage){
            if(currentState.equals(ClientState.CONNECT_STATE)){ //significa che è il nome è stato approvato, quindi lo salviamo in GameBoard
                GB.setNickname(inputParser.getNickname());
            }
            setNextState(((ClientStateMessage) receivedMessage).getNewState()); //Se è uno stato aggiorna quello corrente
        }else if(receivedMessage instanceof ErrorMessage) {
            visualizeServerErrorMessage(); //se è un errore visualizzalo
        }else{ //messaggio di view (a esclusione)
            updateView(receivedMessage); //se è un update della view aggiorna la view
        }

    }


    public void instantiateSocket() throws IOException {
        boolean connectionAccepted = false;
        while(!connectionAccepted){
            try{
                String ip = askIP();
                int port = askPort();
                clientSocket = new ClientSocket(ip,port,this);
                Thread socketThread = new Thread(clientSocket); //la sposti su un nuovo thread (parte run() in automatico)
                socketThread.start();
                connectionAccepted = true;
            }catch(UnknownHostException |SocketException e){
                visualizeCustomMessage("Connessione fallita. Scegli un altro server o riprova più tardi");
                connectionAccepted = false;
            }
        }
    }

    //stampo solo gli elementi di gioco che dipendono dal numero di giocatori e dalla modalità
    public void prepareView(ArrayList<Object> data){
        GB.setNumberOfPlayers((Integer)data.get(0));
        GB.setExpertGame((Boolean)data.get(1));
        //GB.instantiateGameElements();
        // spostato dopo che vengono estratti i primi studenti per le isole e viene posizionata MN
    }

    public void updateView(Message updateMessage) {
        if (updateMessage instanceof AvailableWizardMessage)
            updateAvailableWizard((AvailableWizardMessage) updateMessage);
        else if (updateMessage instanceof AvailableTowerMessage)
            updateAvailableTower((AvailableTowerMessage) updateMessage);
        else if (updateMessage instanceof GameInstantiationMessage)
            setInitialGameBoard((GameInstantiationMessage) updateMessage);
        else if (updateMessage instanceof GameStartMessage)
            updatePlayerTowerAssociation((GameStartMessage) updateMessage);
        else if (updateMessage instanceof CurrentTurnAssistantCardsUpdateMessage)
            updateCurrentTurnAssistantCards((CurrentTurnAssistantCardsUpdateMessage) updateMessage);
        else if (updateMessage instanceof AssistantDeckUpdateMessage)
            updatePlayerDeck((AssistantDeckUpdateMessage) updateMessage);
        else if (updateMessage instanceof IslandStudentsUpdateMessage)
            updateIslandStudents((IslandStudentsUpdateMessage) updateMessage);
        else if (updateMessage instanceof IslandTowersUpdateMessage)
            updateIslandTowers((IslandTowersUpdateMessage) updateMessage);
        else if (updateMessage instanceof CloudUpdateMessage)
            updateCloud((CloudUpdateMessage) updateMessage);
        else if (updateMessage instanceof MotherNatureMovementUpdateMessage)
            updateMotherNaturePosition((MotherNatureMovementUpdateMessage) updateMessage);
        else if (updateMessage instanceof IslandMergeUpdateMessage)
            updateIslandsMerge((IslandMergeUpdateMessage) updateMessage);
        else if (updateMessage instanceof BoardUpdateMessage)
            updatePlayerBoard((BoardUpdateMessage) updateMessage);
        else if (updateMessage instanceof CloudsRefillMessage)
            updateRefilledClouds((CloudsRefillMessage) updateMessage);
        else if (updateMessage instanceof ActivePersonalityMessage)
            updateActivePersonality((ActivePersonalityMessage) updateMessage);
        else if (updateMessage instanceof InactivePersonalityMessage)
            updateInactivePersonality((InactivePersonalityMessage) updateMessage);
    }

    public void updateAvailableWizard(AvailableWizardMessage message){
        GB.setAvailableWizards(message.getRemainingWizards());
    }

    public void updateAvailableTower(AvailableTowerMessage message){
        GB.setAvailableTowers(message.getRemainingTowers());
    }

    public void setInitialGameBoard(GameInstantiationMessage message){
        GB.instantiateGameElements(message.getIslands());
        System.out.println("PRIMA PRINT DELLA BOARD");
        GB.print();
    }

    public void updatePlayerTowerAssociation(GameStartMessage message){
        HashMap<String, Tower> associations = message.getChosenTeam();
        for (String player: associations.keySet()){
            GB.addClientBoard(player);
            GB.setClientTeam(player, associations.get(player));
        }
        GB.print();
    }

    public void updateCurrentTurnAssistantCards(CurrentTurnAssistantCardsUpdateMessage message){
        GB.setTurnCard( message.getCurrentTurnAssistantCards());
        System.out.println("Modifica delle currentAssistantCards (TODO, non sono ancora visibili)");
        System.out.println();
        GB.print();
    }

    public void updatePlayerDeck(AssistantDeckUpdateMessage message){
        GB.setPlayerDeck(message.getOwner(), message.getCards());
    }

    public void updateIslandStudents(IslandStudentsUpdateMessage message){
        GB.setIslandStudents(message.getIslandIndex(), message.getStudents());
        System.out.println("Modifica degli studenti nelle isole");
        System.out.println();
        GB.print();
    }

    public void updateIslandTowers(IslandTowersUpdateMessage message){
        GB.setIslandTowers(message.getIslandIndex(), message.getTowers());
        System.out.println("Modifica delle torri nelle isole");
        System.out.println();
        GB.print();
    }

    public void updateCloud(CloudUpdateMessage message){
        GB.emptyCloud(message.getCloudIndex());
        System.out.println("Svuotamento di una nuvola");
        System.out.println();
        GB.print();
    }

    public void updateMotherNaturePosition(MotherNatureMovementUpdateMessage message){
        GB.changeMNPosition(message.getIslandIndex());
        System.out.println("Spostamento di MN");
        System.out.println();
        GB.print();
    }

    public void updateIslandsMerge(IslandMergeUpdateMessage message){
        GB.setIslands(message.getUpdatedClientIslands());
        System.out.println("Merge di isole");
        System.out.println();
        GB.print();
    }

    public void updatePlayerBoard(BoardUpdateMessage message){
        String boardOwner = message.getOwner();
        GB.setUpdatedClientBoard(boardOwner, message.getUpdatedBoardTable(), message.getUpdatedLobbyTable(), message.getUpdatedTeacherTable());
        System.out.println("Aggiornata la board di" + boardOwner);
        System.out.println();
        GB.print();
    }

    public void updateRefilledClouds(CloudsRefillMessage message){
        GB.setClouds(message.getClouds());
        System.out.println("Riempite le clouds");
        System.out.println();
        GB.print();
    }

    public void updateActivePersonality(ActivePersonalityMessage message){
        GB.setActivePersonality(message.getActiveCardId());
        System.out.println("È stata attivata una carta personaggio");
        System.out.println();
        GB.print();
    }

    public void updateInactivePersonality(InactivePersonalityMessage message){
        GB.resetActivePersonality(message.getInactiveCardId());
        //non so se serve stampare il fatto che una carta non è più attiva
    }


    private void visualizeContextMessage(){
        //System.out.println("Vediamo che messaggio ho ricevuto");
        switch(currentState){
            case CONNECT_STATE:
                outputStream.println("Inserisci il tuo nickname:");
                break;
            case INSERT_NEW_GAME_PARAMETERS:
                outputStream.println("Inserisci il numero di giocatori (2/3) e la tipologia di partita (base o expert) per dare via al matchmaking:");
                break;
            case WAIT_IN_LOBBY:
                outputStream.println("Resta in attesa di altri giocatori...");
                break;
            case WAIT_TURN:
                outputStream.println("Resta in attesa del tuo turno");
                break;
            case SET_UP_WIZARD_PHASE:
                outputStream.println("Inserisci il deck che vuoi utilizzare");
                outputStream.println("Deck disponibili:"+GB.getAvailableWizards()); //prendiamo dalla view le informazioni da stampare a schermo
                break;
            case SET_UP_TOWER_PHASE:
                outputStream.println("Scegli il colore della tua squadra!");
                outputStream.println("Torri disponibili:"+GB.getAvailableTowers()); //prendiamo dalla view le informazioni da stampare a schermo
                break;
            case PLAY_ASSISTANT_CARD:
                GB.print(); //questo è il momento in cui printiamo l'attuale stato della partita.
                outputStream.println("Scegli una carta da giocare!");
                break;
            case MOVE_FROM_LOBBY:
                GB.print();
                outputStream.println("Scegli tre studenti da spostare nella table o su un'isola");
                outputStream.println("Per esempio digita move studentID1,studentID2,studentID3 in table,2,3 per muovere il primo studente nella table,il secondo sull'isola 2, il terzo sull'isola 3");
                break;


        }

    }
    private void visualizeServerErrorMessage(){ //messaggi di errore se qualcosa va storto lato server (sarebbe il caso di riscrivere decentemente il metodo)
        switch (currentState){
            case CONNECT_STATE:
                outputStream.println("Il nickname scelto è già stato scelto! Scegline un altro");
                break;
            case SET_UP_WIZARD_PHASE:
                outputStream.println("Il wizard scelto appartiene già ad un altro giocatore! Scegline un altro");
                break;
            case SET_UP_TOWER_PHASE:
                outputStream.println("La torre scelta appartiene già ad un altro giocatore! Scegline un'altra");
                break;
            case PLAY_ASSISTANT_CARD:
                outputStream.println("La carta scelta non è disponibile! Scegline un altro");
                break;
            case MOVE_FROM_LOBBY:
                outputStream.println("Scelta non valida! Riprova");
                break;
        }
    }

    private void visualizeInputErrorMessage(){ //messaggi visualizzati quando il giocatore scrive qualcosa che non deve
        switch (currentState){
            case CONNECT_STATE:
                outputStream.println("Il nickname scelto non è valido!");
                break;
            case INSERT_NEW_GAME_PARAMETERS:
                outputStream.println("I parametri inseriti non sono validi!");
                break;
            case WAIT_IN_LOBBY:
                outputStream.println("Non sei ancora in partita! Attendi altri giocatori per iniziare");
                break;
            case WAIT_TURN:
                outputStream.println("Non è il tuo turno!");
                break;
            case SET_UP_WIZARD_PHASE:
                outputStream.println("Non hai inserito un numero da 0 a 4");
                break;
            case SET_UP_TOWER_PHASE:
                outputStream.println("Non hai inserito uno dei 3 colori disponibili");
                break;
            case PLAY_ASSISTANT_CARD:
                outputStream.println("Le carte sono numerate da 1 a 10!");
                break;
            case MOVE_FROM_LOBBY:
                outputStream.println("I parametri inseriti non sono validi!");
                break;
        }
    }

    public void visualizeCustomMessage(String customMessage){
        outputStream.println(customMessage);
    }


    public String askIP(){
        outputStream.println("Benvenuto!");
        outputStream.println("Inserisci l'indirizzo ip del server: ");
        outputStream.println(">");
        return inputStream.nextLine();
    }

    public int askPort(){ //ogni metodo di CLI richiede gli input e gestisce gli errori base (tipo scrivo davide come porta per il server)
        boolean validInput = false; //si potrebbe fare la stessa cosa con while(1) e break ma così è più elegante
        int port = 0;
        while(!validInput){
            try{
                outputStream.println("Inserisci la porta del server: ");
                outputStream.println(">");
                port = Integer.parseInt(inputStream.nextLine());
                validInput = true;
            }catch(NumberFormatException e){
                outputStream.println("La porta dovrebbe essere un numero intero, riprova");
                validInput = false; //si può omettere, lo scrivo per chiarezza
            }
        }
        return port;
    }


    public static void main(String[] args) throws IOException {
        CLI cli = new CLI();

        System.out.println(Constants.Logo);
        cli.run();

    }

}



//GH.moveMotherNature


//GAME HANDLER
// game handler che manda a tutti i giocatori waitState e a currentPlayer moveMNState
// waitState = new WaitStateMessage();
// server.sendExcept(currentPlayer,waitState)
// moveMNState = new MoveMNMessage();
// server.send(currentPlayer,moveMNState)


//server.sendExcept(currentPlayer,currentState.states[1])
//server.send(currentPlayer,currentState.states[0])


//SERVER
//for clienthandlers except clienthandler(currentPlayer) :
// clienthandler.sendMessage(message)



//CLIENTHANDLER
// sendMessage(message) -> manda message sulla socket

// IN ATTESA DI UNA RISPOSTA


//ARRIVA UNA RISPOSTA DAL SOCKET DEL CLIENT DENTRO IL THREAD DEL CORRISPONDNETE CLIENTHANDLER

//CLIENTHANDLER
// run()-> deserializza il messaggio e ottiene l'istanza di un Message
// run()-> chiamare controller.handleMessage(message)


//GAME CONTROLLER
// handleMessage(message)-> instanceof(message) e in base a cosa riceve decide quali metodi del model chiamare




//