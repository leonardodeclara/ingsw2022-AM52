package it.polimi.ingsw.controller;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.Game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

//AGGIUNGERE ACTIVE PER DISATTIVARE I CLIENTHANDLER DEI CLIENT IN WAIT STATE (TANTO PER SICUREZZA)

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Server server;
    private int ID; //same id of player
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private GameHandler gameHandler;
    private Message responseMessage;
    private boolean active;


    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
    }

    public void setID(int ID){
        this.ID = ID;
    }

    public void run() {
        try {
            // Leggo l'input dal player, lo deserializzo, lo mando a gameHandler, mando la rispost al player
            while (true) {
                Message receivedMessage = (Message) in.readObject();
                readMessage(receivedMessage);

                //invece di salvare un attributo responseMessage si potrebbe gestire la scrittura della risposta all'interno della
                //catena di metodi che vengono chiamati
                //out.writeObject(responseMessage); //infine manda fuori la risposta del server
                }
            } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public void readMessage(Message message){
        if (message instanceof LoginRequestMessage)
            handleLogin((LoginRequestMessage) message);
        else if (message instanceof GameParametersMessage)
            handleGameParameters((GameParametersMessage) message);
        else
            gameHandler.handleMessage(message,ID); //attende che gamehandler,gamecontroller e gli altri facciano quello che devono
    }

    public void handleLogin(LoginRequestMessage message ){
        String nickname = ((LoginRequestMessage) message).getPlayerNickname();

        //questo controllo deve essere a livello server perché in teoria il nome deve essere univoco a livello server, non a livello partita
        if(server.isNicknameAvailable(nickname)){
            server.registerPlayer(nickname);
            server.registerClientConnection(nickname, this);
            //sendMessage(new LoginReplyMessage(nickname));
            sendMessage(new ClientStateMessage(ClientState.INSERT_NEW_GAME_PARAMETERS));

        }else{
            ErrorMessage error = new ErrorMessage(ErrorKind.INVALID_NICKNAME);
            sendMessage(error);
        }
    }


    public void handleGameParameters(GameParametersMessage message){
        boolean expertGame = message.isExpertGame();
        int numberOfPlayers = message.getNumberPlayers();
        if (numberOfPlayers< Constants.MIN_NUMBER_OF_PLAYERS || numberOfPlayers>Constants.MAX_NUMBER_OF_PLAYERS){
            sendMessage(new ErrorMessage(ErrorKind.INVALID_INPUT));
            sendMessage(new ClientStateMessage(ClientState.INSERT_NEW_GAME_PARAMETERS)); //non so se serve
            return;
        }
        if(server.joinLobby(ID,numberOfPlayers,expertGame)){ //c'è una lobby e il gioco sta per partire
            gameHandler.startGame();
        } else { //lobby appena creata/lobby già esistente ma non abbastanza players
            sendMessage(new ClientStateMessage(ClientState.WAIT_IN_LOBBY));
        }
    }

    public void sendMessage(Message message){
        try{
            out.writeObject(message);}
        catch (IOException e){
            //chiudo la connessione.
        }
    }

    public void setGameHandler(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    //public void sendTo(Message message){
    //    responseMessage = message;
    //}
}


/*
i client handler sono collegati singolarmente alla classe Game_Handler.
La classe game_handler ha un metodo handleMessage(msg,playerID) che viene chiamato da ClientHandler.
Questo metodo ha uno switch instanceOf che chiama metodi differenti in base al tipo di messaggio
I metodi si chiamano handleMoveStudentMessage, handleConnectionRequest ecc...
La risposta del server viene scritta in un attributo di clienthandler, che la legge e la manda al client
Per i metodi di gioco che richiedono aggiornamenti del model, viene chiamato GC.updateModel(MoveStudent message)
GameController a sua volta ha un instanceof e chiama il metodo di GM in base al tipo di messaggio ricevuto
Se riceve errore



 */