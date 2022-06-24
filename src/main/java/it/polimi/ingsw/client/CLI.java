package it.polimi.ingsw.client;

import it.polimi.ingsw.CLI.GameBoard;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.GUI.UI;
import it.polimi.ingsw.exceptions.EndGameException;
import it.polimi.ingsw.exceptions.QuitException;
import it.polimi.ingsw.messages.*;

import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
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


    public CLI() {
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
        ((UpdateMessage) updateMessage).update(GB);
    }


    private void visualizeContextMessage(){
        ArrayList<String> texts = currentState.getCLIContextMessage(GB);
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

    @Override
    public void handleClosingServer() {
        System.out.println("Il server è crashato");
        System.exit(0);
    }

    public static void main(String[] args) {
        CLI cli = new CLI();

        System.out.println(Constants.Logo);
        cli.run();

    }

}
