package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ClientSocket implements Runnable{
    private static int PING_PERIOD = 5000;
    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
    String ip;
    int port;
    boolean active;
    CLI cli; //andrà sostituita con una classe madre User Interface a breve o con client
    Thread pinger;

    public ClientSocket(String ip,int port, CLI cli) throws IOException, SocketException {
        this.ip = ip;
        this.port = port;
        this.cli = cli;
        socket = new Socket(ip, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }



    @Override
    public void run() {
        //System.out.println("Thread del client socket partito");
        active=true;
        //Sezione pinger
        pinger = new Thread(() ->{
            try {
                //rivedere gestione active
                while(active){
                    Thread.sleep(PING_PERIOD);
                    //System.out.println("mando ping");
                    send(new Ping());
                }
            } catch (InterruptedException | IOException e) {
                //gestione dell'eccezione
            }
        });
        pinger.start();

        try{
            while(active){
                //System.out.println("Qui aspetto messaggi dal server");
                Message receivedMessage = (Message) in.readObject();

                //SE IL MESSAGGIO è DI TIPO ERROR/CLIENTSTATE/ALTRO
                cli.setReceivedMessage(receivedMessage); //gli passi il messaggio (Risvegliandolo in automatico)


                //SE IL MESSAGGIO è DI TIPO UPDATEVIEW
                //aggiorna la view con chiamata a un metodo/propertychange
            }
        }
        catch (IOException | ClassNotFoundException ioException){
            //gestione dell'errore
        } finally {
            //chiusura connessione
        }

    }


    public void send(Message msg) throws IOException {
        out.reset();
        out.writeObject(msg);
        out.flush();
    }


}







/* public void connect(String nickname) throws IOException, ClassNotFoundException, UnknownHostException { //connectionPhase
        LoginRequestMessage nicknameMessage = new LoginRequestMessage(nickname);
        send(nicknameMessage);
        //System.out.println("Ho mandato il nickname!");
        //qui il metodo rimane in attesa finchè non gli arriva response
        //Message response = (Message) in.readObject(); //Deserializza il messaggio del server
        //System.out.println("Risposta ricevuta");
        //return response;
    }

    public void sendGameParameters(ArrayList<Object> parameters) throws IOException, ClassNotFoundException {
        int numberOfPlayers = (int)parameters.get(0);
        boolean expertGame = (boolean)parameters.get(1);
        GameParametersMessage parametersMessage = new GameParametersMessage(numberOfPlayers,expertGame);
        send(parametersMessage);
        //System.out.println("Ho mandato i dati per la lobby");
        //Message response = (Message) in.readObject();
        //System.out.println("Risposta ricevuta!");
        //return response;
    }

    public void sendReadyToPlayConfirmation() throws IOException, ClassNotFoundException {
        Message confirmationMessage = new ClientConfirmationMessage();
        send(confirmationMessage);
        //Message response = (Message) in.readObject();
        //return response;
    }*/