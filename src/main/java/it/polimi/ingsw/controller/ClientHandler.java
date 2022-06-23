package it.polimi.ingsw.controller;

import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.exceptions.QuitException;
import it.polimi.ingsw.messages.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;


//AGGIUNGERE ACTIVE PER DISATTIVARE I CLIENTHANDLER DEI CLIENT IN WAIT STATE (TANTO PER SICUREZZA)

/**
 * ClientHandler's class handles the communication between a single client and the server by making use of sockets.
 * Serialized messages are exchanged through this class.
 */
public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Server server;
    private int ID; //same id of player
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private GameHandler gameHandler;
    private boolean active;
    private ClientState currentClientState;

    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
    }

    /**
     * It loops continuing to read and handle messages sent by the client: as the loop's break it closes the connection
     * to client and eventually terminates the active match.
     * see @Runnable
     */
    public void run() {
        try {
            // Leggo l'input dal player, lo deserializzo, lo mando a gameHandler, mando la rispost al player
            while (true) {
                Message receivedMessage = (Message) in.readObject();
                readMessage(receivedMessage); }
        }
        catch (QuitException | EOFException | SocketTimeoutException e) //capire perché viene lanciata una EOF exception quando chiudo brutalmente il client
        {
            System.out.println(ID + " si è disconnesso da solo. Chiudo la connessione e chiudo la partita");
            if (gameHandler != null) gameHandler.removeClientHandler(this);
            closeConnection();
            if (gameHandler != null) gameHandler.closeMatch();
        }
        catch (SocketException e) //se chiudo da server la connessione viene lanciata una SocketException
        {
            System.out.println("Qualcuno si è disconnesso chiudo la connessione con il client " + ID);
            //e.printStackTrace(); //for debugging
            if (gameHandler != null) gameHandler.closeMatch();
            //closeConnection();
        }
        catch (ClassNotFoundException | IOException e)
        {
                e.printStackTrace();
        }
    }

    /**
     * Method responsible for the correct dispatch of client's messages: login and game parameters' messages are handled
     * the Server instance, while the others are managed by ClientHandler and GameHandler's instances.
     * @param message: Message instance carrying the information sent by the client.
     */
    public void readMessage(Message message){
        if (message instanceof Ping){
            //System.out.println("Ricevuto ping da " + ID);
        }
        else if (message instanceof LoginRequestMessage) //manda al server, fase di connessione
        {
            System.out.println("ClientHandler: è arrivato un messaggio di loginRequest");
            server.handleMessage(message,this);
        }
        else if (message instanceof GameParametersMessage) //manda al server, fase di connessione
        {
            System.out.println("ClientHandler: è arrivato un messaggio di gameParameters");
            server.handleMessage(message,this);
        }
        else if (message instanceof DisconnectMessage){
            System.out.println("ClientHandler: è arrivato un messaggio di Disconnect");
            throw new QuitException();
        }
        else
            gameHandler.handleMessage(message,this); //attende che gamehandler,gamecontroller e gli altri facciano quello che devono
    }


    /**
     * It sends a serialized message to the client through socket.
     * @param message: Message instance that is being sent to the client.
     */
    public void sendMessage(Message message){
        try{
            System.out.println("Sono CH " + ID + " e sto mandando un messaggio " + (message.getClass().toString()));
            if(message instanceof ClientStateMessage)
                if (((ClientStateMessage) message).getNewState().getOptionalID()==0)
                    currentClientState = ((ClientStateMessage) message).getNewState();
            out.reset();
            out.writeObject(message);
            out.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    //questo metodo va chiamato in caso di termine/crash partita,
    // chiusura inaspettata della connessione lato client, chiusura volontaria lato client (manca messaggio disconnect)

    /**
     * It terminates the server's connection to client by removing its reference from the associations contained in the Server class.
     * Ultimately it closes the communication's endpoint.
     */
    public void closeConnection() {
        System.out.println("ClientHandler "+ ID+ ": tolgo i miei riferimenti dal server e poi chiudo la socket");
        //toglie da tutte le mappe di server questo client, connessioni ecc.
        server.removeClientConnection(this);
        //rivedere se
        try {
            socket.close();
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }
        System.out.println("ClientHandler, closeConnection: chiusa la connessione con " + ID);
    }


    public void setID(int ID){
        this.ID = ID;
    }

    public int getID(){
        return ID;
    }

    public void setGameHandler(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    public GameHandler getGameHandler() {
        return gameHandler;
    }

    public ClientState getCurrentClientState(){
        return currentClientState;
    }
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