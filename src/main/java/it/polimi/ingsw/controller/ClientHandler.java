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

    public int getID(){
        return ID;
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
        System.out.println("Messaggio in arrivo dal client: "+ID);
        if (message instanceof LoginRequestMessage) //manda al server, fase di connessione
        {
            System.out.println("è arrivato un messaggio di loginRequest");
            server.handleMessage(message,this);
        }
        else if (message instanceof GameParametersMessage) //manda al server, fase di connessione
        {
            System.out.println("è arrivato un messaggio di gameParameters");
            server.handleMessage(message,this);
        }
        else
            gameHandler.handleMessage(message,this); //attende che gamehandler,gamecontroller e gli altri facciano quello che devono
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