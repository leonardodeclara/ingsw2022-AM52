package it.polimi.ingsw.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerSocketConnection implements Runnable {
    private final int port;
    private final ExecutorService executorService;
    private final Server server;

    /**
     * Constructor SocketServer creates a new SocketServer instance.
     *
     * @param port of type int - the port on which server will listen.
     * @param server of type Server - the main server object.
     */
    public ServerSocketConnection(int port, Server server) {
        this.server = server;
        this.port = port;
        executorService = Executors.newCachedThreadPool();
    }

    /**
     * Method acceptConnections accepts connections from clients and create a new thread, one for each
     * connection. Each thread lasts until client disconnection.
     *
     * @param serverSocket of type ServerSocket - the server socket, which accepts connections.
     */
    public void acceptClientConnections(ServerSocket serverSocket) {
        while (true) {
            try {
                ClientHandler clientHandler = new ClientHandler(serverSocket.accept());
                executorService.submit(clientHandler);
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Method run is the runnable method which instantiates a new socket on server side.
     *
     *
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
}