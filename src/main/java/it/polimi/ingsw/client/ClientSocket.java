package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.*;

import java.io.*;
import java.net.Socket;

public class ClientSocket {
    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
    String ip;
    int port;
    public ClientSocket(String ip,int port){
        this.ip = ip;
        this.port = port;
    }

    public Message connect(String nickname) throws IOException, ClassNotFoundException { //connectionPhase
        socket = new Socket(ip,port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
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
        out.writeObject(msg);
    }




}