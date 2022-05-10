package it.polimi.ingsw.client;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class CLI{
    private final Scanner inputStream;
    private final PrintStream outputStream;
    private boolean active;


    public CLI() {
        inputStream = new Scanner(System.in);
        outputStream = new PrintStream(System.out);
        active = true;
    }

    public void print(){
        //GB.print()

    }

    /*
    @Override
    public void run() {
        outputStream.println("Benvenuto in Eriantys"); //ascii art
        startConnection();

        while (active){
            //String input = inputStream.nextLine();
            //client.executeCurrentState(input);
            client.executeCurrentState();
            //concettualmente non dovrebbe funzionare così, ma intanto proviamo

        }
    }
*/

    //è nella cli/gui che deve avvenire l'error handling degli input (per quanto riguarda la tipizzazione, es: chiedo nickname, mi dai un numero float)
    //nel client invece avviene l'error handling dei messaggi che vengono dal server
    //il problema è che non possiamo mandare indietro a client una serie di parametri di tipi diversi (grazie java).
    //o ritorniamo array object, o creiamo una classe che crea messaggi ad hoc

    public void visualizeCustomMessage(String customMessage){
        outputStream.println(customMessage);
    }
    public String askIP(){
        outputStream.println("Inserisci ip: ");
        outputStream.println(">");
        return inputStream.nextLine();
    }

    public int askPort(){ //ogni metodo di CLI richiede gli input e gestisce gli errori base (tipo scrivo davide come porta per il server)
        boolean validInput = false; //si potrebbe fare la stessa cosa con while(1) e break ma così è più elegante
        int port = 0;
        while(!validInput){
            try{
                outputStream.println("Inserisci port: ");
                outputStream.println(">");
                port = Integer.parseInt(inputStream.nextLine());
                validInput = true;
            }catch(NumberFormatException e){
                outputStream.println("Port dovrebbe essere un numero intero, riprova");
                validInput = false; //si può omettere, lo scrivo per chiarezza
            }
        }
        return port;
    }


    public String askNickname(){
        String nickname = "\n";

        while((nickname.length() < 3)){
                outputStream.println("Inserisci il nickname desiderato (almeno 3 caratteri): ");
                outputStream.println(">");
                nickname = inputStream.nextLine();
        }

        return nickname;
    }


    public ArrayList<Object> askGameParameters(){
        int numberOfPlayers = 0;
        boolean expertGame = false;
        boolean validInput = false; //questo lo mettiamo quando ci sono i try catch, così quando c'è eccezione, ricominciamo il ciclo

        outputStream.println("Inserisci numero di giocatori (2 o 3) e tipologia di partita (expert o base) per avviare il matchmaking!");
        while(!validInput || (numberOfPlayers <= 1 || numberOfPlayers > 3)){
            try{
                outputStream.println("Numero di giocatori: ");
                numberOfPlayers = Integer.parseInt(inputStream.nextLine());
                validInput = true;
            }catch(NumberFormatException e){
                outputStream.println("Il numero di giocatori deve essere un numero intero, riprova");
                validInput = false;
            }
        }

        String expertString = "\n";
        while(!expertString.equalsIgnoreCase("expert") && !expertString.equalsIgnoreCase("base")){
                outputStream.println("Scrivi expert per cercare una partita esperto e base per cercare una partita base: ");
                expertString = inputStream.nextLine();
        }
        expertGame = expertString.equalsIgnoreCase("expert") ? true : false;

        ArrayList<Object> parameters = new ArrayList<>();
        parameters.add(numberOfPlayers);
        parameters.add(expertGame);
        return parameters;
    }

    public void waitInLobby(){
        outputStream.println("La partita sta per cominciare! Attendi gli altri giocatori...");
    }

    public void waitTurn(){
        outputStream.println("Un altro giocatore sta pianificando la sua mossa. Attendi il tuo turno!");
    }

    /*
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
    */
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