package it.polimi.ingsw.client;

import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.controller.ServerSocketConnection;
import it.polimi.ingsw.messages.ClientState;
import it.polimi.ingsw.messages.ClientStateMessage;
import it.polimi.ingsw.messages.ErrorMessage;
import it.polimi.ingsw.messages.Message;

import java.io.IOException;
import java.util.Scanner;

public class Client {
    ClientState currentState;
    ClientSocket clientSocket;

    public Client() {
    }

    public void instantiateSocket(String ip,int port) {
        clientSocket = new ClientSocket(ip,port);
    }

    public boolean connect(String nickname) throws IOException, ClassNotFoundException {
        Message serverResponse = clientSocket.connect(nickname);
        if(serverResponse instanceof ClientStateMessage) {
            System.out.println("\nConnessione avvenuta con successo,"+nickname); //sostituiremo i print con metodi di GUI/CLI
            ClientStateMessage newStateMessage = (ClientStateMessage) serverResponse;
            currentState = newStateMessage.getNewState(); //switch del client al prossimo stato
            return true;
        }
        else if(serverResponse instanceof ErrorMessage) {
            System.out.println("\nIl nome selezionato non è disponibile");
            return false;
        }
        return false; //mai raggiunto, è per forza error o client state
    }

    public void executeCurrentState(){
        switch(currentState){
            case INSERT_NEW_GAME_PARAMETERS:


        }

    }



    public ClientState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(ClientState currentState) {
        this.currentState = currentState;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Client client = new Client();
        client.instantiateSocket("127.0.0.1",1234);

        String nickname = "Frizio";
        client.connect(nickname);

        nickname = "Leoviatano";
        client.connect(nickname);



    }
}













