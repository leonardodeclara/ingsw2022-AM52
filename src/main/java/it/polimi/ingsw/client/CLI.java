package it.polimi.ingsw.client;

import it.polimi.ingsw.CLI.GameBoard;
import it.polimi.ingsw.messages.*;

import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class CLI implements Runnable{
    private final Scanner inputStream;
    private final PrintStream outputStream;
    private boolean active;
    Client client;
    ClientSocket clientSocket;
    ClientState currentState;
    InputParser inputParser;
    ArrayList<Object> playerInput;
    private volatile Message receivedMessage;
    final ScheduledExecutorService executorService;
    GameBoard GB;

    /*
    TODO:
    controllare se mandare più messaggi di fila funziona. Se funziona potrebbero essere usati per arricchire un po' alcune fasi di gioco preliminari
    (messaggi dal server ad esempio o informazioni utili come il nome degli altri giocatori )
     */
    public CLI() {
        inputStream = new Scanner(System.in);
        outputStream = new PrintStream(System.out);
        client = new Client();
        receivedMessage = null;
        currentState = ClientState.CONNECT_STATE;
        inputParser = new InputParser();
        executorService = Executors.newSingleThreadScheduledExecutor();
        GB = new GameBoard();
    }


    public void run(){
        try {
            instantiateSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }

        active = true;

        while(active){
                receivedMessage = null;
                if(!currentState.equals(ClientState.WAIT_IN_LOBBY) && !currentState.equals(ClientState.WAIT_TURN)){
                    visualizeContextMessage();
                    playerInput = inputParser.parse(inputStream.nextLine(),currentState);

                    if(currentState.equals(ClientState.INSERT_NEW_GAME_PARAMETERS)) //comunichiamo alla view i parametri expertGame e numberOfPlayers così non deve darceli il server
                        prepareView(playerInput);


                    if(playerInput.size() > 0){
                        Message messageToSend = client.buildMessageFromPlayerInput(playerInput,currentState);
                        try {
                            clientSocket.send(messageToSend);
                            waitForResponse();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        handleMessageFromServer(receivedMessage);
                    }
                    else{
                        visualizeErrorMessage();
                    }
                }
                else{ //printa ogni 5 secondi il messaggio di attesa
                    //executorService.scheduleWithFixedDelay(this::visualizeContextMessage, 0, 5, TimeUnit.SECONDS);
                }
            }
        }

    private void waitForResponse(){ //come wait() ma più semplice da gestire e comprendere
        while (receivedMessage == null) {
        }

    }

    public void setReceivedMessage(Message message){ //usato da clientSocket per passare la risposta asincrona del server alla CLI
        receivedMessage = message;
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



    private void handleMessageFromServer(Message receivedMessage){
        if(receivedMessage instanceof ClientStateMessage){
            currentState = ((ClientStateMessage) receivedMessage).getNewState();
        }
        if(receivedMessage instanceof ErrorMessage){
            visualizeErrorMessage();
        }

        //if(message instanceof updateViewMessage){
            //gameBoard.update(); //aggiorna la view
            //gameBoard.print(); //la ristampa a schermo
        //}

    }

    public void print(){
        //GB.print()
    }

    public void prepareView(ArrayList<Object> data){
        GB.setNumberOfPlayers((Integer)data.get(0));
        GB.setExpertGame((Boolean)data.get(1));
    }

    private void visualizeContextMessage(){
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
            case SET_UP_PHASE:
                outputStream.println("Inserisci il deck che vuoi utilizzare");
                outputStream.println("Deck disponibili:"+GB.getAvailableWizards()); //prendiamo dalla view le informazioni da stampare a schermo
                break;

        }

    }
    private void visualizeErrorMessage(){
        switch (currentState){
            case CONNECT_STATE:
                outputStream.println("Il nickname scelto non è disponibile! Scegline un'altro");

        }
    }

    public void visualizeCustomMessage(String customMessage){
        outputStream.println(customMessage);
    }


    public String askIP(){
        outputStream.println("Inserisci ip: ");
        outputStream.println(">");
        return inputStream.nextLine();
    }

    public int askPort(){ //ogni metodo di CLI richiede gli input e gestisce gli errori base (tipo scrivo davide come porta per il server)
        boolean validInput = false; //si potrebbe fare la stessa cosa con while(1) e break ma così è più elegante
        int port = 0;
        while(!validInput){
            try{
                outputStream.println("Inserisci port: ");
                outputStream.println(">");
                port = Integer.parseInt(inputStream.nextLine());
                validInput = true;
            }catch(NumberFormatException e){
                outputStream.println("Port dovrebbe essere un numero intero, riprova");
                validInput = false; //si può omettere, lo scrivo per chiarezza
            }
        }
        return port;
    }


    public static void main(String[] args) throws IOException {
        CLI cli = new CLI(); //partirà su questo thread
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