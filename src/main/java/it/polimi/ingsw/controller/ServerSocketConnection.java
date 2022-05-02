package it.polimi.ingsw.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerSocketConnection implements Runnable {
    private final int port;
    private final ExecutorService executorService;
    private final Server server;
    private final GameHandler gameHandler;
    ArrayList<ClientHandler> clientHandlers;

    /**
     * Constructor SocketServer creates a new SocketServer instance.
     *
     * @param port of type int - the port on which server will listen.
     * @param server of type Server - the main server object.
     */
    public ServerSocketConnection(int port, Server server,GameHandler gameHandler) {
        this.server = server;
        this.port = port;
        this.gameHandler = gameHandler;
        clientHandlers = new ArrayList<ClientHandler>();
        executorService = Executors.newCachedThreadPool();
    }

    /**
     * Method acceptConnections handles the process of accepting connections from clients and is responsible for the
     * creations of new threads, one for each connection. Each thread is kept alive until client disconnection.
     *
     * @param serverSocket of type ServerSocket - the server socket, which accepts connections.
     */
    public void acceptClientConnections(ServerSocket serverSocket) {
        while (true) {
            try {
                System.out.println("In attesa...");
                ClientHandler clientHandler = new ClientHandler(serverSocket.accept(),gameHandler);
                System.out.println("Connessione avvenuta con successo");
                executorService.submit(clientHandler);
                clientHandlers.add(clientHandler);
                clientHandler.setID(clientHandlers.indexOf(clientHandler));
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Method run is the runnable method which instantiates a new socket on server side.
     */
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Started Listening");
            acceptClientConnections(serverSocket);
        } catch (IOException e) {
            System.err.println("Error during Socket initialization, quitting...");
            System.exit(0);
        }
    }

    public ArrayList<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

}