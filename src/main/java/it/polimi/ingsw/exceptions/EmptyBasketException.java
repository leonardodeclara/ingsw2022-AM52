package it.polimi.ingsw.exceptions;

public class EmptyBasketException extends RuntimeException{

    public EmptyBasketException() {
        super("There are no students tile left!");
    }
}
