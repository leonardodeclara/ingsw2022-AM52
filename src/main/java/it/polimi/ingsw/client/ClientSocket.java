package it.polimi.ingsw.client;

import it.polimi.ingsw.GUI.UI;
import it.polimi.ingsw.messages.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import it.polimi.ingsw.CLI.ClientIsland;

public class ClientSocket implements Runnable{
    private static int PING_PERIOD = 5000;
    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
    String ip;
    int port;
    boolean active;
    boolean IsClientGUI;
    UI cli;
    Thread pinger;

    public ClientSocket(String ip,int port, UI cli) throws IOException, SocketException {
        this.ip = ip;
        this.port = port;
        this.cli = cli;
        socket = new Socket(ip, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        active= true;
        pinger = new Thread(() ->{
            try {
                //rivedere gestione active
                while(active){
                    Thread.sleep(PING_PERIOD);
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
                cli.handleMessageFromServer(receivedMessage);
            }
        }
        catch (IOException | ClassNotFoundException ioException){
            //gestione dell'errore
        } finally {
            closeConnection();
            //System.exit(0);
        }
    }

    public void send(Message msg) throws IOException {

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
