package it.polimi.ingsw.client;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class CLI implements Runnable{
    private final Scanner inputStream;
    private final PrintStream outputStream;
    private final Client client;

    public CLI() {
        inputStream = new Scanner(System.in);
        outputStream = new PrintStream(System.out);
        client = new Client();
    }

    @Override
    public void run() {
        outputStream.println("Benvenuto nel gioco");
        startConnection();
        client.executeCurrentState();
        while (true){}
    }

    public void startConnection() {
        boolean connectionResult=false;
        String ip;
        int port;
        do{
            try{
                outputStream.println("Inserisci ip: ");
                outputStream.println(">");
                ip = inputStream.nextLine();
                outputStream.println("Inserisci port: ");
                outputStream.println(">");
                port = Integer.parseInt(inputStream.nextLine()); //se scrivo \n salta tutto
                connectionResult= client.instantiateSocket(ip, port);
            }
            catch (IOException e){
                outputStream.println("Non è stato possibile instaurare una connessione. Reinserire ip e port");
            }
        } while(!connectionResult);

        try{
            outputStream.println("Inserisci il nickname desiderato: ");
            outputStream.println(">");
            do{
                String input = inputStream.nextLine();
                connectionResult = client.connect(input);
            } while (!connectionResult);
        }
        catch (IOException | ClassNotFoundException e){
            outputStream.println("Errore");
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException { //simula CLI
        CLI cli = new CLI();
        cli.run();
    }
}



//GH.moveMotherNature


//GAME HANDLER
// game handler che manda a tutti i giocatori waitState e a currentPlayer moveMNState
// waitState = new WaitStateMessage();
// server.sendExcept(currentPlayer,waitState)
// moveMNState = new MoveMNMessage();
// server.send(currentPlayer,moveMNState)


//server.sendExcept(currentPlayer,currentState.states[1])
//server.send(currentPlayer,currentState.states[0])


//SERVER
//for clienthandlers except clienthandler(currentPlayer) :
// clienthandler.sendMessage(message)



//CLIENTHANDLER
// sendMessage(message) -> manda message sulla socket

// IN ATTESA DI UNA RISPOSTA


//ARRIVA UNA RISPOSTA DAL SOCKET DEL CLIENT DENTRO IL THREAD DEL CORRISPONDNETE CLIENTHANDLER

//CLIENTHANDLER
// run()-> deserializza il messaggio e ottiene l'istanza di un Message
// run()-> chiamare controller.handleMessage(message)


//GAME CONTROLLER
// handleMessage(message)-> instanceof(message) e in base a cosa riceve decide quali metodi del model chiamare




//