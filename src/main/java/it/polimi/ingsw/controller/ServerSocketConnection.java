package it.polimi.ingsw.controller;

import it.polimi.ingsw.Constants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerSocketConnection implements Runnable {
    private final int port;
    private final ExecutorService executorService;
    private final Server server;
    private final boolean serverRunning;

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
        serverRunning =true;
    }

    /**
     * Method acceptConnections handles the process of accepting connections from clients and is responsible for the
     * creations of new threads, one for each connection. Each thread is kept alive until client disconnection.
     *
     * @param serverSocket of type ServerSocket - the server socket, which accepts connections.
     */
    public void acceptClientConnections(ServerSocket serverSocket) {
        while (serverRunning) {
            try {
                System.out.println("Waiting...");
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(Constants.TIMEOUT);
                ClientHandler clientHandler = new ClientHandler(socket, server);
                System.out.println("Established connection");
                executorService.submit(clientHandler);

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
            e.printStackTrace();
            System.err.println("Error during Socket initialization, quitting...");
            System.exit(0);
        }
    }

}