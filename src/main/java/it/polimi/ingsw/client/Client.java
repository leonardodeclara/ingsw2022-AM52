package it.polimi.ingsw.client;

import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.controller.ServerSocketConnection;
import it.polimi.ingsw.messages.ClientState;
import it.polimi.ingsw.messages.ClientStateMessage;
import it.polimi.ingsw.messages.ErrorMessage;
import it.polimi.ingsw.messages.Message;

import java.io.IOException;

public class Client { //gestisce la socket da un lato e dialoga con CLI/GUI dall'altro
    ClientState currentState;
    ClientSocket clientSocket;
    InputParser inputParser;

    public Client() {
        inputParser=new InputParser();
    }

    public boolean instantiateSocket(String ip,int port) throws IOException {
        clientSocket = new ClientSocket(ip,port,this);
        return true;
    }

    public void handleServerMessage(Message message){
        System.out.println("Ho ricevuto un messaggio dal server");
    }

    public boolean connect(String nickname) throws IOException, ClassNotFoundException { //returna boolean in modo da far sapere al chiamante (CLI/GUI) se deve chiedere di nuovo l'input o no
        Message serverResponse = clientSocket.connect(nickname);
        if(serverResponse instanceof ClientStateMessage) {
            System.out.println("\nConnessione avvenuta con successo, "+nickname); //sostituiremo i print con metodi di GUI/CLI
            ClientStateMessage newStateMessage = (ClientStateMessage) serverResponse;
            currentState = newStateMessage.getNewState(); //switch del client al prossimo stato
            System.out.println("il mio stato attuale è " + currentState.toString());
            //clientSocket.run();
            //faccio partire il thread che gestisce la connessione server-client, non so dove farlo partire
            //se lo eseguo qui poi non termina l'esecuzione del metodo (why?)

            return true;
        }
        else if(serverResponse instanceof ErrorMessage) {
            System.out.println("\nIl nome selezionato non è disponibile, riprova");
            return false;
        }
        return false; //mai raggiunto, è per forza error o client state
    }

    public void executeCurrentState(){ //andrebbe aggiunto un listener così quando lo stato cambia viene in automatico chiamato questo
        System.out.println("Qui eseguo azioni in base allo stato");
        //String[] inputTokens = playerInput.split(" ");
        switch(currentState){
            case INSERT_NEW_GAME_PARAMETERS: //prima viene richiesto al giocatore l'input mediante CLI/GUI e poi lo si inserisce nel metodo previsto
                //questa parte non va gestita con try catch molto probabilmente
                //try{
                //    send(inputParser.parseGameParameters(inputTokens));
                //}
                //catch (IOException e){
                //    System.out.println("qualcosa è andato storto");
                //}

                //qui in base allo stato dovrebbe essere comunicato un messaggio al client -> input ->
                //input viene processato e scatta il messaggio al server

                System.out.println("Adesso procedo come previsto dallo stato INSERT_NEW_GAME_PARAMETERS");
                try{
                    insertNewGameParameters(3, true);
                }
                catch (IOException | ClassNotFoundException e){
                    //gestire in qualche modo
                }


        }

    }


    public boolean insertNewGameParameters(int numberPlayers,boolean expertGame) throws IOException, ClassNotFoundException { //returna boolean in modo da far sapere al chiamante (CLI/GUI) se deve chiedere di nuovo l'input o no
        Message serverResponse = clientSocket.sendGameParameters(numberPlayers,expertGame);
        if(serverResponse instanceof ClientStateMessage){
            ClientStateMessage newStateMessage = (ClientStateMessage) serverResponse;
            currentState = newStateMessage.getNewState(); //switch del client al prossimo stato
        }else if(serverResponse instanceof ErrorMessage) {
            System.out.println("\nI dati inseriti non sono corretti");
            return false;
        }
        return false;  //mai raggiunto, è per forza error o client state
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
}




//send dei messaggi vanno gestiti separatamente dai metodi che si occupano dell'elaborazione








