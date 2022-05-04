package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.*;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientSocket implements Runnable{
    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
    String ip;
    int port;
    boolean active;
    Client client;

    public ClientSocket(String ip,int port, Client client) throws IOException {
        this.ip = ip;
        this.port = port;
        this.client = client;
        socket = new Socket(ip, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public Message connect(String nickname) throws IOException, ClassNotFoundException, UnknownHostException { //connectionPhase
        LoginRequestMessage nicknameMessage = new LoginRequestMessage(nickname);
        send(nicknameMessage);
        System.out.println("Ho mandato il nickname!");
        //qui il metodo rimane in attesa finchè non gli arriva response
        Message response = (Message) in.readObject(); //Deserializza il messaggio del server
        System.out.println("Risposta ricevuta");
        return response;
    }

    public Message sendGameParameters(int numberPlayers,boolean expertGame) throws IOException, ClassNotFoundException {
        GameParametersMessage parametersMessage = new GameParametersMessage(numberPlayers,expertGame);
        send(parametersMessage);
        System.out.println("Ho mandato i dati per la lobby");
        Message response = (Message) in.readObject();
        System.out.println("Risposta ricevuta!");
        return response;
    }

    public void send(Message msg) throws IOException {
        out.reset();
        out.writeObject(msg);
        out.flush();
    }


    @Override
    public void run() {
        System.out.println("Thread del client socket partito");
        active=true;
        try{
            while(active){
                System.out.println("Qui aspetto messaggi dal server");
                Message receivedMessage = (Message) in.readObject();
                client.handleServerMessage(receivedMessage);
            }
        }
        catch (IOException ioException){
            //gestione dell'errore
        }
        catch (ClassNotFoundException classNotFoundException){
            //gestione dell'errore
        }
        finally {
            //chiusura connessione
        }

    }
}