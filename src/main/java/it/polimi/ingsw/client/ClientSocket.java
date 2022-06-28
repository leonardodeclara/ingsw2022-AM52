package it.polimi.ingsw.client;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.GUI.UI;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.ServerMessages.Ping;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ClientSocket implements Runnable{
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String ip;
    private int port;
    private boolean active;
    private boolean IsClientGUI;
    private UI cli;
    private Thread pinger;

    public ClientSocket(String ip,int port, UI cli) throws IOException, SocketException {
        this.ip = ip;
        this.port = port;
        this.cli = cli;
        socket = new Socket(ip, port);
        socket.setSoTimeout(Constants.TIMEOUT);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        active= true;
        pinger = new Thread(() ->{
            try {
                //rivedere gestione active
                while(active){
                    Thread.sleep(Constants.PING_PERIOD);
                    //System.out.println("mando ping");
                    send(new Ping());
                }
            } catch (InterruptedException | IOException e) {
                System.out.println("Morto il thread del ping");
            }
        });
    }

    /**
     * TODO: per ora la fine partita è gestita con la chiusura di tutto lato server, che manda un messaggio di tipo Disconnect.
     * Bisogna gestirlo opportunamente e far chiudere in qualche modo la cli/gui perché per ora bisogna scrivere comunque quit
     */

    @Override
    public void run() {
        //System.out.println("Thread del client socket partito");
        active=true;
        //Sezione pinger

        pinger.start();

        try{
            while(active){
                Message receivedMessage = (Message) in.readObject();
                System.out.println("Ho ricevuto lato client "+receivedMessage);
                if (!(receivedMessage instanceof Ping))
                    cli.handleMessageFromServer(receivedMessage);
            }
        }
        catch(SocketTimeoutException | EOFException exception ){
            System.out.println("ClientSocket: catchata eccezione "+exception.getClass().toString());
            cli.handleClosingServer();
        }
        catch (IOException | ClassNotFoundException exception){
            System.out.println("ClientSocket: catchata eccezione "+exception.getClass().toString());
        } finally {
            closeConnection();

        }
    }

    public synchronized void send(Message msg) throws IOException {
        out.reset();
        out.writeObject(msg);
        out.flush();
        if (!(msg instanceof  Ping) )
            System.out.println("Ho mandato un messaggio");
    }

    private void closeConnection(){
        System.out.println("Chiudo il thread del clientSocket");
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
