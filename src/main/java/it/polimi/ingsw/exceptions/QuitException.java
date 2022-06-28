package it.polimi.ingsw.exceptions;

/**
 * Class QuitException is thrown when a client send a DisconnectMessage to server.
 */
public class QuitException extends RuntimeException{

    public QuitException(){
        super();
    }
}
