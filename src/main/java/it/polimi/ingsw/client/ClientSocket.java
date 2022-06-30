package it.polimi.ingsw.client;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.GUI.UI;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.ServerMessages.Ping;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Class ClientSocket handles client communication with server
 * It receives messages from the server and passes it to higher level classes (CLI or GUI)
 * and sends messages received from the above-mentioned higher level classes
 */
public class ClientSocket implements Runnable{
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private String ip;
    private int port;
    private boolean active;
    private boolean IsClientGUI;
    private final UI ui;
    private final Thread pinger;


    /**
     * Method ClientSocket sets a socket variable given ip and port
     * It also sets a ping system to handle disconnections
     * @param ip is socket ip as string
     * @param port is socket port
     * @param ui refers to higher level client class that instantiated this ClientSocket instance
     */
    public ClientSocket(String ip,int port, UI ui) throws IOException, SocketException {
        this.ip = ip;
        this.port = port;
        this.ui = ui;
        socket = new Socket(ip, port);
        socket.setSoTimeout(Constants.TIMEOUT);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        active= true;
        pinger = new Thread(() ->{
            try {
                while(active){
                    Thread.sleep(Constants.PING_PERIOD);
                    send(new Ping());
                }
            } catch (InterruptedException | IOException e) {
                System.out.println("Morto il thread del ping");
                ui.handleClosingServer();
            }
        });
    }

    /**
     * Method run starts the ping system and while is active passes incoming messages
     * to ui class
     *If runtime exception is caught, closes connection and calls ui for disconnection handling
     */
    @Override
    public void run() {
        active=true;
        pinger.start();

        try{
            while(active){
                Message receivedMessage = (Message) in.readObject();
                if (!(receivedMessage instanceof Ping))
                    ui.handleMessageFromServer(receivedMessage);
            }
        }
        catch(SocketTimeoutException | EOFException | SocketException exception ){
            ui.handleClosingServer();
        } catch (IOException | ClassNotFoundException ignored){
        } finally {
            closeConnection();

        }
    }

    /**
     * Method send sends an incoming message instance to the server
     * @param msg is the message to send
     */
    public synchronized void send(Message msg) throws IOException {
        out.reset();
        out.writeObject(msg);
        out.flush();
    }

    /**
     * Method closeConnection closes input stream,output stream and socket
     */
    private void closeConnection(){
        try{
            in.close();
            out.close();
            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
