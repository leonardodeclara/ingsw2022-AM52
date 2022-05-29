package it.polimi.ingsw.client;

import it.polimi.ingsw.CLI.GameBoard;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.GUI.UI;
import it.polimi.ingsw.exceptions.EndGameException;
import it.polimi.ingsw.exceptions.QuitException;
import it.polimi.ingsw.messages.*;
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

public class CLI implements Runnable,UI{
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

        try{
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
        //creare eccezione ad hoc
        catch (QuitException e){
            try {
                clientSocket.send(new DisconnectMessage("Chiudo"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println("Ora mi chiudo per quit dell'utente");
        }
        catch (EndGameException e){
            System.out.println("Ora mi chiudo perché la partita è finita e l'utente ha scritto close");
        }
        System.out.println("Qui muore il thread della cli");
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
        inputParser.setIsExpert((Boolean)data.get(1));
    }

    public void updateView(Message updateMessage) {
        if (updateMessage instanceof AvailableWizardMessage)
            updateAvailableWizard((AvailableWizardMessage) updateMessage);
        else if (updateMessage instanceof AvailableTowerMessage)
            updateAvailableTower((AvailableTowerMessage) updateMessage);
        else if (updateMessage instanceof GameInstantiationMessage)
            setInitialGameBoard((GameInstantiationMessage) updateMessage);
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
        else if (updateMessage instanceof LastRoundMessage)
            setLastRound((LastRoundMessage) updateMessage);
        else if (updateMessage instanceof CoinsUpdateMessage)
            updateCoins((CoinsUpdateMessage) updateMessage);
        else if (updateMessage instanceof IslandBanUpdateMessage)
            updateIslandBans((IslandBanUpdateMessage) updateMessage);
        else if (updateMessage instanceof PersonalityUpdateMessage)
            updatePersonalityState((PersonalityUpdateMessage) updateMessage);
        else if (updateMessage instanceof EndGameMessage)
            showEndGameMessage((EndGameMessage) updateMessage);
    }

    public void updateAvailableWizard(AvailableWizardMessage message){
        GB.setAvailableWizards(message.getRemainingWizards());
    }

    public void updateAvailableTower(AvailableTowerMessage message){
        GB.setAvailableTowers(message.getRemainingTowers());
    }

    public void setInitialGameBoard(GameInstantiationMessage message){
        GB.instantiateGameElements(message.getIslands(), message.getBoards(),message.getPersonalities());
        System.out.println("PRIMA PRINT DELLA BOARD");
        GB.print();
    }

    //si potrebbe mettere in questo metodo anche la rimozione delle carta dal deck del giocatore.
    //in questo momento vengono mandati due messaggi di update distinti per deck e per currentAssistantCard
    //mettendo la rimozione della carta in questo metodo AssistantDeckUpdateMessage verrebbe utilizzata solo all'inizio della partita
    //che non è un problema btw
    public void updateCurrentTurnAssistantCards(CurrentTurnAssistantCardsUpdateMessage message){
        GB.setTurnCard( message.getCurrentTurnAssistantCards());
        System.out.println("Modifica delle currentAssistantCards");
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
        outputStream.println("Svuotamento di una nuvola");
        outputStream.println();
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
        GB.setUpdatedClientBoard(boardOwner, message.getClientBoard());
        System.out.println("Aggiornata la board di " + boardOwner);
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
        System.out.println("La carta personaggio non è più attiva");
        System.out.println();
        GB.print();
    }

    public void updateCoins(CoinsUpdateMessage message){
        GB.updateCoins(message.getCoins(), message.getPlayer());
        System.out.println("Sono state scambiate delle monete");
        System.out.println();
        GB.print();
    }

    public void updateIslandBans(IslandBanUpdateMessage message){
        GB.setIslandBans(message.getIslandId(),message.getBanCount());
        outputStream.println("È stato inserito/tolto un ban sull'isola " + message.getIslandId());
        outputStream.println();
        GB.print();
    }

    public void updatePersonalityState(PersonalityUpdateMessage message){
        GB.updateActivePersonality(message.getCardId(),message.getStudents(),message.getBans());
        outputStream.println("È stato aggiornato lo stato di una carta personaggio");
        outputStream.println();
        GB.print();
    }

    public void setLastRound(LastRoundMessage message){
        outputStream.println(message.getLastRoundMessage());
    }

    public void showEndGameMessage(EndGameMessage message){
        if(message.getWinnerName().equals(Constants.TIE))
            outputStream.println("La partita è terminata in pareggio!");
        else
            outputStream.println("Il vincitore è " + message.getWinnerName() + "!");
    }

    private void visualizeContextMessage(){
        ArrayList<String> texts = currentState.getContextMessage(GB);
        for(String text : texts)
            outputStream.println(text);
    }
    private void visualizeServerErrorMessage(){
        ArrayList<String> texts = currentState.getServerErrorMessage();
        for(String text : texts)
            outputStream.println(text);
    }

    private void visualizeInputErrorMessage(){
        ArrayList<String> texts = currentState.getInputErrorMessage();
        for(String text : texts)
            outputStream.println(text);
    }

    public void visualizeCustomMessage(String customMessage){
        outputStream.println(customMessage);
    }


    public String askIP(){
        String ip;
        outputStream.println("Benvenuto!");
        outputStream.println("Inserisci l'indirizzo ip del server: ");
        outputStream.println(">");
        ip = inputStream.nextLine();
        ip = ip.replaceAll("\s","");
        return ip;
    }

    public int askPort(){ //ogni metodo di CLI richiede gli input e gestisce gli errori base (tipo scrivo davide come porta per il server)
        boolean validInput = false; //si potrebbe fare la stessa cosa con while(1) e break ma così è più elegante
        String input;
        int port = 0;
        while(!validInput){
            try{
                outputStream.println("Inserisci la porta del server: ");
                outputStream.println(">");
                input = inputStream.nextLine();
                input = input.replaceAll("\s","");
                port = Integer.parseInt(input);
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