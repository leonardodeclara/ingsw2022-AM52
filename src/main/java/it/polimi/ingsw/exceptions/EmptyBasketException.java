package it.polimi.ingsw.exceptions;

/**
 * Class EmptyBasketException is thrown when has been emptied of all its student tiles.
 */
public class EmptyBasketException extends RuntimeException{

    public EmptyBasketException() {
        super("There are no students tile left!");
    }
}
