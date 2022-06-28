package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.ClientMessageBuilder;
import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.client.GUI.UI;
import it.polimi.ingsw.client.InputParser;
import it.polimi.ingsw.exceptions.EndGameException;
import it.polimi.ingsw.exceptions.QuitException;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.ServerMessages.ClientStateMessage;
import it.polimi.ingsw.messages.ServerMessages.DisconnectMessage;
import it.polimi.ingsw.messages.ServerMessages.ErrorMessage;

import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


/**
 * Class CLI represents the main class for the Client Command Line Interface component.
 * It handles messages coming from ClientSocket, parses input text from player and
 * prints context and error messages accordingly to the client current state
 */
public class CLI implements Runnable,UI{
    private final Scanner inputStream;
    private final PrintStream outputStream;
    private boolean active;
    private ClientMessageBuilder clientMessageBuilder;
    private ClientSocket clientSocket;
    private ClientState currentState;
    private InputParser inputParser;
    private ArrayList<Object> playerInput;
    private final ScheduledExecutorService executorService;
    private GameBoard GB;


    public CLI() {
        inputStream = new Scanner(System.in);
        outputStream = new PrintStream(System.out);
        clientMessageBuilder = new ClientMessageBuilder(this);
        executorService = Executors.newSingleThreadScheduledExecutor();
        GB = new GameBoard(outputStream);
        inputParser = new InputParser();
        inputStream.useDelimiter("\n");
    }

    /**
     * Method setNextState set the current client state to a new one
     * given as parameter. After that it prints the new context message
     * @param newState is the new state
     */
    public void setNextState(ClientState newState){
        currentState = newState;
        visualizeContextMessage();
    }


    /**
     * Method run instantiates the client socket and
     * while it's active parses input text from player
     * If input is valid, a message is built accordingly to client state
     * and passed to client socket to be sent to the server
     */
    public void run(){
        try {
            instantiateSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }

        active = true;
        setNextState(ClientState.CONNECT_STATE);

        try{
            while(active) {
                if (inputStream.hasNext()) {
                    playerInput = inputParser.parse(inputStream.nextLine(), currentState);
                    if (playerInput.size() > 0) {
                        Message messageToSend = clientMessageBuilder.buildMessageFromPlayerInput(playerInput, currentState);
                        try {
                            clientSocket.send(messageToSend);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        visualizeInputErrorMessage();
                        visualizeContextMessage();
                    }
                }
            }
        }
        catch (QuitException e){
            try {
                clientSocket.send(new DisconnectMessage("Chiudo"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        catch (EndGameException e){
            System.out.println("Game is over. Closing...");
        }
    }

    /**
     * Method handleMessageFromServer receives a Message instance from ClientSocket and react accordingly to the message type.
     * @param receivedMessage received from server.
     * If the message is instance of ClientStateMessage, handleMessageFromServer changes the current client state
     * If the message is instance of ErrorMessage, handleMessageFromServer prints the error message accordingly to the current client state
     * If the message is instance of UpdateMessage, handleMessageFromServer updates the GameBoard instance
     */
    public void handleMessageFromServer(Message receivedMessage){
        if(receivedMessage instanceof ClientStateMessage){
            if(currentState.equals(ClientState.CONNECT_STATE)){
                GB.setNickname(inputParser.getNickname());
            }
            setNextState(((ClientStateMessage) receivedMessage).getNewState());
        }else if(receivedMessage instanceof ErrorMessage) {
            visualizeServerErrorMessage();
        }else{
            updateView(receivedMessage);
        }

    }

    /**
     * Method instantiateSocket asks player for IP and port and tries to instantiate a client socket
     * on a new thread with the input parameters. If connection fails an error message is print
     */
    public void instantiateSocket() throws IOException {
        boolean connectionAccepted = false;
        while(!connectionAccepted){
            try{
                String ip = askIP();
                int port = askPort();
                clientSocket = new ClientSocket(ip,port,this);
                Thread socketThread = new Thread(clientSocket);
                socketThread.start();
                connectionAccepted = true;
            }catch(UnknownHostException |SocketException e){
                visualizeCustomMessage("Connessione fallita. Scegli un altro server o riprova più tardi");
                connectionAccepted = false;
            }
        }
    }

    /**
     * Method prepareView initializes GameBoard instance with game parameters
     * @param data contains game parameters
     */
    public void prepareView(ArrayList<Object> data){
        GB.setNumberOfPlayers((Integer)data.get(0));
        GB.setExpertGame((Boolean)data.get(1));
        inputParser.setIsExpert((Boolean)data.get(1));
    }

    /**
     * Method updateView updates GameBoard using contents of a message passed as parameter
     * @param updateMessage is the message which contains contents needed for the update
     */
    public void updateView(Message updateMessage) {
        ((UpdateMessage) updateMessage).update(GB);
    }

    /**
     * Method visualizeContextMessage prints a context message accordingly to the current client state
     */
    private void visualizeContextMessage(){
        ArrayList<String> texts = currentState.getCLIContextMessage(GB);
        for(String text : texts)
            outputStream.println(text);
    }

    /**
     * Method visualizeServerErrorMessage prints a server error message accordingly to the current client state
     */
    private void visualizeServerErrorMessage(){
        ArrayList<String> texts = currentState.getServerErrorMessage();
        for(String text : texts)
            outputStream.println(text);
    }

    /**
     * Method visualizeInputErrorMessage prints an input error message accordingly to the current client state
     */
    private void visualizeInputErrorMessage(){
        ArrayList<String> texts = currentState.getInputErrorMessage();
        for(String text : texts)
            outputStream.println(text);
    }

    /**
     * Method visualizeCustomMessage prints a custom message accordingly to parameter string
     * @param customMessage  is the message to print on the output stream
     */
    public void visualizeCustomMessage(String customMessage){
        outputStream.println(customMessage);
    }


    /**
     * Method askIP asks the player for the server IP
     * @return ip as a string
     */
    public String askIP(){
        String ip;
        outputStream.println("Benvenuto!");
        outputStream.println("Inserisci l'indirizzo ip del server: ");
        outputStream.println(">");
        ip = inputStream.nextLine();
        ip = ip.replaceAll("\s","");
        return ip;
    }

    /**
     * Method askPort asks the player for the server port
     * @return port as a string
     */
    public int askPort(){
        boolean validInput = false;
        String input;
        int port = 0;
        while(!validInput){
            try{
                outputStream.println("Inserisci la porta del server: ");
                outputStream.println(">");
                input = inputStream.nextLine();
                input = input.replaceAll("\s","");
                port = Integer.parseInt(input);
                validInput = true;
            }catch(NumberFormatException e){
                outputStream.println("La porta dovrebbe essere un numero intero, riprova");
                validInput = false;
            }
        }
        return port;
    }

    /**
     * Method handleClosingServer handles server crash by printing a message
     * and closing the JVM
     */
    @Override
    public void handleClosingServer() {
        System.out.println("Server connection timed out. Closing...");
        System.exit(0);
    }

    public static void main(String[] args) {
        CLI cli = new CLI();

        System.out.println(Constants.Logo);
        cli.run();

    }

}
