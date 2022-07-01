package it.polimi.ingsw.controller;

import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.exceptions.QuitException;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.ClientMessages.GameParametersMessage;
import it.polimi.ingsw.messages.ClientMessages.LoginRequestMessage;
import it.polimi.ingsw.messages.ServerMessages.ClientStateMessage;
import it.polimi.ingsw.messages.ServerMessages.DisconnectMessage;
import it.polimi.ingsw.messages.ServerMessages.Ping;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * ClientHandler's class handles the communication between a single client and the server by making use of sockets.
 * Serialized messages are exchanged through this class.
 */
public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Server server;
    private int ID;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private GameHandler gameHandler;
    private boolean active;
    private ClientState currentClientState;

    /**
     * Constructor ClientHandler creates a new ClientHandler instance.
     * @param socket: Socket instance through which the server is connected to the client.
     * @param server: Server instance holding information about the clients and the server state.
     * @throws IOException
     */
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
            while (true) {
                Message receivedMessage = (Message) in.readObject();
                readMessage(receivedMessage); }
        }
        catch (QuitException | EOFException | SocketTimeoutException e)
        {
            System.out.println("Client "+ID + " has disconnected. Closing connection..");
            if (gameHandler != null) gameHandler.removeClientHandler(this);
            closeConnection();
            if (gameHandler != null) gameHandler.closeMatch();
        }
        catch (SocketException e)
        {
            System.out.println("Closing connection with Client " + ID);
            if (gameHandler != null) gameHandler.closeMatch();
        }
        catch (ClassNotFoundException | IOException e)
        {
            System.out.println("Closing thread");
        }
    }

    /**
     * Method responsible for the correct dispatch of client's messages: login and game parameters' messages are handled
     * the Server instance, while the others are managed by ClientHandler and GameHandler's instances.
     * @param message: Message instance carrying the information sent by the client.
     */
    public void readMessage(Message message){
        if (message instanceof Ping){
            sendMessage(new Ping());
        }
        else if (message instanceof LoginRequestMessage)
        {
            server.handleMessage(message,this);
        }
        else if (message instanceof GameParametersMessage)
        {
            server.handleMessage(message,this);
        }
        else if (message instanceof DisconnectMessage){
            throw new QuitException();
        }
        else
            gameHandler.handleMessage(message,this);
    }


    /**
     * It sends a serialized message to the client through socket.
     * @param message: Message instance that is being sent to the client.
     */
    public synchronized void sendMessage(Message message){
        try{
            if(message instanceof ClientStateMessage)
                if (((ClientStateMessage) message).getNewState().getOptionalID()==0)
                    currentClientState = ((ClientStateMessage) message).getNewState();
            out.reset();
            out.writeObject(message);
            out.flush();
        }
        catch (IOException e){
            System.out.println("Connection reset by peer. No reset needed");
        }
    }

    /**
     * It terminates the server's connection to client by removing its reference from the associations contained in the Server class.
     * Ultimately it closes the communication's endpoint.
     */
    public void closeConnection() {
        server.removeClientConnection(this);
        try {
            socket.close();
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }
    }


    /**
     * Method setID sets ClientHandler's identification number.
     * @param ID: unique number identification for the ClientHandler.
     */
    public void setID(int ID){
        this.ID = ID;
    }

    /**
     * Method getID returns ClientHandler's identification number.
     * @return ID, the unique number identification for the ClientHandler.
     */
    public int getID(){
        return ID;
    }

    /**
     * Method setGameHandler sets ClientHandler's reference to a GameHandler instance.
     * @param gameHandler: GameHandler instance managing the application of client's moves.
     */
    public void setGameHandler(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    /**
     * Method getGameHandler returns ClientHandler's reference to a GameHandler instance.
     * @return GameHandler instance managing the application of client's moves.
     */
    public GameHandler getGameHandler() {
        return gameHandler;
    }

    /**
     * Method getCurrentClientState returns the game state in which the client is currently set to.
     * @return ClientState instance identifying the client's game status.
     */
    public ClientState getCurrentClientState(){
        return currentClientState;
    }
}